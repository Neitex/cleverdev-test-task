import random
import uuid
import json
import os
from itertools import batched

from faker import Faker

Faker.seed(42)
fake = Faker()

users = [fake.user_name() for _ in range(100)]


def create_clients(num_clients=10):
    """Generates a list of fake clients."""
    clients = []
    for _ in range(num_clients):
        clients.append({
            "agency": fake.random_element(elements=("vhh4", "agcy1", "other")),
            "guid": str(uuid.uuid4()),
            "firstName": fake.first_name(),
            "lastName": fake.last_name(),
            "status": fake.random_element(elements=("ACTIVE", "INACTIVE")),
            "dob": fake.date_of_birth(minimum_age=18, maximum_age=90).strftime('%m-%d-%Y'),
            "createdDateTime": fake.date_time_this_decade().strftime('%Y-%m-%d %H:%M:%S')
        })
    return clients


def create_notes_for_client(client_guid, num_notes=5):
    """Generates a list of fake notes for a specific client."""
    notes = []
    for _ in range(num_notes):
        notes.append({
            "comments": fake.text(),
            "guid": str(uuid.uuid4()),
            "modifiedDateTime": fake.date_time_this_decade().strftime('%Y-%m-%d %H:%M:%S'),
            "clientGuid": client_guid,
            "datetime": fake.date_time_between(start_date='-2y', end_date='now').strftime(
                '%Y-%m-%d %H:%M:%S'),
            "loggedUser": users[random.randint(0, len(users) - 1)],
            "createdDateTime": fake.date_time_this_decade().strftime('%Y-%m-%d %H:%M:%S')
        })
    return notes


clients = create_clients(5500)

def sql_sanitize(value):
    """Sanitizes a string for SQL insertion."""
    if isinstance(value, str):
        return value.replace("'", "''")  # Escape single quotes
    return value

os.remove("data.sql") if os.path.exists("data.sql") else None
with open("data.sql", "a") as f:
    for client in batched(clients, 100):
        f.write("INSERT INTO client_profile (guid, agency, first_name, last_name, status, dob, created_date_time) VALUES\n")
        values = []
        for c in client:
            values.append(f"('{c['guid']}', '{c['agency']}', '{c['firstName']}', '{c['lastName']}', "
                          f"'{c['status']}', '{c['dob']}', '{c['createdDateTime']}')")
        f.write(",\n".join(values))
        f.write(";\n")
    f.write('\n\n')
    for client in clients:
        f.write("INSERT INTO client_note (guid, comments, modified_date_time, client_guid, datetime, logged_user, created_date_time) VALUES\n")
        notes = create_notes_for_client(client['guid'], 15)
        values = []
        for note in notes:
            values.append(f"('{note['guid']}', '{sql_sanitize(note['comments'])}', '{note['modifiedDateTime']}', "
                          f"'{note['clientGuid']}', '{note['datetime']}', '{note['loggedUser']}', "
                          f"'{note['createdDateTime']}')")
        f.write(",\n".join(values))
        f.write(";\n")
