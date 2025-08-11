FROM ghcr.io/astral-sh/uv:alpine3.21
LABEL authors="pavel"
LABEL description="CleverDev test task old system"

WORKDIR /app
COPY app.py data_generator.py pyproject.toml ./
RUN uv sync
RUN uv run data_generator.py

CMD ["uv", "run", "app.py"]
