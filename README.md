# 拾感日记

一个功能完整的个人博客系统，采用前后端分离架构，支持 Docker 一键部署。

## 技术栈

| 层级 | 技术 |
|------|------|
| 前台 / 后台 | Vue 3 + Pinia + Vue Router + Element Plus |
| 编辑器 | md-editor-v3（Markdown）+ wangEditor（富文本）|
| 后端 | Spring Boot 3.4 + Spring Security + MyBatis |
| 数据库 | MySQL 8.0 + Redis |
| 对象存储 | MinIO |
| 部署 | Docker Compose + Nginx 反向代理 |

## 项目结构

```
blog/
├── blog-springboot/              # 后端
│   └── src/main/java/.../blog/
│       ├── controller/admin/     # 后台接口
│       ├── controller/front/     # 前台接口
│       ├── service/              # 业务逻辑
│       ├── mapper/               # MyBatis Mapper
│       ├── entity/               # 实体类（17 张表）
│       ├── dto/                  # 数据传输对象
│       ├── config/               # Security、JWT、MinIO 配置
│       ├── filter/               # JWT 认证过滤器
│       └── common/
│           ├── annotation/       # @MinioFile、@MinioScan 自定义注解
│           ├── advice/           # MinioResponseAdvice 响应拦截
│           ├── scheduler/        # 定时任务（访问量同步）
│           └── util/             # 工具类
├── blog-vue3/
│   ├── blog-vue3-front/          # 前台博客
│   └── blog-vue3-back/           # 后台管理
├── docker-compose.yml            # 本地 Docker 配置
└── README.md
```

## 功能特性

### 前台（游客 / 注册用户）

- 文章浏览、分类、标签、归档
- 全文搜索
- 评论系统（树形结构，支持嵌套回复）
- 留言板 + 弹幕互动
- 说说 / 动态
- 相册展示
- 友链展示
- 点赞（文章 + 评论）
- RSS 订阅
- 深色模式切换

### 后台（管理员）

- 仪表盘（访问统计、趋势图表）
- 文章管理（Markdown + 富文本双编辑器）
- 评论审核
- 分类 / 标签管理
- 友链管理
- 相册管理
- 说说管理
- 图片素材库（上传、浏览、选择复用）
- 网站设置（logo、favicon、头像、背景、二维码等）
- 用户管理
- 路由管理（动态菜单）

## 设计亮点

### 前端设计

#### Axios 统一封装

前后台各有一套完整的 Axios 封装（~200 行），核心特性：

- **双令牌无感刷新**：Access Token 过期时自动用 Refresh Token 刷新，挂起的请求队列批量重试，用户无感知
- **请求拦截器**：自动注入 Authorization 头
- **响应拦截器**：统一处理业务状态码（200/400/401/403/404/500），二进制数据直接放行
- **silent 模式**：请求配置 `silent: true` 时不弹错误提示，适用于静默加载场景
- **错误提示**：带 Emoji 表情的友好错误信息

#### 类式 API 封装

所有接口按模块封装为静态类方法，调用时无需实例化：

```typescript
// 按模块组织：HomeService、ArticleService、PhotoService...
export class HomeService {
  static getOneSentence() {
    return request.get<string>({ url: '/front/utils/oneSentence' })
  }
}
```

#### 组件库

前台包含 18 个通用组件：`SwitchTheme`（深色模式切换）、`Comment`（树形评论）、`Search`（搜索）、`TypeWriter`（打字机效果）、`GsapCount`（数字动画）、`TimeLine`（时间线）、`TextOverflow`（文本溢出）等。

后台包含 `ImagePicker`（图片素材库选择器）、`CutterImg`（图片裁剪）、`Charts`（图表）、`Watermark`（水印）、`VideoPlayer`（视频播放）等。

### 图片素材库

后台提供统一的图片素材库，上传的图片可以在文章封面、编辑器、网站设置等场景复用，避免重复上传。

### URL 自动清洗

数据库统一存储相对路径（如 `my-bucket/uuid.png`），通过 `UrlNormalizeUtil` 在写入时剥离域名前缀，通过 `@MinioFile` 注解 + `MinioResponseAdvice` 在响应时自动拼接完整 URL。更换存储地址只需改配置，无需批量更新数据库。

### RBAC 权限控制

采用用户 → 角色 → 权限（User → Role → Permission）三级模型，支持菜单级和按钮级权限控制。

### JWT 认证

Access Token + Refresh Token 双令牌机制，支持无感刷新。白名单控制哪些接口不需要认证。

### 实体-图片通用关联

通过 `entity_images` 关联表，将图片与文章、相册等实体解耦，支持多用途（封面、logo 等）和排序。

## 本地开发

### 环境要求

- JDK 17
- Node.js 18+
- Docker & Docker Compose

### 启动

```bash
# 1. 启动基础设施
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

## 数据库设计

共 17 张表，核心表包括：

- `users` — 用户表
- `articles` — 文章表
- `comments` — 评论表（树形结构）
- `images` — 图片表
- `blog_settings` — 站点配置（单记录）
- `categories` / `tags` — 分类和标签
- `friend_link` — 友链
- `likes` — 点赞（通用）
- `roles` / `permissions` — RBAC 权限
- `routes` — 动态路由
- `entity_images` — 实体-图片通用关联

## 参考项目

- [mrzym99/vue3-blog](https://github.com/mrzym99/vue3-blog)
- [851543/blog-admin](https://github.com/851543/blog-admin)
- [weiwosuoai/weblog](https://github.com/weiwosuoai/WeBlog)
- [kuailemao/Ruyu-Blog](https://github.com/kuailemao/Ruyu-Blog)
- [pure-admin/pure-admin-thin](https://github.com/pure-admin/pure-admin-thin)
- [galaxy-s10/vue3-blog-admin](https://github.com/galaxy-s10/vue3-blog-admin)
