# CleverDev test task

## Additions to the data schema

- `legacy_guid` column to `patient_note` table to store the legacy system's unique identifier for
  each patient note.

## Assumptions

- DateTimes in Legacy system are in local timezone unless mentioned otherwise
- DateTimes in New system are stored as timestamps in UTC
- It is assumed that `old_client_guid` with 255-character limit is sufficient to store legacy ids.
  If not, it is possible to replace that with a JOINable table with legacy ids.
- This solution is tailored for the PostgreSQL
- It is assumed that the modern system database is pre-populated with the patients data
- Racing conditions with data disappearing from the legacy system are not considered
- "каждые два часа, в 15 минут первого часа" means every two hours starting from 1:15 AM
