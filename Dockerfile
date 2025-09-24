# Step 1: Build all modules
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent pom
COPY pom.xml .

# Copy all modules pom.xml
COPY web/pom.xml web/
COPY common-service/pom.xml common-service/
COPY email-service/pom.xml email-service/
COPY file-service/pom.xml file-service/
COPY security-service/pom.xml security-service/
COPY user-service/pom.xml user-service/
COPY course-service/pom.xml course-service/
COPY notification-service/pom.xml notification-service/
COPY commerce-service/pom.xml commerce-service/
COPY enrollment-service/pom.xml enrollment-service/

# Copy all source code
COPY web/src web/src
COPY common-service/src common-service/src
COPY email-service/src email-service/src
COPY file-service/src file-service/src
COPY security-service/src security-service/src
COPY user-service/src user-service/src
COPY course-service/src course-service/src
COPY notification-service/src notification-service/src
COPY commerce-service/src commerce-service/src
COPY enrollment-service/src enrollment-service/src

# Build entire project
RUN mvn -B package -DskipTests

# Step 2: Create runtime image
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/web/target/web-0.0.1-SNAPSHOT.jar ./web.jar
EXPOSE 8105
ENTRYPOINT ["java","-jar","web.jar"]