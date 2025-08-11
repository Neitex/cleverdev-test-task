# Old System mock server

A simple data generator for both the mock server and the migration application start state (
`data_generator.py`) and a simple server (`app.py`) that serves the generated data while complaining
with the given API description.

## Usage

Project uses Docker, so this server is containerized too.

```bash
docker build --tag cleverdev-migrator-oldsystem .
docker run --rm -p 5000:5000 cleverdev-migrator-oldsystem
```

This will generate three files:

- `clients.json` - the client data
- `notes.json` - the notes data
- `patient_profiles.sql` - an SQL script to import the clients data into the migration application

Then, run the server:

```bash
uv run app.py
```

The server will start on `http://localhost:5000` and serve the generated data at the following
endpoints:

- `POST /clients` - the client data
- `POST /notes` - the notes data (requires a body with parameters `clientGuid`, `agency`, `dateFrom`
  and `dateTo`)

