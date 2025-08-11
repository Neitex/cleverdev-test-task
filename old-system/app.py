import datetime
import json
import uuid

from faker import Faker
from flask import Flask, jsonify, request

app = Flask(__name__)

with open("clients.json", "r") as f:
    clients = json.load(f)
with open("notes.json", "r") as f:
    notes_list = json.load(f)
    notes = {}
    for client in clients:
        notes[client["guid"]] = []
    for note in notes_list:
        notes[note["clientGuid"]] = notes[note["clientGuid"]] + [note]

@app.route('/clients', methods=['POST'])
def get_clients():
    """
    Returns a list of all clients.
    ---
    responses:
      200:
        description: A list of clients.
        schema:
          type: array
          items:
            type: object
            properties:
              agency:
                type: string
              guid:
                type: string
              firstName:
                type: string
              lastName:
                type: string
              status:
                type: string
              dob:
                type: string
              createdDateTime:
                type: string
    """
    return jsonify(clients)

@app.route('/notes', methods=['POST'])
def get_notes():
    """
    Returns a list of notes for a specific client within a date range.
    ---
    parameters:
      - in: body
        name: body
        required: true
        schema:
          type: object
          properties:
            agency:
              type: string
            dateFrom:
              type: string
            dateTo:
              type: string
            clientGuid:
              type: string
    responses:
      200:
        description: A list of notes for the specified client.
        schema:
          type: array
          items:
            type: object
            properties:
              comments:
                type: string
              guid:
                type: string
              modifiedDateTime:
                type: string
              clientGuid:
                type: string
              datetime:
                type: string
              loggedUser:
                type: string
              createdDateTime:
                type: string
      400:
        description: Invalid request payload.
      404:
        description: Client not found.
    """
    data = request.get_json()
    if not data or not all(k in data for k in ['agency', 'dateFrom', 'dateTo', 'clientGuid']):
        return jsonify({"error": "Missing required fields in payload"}), 400

    client_guid = data['clientGuid']
    agency = data['agency']
    date_from_str = data['dateFrom']
    date_to_str = data['dateTo']

    try:
        date_from = datetime.datetime.strptime(date_from_str, '%Y-%m-%d')
        date_to = datetime.datetime.strptime(date_to_str, '%Y-%m-%d')
    except ValueError:
        return jsonify({"error": "Invalid date format. Use YYYY-MM-DD."}), 400

    client_notes = notes.get(client_guid)

    if not client_notes:
        return jsonify({"error": "Client not found"}), 404

    # This is a simplified filtering. A real implementation might need to parse the 'datetime' field more robustly.
    filtered_notes = [
        note for note in client_notes
        if date_from <= datetime.datetime.strptime(note['datetime'].split(' ')[0], '%Y-%m-%d') <= date_to
    ]

    return jsonify(filtered_notes)

@app.route('/patient-profiles', methods=['POST'])
def get_patient_profiles_import_sql():
    """
    Returns a SQL insert statement for patient profiles.
    ---
    responses:
      200:
        description: A SQL insert statement for patient profiles.
        schema:
          type: string
    """
    with open("patient_profiles.sql", "r") as f:
        sql_content = f.read()
    return sql_content

if __name__ == '__main__':
    app.run(host='0.0.0.0')
