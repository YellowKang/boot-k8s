FROM fabletang/jre8-alpine
ADD target/boot-k8s-v1.jar /app.jar
ENV JAVA_OPTS=""
ENV APP_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar  $APP_OPTS" ]
EXPOSE 8080
