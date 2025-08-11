FROM gradle:7.6.1-jdk17-alpine AS build
WORKDIR /app

COPY gradle/ gradle/
COPY gradlew settings.gradle.kts build.gradle.kts ./

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon --stacktrace

COPY src/ src/

RUN ./gradlew build --no-daemon -x test

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
