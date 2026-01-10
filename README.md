# 需求分析

角色：游客、用户、管理员。

游客可以浏览以及点赞站内文章，但不能评论、留言、发送弹幕

用户在游客的基础上可以评论、留言、发送弹幕，能登录后台

管理员可以登录后台，并拥有所有权限


# 架构设计

## 总体结构

采用前后端分离模式

## 技术选型

### 前端

- 核心框架：Vue3
- 状态管理: Pina
- 路由：Vue Router
- 后端交互：axios
- 组件库：element-plus
- 语法和错误检查: Prettier、ESLint

不采用任何后台框架
### 后端

- 核心框架：Spring boot
- 数据库：MySQL
- 数据访问层：Mybatis
- 文件存储：本地存储
- 缓存：Redis

## 部署

- Docker

# 数据设计

## 数据库表设计

```sql
CREATE DATABASE IF NOT EXISTS `blog` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `blog`;
```
### 用户表 users 

用于存储用户信息

| **字段名**             | **数据类型**           | **是否可为空** | **默认值**    | **约束/描述**                                     |
| ------------------- | ------------------ | --------- | ---------- | --------------------------------------------- |
| `id`                | `BIGINT`           | 否         | 自增         | 主键，自增，唯一标识用户                                  |
| `username`          | `VARCHAR(50)`      | 否         | 无          | 不允许为空，唯一，存储用户名                                |
| `email`             | `VARCHAR(100)`     | 否         | 无          | 不允许为空，唯一，存储用户邮箱                               |
| `password`          | `VARCHAR(255)`     | 否         | 无          | 不允许为空，存储加密后的密码                                |
| `avatar_url`        | `VARCHAR(255)`     | 是         | NULL       | 可选，存储用户头像的 URL                                |
| `bio`               | `TEXT`             | 是         | NULL       | 可选，存储用户简介                                     |
| `status`            | `VARCHAR(15)`      | 否         | ACTIVE     | 标识用户账户的状态（ACTIVE, INACTIVE, DISABLED）         |
| `last_login`        | `TIMESTAMP`        | 否         | 无          | 记录用户最后一次登录的时间                                 |
| `login_attempts`    | `BIGINT`           | 是         | 0          | 记录登录失败次数，防止暴力破解                               |
| `is_email_verified` | `TINYINT(1)`       | 否         | FALSE      | 是否已验证邮箱                                       |
| `nick_name`         | `VARCHAR(50)`      | 是         | NULL       | 昵称                                            |
| `phone_number`      | `VARCHAR(32)`      | 是         | NULL       | 电话号码                                          |
| `gender`            | `TINYINT UNSIGNED` | 否         | 9          | 性别：1=男，2=女，9=未知                               |
| `created_at`        | `TIMESTAMP`        | 否         | 创建时间       | CURRENT_TIMESTAMP                             |
| `updated_at`        | `TIMESTAMP`        | 否         | 更新时间（自动更新） | CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |

```sql
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键，自增，唯一标识用户',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名，唯一且不允许为空',
  `email` VARCHAR(100) NOT NULL UNIQUE COMMENT '用户邮箱，唯一且不允许为空',
  `password` VARCHAR(255) NOT NULL COMMENT '加密后的密码',
  `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '用户头像 URL',
  `bio` TEXT DEFAULT NULL COMMENT '用户简介',
  `status` VARCHAR(15) NOT NULL DEFAULT 'ACTIVE' COMMENT '账户状态：ACTIVE/INACTIVE/DISABLED',
  `last_login` TIMESTAMP NOT NULL COMMENT '最后登录时间',
  `login_attempts` BIGINT NOT NULL DEFAULT 0 COMMENT '登录失败次数',
  `is_email_verified` TINYINT(1) NOT NULL DEFAULT FALSE COMMENT '邮箱是否已验证',
  `is_active` TINYINT(1) NOT NULL DEFAULT TRUE COMMENT '账户是否启用',
  `nick_name` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `phone_number` VARCHAR(32) DEFAULT NULL COMMENT '电话号码',
  `gender` TINYINT UNSIGNED DEFAULT 9 COMMENT '性别：1=男，2=女，9=未知',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

```
---

### 角色表 roles

| 字段名          | 数据类型         | 是否可以为空     | 默认值                                             | 约束/描述               |
| ------------ | ------------ | ---------- | ----------------------------------------------- | ------------------- |
| `id`         | BIGINT       | 否          | 自增                                              | 角色ID                |
| `role_name`  | VARCHAR(50)  | 否          | 无                                               | 角色名称，如: admin, user |
| `role_desc`  | VARCHAR(255) | 否          | 无                                               | 角色描述                |
| `created_at` | TIMESTAMP    | 创建时间       | `CURRENT_TIMESTAMP`                             | 用户注册时间，自动生成         |
| `updated_at` | TIMESTAMP    | 更新时间（自动更新） | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 用户信息更新时间            |
```sql
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
	`id`     BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    `role_name`   VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称，如 admin, user',
    `role_desc`   VARCHAR(255) COMMENT '角色描述',
	`created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户注册时间，自动生成',
	`updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '用户信息更新时间'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

