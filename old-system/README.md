# Old System mock server

A simple data generator for both the mock server and the migration application start state (
`data_generator.py`) and a simple server (`app.py`) that serves the generated data while complaining
with the given API description.

## Usage

TODO: dockerize this

Requires uv to run the server and the data generator.

First, run the data generator to create the initial data:

```bash
uv run data_generator.py
```

This will generate three files:

- `clients.json` - the client data
- `notes.json` - the notes data
- `clients_import.sql` - an SQL script to import the clients data into the migration application

Then, run the server:

```bash
uv run app.py
```

The server will start on `http://localhost:5000` and serve the generated data at the following
endpoints:

- `POST /clients` - the client data
- `POST /notes` - the notes data (requires a body with parameters `clientGuid`, `agency`, `dateFrom`
  and `dateTo`)

