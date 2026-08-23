# 拾感日记

个人博客系统，前后端分离，Docker 部署。

## 在线访问

| 服务 | 地址 |
|------|------|
| 前台博客 | https://blog.shiganluo.top |
| 后台管理 | https://back.shiganluo.top |
| MinIO 对象存储 | https://minio.shiganluo.top |

## 技术栈

**前端**：Vue 3 + Pinia + Vue Router + Element Plus + axios + md-editor-v3（Markdown 编辑器）+ wangEditor（富文本编辑器）

**后端**：Spring Boot 3.4 + Spring Security + MyBatis + MySQL 8.0 + Redis + MinIO

**部署**：Docker Compose（nginx-proxy 反向代理 + SSL）

## 项目结构

```
blog/
├── blog-springboot/          # 后端 Spring Boot
│   └── src/main/java/com/baofeng/blog/
│       ├── controller/       # 控制器（admin/、front/）
│       ├── service/          # 业务逻辑
│       ├── mapper/           # MyBatis Mapper
│       ├── entity/           # 实体类
│       ├── dto/              # 数据传输对象
│       ├── config/           # 配置（Security、JWT、MinIO 等）
│       ├── filter/           # JWT 认证过滤器
│       └── common/           # 工具类、注解、定时任务
├── blog-vue3/
│   ├── blog-vue3-front/      # 前台博客（端口 3000）
│   └── blog-vue3-back/       # 后台管理（端口 8888）
├── docker-compose.yml        # 本地开发 Docker 配置
├── publish/                  # 远程部署脚本
│   ├── remote.sh             # 构建 + 导出镜像 + 上传
│   └── remote_publish.sh     # 远程服务器加载镜像 + 重启
└── nginx-proxy.conf          # Nginx 反向代理配置
```

## 本地开发

### 环境要求

- JDK 17
- Node.js 18+
- Docker & Docker Compose

### 启动步骤

```bash
# 1. 启动基础设施（MySQL、Redis、MinIO）
docker compose up -d db redis minio

# 2. 启动后端
cd blog-springboot
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 3. 启动前台
cd blog-vue3/blog-vue3-front
pnpm install && pnpm dev

# 4. 启动后台管理
cd blog-vue3/blog-vue3-back
pnpm install && pnpm dev
```

### 本地端口

| 服务 | 端口 |
|------|------|
| 后端 API | 8080 |
| 前台博客 | 3000 |
| 后台管理 | 8888 |
| MySQL | 3308 |
| Redis | 6380 |
| MinIO API | 9007 |
| MinIO Console | 9008 |

## 远程部署

```bash
# 在本地执行：构建镜像 + 导出 + 上传到远程服务器
bash publish/remote.sh

# 在远程服务器执行：加载镜像 + 重启容器
ssh -p 20225 luosg@39.97.180.240 "cd ~/blog && bash remote_publish.sh"
```

## 功能特性

### 前台（游客/用户）

- 文章浏览、分类、标签、归档
- 文章搜索
- 评论、留言、弹幕
- 点赞
- 友链展示
- 相册展示
- 说说/动态
- 深色模式

### 后台（管理员）

- 仪表盘（访问统计）
- 文章管理（Markdown + 富文本编辑器）
- 评论管理
- 分类/标签管理
- 友链管理
- 相册管理
- 说说管理
- 图片素材库（上传、选择复用）
- 网站设置（logo、favicon、头像、背景、二维码等）
- 用户管理
- 路由管理

## 图片存储

- **存储方式**：MinIO 对象存储
- **数据库**：存储相对路径（如 `my-bucket/uuid.png`）
- **前端展示**：通过 `MinioResponseAdvice` 自动拼接完整 URL
- **URL 清洗**：存入数据库前通过 `UrlNormalizeUtil.stripUrlPrefix()` 剥离域名前缀
- **支持格式**：jpg、png、gif、bmp、ico、svg

## 参考项目

- [mrzym99/vue3-blog](https://github.com/mrzym99/vue3-blog)
- [851543/blog-admin](https://github.com/851543/blog-admin)
- [weiwosuoai/weblog](https://github.com/weiwosuoai/WeBlog)
- [kuailemao/Ruyu-Blog](https://github.com/kuailemao/Ruyu-Blog)
- [pure-admin/pure-admin-thin](https://github.com/pure-admin/pure-admin-thin)
- [galaxy-s10/vue3-blog-admin](https://github.com/galaxy-s10/vue3-blog-admin)
