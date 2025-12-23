# 使用 JDK 17 或 8
FROM openjdk:17-jdk-slim
#图形验证码字体库
RUN apt-get update && apt-get install -y libfontconfig1 && apt-get clean
WORKDIR /app
# 将打包好的 jar 复制进去
COPY target/*.jar app.jar
# 暴露后端端口
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]