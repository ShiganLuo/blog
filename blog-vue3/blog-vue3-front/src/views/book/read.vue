<script setup lang="ts">
import { ref, reactive, computed, nextTick, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { storeToRefs } from "pinia";
import { useStaticData } from "@/stores/index";
import { MdPreview } from "md-editor-v3";
import "md-editor-v3/lib/style.css";
import { useHead } from "@vueuse/head";
import { BookService, type BookDetail, type ChapterResult } from "@/api/blog/bookApi";
import PageHeader from "@/components/PageHeader/index.vue";
import TocTree from "@/components/TocTree/index.vue";
import { ArticleService } from "@/api/blog/articleApi";

const route = useRoute();
const router = useRouter();
const staticStore = useStaticData();
const { codeTheme, previewTheme, mainTheme } = storeToRefs(staticStore);

const scrollElement = document.documentElement;
const loading = ref(true);
const emptyBook = ref(false);
const bookDetail = ref<BookDetail | null>(null);
const chapter = ref<ChapterResult | null>(null);
const drawerShow = ref(false);
const currentArticleId = ref(0);

const mdState = reactive({
  text: "",
  id: "book-reader",
});

// 层级小节目录（MdPreview 解析后回调）
const catalogItems = ref<{ text: string; level: number; line: number }[]>([]);
const onCatalog = (list: { text: string; level: number; line: number }[]) => {
  catalogItems.value = list;
};

const bookId = computed(() => String(route.query.bookId || ""));

// 当前章节名（可能被重命名，取章节目录里的）
const currentChapterTitle = computed(() => {
  if (!bookDetail.value || !chapter.value) return "";
  const item = bookDetail.value.chapters[chapter.value.chapterIndex - 1];
  return item ? item.chapterTitle : "";
});

const progressKey = computed(() => `book:${bookId.value}`);

useHead({
  title: computed(() => {
    if (!chapter.value) return "拾感日记";
    const t = currentChapterTitle.value || chapter.value.content?.articleTitle || "";
    return `${t}《${chapter.value.bookTitle}》 - 拾感日记`;
  }),
});

/** 载入某一章 */
const loadChapter = async (articleId: number | string, pushQuery = true) => {
  if (!bookId.value) return;
  const res = await BookService.getChapter(bookId.value, articleId);
  if (res.code !== 200 || !res.result) {
    ElMessage.error("章节加载失败");
    return;
  }
  chapter.value = res.result as ChapterResult;
  mdState.text = chapter.value.content?.articleContent || "";
  currentArticleId.value = Number(articleId);
  // 阅读进度
  localStorage.setItem(progressKey.value, String(articleId));
  if (pushQuery) {
    router.replace({
      path: "/book/read",
      query: { bookId: bookId.value, articleId: String(articleId), pageTitle: String(chapter.value.bookTitle) }
    });
  }
  // 统计阅读量（与文章页一致，延时避开首屏）
  setTimeout(() => {
    ArticleService.addArticleViews(Number(articleId)).catch(() => {});
  }, 2000);
  nextTick(() => {
    window.scrollTo({ top: 0 });
  });
};

/** 初始化：拿书详情，解析初始章节（query > 上次进度 > 首章） */
const init = async () => {
  if (!bookId.value) {
    loading.value = false;
    emptyBook.value = true;
    return;
  }
  loading.value = true;
  emptyBook.value = false;
  const res = await BookService.getBookById(bookId.value);
  if (res.code !== 200 || !res.result) {
    loading.value = false;
    emptyBook.value = true;
    return;
  }
  bookDetail.value = res.result as BookDetail;
  const chapters = bookDetail.value.chapters || [];
  if (chapters.length === 0) {
    loading.value = false;
    emptyBook.value = true;
    return;
  }
  const queryArticleId = route.query.articleId ? String(route.query.articleId) : "";
  const saved = localStorage.getItem(progressKey.value) || "";
  const inBook = (id: string) => chapters.some((c) => String(c.articleId) === id);
  const target = queryArticleId && inBook(queryArticleId)
    ? queryArticleId
    : saved && inBook(saved)
      ? saved
      : String(chapters[0].articleId);
  await loadChapter(target, true);
  loading.value = false;
};

/** 点击目录章节 */
const goChapter = (articleId: number) => {
  drawerShow.value = false;
  if (articleId === currentArticleId.value) return;
  loadChapter(articleId, true);
};

const toggleDrawer = () => {
  drawerShow.value = !drawerShow.value;
};

// 目录改为右侧滑出面板（桌面端 hover 右边界点把手打开，手机端目录按钮打开，同一个面板）
const tocSize = window.innerWidth <= 768 ? "75%" : "420px";
const edgeHover = ref(false);
const edgeTop = ref(80);

// 把手跟随鼠标在边界上的位置出现
const placeHandle = (e: MouseEvent) => {
  const zone = e.currentTarget as HTMLElement;
  const rect = zone.getBoundingClientRect();
  let top = e.clientY - rect.top - 28; // 把手半高居中对准光标
  top = Math.max(0, Math.min(top, rect.height - 56));
  edgeTop.value = top;
};
const onEdgeIn = (e: MouseEvent) => {
  edgeHover.value = true;
  placeHandle(e);
};
const onEdgeMove = (e: MouseEvent) => placeHandle(e);
const doPrint = () => {
  window.print();
};

// 切换书时重新初始化
watch(
  () => route.query.bookId,
  () => {
    if (route.path === "/book/read") init();
  }
);

onMounted(() => {
  init();
});
</script>

<template>
  <!-- 与其他页面同款头图，压矮高度让正文占比更大 -->
  <div class="read-hero">
    <PageHeader />
  </div>
  <div class="read-page">
    <!-- 顶栏 -->
    <div class="toolbar no-print">
      <div class="toolbar-left">
        <router-link to="/book" class="back-link">← 书架</router-link>
        <span class="book-title" v-if="chapter">{{ chapter.bookTitle }}</span>
        <el-tag size="small" type="info" v-if="chapter">
          第 {{ chapter.chapterIndex }} / {{ chapter.totalChapters }} 章
        </el-tag>
      </div>
      <div class="toolbar-right">
        <el-button size="small" class="mobile-only" @click="toggleDrawer">目录</el-button>
        <el-button size="small" @click="doPrint">打印 / 存PDF</el-button>
        <a v-if="bookId" :href="BookService.exportUrl(bookId)" class="download-link">
          <el-button size="small" type="primary">下载 ZIP</el-button>
        </a>
      </div>
    </div>

    <el-row class="content-row" :gutter="16" v-loading="loading">
      <el-empty
        v-if="!loading && emptyBook"
        description="这本书还没有章节"
      />

      <template v-if="!loading && !emptyBook && chapter">
        <!-- 正文（在左，占大头） -->
        <el-col :xs="24" :sm="24" :md="24" class="content-col">
          <el-card class="md-preview">
            <div class="chapter-head">
              <h2>{{ currentChapterTitle }}</h2>
              <span class="meta">《{{ chapter.bookTitle }}》 第 {{ chapter.chapterIndex }} 章</span>
            </div>
            <MdPreview
              class="md-preview-v3"
              v-model="mdState.text"
              :editorId="mdState.id"
              :preview-theme="previewTheme"
              :code-theme="codeTheme"
              :theme="mainTheme ? 'dark' : 'light'"
              @onGetCatalog="onCatalog"
            />
            <!-- 上一章 / 下一章 -->
            <div class="chapter-nav no-print">
              <div class="nav-prev" :class="{ disabled: !chapter.prev }" @click="chapter.prev && goChapter(chapter.prev.articleId)">
                <template v-if="chapter.prev">
                  <span class="nav-label">← 上一章</span>
                  <span class="nav-title">{{ chapter.prev.chapterTitle }}</span>
                </template>
                <span v-else class="nav-label">已经是第一章</span>
              </div>
              <div class="nav-next" :class="{ disabled: !chapter.next }" @click="chapter.next && goChapter(chapter.next.articleId)">
                <template v-if="chapter.next">
                  <span class="nav-label">下一章 →</span>
                  <span class="nav-title">{{ chapter.next.chapterTitle }}</span>
                </template>
                <span v-else class="nav-label">已经是最后一章</span>
              </div>
            </div>
          </el-card>

          <!-- 文章右边界：悬浮出现把手，点击从右侧滑出目录面板 -->
          <div class="edge-zone no-print" @mouseenter="onEdgeIn" @mousemove="onEdgeMove" @mouseleave="edgeHover = false">
            <button
              :class="['edge-handle', { show: edgeHover }]"
              :style="{ top: edgeTop + 'px' }"
              title="章节目录"
              @click="toggleDrawer"
            >
              »
            </button>
          </div>
        </el-col>
      </template>
    </el-row>

    <!-- 目录面板：统一从右侧滑出（桌面把手 / 手机按钮 均打开此面板） -->
    <el-drawer
      title="目录"
      v-model="drawerShow"
      direction="rtl"
      :append-to-body="true"
      :size="tocSize"
      class="no-print"
    >
      <div class="drawer-sub">章节目录</div>
      <div
        v-for="(c, i) in bookDetail?.chapters"
        :key="c.articleId"
        :class="['chapter-item', c.articleId === currentArticleId ? 'active' : '']"
        @click="goChapter(c.articleId)"
      >
        <span class="idx">{{ i + 1 }}</span>
        <span class="ctitle">{{ c.chapterTitle }}</span>
      </div>
      <div class="drawer-sub drawer-sub-gap">本章小节</div>
      <TocTree v-if="drawerShow && !loading" :items="catalogItems" />
    </el-drawer>
  </div>
</template>

<style lang="scss" scoped>
.read-page {
  max-width: 1360px;
  margin: 0 auto;
  padding: 16px 32px 48px;
  box-sizing: border-box;
}

/* 正文卡片：必须有内边距，文字不能贴卡片边框 */
.md-preview {
  padding: 24px;
}

.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 8px;
  padding: 12px 18px;
  margin-bottom: 14px;
  border-radius: 8px;
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color-lighter);

  .toolbar-left {
    display: flex;
    align-items: center;
    gap: 12px;
  }
  .back-link {
    color: var(--el-color-primary);
    text-decoration: none;
    font-weight: 600;
  }
  .book-title {
    font-weight: 600;
    font-size: 1rem;
  }
  .toolbar-right {
    display: flex;
    gap: 8px;
    align-items: center;
  }
  .download-link {
    text-decoration: none;
  }
}

