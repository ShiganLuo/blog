# 阶段一：编译
FROM node:18 AS build-stage
WORKDIR /app
COPY package*.json ./
RUN pnpm install
COPY . .
RUN pnpm run build

# 阶段二：部署
FROM nginx:stable-alpine
# 复制编译后的文件到 nginx 目录
COPY --from=build-stage /app/dist /usr/share/nginx/html
# 如果有自定义 nginx 配置（处理 Vue 路由 history 模式）
# COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]