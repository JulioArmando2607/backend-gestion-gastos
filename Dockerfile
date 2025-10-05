# ====== Build ======
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# cachea dependencias (modo batch = logs más limpios)
RUN mvn -q -B -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package

# ====== Run ======
FROM eclipse-temurin:21-jre
WORKDIR /app
# usuario no-root
RUN useradd -ms /bin/bash appuser
USER appuser
# copia el jar construido
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081
ENV JAVA_OPTS=""
ENV PORT=8081
CMD ["sh","-c","java $JAVA_OPTS -jar app.jar --server.port=$PORT"]