# 书架与电子书导出功能设计（审阅稿 v1）

## 1. 背景与目标

现状：文章只有 分类 / 标签 / 时间轴 三种散点组织方式，一系列同主题文章（如"生信入门十讲"）无法按顺序编排成可连续阅读的整体，也无法把成果打包带走。

目标：
1. **站内书架**：后台把文章编排成"书"（目录 + 章节顺序），前台提供书架列表页和带目录/上下章导航的阅读页。
2. **导出电子书**：把一本书一键导出为可离线阅读/归档的文件（Markdown 压缩包、浏览器打印 PDF，二期 EPUB）。

## 2. 数据模型

沿用 photo_album + photo_album_images 的关联表先例：

```sql
CREATE TABLE book (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  title        VARCHAR(100)  NOT NULL COMMENT '书名',
  subtitle     VARCHAR(200)           COMMENT '副标题',
  description  TEXT                   COMMENT '简介',
  cover_image  VARCHAR(255)           COMMENT '封面相对路径',
  author_id    BIGINT         NOT NULL,
  status       TINYINT DEFAULT 0      COMMENT '0草稿 1上架',
  is_deleted   TINYINT DEFAULT 0,
  created_at   DATETIME,
  updated_at   DATETIME
);

CREATE TABLE book_article (
  id            BIGINT PRIMARY KEY AUTO_INCREMENT,
  book_id       BIGINT NOT NULL,
  article_id    BIGINT NOT NULL,
  chapter_title VARCHAR(200) COMMENT '章节名，默认取文章标题',
  sort_order    INT DEFAULT 0 COMMENT '章序，从1递增',
  UNIQUE (book_id, article_id),
  KEY idx_book (book_id)
);
```

- 文章与书 **多对多**：一篇文章可进多本书。
- 章节名可单独覆盖（不改文章本身标题）。
- 删除文章时同步清理 book_article（service 层处理，不建外键级联，与现有风格一致）。
- 删除书不影响文章。

## 3. 后端 API

### 管理端 `/api/admin/book`
| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/list` | 分页查书（含章节数） |
| POST | `/add` | 新建书 |
| PUT | `/update` | 修改书信息 |
| DELETE | `/delete/{id}` | 软删除书 |
| POST | `/addArticles` | 批量加入章节 `{bookId, articleIds[]}` |
| POST | `/removeArticle` | 移除章节 `{bookId, articleId}` |
| POST | `/reorder` | 整序 `{bookId, orderedArticleIds[]}` |
| PUT | `/renameChapter` | 改章节名 `{bookId, articleId, chapterTitle}` |
| GET | `/export/{bookId}?format=zip` | 导出（管理端也保留入口） |

### 前台 `/api/front/book`
| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/list` | 上架的书（status=1 且未删） |
| GET | `/{id}` | 书详情 + 章节目录 |
| GET | `/{bookId}/chapter/{articleId}` | 章正文 + prev/next 指针 |
| GET | `/export/{bookId}` | 下载导出文件（仅上架的书） |

代码组织：`Entity(2) / DTO / Mapper.xml / Service / Controller(front+admin)`，完全照 PhotoAlbum 的目录与写法。

## 4. 前台改动（blog-vue3-front）

- `layout/header/blog-header.vue`：桌面 + 移动两处菜单各加 `<el-menu-item index="/book">书架</el-menu-item>`（icon 复用 iconfont 现有书籍类图标，查不到就用 icon-shujuku 之外的通用图标）。
- `router/modules/home.ts` 新增两条路由（绝对路径写法与现有一致）：
  - `/book` → `views/book/book.vue` 书架列表
  - `/book/read/:bookId/:articleId` → `views/book/read.vue` 阅读页（`meta.isHide` 不需要，前台无侧栏）
