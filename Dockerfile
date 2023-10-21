#FROM openjdk:17-jdk-slim
#
## copy the packaged jar file into our docker image
#COPY target/BankDemo-0.0.1-SNAPSHOT.jar /bank-demo-app.jar
#
## set the startup command to execute the jar
#CMD ["java", "-jar", "/bank-demo-app.jar"]

FROM maven:3.8.4-openjdk-17 as builder
WORKDIR /app
COPY . /app/.
RUN mvn -f /app/pom.xml clean package -Dmaven.test.skip=true

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/*.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/*.jar"]
