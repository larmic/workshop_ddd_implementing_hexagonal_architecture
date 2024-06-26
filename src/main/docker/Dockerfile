# Build spring boot app
FROM openjdk:21-jdk as app-build

COPY mvnw .
COPY .mvn/ .mvn/
COPY pom.xml .

# Maven should be executable
RUN chmod +x ./mvnw

# Download dependencies if needed (i.e. pom has changed)
RUN ./mvnw dependency:go-offline -B

# Copy application sources to docker build stage
COPY src src

# Build artifact
RUN ./mvnw package -DskipTests
RUN mv target/*.jar target/spring-boot-app.jar


# OpenJDK has a size of ~517MB, Slim has ~230MB
# Build custom jre using jlink reduces size to ~160MB
FROM openjdk:21-jdk as jre-build

# Create a custom Java runtime
# see https://medium.com/azulsystems/using-jlink-to-build-java-runtimes-for-non-modular-applications-9568c5e70ef4
# Add or remove module if needed!
RUN $JAVA_HOME/bin/jlink \
         --add-modules java.base,java.desktop,java.logging,java.management,java.naming,java.security.jgss,java.instrument,java.sql  \
            # java.logging - java/util/logging/Logger
            # java.naming - javax/naming/NamingException
            # java.desktop - java/beans/PropertyEditorSupport
            # java.management - javax/management/MBeanServer
            # java.security.jgss - org/ietf/jgss/GSSException
            # java.instrument - java/lang/instrument/IllegalClassFormatException
            # java.sql - java/sql/SQLException
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /javaruntime


# Build Spring Boot docker image using latest slim debian image
FROM debian:bullseye-slim

MAINTAINER Lars Michaelis <mail@larmic.de>

# Container should run in non-root user mode
ENV USER_NAME="larmic"
ENV USER_UID=999
ENV USER_GID=999

# Copy custom JRE from jre-build docker stage
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-build /javaruntime $JAVA_HOME

# Copy application from app-build docker stage
COPY --from=app-build /target/spring-boot-app.jar .

# Create non-root user
RUN groupadd --gid $USER_GID $USER_NAME && \
    useradd -r --uid $USER_UID --gid $USER_GID $USER_NAME
USER $USER_NAME

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/spring-boot-app.jar"]