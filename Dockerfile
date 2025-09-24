FROM maven:latest
LABEL authors="D"

WORKDIR /app

COPY pom.xml /app

COPY . /app

RUN mvn package

CMD ("java", "-jar", "target/OTP-1-1.0-SNAPSHOT.jar")