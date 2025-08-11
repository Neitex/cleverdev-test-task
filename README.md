# CleverDev test task

## Running

This project is dockerized, so starting it up is as simple as running:

```bash
docker compose up
```

That will expose the following ports:

- `5432` - PostgreSQL database
- `5000` - the mock server (see [old-system README](old-system/README.md) for details)
- `8080` - the migration application (though only Spring Actuator is available at the moment)

## Populating database with data from the legacy system

The application was built with an assumption that patients will be known to the application
beforehand, so it is required to pre-populate the database with the patients data from the legacy
system. Mock server provides an endpoint `POST /patient-profiles` that returns an SQL script to
import the clients data.
An example script to import clients data into the database is shown below. Please note that the data
structure has to be created first with Flyway DB migration in the application, so run the
application once before executing script below.

```bash
#!/bin/bash
LEGACY_URL="${MIGRATOR_LEGACY_SYSTEM_URL:-http://localhost:5000}"
DB_USER="${MIGRATOR_DB_USERNAME:-username}"
DB_PASSWORD="${MIGRATOR_DB_PASSWORD:-password}"
DB_NAME="${MIGRATOR_DB_NAME:-database}"

DB_CONTAINER=$(docker ps --format '{{.Names}}' | grep -i migrator | grep -i database | head -n 1)
if [ -z "$DB_CONTAINER" ]; then
  echo "Error: Could not find database container. Is it running?"
  exit 1
fi

echo "Using database container: $DB_CONTAINER"

echo "Clearing existing patient data..."
docker exec -i -e PGPASSWORD="${DB_PASSWORD}" "${DB_CONTAINER}" psql -h localhost -U "${DB_USER}" "${DB_NAME}" <<EOF
DELETE FROM patient_note;
DELETE FROM patient_profile;
EOF

echo "Fetching data from legacy system and importing to database..."
curl -s -XPOST "${LEGACY_URL}/patient-profiles" | \
  docker exec -i -e PGPASSWORD="${DB_PASSWORD}" "${DB_CONTAINER}" \
  psql -h localhost -U "${DB_USER}" "${DB_NAME}"

echo "Migration completed successfully"
```

## Additions to the data schema

- `legacy_guid` column to `patient_note` table to store the legacy system's unique identifier for
  each patient note.

## Assumptions in implementation

- DateTimes in Legacy system are in local timezone unless mentioned otherwise
- DateTimes in New system are stored as timestamps in UTC
- It is assumed that `old_client_guid` with 255-character limit is sufficient to store legacy ids.
  If not, it is possible to replace that with a JOINable table with legacy ids.
- This solution is tailored for the PostgreSQL
- It is assumed that the modern system database is pre-populated with the patients data
- Racing conditions with data disappearing from the legacy system are not considered
- "каждые два часа, в 15 минут первого часа" means every two hours starting from 1:15 AM
