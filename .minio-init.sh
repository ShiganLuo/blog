#!/bin/sh

# 定义地址，本地运行建议用 127.0.0.1
MINIO_URL="http://127.0.0.1:9000"

echo "等待 MinIO 服务启动..."

# 循环检测服务是否可用 (取代 sleep 5)
until curl -s "$MINIO_URL/minio/health/live"; do
  echo "MinIO 尚未就绪，等待中..."
  sleep 1
done

echo "MinIO 已启动，开始配置..."

# 配置别名
mc alias set localminio "$MINIO_URL" "$MINIO_ROOT_USER" "$MINIO_ROOT_PASSWORD"

# 创建桶
mc mb --ignore-existing localminio/my-bucket

# 设置策略
mc anonymous set public localminio/my-bucket