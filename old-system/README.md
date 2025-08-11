# Old System mock server

A simple Spring Boot mock server to simulate the old system's API responses.

## Building

The project is built using either Gradle or Docker, but the latter is recommended for simplicity
because it also generates the mock data automatically.

## Data generation

Mock data is to be generated before compile time. The data is generated using Python script in
`data-gen` directory. Please, refer to the `README.md` file in that directory for more information
on the usage.

After `data.sql` is produced by that script, it must be copied to the `src/main/resources`
directory.
