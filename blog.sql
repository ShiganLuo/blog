CREATE DATABASE IF NOT EXISTS `blog` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `blog`;
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,       -- 用户ID，主键，自增
  `username` VARCHAR(50) NOT NULL UNIQUE,     -- 用户名，必须唯一
  `email` VARCHAR(100) NOT NULL UNIQUE,       -- 邮箱，必须唯一
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
  `is_active` BOOLEAN DEFAULT TRUE            -- 账户是否启用（用于禁用账户）
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
DROP TABLE IF EXISTS `articles`;
CREATE TABLE articles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    content TEXT NOT NULL,
    summary VARCHAR(500),
    cover_image VARCHAR(255),
    author_id BIGINT NOT NULL,
    category_id BIGINT DEFAULT NULL,
    status ENUM('DRAFT', 'PUBLISHED', 'DELETED') DEFAULT 'DRAFT',
    views INT DEFAULT 0,
    likes INT DEFAULT 0,
    comments_count INT DEFAULT 0,
    is_featured BOOLEAN DEFAULT FALSE,
    published_at TIMESTAMP NULL DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `categories`;
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,  -- 分类唯一标识符
    name VARCHAR(100) NOT NULL,           -- 分类名称
    description TEXT,                      -- 分类描述
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 更新时间
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `tags`;
CREATE TABLE tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT, -- '标签唯一标识符',
    name VARCHAR(50) NOT NULL, -- '标签名称',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- '标签创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- '标签更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
