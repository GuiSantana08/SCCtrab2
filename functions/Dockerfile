FROM maven:3.6-jdk-11 as build
WORKDIR /app
COPY . /app
RUN mvn clean package
FROM openjdk:11
WORKDIR /app
COPY --from=build /app/target/*.jar /app
CMD ["java", "-jar", "/app/scc2324-functions-1.0.jar"]

