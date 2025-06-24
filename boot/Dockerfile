# === 第一阶段：用 Maven 构建 jar 包 ===
FROM maven:3.9.4-eclipse-temurin-17 AS builder

# 使用国内镜像加速：覆盖默认settings.xml
COPY settings.xml /usr/share/maven/conf/settings.xml

WORKDIR /app
COPY . .
# 显式指定使用自定义settings.xml
RUN mvn -s /usr/share/maven/conf/settings.xml clean package -DskipTests

# === 第二阶段：用更小的运行环境启动 jar ===
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]