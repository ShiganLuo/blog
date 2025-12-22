# 使用 JDK 17 或 8
FROM openjdk:17-jdk-slim
WORKDIR /app
# 将打包好的 jar 复制进去
COPY target/*.jar app.jar
# 暴露后端端口
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]