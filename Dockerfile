FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
COPY . .
RUN ./gradlew build --no-daemon

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=10s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENV SPRING_DATA_MONGODB_URI=mongodb+srv://kumaraayush2310:NNl5SyLDasNJFNlv@my-cluster.fuykbkq.mongodb.net/verto

# Entry point to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]