- **书架页**：卡片网格（封面、书名、简介、N 章），点击进阅读页第一章。
- **阅读页**：复用 article.vue 的模式 —— 左侧 `MdCatalog` 目录（可点击跳章）、中间 `MdPreview` 正文、底部「上一章 / 目录 / 下一章」；阅读进度存 `localStorage`（`book:{bookId}` → articleId），再次打开书架直接回上次章节；顶部「下载」按钮触发导出。
- 阅读页 `@media print` 样式：隐藏导航/目录/页脚，正文全宽 —— 支撑「打印 → 存为 PDF」。

## 5. 后台改动（blog-vue3-back）

- `router/modules/asyncRoutes.ts` 新增 `/blog/book` 父路由（component: Home）+ 子路由 `list`、`edit/:bookId`。
  注意：**子路径必须相对**（坑 #41），且上一个路由对象末尾逗号别漏（坑 #27）。
- 新页面 `views/blog/book/`：
  - `book.vue`：书列表（名称、章节数、状态、操作），art-table。
  - `edit.vue`：书信息表单（封面用 ImagePicker 组件，同文章封面模式）+ 章节编排：
    - 左：已选章节列表，支持上下移/拖拽排序、改章节名、移除；
    - 右：文章候选列表（复用 `getArticleList`，勾选批量加入）。
- 导出按钮：书列表行内「导出 ZIP」。

## 6. 导出方案（分层，按风险递增）

| 格式 | 实现方式 | 新依赖 | 排期 |
|---|---|---|---|
| **Markdown ZIP** | 后端拼接：`00-封面.md`（书名/简介/目录）+ `01-章节名.md`…，文内图片按 `file.public-base-url` 拉取原始文件一并打包；`java.util.zip` 流式输出 | 无 | 一期 |
| **PDF** | 不做服务端生成（iText 中文字体嵌入复杂、wkhtmltopdf 要系统包）。阅读页 `window.print()` + print 样式，浏览器「另存为 PDF」 | 无 | 一期 |
| **EPUB** | epublib 生成 chapter xhtml + 内嵌图片；需验证与 Spring Boot 3.4/Jakarta 的兼容性 | epublib | 二期 |

原则：一期交付 ZIP + 打印 PDF 两条通道，零新依赖、零系统包；EPUB 单独里程碑，先做 spike 验证再排期。

## 7. 联动检查项

- SeoController / sitemap：书列表页、书阅读页是否收录（视 sitemap 实现决定，最小子集是不影响现有生成逻辑）。
- RSS：不动。
- 评论/点赞：阅读页不挂评论组件（书是阅读态，保持简单；要挂再加开关）。
- 浏览量：复用 `addArticleViews`，不新增计数。

## 8. 里程碑

1. **M1 模型 + 后台**：DDL、admin CRUD + 章节编排、后台两个页面。
2. **M2 前台书架 + 阅读**：header 菜单、书架页、阅读页（目录/上下章/进度）。
3. **M3 导出**：ZIP 导出 + 打印样式。
4. **M4（可选）**：EPUB spike。

## 9. 验收清单

- [ ] 后台建书 → 选文章 → 排序 → 上架，全流程页面操作完成。
- [ ] 前台 header 出现「书架」，移动端菜单也有。
- [ ] 书架页卡片正确，点进默认第一章；刷新后回到上次阅读章节。
- [ ] 阅读页目录点击跳章、上/下章边界正确（第一章无上一章、末章无下一章）。
- [ ] 导出 ZIP 解压结构完整、中文文件名不乱码、图片齐全。
- [ ] 阅读页打印 PDF 中文正常、无导航残留。
- [ ] `mvn package` 与前端 `vue-tsc + vite build` 通过；本地 docker compose 起容器后浏览器实测上述全部。
- [ ] 未改动的页面（文章/分类/相册等）回归无异常。

## 10. 待你确认的问题

1. 一期导出只做 **ZIP(MD+图片) + 打印PDF**，EPUB 放二期 —— 可以吗？
2. 阅读页是否挂评论/点赞？（本稿默认不挂）
3. 书架要不要进首页/搜索（文章搜索结果里是否也应命中书）？（本稿默认仅 header 菜单入口）
