FROM amazoncorretto:17.0.9-al2023-headless
ENV TZ Asia/Seoul
COPY ./build/libs/mimo.jar /mimo.jar
ENTRYPOINT ["java", "-jar", "/mimo.jar"]
