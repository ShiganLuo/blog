# 阶段一：编译
FROM node:18 AS build-stage
WORKDIR /app

# 1. 安装 pnpm (解决 exit code 127 的关键)
# 也可以设置国内镜像源加速
RUN npm install -g pnpm && pnpm config set registry https://registry.npmmirror.com

# 2. 先复制依赖描述文件
# 这样做的好处是：只要 package.json 和 pnpm-lock.yaml 没变，
# 再次构建时会直接跳过安装步骤，利用 Docker 缓存，速度极快
COPY package*.json pnpm-lock.yaml* ./

# 3. 安装依赖
RUN pnpm install

# 4. 复制其他源代码
COPY . .

# 5. 执行打包
RUN pnpm run build

# 阶段二：部署
FROM nginx:stable-alpine

# 复制编译后的文件到 nginx 目录
COPY --from=build-stage /app/dist /usr/share/nginx/html

# 重要：对于 Vue/React 项目，如果不开启 try_files，
# 刷新页面会出现 404（尤其是在使用 history 模式路由时）
# 建议你创建一个简单的 nginx.conf 并取消下面这行的注释
# COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]