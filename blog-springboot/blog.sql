CREATE DATABASE IF NOT EXISTS `blog` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `blog`;

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
  `nick_name` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `phone_number` VARCHAR(32) DEFAULT NULL COMMENT '电话号码',
  `gender` TINYINT UNSIGNED NOT NULL DEFAULT 9 COMMENT '性别：1=男，2=女，9=未知',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
	`id`     BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    `role_name`   VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称，如 admin, user',
    `role_desc`   VARCHAR(255) COMMENT '角色描述',
	`created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户注册时间，自动生成',
	`updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '用户信息更新时间'
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

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

DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
  `root_id` BIGINT  DEFAULT NULL COMMENT '根评论ID，可以为null，方便维护文章评论树',
  `user_id` BIGINT NOT NULL COMMENT '评论者用户ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `for_id` BIGINT DEFAULT NULL COMMENT '评论目标ID：文章或评论ID',
  `type` VARCHAR(32) NOT NULL DEFAULT 'post' COMMENT '类型：post=文章评论，comment=回复评论',
  `author_id` BIGINT DEFAULT NULL COMMENT '被评论对象的作者ID（用于通知）',
  `reply_user_id` BIGINT DEFAULT NULL COMMENT '回复评论用户ID',
  `likes` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `ip_address` VARCHAR(45) DEFAULT NULL COMMENT '用户评论时ip',
  `is_deleted` TINYINT(1) DEFAULT 0 COMMENT '是否逻辑删除：0=否，1=是',
  `status` TINYINT DEFAULT 0 COMMENT '审核状态：0=待审，1=通过，2=拒绝',
  `tag` VARCHAR(32) DEFALUT NULL COMMENT 'message类型独有',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  CONSTRAINT fk_comments_author_user FOREIGN KEY (author_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';



DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(100) NOT NULL UNIQUE COMMENT '分类名称',
    `description` VARCHAR(250) DEFAULT NULL COMMENT '分类描述',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='目录表';

DROP TABLE IF EXISTS `tags`;
CREATE TABLE tags (
	`id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID',
	`name` VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称',
	`created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

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

