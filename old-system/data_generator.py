import random
import uuid
import json
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
            "datetime": fake.date_time_between(start_date='-2y', end_date='now').strftime('%Y-%m-%d %H:%M:%S CDT'),
            "loggedUser": users[random.randint(0, len(users) - 1)],
            "createdDateTime": fake.date_time_this_decade().strftime('%Y-%m-%d %H:%M:%S')
        })
    return notes


clients = create_clients(5500)

with open("clients.json", "w") as f:
    json.dump(clients, f)

with open("notes.json", "w") as f:
    notes = []
    for client in clients:
        notes = notes + create_notes_for_client(client["guid"], 15)
    json.dump(notes, f)

with open("clientele", "a") as f:
    for client_list in list(batched(clients[0:999], 3)):
        client = client_list[0]
        f.write(
            f"('{client["firstName"]}','{client["lastName"]}','{client_list[0]["guid"]},{client_list[1]["guid"]},{client_list[2]["guid"]}'),")