```


### 权限表 permissions


| 字段名          | 数据类型         | 是否可以为空 | 默认值                                             | 约束 / 描述                        |
| ------------ | ------------ | ------ | ----------------------------------------------- | ------------------------------ |
| `id`         | BIGINT       | 否      | 自增                                              | 权限 ID，主键，自增                    |
| `name`       | VARCHAR(100) | 否      | 无                                               | 权限名称（如 “查看文章”）                 |
| `permission` | VARCHAR(100) | 否      | 无                                               | 权限标识（如 `blog:article:view`），唯一 |
| `type`       | VARCHAR(20)  | 是      | `'button'`                                      | 权限类型（如 `menu`、`button`、`api`）  |
| `parent_id`  | BIGINT       | 是      | `NULL`                                          | 父权限 ID（用于菜单树结构）                |
| `path`       | VARCHAR(100) | 是      | `NULL`                                          | 对应路由或接口路径                      |
| `created_at` | TIMESTAMP    | 否      | `CURRENT_TIMESTAMP`                             | 创建时间                           |
| `updated_at` | TIMESTAMP    | 否      | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间（自动更新）                     |


```sql
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    `name` VARCHAR(100) NOT NULL COMMENT '权限名称',
    `permission` VARCHAR(100) NOT NULL UNIQUE COMMENT '权限标识，如 blog:article:view',
    `type` VARCHAR(20) DEFAULT 'button' COMMENT '权限类型(menu, api, button)',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父权限ID（用于树结构）',
    `path` VARCHAR(100) DEFAULT NULL COMMENT '对应路由或接口路径',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

```
### 文章表 articles

用于存储文章内容。

一个文章可以有多个分类表示其分级关系，也可以有多个标签用来索引，但只能有一个作者
作者信息删除，对应的文章全部销毁

| 字段名              | 数据类型         | 是否可以为空 | 默认值                                             | 约束/描述                                     |
| ---------------- | ------------ | ------ | ----------------------------------------------- | ----------------------------------------- |
| `id`             | BIGINT       | 否      | 自增                                              | 文章ID，主键，自增                                |
| `title`          | VARCHAR(255) | 否      | 无                                               | 文章标题，不能为空                                 |
| `content`        | TEXT         | 否      | 无                                               | 文章正文，支持 Markdown 或 HTML                   |
| `summary`        | VARCHAR(500) | 是      | null                                            | 文章摘要，首页或列表页展示                             |
| `cover_image`    | VARCHAR(255) | 是      | null                                            | 封面图片 URL，可选                               |
| `author_id`      | BIGINT       | 否      | 无                                               | 文章作者（外键关联 `users.id`）当作者被删除时，该作者所有文章都会被删除 |
| `status`         | INT          | 否      | 1                                               | 文章状态（1：公开，私密：2，公开：3）                      |
| `views`          | INT          | 否      | 0                                               | 文章浏览量，默认 `0`                              |
| `likes`          | INT          | 否      | 0                                               | 点赞数，默认 `0`                                |
| `comments_count` | INT          | 否      | 0                                               | 评论数量，默认 `0`                               |
| `is_top`         | TINYINT(1)   | 否      | 0                                               | 是否置顶，默认`FALSE`                            |
| `is_featured`    | TINYINT(1)   | 否      | 0                                               | 是否推荐，默认 `FALSE`                           |
| `published_at`   | TIMESTAMP    | 是      | CURRENT_TIMESTAMP                               | 发布时间                                      |
| `type`           | INT          | 否      | 1                                               | 文章类型（1:原创,2:转载,3:翻译……）                    |
| `origin_url`     | VARCHAR(255) | 是      | null                                            | 非原创文章原文链接                                 |
| `is_deleted`     | TINYINT(1)   | 否      | 0                                               | 逻辑删除标志                                    |
| `created_at`     | TIMESTAMP    | 否      | `CURRENT_TIMESTAMP`                             | 创建时间                                      |
| `updated_at`     | TIMESTAMP    | 否      | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间（自动更新）                                |

```sql
DROP TABLE IF EXISTS `articles`;
CREATE TABLE `articles` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '文章ID',
    `title` VARCHAR(255) NOT NULL COMMENT '文章标题',
    `content` TEXT NOT NULL COMMENT '文章正文，支持 Markdown 或 HTML',
    `summary` VARCHAR(500) COMMENT '文章摘要',
    `cover_image` VARCHAR(255) COMMENT '封面图片 URL',
    `author_id` BIGINT NOT NULL COMMENT '文章作者',
    `status` INT NOT NULL DEFAULT 1 COMMENT '文章状态',
    `views` INT  NOT NULL DEFAULT 0 COMMENT '文章浏览量',
    `likes` INT  NOT NULL DEFAULT 0 COMMENT '点赞数',
    `comments_count` INT DEFAULT 0 COMMENT '评论数量',
    `is_top` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否置顶',
    `is_featured` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否推荐',
    `published_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '文章发布时间，第一次设为PUBLISHED状态',
    `type` INT NOT NULL DEFAULT 1 COMMENT '文章类型（1:原创,2:转载,3:翻译……）',
    `origin_url` VARCHAR(255) COMMENT '非原创文章原文链接',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

```

---

### 评论表 comments

用于存储文章的评论。

再在表中加入一个root_id字段，用来表示评论根节点，可以为null，因为文章评论可以表示为树状结构；但是后续type可能扩展类型，可能该类型没有父子关系；并且后续如果有其它评论也能表现出树形结构，可以再添加一个字段用于维护，目前只有文章评论可以表示为树形结构。
root_id主要目的是可以方便一次性查询文章所有评论，再在java中组装树。