/* 文章右边界：悬浮折叠/展开把手 */
.content-col {
  position: relative;
}
.edge-zone {
  position: absolute;
  right: -14px;
  top: 0;
  bottom: 0;
  width: 28px;
  z-index: 30;
}
.edge-handle {
  position: absolute;
  left: 0;
  right: 0;
  top: 80px;
  display: block;
  margin: 0 auto;
  width: 20px;
  height: 56px;
  padding: 0;
  border: none;
  border-radius: 6px;
  background: rgba(29, 30, 31, 0.75);
  color: #fff;
  font-size: 16px;
  line-height: 56px;
  text-align: center;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.15s ease;
  &:hover {
    background: rgba(29, 30, 31, 0.95);
  }
  &.show {
    opacity: 1;
  }
}

.drawer-sub {
  font-size: 0.95rem;
  font-weight: 600;
  margin-bottom: 8px;
  color: var(--el-text-color-primary);
}
.drawer-sub-gap {
  margin-top: 18px;
  padding-top: 14px;
  border-top: 1px dashed var(--el-border-color);
}

/* 头部"目录"按钮：仅手机/平板显示（桌面用右边界把手打开同一面板） */
@media screen and (min-width: 999px) {
  .mobile-only {
    display: none !important;
  }
}

.chapter-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 7px 8px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background 0.2s;

  .idx {
    flex-shrink: 0;
    width: 22px;
    height: 22px;
    line-height: 22px;
    text-align: center;
    border-radius: 50%;
    background: var(--el-fill-color);
    font-size: 0.75rem;
    color: var(--el-text-color-secondary);
  }
  .ctitle {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  &:hover {
    background: var(--el-fill-color-light);
  }
  &.active {
    background: var(--el-color-primary-light-9);
    color: var(--el-color-primary);
    font-weight: 600;
    .idx {
      background: var(--el-color-primary);
      color: #fff;
    }
  }
}

