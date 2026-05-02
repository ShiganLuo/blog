#!/bin/bash

docker run -d \
  --name blog_mysql \
  -p 3308:3306 \
  -e MYSQL_DATABASE=blog \
  -e MYSQL_ROOT_PASSWORD=7897564 \
  -v $(pwd)/blog-springboot/blog.sql:/docker-entrypoint-initdb.d/blog.sql \
  -v $(pwd)/database/mysql:/var/lib/mysql \
  --restart always \
  mysql:8.0.33


docker run -d \
  --name blog_redis \
  -p 6380:6379 \
  -v $(pwd)/database/redis/data:/data \
  -v $(pwd)/redis.conf:/usr/local/etc/redis/redis.conf \
  --restart always \
  redis:7.0.10 \
  redis-server /usr/local/etc/redis/redis.conf

docker run -d \
  --name blog_minio \
  -p 9007:9000 \
  -p 9008:9001 \
  -e MINIO_ROOT_USER=admin \
  -e MINIO_ROOT_PASSWORD=27654576 \
  -v $(pwd)/database/minio/data:/data \
  -v $(pwd)/minio-init.sh:/minio-init.sh \
  --restart always \
  minio/minio server /data --console-address ":9001"
