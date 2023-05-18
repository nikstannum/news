FROM openjdk:17-alpine
LABEL maintaineer="Semeniuk N.L."
WORKDIR /app
COPY ./build/libs/*.jar /app/
RUN apk update && apk add --no-cache bash
ENTRYPOINT ["java", "-jar"]
CMD ["config-server-0.0.1-SNAPSHOT.jar"]