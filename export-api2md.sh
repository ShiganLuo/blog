#!/bin/bash

API_URL="http://localhost:8080/v3/api-docs"
JSON_FILE="openapi.json"
MD_FILE="api-docs.md"

echo "📥 拉取 Swagger OpenAPI JSON..."
curl -s ${API_URL} -o ${JSON_FILE}

if [ ! -s ${JSON_FILE} ]; then
  echo "❌ 拉取失败，请检查 API_URL 是否正确"
  exit 1
fi

echo "📄 转换为 Markdown..."
widdershins ${JSON_FILE} -o ${MD_FILE}

if [ -s ${MD_FILE} ]; then
  echo "✅ 成功导出 -> ${MD_FILE}"
else
  echo "❌ 转换失败"
fi