| 字段名          | 类型          | 是否可以为空 | 默认值                                             | 约束/描述                          |
| ------------ | ----------- | ------ | ----------------------------------------------- | ------------------------------ |
| `id`         | 主键          | 否      | 自增                                              | 自增评论唯一标识                       |
| `from_id`    | BIGINT      | 否      | 无                                               | 评论人用户 ID                       |
| `root_id`    | BIGINT      | 是      | NULL                                            | 根评论id，可以为null，方便维护文章评论树        |
| `content`    | TEXT        | 否      | 无                                               | 评论文本内容                         |
| `for_id`     | BIGINT      | 是      | NULL                                            | 被评论对象 ID（文章 ID 或评论 ID）；没有外键约束  |
| `type`       | varchar(32) | 否      | post                                            | （如 post、comment、message、talk）  |
| `author_id`  | BIGINT      | 是      | NULL                                            | 被评论对象作者的 ID（用于通知，外键关联users.id） |
| `to_id`      | BIGINT      | 是      | NULL                                            | 被评论用户ID；回复评论才赋值，评论文章不赋值        |
| `likes`      | INT         | 否      | 0                                               | 点赞数                            |
| `ip_address` | VARCHAR(45) | 是      | NULL                                            | 评论时ip地址                        |
| `is_deleted` | TINYINT     | 否      | 0                                               | 逻辑删除标志                         |
| `status`     | TINYINT     | 否      | 0                                               | 审核状态：0=待审，1=通过，2=拒绝            |
| `tag`        | varchar(32) | 是      | NULL                                            | message类型独有                    |
| `created_at` | TIMESTAMP   | 否      | `CURRENT_TIMESTAMP`                             | 创建时间                           |
| `updated_at` | TIMESTAMP   | 否      | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间（自动更新）                     |

```sql
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
  `root_id` BIGINT  DEFAULT NULL COMMENT '根评论ID，可以为null，方便维护文章评论树',
  `from_id` BIGINT NOT NULL COMMENT '评论者用户ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `for_id` BIGINT DEFAULT NULL COMMENT '评论目标ID：文章或评论ID',
  `type` VARCHAR(32) NOT NULL DEFAULT 'post' COMMENT '类型：post=文章评论，comment=回复评论',
  `author_id` BIGINT DEFAULT NULL COMMENT '被评论对象的作者ID（用于通知）',
  `to_id` BIGINT DEFAULT NULL COMMENT '被评论用户ID',
  `likes` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `ip_address` VARCHAR(45) DEFAULT NULL COMMENT '用户评论时ip',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否逻辑删除：0=否，1=是',
  `status` TINYINT DEFAULT 0 COMMENT '审核状态：0=待审，1=通过，2=拒绝',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  CONSTRAINT fk_comments_author_user FOREIGN KEY (author_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';


```
---

### 目录表 categories

用于存储文章分类。

| 字段名           | 数据类型         | 是否可以为空 | 默认值                                             | 约束/描述      |
| ------------- | ------------ | ------ | ----------------------------------------------- | ---------- |
| `id`          | BIGINT (PK)  | 否      | 自增                                              | 分类ID       |
| `name`        | VARCHAR(100) | 否      | 无                                               | 分类名称       |
| `description` | VARCHAR(250) | 是      | null                                            | 分类描述       |
| `created_at`  | TIMESTAMP    | 否      | `CURRENT_TIMESTAMP`                             | 创建时间       |
| `updated_at`  | TIMESTAMP    | 否      | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间（自动更新） |

---
```sql
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(100) NOT NULL UNIQUE COMMENT '分类名称',
    `description` VARCHAR(250) DEFAULT NULL COMMENT '分类描述',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='目录表';

```

### 标签表 tags

用于存储文章标签。

| 字段名          | 数据类型        | 是否可以为空 | 默认值                                             | 约束/描述      |
| ------------ | ----------- | ------ | ----------------------------------------------- | ---------- |
| `id`         | BIGINT (PK) | 否      | 自增                                              | 标签ID       |
| `name`       | VARCHAR(50) | 否      | 无                                               | 标签名称       |
| `created_at` | TIMESTAMP   | 否      | `CURRENT_TIMESTAMP`                             | 创建时间       |
| `updated_at` | TIMESTAMP   | 否      | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间（自动更新） |
```sql
DROP TABLE IF EXISTS `tags`;
CREATE TABLE tags (
	`id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
	`name` VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
	`created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';
```
---

### 图片表 images

| 字段名           | 数据类型         | 是否可以为空 | 默认值                                             | 约束/描述                                    |
| ------------- | ------------ | ------ | ----------------------------------------------- | ---------------------------------------- |
| ​`id`         | BIGINT       | 否      | 自增                                              | 图片ID（主键，自增长）                             |
| ​`file_path`  | VARCHAR(512) | 否      | 无                                               | 图片存储的相对路径（如：`articles/2023/08/uuid.jpg`） |
| ​`file_name`  | VARCHAR(255) | 否      | 无                                               | 原始文件名（如：`example.jpg`）                   |
| ​`file_size`  | BIGINT       | 否      | 无                                               | 文件大小（单位：字节）                              |
| ​`mime_type`  | VARCHAR(100) | 否      | 无                                               | 文件类型（如：`image/jpeg`）                     |
| ​`created_by` | VARCHAR(50)  | 否      | 无                                               | 上传人（存储用户名或用户ID）                          |
| `created_at`  | TIMESTAMP    | 否      | `CURRENT_TIMESTAMP`                             | 创建时间                                     |
| `updated_at`  | TIMESTAMP    | 否      | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间（自动更新）                               |
```sql
DROP TABLE IF EXISTS `images`;
CREATE TABLE `images` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `file_path` VARCHAR(512) NOT NULL COMMENT '图片存储路径(相对路径)',
  `file_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
  `file_size` BIGINT NOT NULL COMMENT '文件大小(字节)',
  `mime_type` VARCHAR(100) NOT NULL COMMENT '文件类型',
  `created_by` VARCHAR(50) NOT NULL COMMENT '上传人',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片表';