.chapter-head {
  margin-bottom: 10px;
  h2 {
    margin: 0 0 4px;
    font-size: 1.5rem;
  }
  .meta {
    color: var(--el-text-color-secondary);
    font-size: 0.85rem;
  }
}

.chapter-nav {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px dashed var(--el-border-color);

  .nav-prev,
  .nav-next {
    display: flex;
    flex-direction: column;
    gap: 4px;
    max-width: 45%;
    padding: 10px 14px;
    border-radius: 8px;
    background: var(--el-fill-color-light);
    cursor: pointer;
    transition: background 0.2s;
    &:hover {
      background: var(--el-color-primary-light-9);
    }
    &.disabled {
      cursor: not-allowed;
      opacity: 0.55;
      &:hover {
        background: var(--el-fill-color-light);
      }
    }
  }
  .nav-next {
    text-align: right;
    margin-left: auto;
  }
  .nav-label {
    font-weight: 600;
    color: var(--el-color-primary);
    font-size: 0.9rem;
  }
  .nav-title {
    color: var(--el-text-color-regular);
    font-size: 0.85rem;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

@media screen and (max-width: 768px) {
  .book-title {
    display: none;
  }
}

/* 打印：只留正文 */
@media print {
  .no-print,
  .page-header,
  .toolbar,
  .sidebar-col,
  .chapter-nav,
  .el-drawer,
  .mobile-affix {
    display: none !important;
  }
  .read-page {
    padding: 0;
    max-width: none;
  }
  .content-row {
    display: block !important;
  }
  .md-preview {
    border: none;
    box-shadow: none;
    padding: 0;
  }
  body {
    background: #fff !important;
  }
}
</style>
