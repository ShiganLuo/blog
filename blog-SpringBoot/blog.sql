CREATE DATABASE IF NOT EXISTS `blog` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `blog`;

DROP TABLE IF EXISTS `users`;
CREATE TABLE users (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,       -- 用户ID，主键，自增
  `username` VARCHAR(50) NOT NULL UNIQUE,     -- 用户名，必须唯一
  `email` VARCHAR(100) UNIQUE,       -- 邮箱，必须唯一
  `password` VARCHAR(255) NOT NULL,           -- 密码（经过加密存储）
  `avatar_url` VARCHAR(255) DEFAULT NULL,     -- 用户头像URL（可选）
  `bio` TEXT DEFAULT NULL,                    -- 用户简介（可选）
  `role` ENUM('USER', 'ADMIN') DEFAULT 'USER', -- 用户角色（USER：普通用户，ADMIN：管理员）
  `status` ENUM('ACTIVE', 'INACTIVE', 'BANNED') DEFAULT 'ACTIVE', -- 用户状态（ACTIVE：激活，INACTIVE：未激活，BANNED：禁用）
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 用户注册时间，自动生成
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 用户信息更新时间
  `last_login` TIMESTAMP DEFAULT NULL,        -- 最后登录时间
  `login_attempts` INT DEFAULT 0,             -- 登录失败次数（防止暴力破解）
  `is_email_verified` BOOLEAN DEFAULT FALSE,  -- 邮箱是否已验证
  `is_active` BOOLEAN DEFAULT TRUE,            -- 账户是否启用（用于禁用账户）
  `nick_name` VARCHAR(50) DEFAULT NULL      -- 用户设置昵称
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `articles`;
CREATE TABLE articles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    summary VARCHAR(500),
    cover_image VARCHAR(255),
    author_id BIGINT NOT NULL,
    status ENUM('DRAFT', 'PUBLISHED', 'DELETED') DEFAULT 'DRAFT',
    views INT  NOT NULL DEFAULT 0,
    likes INT  NOT NULL DEFAULT 0,
    comments_count INT DEFAULT 0,
    is_featured BOOLEAN DEFAULT FALSE,
    published_at TIMESTAMP NULL DEFAULT NULL,
    type INT ,
    origin_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `comments`;
CREATE TABLE comments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',

  from_id BIGINT NOT NULL COMMENT '评论者用户ID',

  content TEXT NOT NULL COMMENT '评论内容',

  for_id BIGINT NOT NULL COMMENT '评论目标ID：文章或评论ID',
  type VARCHAR(32) NOT NULL DEFAULT 'post' COMMENT '类型：post=文章评论，comment=回复评论',

  author_id BIGINT NOT NULL COMMENT '被评论对象的作者ID（用于通知）',

  likes INT DEFAULT 0 COMMENT '点赞数',

  is_deleted TINYINT DEFAULT 0 COMMENT '是否逻辑删除：0=否，1=是',
  status TINYINT DEFAULT 1 COMMENT '审核状态：0=待审，1=通过，2=拒绝',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  CONSTRAINT fk_comments_author_user FOREIGN KEY (author_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';


DROP TABLE IF EXISTS `categories`;
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,  -- 分类唯一标识符
    name VARCHAR(100) NOT NULL UNIQUE,           -- 分类名称
    description TEXT,                      -- 分类描述
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 更新时间
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `tags`;
CREATE TABLE tags (
	id BIGINT PRIMARY KEY AUTO_INCREMENT, -- '标签唯一标识符',
	name VARCHAR(50) NOT NULL UNIQUE, -- '标签名称',
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- '标签创建时间',
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- '标签更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `images`;
CREATE TABLE `images` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `file_path` VARCHAR(512) NOT NULL COMMENT '图片存储路径(相对路径)',
  `file_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
  `file_size` BIGINT NOT NULL COMMENT '文件大小(字节)',
  `mime_type` VARCHAR(100) NOT NULL COMMENT '文件类型',
  `created_by` VARCHAR(50) NOT NULL COMMENT '上传人',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图片表';

DROP TABLE IF EXISTS `blog_settings`;
CREATE TABLE blog_settings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    site_title VARCHAR(255),
    site_description TEXT,
    site_logo VARCHAR(500),
    blog_notice VARCHAR(500),
    personal_say VARCHAR(255),
    gitee_link VARCHAR(255),
    bilibili_link VARCHAR(255),
    github_link VARCHAR(255),
    qq_group VARCHAR(100),
    qq_link VARCHAR(255),
    wechat_group VARCHAR(100),
    wechat_link VARCHAR(255),
    ali_pay VARCHAR(500),
    wechat_pay VARCHAR(500),
    enable_comments BOOLEAN DEFAULT TRUE,
    visit_count BIGINT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `likes`;
CREATE TABLE `likes` (
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT '点赞用户 ID',
    `target_type` VARCHAR(32) NOT NULL COMMENT '点赞对象类型，如 post, comment',
    `target_id` BIGINT UNSIGNED NOT NULL COMMENT '点赞对象 ID',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '是否有效，1 为点赞，0 为取消',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_user_target` (`user_id`, `target_type`, `target_id`),
    INDEX `idx_target` (`target_type`, `target_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户点赞表';

DROP TABLE IF EXISTS `routes`;
CREATE TABLE routes (
  id INT PRIMARY KEY AUTO_INCREMENT,
  parent_id INT NULL,
  path VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL UNIQUE,
  redirect VARCHAR(255),
  component VARCHAR(255),
  
  /* meta信息 */
  title VARCHAR(255) NOT NULL,
  icon VARCHAR(255),
  extra_icon VARCHAR(255),
  show_link BOOLEAN DEFAULT TRUE,
  show_parent BOOLEAN DEFAULT TRUE,
  ranks INT,
  
  /* 权限相关 */
  roles JSON,
  auths JSON,
  
  /* 页面缓存和性能 */
  keep_alive BOOLEAN DEFAULT FALSE,
  
  /* iframe相关 */
  frame_src VARCHAR(255),
  frame_loading BOOLEAN DEFAULT FALSE,
  
  /* 动画相关 */
  transition_name VARCHAR(255),
  enter_transition VARCHAR(255),
  leave_transition VARCHAR(255),
  
  /* 标签页相关 */
  hidden_tag BOOLEAN DEFAULT FALSE,
  dynamic_level INT,
  active_path VARCHAR(255),
  
  FOREIGN KEY (parent_id) REFERENCES routes(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `article_tags`;
CREATE TABLE article_tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT, --  '唯一标识符'
    article_id BIGINT NOT NULL , -- '文章 ID（关联 Articles.id）'
    tag_id BIGINT NOT NULL , -- '标签 ID（关联 Tags.id）'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- '创建时间'
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- COMMENT '更新时间'
    CONSTRAINT fk_article FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
    CONSTRAINT fk_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE,
    UNIQUE KEY uk_article_tag (article_id, tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `article_images`;
CREATE TABLE article_images (
    id BIGINT PRIMARY KEY AUTO_INCREMENT, --  '唯一标识符'
    article_id BIGINT NOT NULL , -- '文章 ID（关联 articles.id）'
    image_id BIGINT NOT NULL , -- '标签 ID（关联 images.id）'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- '创建时间'
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- COMMENT '更新时间'
    CONSTRAINT fk_article1 FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
    CONSTRAINT fk_image FOREIGN KEY (image_id) REFERENCES images(id) ON DELETE CASCADE,
    UNIQUE KEY uk_article_tag (article_id, image_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `article_categories`;
CREATE TABLE article_categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT, --  '唯一标识符'
    article_id BIGINT NOT NULL , -- '文章 ID（关联 articles.id）'
    category_id BIGINT NOT NULL , -- '标签 ID（关联 categories.id）'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- '创建时间'
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- COMMENT '更新时间'
    CONSTRAINT fk_article2 FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    UNIQUE KEY uk_article_tag (article_id, category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