```
---

### 博客设置表 blog_settings

用于存储博客配置，只允许有一条记录，原则上都是低频更新字段

| 字段名                     | 数据类型         | 是否可以为空 | 默认值               | 约束 / 描述             |
| ----------------------- | ------------ | ------ | ----------------- | ------------------- |
| `id`                    | BIGINT       | 否      | AUTO_INCREMENT    | 主键，站点配置唯一标识（单站点仅一条） |
| `website_chinese_name`  | VARCHAR(100) | 否      | —                 | 网站中文名称              |
| `website_english_name`  | VARCHAR(100) | 是      | NULL              | 网站英文名称              |
| `website_title`         | VARCHAR(255) | 否      | —                 | 网站标题（浏览器 title）     |
| `website_create_time`   | DATETIME     | 是      | NULL              | 网站创建时间              |
| `website_intro`         | VARCHAR(500) | 是      | NULL              | 网站介绍                |
| `front_head_background` | VARCHAR(500) | 是      | NULL              | 前台博客背景              |
| `logo`                  | VARCHAR(500) | 否      | —                 | 网站 Logo 地址          |
| `favicon`               | VARCHAR(500) | 是      | NULL              | 网站 Favicon 地址       |
| `visit_count`           | BIGING       | 否      | 0                 | 网站访问次数              |
| `icp_filing_number`     | VARCHAR(100) | 是      | NULL              | ICP 备案号             |
| `psb_filing_number`     | VARCHAR(100) | 是      | NULL              | 公安备案号               |
| `author`                | VARCHAR(100) | 是      | NULL              | 博客作者名称              |
| `author_avatar`         | VARCHAR(500) | 是      | NULL              | 作者头像                |
| `author_intro`          | VARCHAR(500) | 是      | NULL              | 作者简介                |
| `author_personal_say`   | VARCHAR(500) | 是      | NULL              | 作者人生格言              |
| `user_avatar`           | VARCHAR(500) | 是      | NULL              | 注册用户默认头像            |
| `tourist_avatar`        | VARCHAR(500) | 是      | NULL              | 游客默认头像              |
| `github`                | VARCHAR(255) | 是      | NULL              | GitHub 主页地址         |
| `gitee`                 | VARCHAR(255) | 是      | NULL              | Gitee 主页地址          |
| `bilibili`              | VARCHAR(255) | 是      | NULL              | Bilibili 主页地址       |
| `qq`                    | VARCHAR(50)  | 是      | NULL              | QQ 号                |
| `qq_group`              | VARCHAR(100) | 是      | NULL              | QQ 群                |
| `wechat`                | VARCHAR(50)  | 是      | NULL              | 微信号                 |
| `wechat_group`          | VARCHAR(100) | 是      | NULL              | 微信群                 |
| `weibo`                 | VARCHAR(255) | 是      | NULL              | 微博主页                |
| `csdn`                  | VARCHAR(255) | 是      | NULL              | CSDN 主页             |
| `zhihu`                 | VARCHAR(255) | 是      | NULL              | 知乎主页                |
| `juejin`                | VARCHAR(255) | 是      | NULL              | 掘金主页                |
| `twitter`               | VARCHAR(255) | 是      | NULL              | Twitter 主页          |
| `stackoverflow`         | VARCHAR(255) | 是      | NULL              | StackOverflow 主页    |
| `multi_language`        | TINYINT(1)   | 否      | 0                 | 是否开启多语言：0 否，1 是     |
| `is_comment_review`     | TINYINT(1)   | 否      | 1                 | 评论是否需要审核            |
| `is_email_notice`       | TINYINT(1)   | 否      | 0                 | 是否开启邮件通知            |
| `wechat_qrcode`         | VARCHAR(500) | 是      | NULL              | 微信收款二维码             |
| `alipay_qrcode`         | VARCHAR(500) | 是      | NULL              | 支付宝收款二维码            |
| `created_at`            | DATETIME     | 否      | CURRENT_TIMESTAMP | 记录创建时间              |
| `updated_at`            | DATETIME     | 否      | CURRENT_TIMESTAMP | 记录更新时间（自动更新）        |


```sql
DROP TABLE IF EXISTS `blog_settings`;
CREATE TABLE `blog_settings` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，固定仅一条记录',
    `website_chinese_name` VARCHAR(100) NOT NULL COMMENT '网站中文名称',
    `website_english_name` VARCHAR(100) DEFAULT NULL COMMENT '网站英文名称',
    `website_title` VARCHAR(255) NOT NULL COMMENT '网站标题（浏览器 title）',
	`website_intro` VARCHAR(500) NOT NULL COMMENT '网站介绍',
    `website_create_time` DATETIME DEFAULT NULL COMMENT '网站创建时间',
    `logo` VARCHAR(500) NOT NULL COMMENT '网站 Logo',
	`front_head_background` VARCHAR(255) DEFAULT NULL COMMENT '前台博客背景',
    `favicon` VARCHAR(255) DEFAULT NULL COMMENT '网站 Favicon',
    `notice` VARCHAR(500) DEFAULT NULL COMMENT '网站公告',
	`visit_count` BIGINT NOT NULL DEFAULT 0 COMMENT '网站访问次数',
    `icp_filing_number` VARCHAR(100) DEFAULT NULL COMMENT 'ICP备案号',
    `psb_filing_number` VARCHAR(100) DEFAULT NULL COMMENT '公安备案号',
    `author` VARCHAR(100) DEFAULT NULL COMMENT '作者名称',
    `author_avatar` VARCHAR(500) DEFAULT NULL COMMENT '作者头像',
    `author_intro` VARCHAR(500) DEFAULT NULL COMMENT '作者简介',
	`author_personal_say` VARCHAR(500) DEFAULT NULL COMMENT '作者人生格言',
    `user_avatar` VARCHAR(500) DEFAULT NULL COMMENT '用户默认头像',
    `tourist_avatar` VARCHAR(500) DEFAULT NULL COMMENT '游客头像',
    `github` VARCHAR(255) DEFAULT NULL COMMENT 'GitHub 地址',
    `gitee` VARCHAR(255) DEFAULT NULL COMMENT 'Gitee 地址',
    `bilibili` VARCHAR(255) DEFAULT NULL COMMENT 'Bilibili 地址',
    `qq` VARCHAR(50) DEFAULT NULL COMMENT 'QQ 号',
    `qq_group` VARCHAR(100) DEFAULT NULL COMMENT 'QQ群',
    `wechat` VARCHAR(50) DEFAULT NULL COMMENT '微信号',
    `wechat_group` VARCHAR(100) DEFAULT NULL COMMENT '微信群',
    `weibo` VARCHAR(255) DEFAULT NULL COMMENT '微博地址',
    `csdn` VARCHAR(255) DEFAULT NULL COMMENT 'CSDN 地址',
    `zhihu` VARCHAR(255) DEFAULT NULL COMMENT '知乎地址',
    `juejin` VARCHAR(255) DEFAULT NULL COMMENT '掘金地址',
    `twitter` VARCHAR(255) DEFAULT NULL COMMENT 'Twitter 地址',
    `stackoverflow` VARCHAR(255) DEFAULT NULL COMMENT 'StackOverflow 地址',
    `multi_language` TINYINT(1) DEFAULT 0 COMMENT '是否开启多语言：0 否 1 是',
    `is_comment_review` TINYINT(1) DEFAULT 1 COMMENT '评论是否需要审核',
    `is_email_notice` TINYINT(1) DEFAULT 0 COMMENT '是否开启邮件通知',
    `wechat_qrcode` VARCHAR(500) DEFAULT NULL COMMENT '微信收款二维码',
    `alipay_qrcode` VARCHAR(500) DEFAULT NULL COMMENT '支付宝收款二维码',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='博客站点全局配置表（单站点单记录）';

