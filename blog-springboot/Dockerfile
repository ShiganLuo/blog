# =========================
# Stage 1: Build
# =========================
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /build

# 先复制 pom.xml，利用 Docker 缓存
COPY pom.xml .

# 下载依赖（这一层只要 pom 不变就会缓存）
RUN mvn -B -q dependency:go-offline

# 再复制源码
COPY src ./src

# 打包（跳过测试可加 -DskipTests）
RUN mvn -B clean package -DskipTests


# =========================
# Stage 2: Runtime
# =========================
FROM openjdk:17-jdk-slim

# 图形验证码字体库
RUN apt-get update \
    && apt-get install -y libfontconfig1 \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# 只拷贝最终 jar
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
