
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app


COPY pom.xml .
RUN mvn dependency:go-offline -B


COPY src ./src


RUN mvn clean package -DskipTests -B


FROM eclipse-temurin:17-jre-alpine
WORKDIR /app


ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-docker}
ENV QR_ENCRYPTION_PASSWORD=${QR_ENCRYPTION_PASSWORD:-ECIEXPRESS_QR_PASSWORD_2025}
ENV QR_ENCRYPTION_SALT=${QR_ENCRYPTION_SALT:-1234567890abcdef}

COPY --from=build /app/target/*.jar app.jar


EXPOSE 8088


ENTRYPOINT ["java", "-jar", "app.jar"]