```

### 友链表 `friend_link`

存储友链，注册用户才能申请友链，而且一个用户只能申请一个友链

| 字段名           | 数据类型         | 是否可以为空 | 默认值               | 约束 / 描述              |
| ------------- | ------------ | ------ | ----------------- | -------------------- |
| id            | BIGINT       | 否      | AUTO_INCREMENT    | 主键，友链唯一标识            |
| site_name     | VARCHAR(255) | 否      | —                 | 友链网站名称               |
| site_desc     | VARCHAR(500) | 是      | NULL              | 友链网站描述               |
| site_logo     | VARCHAR(500) | 是      | NULL              | 友链网站 Logo            |
| site_url      | VARCHAR(255) | 否      | —                 | 友链网站地址               |
| user_id       | BIGINT       | 否      | —                 | 友链所属用户（提交者 / 站长）     |
| status        | TINYINT(1)   | 否      | 1                 | 状态：0 待审核，1 已通过，2 已禁用 |
| sort_order    | INT          | 否      | 0                 | 排序权重（越小越靠前）          |
| is_visible    | TINYINT(1)   | 否      | 1                 | 是否展示：0 否，1 是         |
| apply_message | VARCHAR(500) | 是      | NULL              | 申请友链时的备注             |
| created_at    | DATETIME     | 否      | CURRENT_TIMESTAMP | 创建时间                 |
| updated_at    | DATETIME     | 否      | CURRENT_TIMESTAMP | 更新时间（自动更新）           |

```sql
DROP TABLE IF EXISTS `friend_link`;
CREATE TABLE `friend_link` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键，友链唯一标识',

    `site_name` VARCHAR(255) NOT NULL COMMENT '友链网站名称',
    `site_desc` VARCHAR(500) DEFAULT NULL COMMENT '友链网站描述',
    `site_logo` VARCHAR(500) DEFAULT NULL COMMENT '友链网站 Logo',
    `site_url` VARCHAR(255) NOT NULL COMMENT '友链网站地址',

    `user_id` BIGINT NOT NULL COMMENT '友链提交用户 ID',

    `status` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '状态：0 待审核，1 已通过，2 已禁用',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序权重（越小越靠前）',
    `is_visible` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否展示：0 否，1 是',

    `apply_message` VARCHAR(500) DEFAULT NULL COMMENT '申请友链时的备注',

    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_friend_link_user_site UNIQUE KEY (`user_id`, `site_url`),
    CONSTRAINT fk_friend_link_user FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    KEY idx_friend_link_visible_sort (`status`, `is_visible`, `sort_order`),
    KEY idx_friend_link_user (`user_id`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='博客友链表';

```

### 点赞表 likes

用于记录用户点赞记录，点赞对象既可以是文章，也可以是评论。

| 字段名           | 类型          | 是否可以为空 | 默认值                                             | 约束/描述                                      |
| ------------- | ----------- | ------ | ----------------------------------------------- | ------------------------------------------ |
| `id`          | BIGINT      | 否      | 自增                                              | 主键，自增 ID                                   |
| `user_id`     | BIGINT      | 否      | 无                                               | 点赞的用户 ID，唯一键uniq_user_target               |
| `target_type` | VARCHAR(32) | 否      | post                                            | 点赞对象类型（如 post、comment），唯一键uniq_user_target |
| `target_id`   | BIGINT      | 否      | 无                                               | 点赞对象主键 ID，唯一键uniq_user_target              |
| `status`      | TINYINT(1)  | 否      | 0                                               | 点赞状态（1=点赞，0=取消）                            |
| `created_at`  | TIMESTAMP   | 否      | `CURRENT_TIMESTAMP`                             | 创建时间                                       |
| `updated_at`  | TIMESTAMP   | 否      | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间（自动更新）                                 |


```sql
DROP TABLE IF EXISTS `likes`;
CREATE TABLE `likes` (
    `id` BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT '主键 ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '点赞用户 ID',
    `target_type` VARCHAR(32) NOT NULL COMMENT '点赞对象类型，如 post, comment',
    `target_id` BIGINT UNSIGNED NOT NULL COMMENT '点赞对象 ID',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '是否有效，1 为点赞，0 为取消',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uniq_user_target` (`user_id`, `target_type`, `target_id`),
    INDEX `idx_target` (`target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

```
### 路由表 routes


| 字段名                | 类型           | 是否可以为空 | 默认值  | 约束/描述       |
| ------------------ | ------------ | ------ | ---- | ----------- |
| `id`               | BIGINT       | 否      | 自增   | 路由ID        |
| `parent_id`        | BIGINT       | 否      | 无    | 父路由ID       |
| `path`             | VARCHAR(255) | 否      | 无    | 路由路径        |
| `name`             | VARCHAR(255) | 否      | 无    | 路由名称，唯一的    |
| `redirect`         | VARCHAR(255) | 是      | NULL | 路由重定向       |
| `component`        | VARCHAR(255) | 否      | 无    | 路由组件        |
| `title`            | VARCHAR(255) | 否      | 无    | 路由标题        |
| `icon`             | VARCHAR(255) | 是      | NULL | 路由icon      |
| `extra_icon`       | VARCHAR(255) | 是      | NULL | 路由备用icon    |
| `show_link`        | TINYINT(1)   | 否      | 1    | 是否展示link    |
| `show_parent`      | TINYINT(1)   | 否      | 1    | 是否展示父路由     |
| `ranks`            | INT          | 是      | NULL | 路由排名        |
| `roles`            | JSON         | 是      | NULL | 可访问路由角色     |
| `auths`            | JSON         | 是      | NULL | 权限          |
| `keep_alive`       | TINYINT(1)   | 否      | 0    | 是否允许页面缓存    |
| `frame_src`        | VARCHAR(255) | 是      | NULL | frame资源路径   |
| `frame_loading`    | TINYINT(1)   | 否      | 0    | 是否允许frame加载 |
| `transition_name`  | VARCHAR(255) | 是      | NULL | 过渡动画名称      |
| `enter_transition` | VARCHAR(255) | 是      | NULL | 进入动画        |
| `leave_transition` | VARCHAR(255) | 是      | NULL | 离开动画        |
| `hidden_tag`       | TINYINT(1)   | 是      | NULL | 是否隐藏标签      |
| `dynamic_level`    | INT          | 是      | NULL | 标签水平        |
| `active_path`      | VARCHAR(255) | 是      | NULL | 激活路径        |


```sql
DROP TABLE IF EXISTS `routes`;
CREATE TABLE `routes` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '路由ID',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父路由ID',
  `path` VARCHAR(255) NOT NULL COMMENT '路由路径',
  `name` VARCHAR(255) NOT NULL UNIQUE COMMENT '路由名称',
  `redirect` VARCHAR(255) DEFAULT NULL COMMENT '路由重定向',
  `component` VARCHAR(255) NOT NULL COMMENT '路由组件',
  /* meta信息 */
  `title` VARCHAR(255) NOT NULL,
  `icon` VARCHAR(255) DEFAULT NULL COMMENT '路由icon',
  `extra_icon` VARCHAR(255) DEFAULT NULL COMMENT '路由备用icon',
  `show_link` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否展示link',
  `show_parent` TINYINT(1) DEFAULT 1 COMMENT '是否展示父路由',
  `ranks` INT DEFAULT NULL COMMENT '路由排名',
  /* 权限相关 */
  `roles` JSON DEFAULT NULL COMMENT '可访问路由角色',
  `auths` JSON DEFAULT NULL COMMENT '权限',
  /* 页面缓存和性能 */
  `keep_alive` BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否允许页面缓存',
  /* iframe相关 */
  `frame_src` VARCHAR(255) DEFAULT NULL COMMENT 'frame路径',
  `frame_loading` BOOLEAN DEFAULT FALSE COMMENT '是否允许frame加载',
  /* 动画相关 */
  `transition_name` VARCHAR(255) DEFAULT NULL COMMENT '过渡动画名称',
  `enter_transition` VARCHAR(255) DEFAULT NULL COMMENT '进入动画',
  `leave_transition` VARCHAR(255) DEFAULT NULL COMMENT '离开动画',
  /* 标签页相关 */
  `hidden_tag` TINYINT(1) NOT NULL DEFAULT FALSE COMMENT '是否隐藏标签',
  `dynamic_level` INT DEFAULT NULL COMMENT '标签水平',
  `active_path` VARCHAR(255) DEFAULT NULL COMMENT '激活路径',
  FOREIGN KEY (parent_id) REFERENCES routes(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='路由表';
```



### 关联表

采用第三张关联表，来映射不同表之间的关系。好处是可以复用，缺点在于双重查询，时间开销会增加

#### 用户-角色关系表 user_roles

用于维护 **用户和角色的多对多关系**。


| 字段名          | 数据类型      | 是否可以为空 | 默认值                                             | 约束/描述      |
| ------------ | --------- | ------ | ----------------------------------------------- | ---------- |
| `id`         | BIGINT    | 否      | 自增                                              | 唯一标识符      |
| `user_id`    | BIGINT    | 否      | 无                                               | 用户id       |
| `role_id`    | BIGINT    | 否      | 无                                               | 角色id       |
| `created_at` | TIMESTAMP | 否      | `CURRENT_TIMESTAMP`                             | 创建时间       |
| `updated_at` | TIMESTAMP | 否      | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间（自动更新） |



```sql
DROP TABLE IF EXISTS `user_roles`;
CREATE TABLE `user_roles` (
    `id`       BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '唯一标识符',
    `user_id`  BIGINT NOT NULL COMMENT '用户ID（外键，关联user表）',
    `role_id`  BIGINT NOT NULL COMMENT '角色ID（外键，关联role表）',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE (user_id, role_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色关系表';

```

#### 文章-标签关系表article_tags

多对多关系表，用于关联文章与标签。
- 记录文章与标签的关联关系
- 通过外键约束保证数据完整性
- 唯一约束避免重复关联

| 字段名          | 数据类型        | 是否可以为空 | 默认值                                             | 约束/描述                   |
| ------------ | ----------- | ------ | ----------------------------------------------- | ----------------------- |
| `id`         | BIGINT (PK) | 否      | 自增                                              | 唯一标识符                   |
| `article_id` | BIGINT (FK) | 否      | 无                                               | 文章 ID（关联 `Articles.id`） |
| `tag_id`     | BIGINT (FK) | 否      | 无                                               | 标签 ID（关联 `Tags.id`）     |
| `created_at` | TIMESTAMP   | 否      | `CURRENT_TIMESTAMP`                             | 创建时间                    |
| `updated_at` | TIMESTAMP   | 否      | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间（自动更新）              |

```sql
DROP TABLE IF EXISTS `article_tags`;
CREATE TABLE `article_tags` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '唯一标识符',
    `article_id` BIGINT NOT NULL COMMENT '文章 ID（关联 Articles.id）',
    `tag_id` BIGINT NOT NULL COMMENT '标签 ID（关联 Tags.id）', 
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_article FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
    CONSTRAINT fk_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE,
    UNIQUE KEY uk_article_tag (article_id, tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章-标签关系表';

```
---

#### 实体-图片关系表 entity_images

多对多通用关系表，用来关联多个表与图片：
- 记录文章与图片的关联关系
- 通过外键约束保证数据完整性
- 唯一约束避免重复关联

| 字段名           | 数据类型        | 是否可以为空 | 默认值                                             | 约束/描述                             |
| ------------- | ----------- | ------ | ----------------------------------------------- | --------------------------------- |
| `id`          | BIGINT (PK) | 否      | 自增                                              | 唯一标识符                             |
| `entity_type` | BIGINT (FK) | 否      | 无                                               | 表种类                               |
| `entity_id`   | BIGINT (FK) | 否      | 无                                               | 对应表的主键id                          |
| `image_id`    | BIGINT (FK) | 否      | 无                                               | 引用的图片 ID                          |
| `usage_type`  | VARCHAR(50) | 否      | logo                                            | 图片用途: 'logo', 'banner', 'cover' 等 |
| `sort_order`  | INT         | 否      | 0                                               | 图片排序（比如文章多图时）                     |
| `created_at`  | TIMESTAMP   | 否      | `CURRENT_TIMESTAMP`                             | 创建时间                              |
| `updated_at`  | TIMESTAMP   | 否      | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间（自动更新）                        |

```sql
DROP TABLE IF EXISTS `entity_images`;
CREATE TABLE entity_images (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    entity_type VARCHAR(50) NOT NULL COMMENT '表名',
    entity_id BIGINT NOT NULL COMMENT '对于表主键id',
    image_id BIGINT NOT NULL COMMENT '引言图片id',
    usage_type VARCHAR(50) NOT NULL DEFAULT "logo" COMMENT '图片用途',
    sort_order INT NOT NULL DEFAULT 0 COMMENT "图片顺序",
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uq_entity_image (entity_type, entity_id,usage_type,sort_order),
    CONSTRAINT fk_entity_image_image FOREIGN KEY (image_id) REFERENCES images(id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实体-图片关系表';

```
---

#### 文章-目录关系表 article_categories

多对多关系表，用来关联文章与分类：
- 记录文章与图片的关联关系
- 通过外键约束保证数据完整性
- 唯一约束避免重复关联

| 字段名           | 数据类型        | 是否可以为空 | 默认值                                             | 约束/描述                   |
| ------------- | ----------- | ------ | ----------------------------------------------- | ----------------------- |
| `id`          | BIGINT (PK) | 否      | 自增                                              | 唯一标识符                   |
| `article_id`  | BIGINT (FK) | 否      | 无                                               | 文章 ID（关联 `articles.id`） |
| `category_id` | BIGINT (FK) | 否      | 无                                               | 标签 ID（关联 `images.id`）   |
| `created_at`  | TIMESTAMP   | 否      | `CURRENT_TIMESTAMP`                             | 创建时间                    |
| `updated_at`  | TIMESTAMP   | 否      | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间（自动更新）              |

```sql
DROP TABLE IF EXISTS `article_categories`;
CREATE TABLE `article_categories` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '唯一标识符',
    `article_id` BIGINT NOT NULL COMMENT '文章 ID（关联 articles.id）',
    `category_id` BIGINT NOT NULL COMMENT '标签 ID（关联 categories.id）',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_article2 FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    UNIQUE KEY uk_article_tag (article_id, category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章-目录关系表';
```

#### 角色-权限关系表 role_permissions

| 字段名             | 数据类型        | 是否可以为空 | 默认值                                             | 约束 / 描述                    |
| --------------- | ----------- | ------ | ----------------------------------------------- | -------------------------- |
| `id`            | BIGINT (PK) | 否      | 自增                                              | 唯一标识符                      |
| `role_id`       | BIGINT      | 否      | 无                                               | 角色 ID，关联到 `roles.id`       |
| `permission_id` | BIGINT      | 否      | 无                                               | 权限 ID，关联到 `permissions.id` |
| `created_at`    | TIMESTAMP   | 否      | `CURRENT_TIMESTAMP`                             | 创建时间                       |
| `updated_at`    | TIMESTAMP   | 否      | `CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP` | 更新时间（自动更新）                 |

```sql
DROP TABLE IF EXISTS `role_permissions`;
CREATE TABLE `role_permissions` (
	`id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '唯一标识符',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限关系表';
```


## 权限相关

**RBAC（Role-Based Access Control）**，即：

> 用户 → 角色 → 权限（User → Role → Permission）

### 用户表 users

[[#用户表 users]]

### 角色表 roles

[[#角色表 roles]]
### 权限表 permissions

[[#权限表 permissions]]

### 用户-角色关系表 user_roles

[[#用户-角色关系表 user_roles]]

### 角色-权限关系表 role_permissions

[[#角色-权限关系表]]


## 将utf-8转换为utf8mb4

转变数据库字符集

```sql

ALTER DATABASE blog
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

```

生成sql

```sql
SELECT CONCAT(
  'ALTER TABLE `', table_name,
  '` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;'
) AS sql_statement
FROM information_schema.tables
WHERE table_schema = 'blog';
```

执行上面生成的sql



# 状态码

| 状态码                           | 含义                  | 常见场景           | 前端处理建议                      |
| ----------------------------- | ------------------- | -------------- | --------------------------- |
| **200 OK**                    | 请求成功                | 获取用户信息、菜单、业务数据 | 正常处理返回结果                    |
| **201 Created**               | 资源创建成功              | 注册成功、上传成功      | 正常提示“成功”并跳转                 |
| **400 Bad Request**           | 请求参数错误              | 缺少字段、类型不对、格式错误 | 校验输入并提示用户                   |
| **401 Unauthorized**          | 未认证 / token 缺失 / 过期 | 访问需要登录的接口但没登录  | 尝试刷新 `accessToken`，失败则跳转登录页 |
| **403 Forbidden**             | 已认证但无权限             | 普通用户访问管理员接口    | 提示“无权限”，可引导联系管理员            |
| **404 Not Found**             | 资源不存在               | URL 写错，接口不存在   | 提示“资源不存在”                   |
| **409 Conflict**              | 请求冲突                | 注册时账号已存在       | 提示“账号已存在”                   |
| **422 Unprocessable Entity**  | 数据语义错误              | 邮箱非法、密码过短      | 提示用户修正输入                    |
| **500 Internal Server Error** | 服务器错误               | 后端异常           | 通用错误提示“服务器开小差了”             |
| **502 Bad Gateway**           | 网关错误                | 微服务挂掉          | 提示“服务异常，请稍后再试”              |
| **503 Service Unavailable**   | 服务不可用               | 高并发、维护中        | 提示“系统维护中”                   |
| **504 Gateway Timeout**       | 网关超时                | 后端响应过慢         | 提示“请求超时，请重试”                |

参考资料：
[mrzym99/vue3-blog](https://github.com/mrzym99/vue3-blog)
[851543/blog-admin](https://github.com/851543/blog-admin)
[weiwosuoai/weblog](https://github.com/weiwosuoai/WeBlog)
[kuailemao/Ruyu-Blog](https://github.com/kuailemao/Ruyu-Blog)
[xiaoxian521/pure-admin-thin](https://github.com/pure-admin/pure-admin-thin)
[galaxy-s10/vue3-blog-admin](https://github.com/galaxy-s10/vue3-blog-admin)






