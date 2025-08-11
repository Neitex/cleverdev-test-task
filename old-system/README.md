# Old System mock server

A simple Spring Boot mock server to simulate the old system's API responses.

## Building

The project is built using either Gradle or Docker, but the latter is recommended for simplicity
because it also generates the mock data automatically.

```bash
docker build -t cleverdev-migrator-old_system .
docker run -p 5000:8080 cleverdev-migrator-old_system
```

## Data generation

Mock data is to be generated before compile time. The data is generated using Python script in
`data-gen` directory. After `data.sql` is produced by the script, it must be copied to the
`src/main/resources`
directory. To generate the mock data manually, run the following script with `uv` installed in
your system:

```bash
cd data-gen
uv run data_generator.py
cp data.sql ../src/main/resources/
```

Please note that Docker container build will automatically run the script inside the container
and copy the generated `data.sql` to the correct location, so you don't have to do this manually.
