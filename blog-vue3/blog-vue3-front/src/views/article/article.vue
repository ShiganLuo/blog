<script setup lang="ts">
import { ref, watch, reactive, h, nextTick, onBeforeUnmount, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useStaticData, useUserStore } from "@/stores/index";
import { storeToRefs } from "pinia";
import { MdPreview } from "md-editor-v3";
import "md-editor-v3/lib/style.css";
import { useHead } from "@vueuse/head";

import { ArticleService } from "@/api/blog/articleApi";
import { LikeService } from "@/api/likeApi";

import Comment from "@/components/Comment/index.vue";
import Tooltip from "@/components/ToolTip/index.vue";
import PageHeader from "@/components/PageHeader/index.vue";
import TocTree from "@/components/TocTree/index.vue";
import GsapCount from "@/components/GsapCount/index.vue";
import SvgIcon from "@/components/SvgIcon/index.vue";

interface MdState {
  text: string;
  id: string;
  switch: boolean;
}

let setUpTimes: Date | null = null;
let lastVisitTime = 0;
let lastArticleId: string | null = null;
let comment: Element | null = null;
let observe: IntersectionObserver | null = null; // 用于监听评论是否出现在可视区域内

const commentRef = ref<InstanceType<typeof Comment> | null>(null);
const commentIsOpen = ref(false);

// 初始化pinia
const router = useRouter();
const route = useRoute();
const staticStore = useStaticData();
const userStore = useUserStore();
const { codeTheme, previewTheme, mainTheme } = storeToRefs(staticStore);
const { getUserInfo } = storeToRefs(userStore);

const mdState = reactive<MdState>({
  text: "",
  id: "my-editor",
  switch: true,
});

const articleFormState = {
  id: 0,
  articleCover: '',
  articleTitle: '',
  articleContent: '',
  originUrl: '',  
  createdAt: '',
  updatedAt: '',
  categoryNameList: [],
  tagNameList: [],
  thumbsUpTimes: 0,
  viewTimes: 0,
  authorName: '',
  type: 1,
  authorId: 0,
  
}

const previousArticleFormState = {
    article_cover: '',
    article_title: '',
    id: 0
}

const nextArticleFormState = {
    article_cover: '',
    article_title: '',
    id: 0
}

type RecommendArticle = {
  id: number
  article_cover: string
  article_title: string
  createdAt: string
}
const articleForm = reactive({ ...articleFormState });
const previousArticleForm = reactive({ ... previousArticleFormState});//上一篇文章
const nextArticleForm = reactive({...nextArticleFormState});//下一篇文章
const recommendArticleListForm = ref<RecommendArticle[]>([]); // 推荐文章
const loading = ref(false);

// SEO: 动态 meta 标签
const articleDescription = computed(() => {
  // 优先用 articleDescription，否则从 content 截取前 160 字符
  const desc = (articleForm as any).articleDescription;
  if (desc) return desc;
  const content = articleForm.articleContent || '';
  // 去掉 markdown 标记，取纯文本前 160 字符
  const plainText = content.replace(/[#*`>\[\]!\-_]/g, '').replace(/\n+/g, ' ').trim();
  return plainText.length > 160 ? plainText.substring(0, 160) + '...' : plainText;
});

useHead({
  title: computed(() => articleForm.articleTitle
    ? `${articleForm.articleTitle} - 拾感日记`
    : '拾感日记'),
  meta: computed(() => {
    const tags: any[] = [
      { name: 'description', content: articleDescription.value },
      { property: 'og:title', content: articleForm.articleTitle || '拾感日记' },
      { property: 'og:description', content: articleDescription.value },
      { property: 'og:type', content: 'article' },
      { property: 'og:url', content: window.location.href },
      { property: 'og:site_name', content: '拾感日记' },
      { name: 'twitter:card', content: articleForm.articleCover ? 'summary_large_image' : 'summary' },
      { name: 'twitter:title', content: articleForm.articleTitle || '拾感日记' },
      { name: 'twitter:description', content: articleDescription.value },
    ];
    if (articleForm.articleCover) {
      tags.push({ property: 'og:image', content: articleForm.articleCover });
      tags.push({ name: 'twitter:image', content: articleForm.articleCover });
    }
    if ((articleForm as any).tagNameList?.length) {
      tags.push({ name: 'keywords', content: (articleForm as any).tagNameList.join(', ') });
    }
    return tags;
  }),
  link: computed(() => [
    { rel: 'canonical', href: window.location.origin + '/article?id=' + articleForm.id }
  ]),
  script: computed(() => {
    if (!articleForm.articleTitle) return [];
    return [{
      type: 'application/ld+json',
      children: JSON.stringify({
        "@context": "https://schema.org",
        "@type": "Article",
        "headline": articleForm.articleTitle,
        "description": articleDescription.value,
        "image": articleForm.articleCover || undefined,
        "author": {
          "@type": "Person",
          "name": articleForm.authorName || "Sg Luo"
        },
        "datePublished": articleForm.createdAt || undefined,
        "dateModified": articleForm.updatedAt || articleForm.createdAt || undefined,
        "mainEntityOfPage": {
          "@type": "WebPage",
          "@id": window.location.origin + '/article?id=' + articleForm.id
        }
      })
    }];
  })
});

// 层级小节目录（MdPreview 解析后回调）
const catalogItems = ref<{ text: string; level: number; line: number }[]>([]);
const onCatalog = (list: { text: string; level: number; line: number }[]) => {
  catalogItems.value = list;
};

const scrollElement = document.documentElement;
const currentUrl = window.location.href;
const isLike = ref(false);
const likePending = ref(false);
const drawerShow = ref(false); // 移动端目录是否可见

// 目录改为右侧滑出面板（桌面 hover 右边界点把手，手机点右侧箭头，同一个面板）
const tocSize = window.innerWidth <= 768 ? "60%" : "420px";
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

const toggleDrawer = (): void => {
  drawerShow.value = !drawerShow.value;
};

const goToArticle = (id: number): void => {
  router.push({ path: "/article", query: { id: id } });
};

// 文章点赞
const like = async (): Promise<void> => {
  if (likePending.value) return;
  likePending.value = true;
  const userId = getUserInfo.value.id;
  // 取消点赞
  if (isLike.value) {
    const res = await LikeService.cancelLike({ for_id: articleForm.id,type: "post", user_id: userId });
    if (res.code === 200) {
      articleForm.thumbsUpTimes--;
      isLike.value = false;
      likePending.value = false;
      ElMessage.success("有什么不足可以给我留下评论，感谢指正");
    }
  } else { // 点赞
    const res = await LikeService.addLike({ for_id: articleForm.id,type: "post", user_id: userId });
    if (res.code === 200) {
      articleForm.thumbsUpTimes++;
      isLike.value = true;
      likePending.value = false;
      ElMessage.success("点赞成功，谢谢支持");
    }
  }
};

const getArticleDetails = async (id: string | number): Promise<void> => {
  const res = await ArticleService.getArticleById(id);
  if (res.code === 200) {
    Object.assign(articleForm,res.result);
    mdState.text = articleForm.articleContent;
    const LRes = await LikeService.getIsLikeByIdOrIpAndType({ for_id: articleForm.id, type: "post", user_id: getUserInfo.value.id });
    if (LRes.code === 200) {
      isLike.value = LRes.result;
    }
  }
};


const getRecommendArticle = async (id: string | number): Promise<void> => {
  const res = await ArticleService.getRecommendArticleById(id);
  if (res.code === 200) {
    const { previous, next, recommend } = res.result;
    const recommendArticleTemp:RecommendArticle[] = recommend.map(item => ({
        id: item.id ?? item.id ?? 0,
        article_title: item.articleTitle ?? item.articleTitle ?? '',
        article_cover: item.articleCover ?? item.articleCover ?? '',
        createdAt: item.createdAt ?? item.createdAt ?? ''
    }));
    recommendArticleListForm.value = recommendArticleTemp;
    Object.assign(previousArticleForm,previous);
    Object.assign(nextArticleForm,next);
  }
};

const init = async (id: string | number): Promise<void> => {
  loading.value = true;
  await getArticleDetails(id);
  await getRecommendArticle(id);
  loading.value = false;
  nextTick(() => {
    commentIsOpen.value = false;
    observeBox();
  });
};

const observeBox = (): void => {
  comment = document.querySelector(".comment-box");
  observe = new IntersectionObserver((entries) => {
    entries.forEach((e) => {
      if (e.isIntersecting && e.intersectionRatio > 0 && !commentIsOpen.value) {
        commentRef.value?.toggleExpand();
        commentIsOpen.value = true;
      }
    });
  }, { threshold: [1] });
  if (comment) observe.observe(comment);
};

// 增加文章访问量
const addArticleViews = async (id: string) => {
  try {
    await ArticleService.addArticleViews(Number(id));
  } catch (error) {
    console.error("增加阅读量失败", error);
  }
};

watch(
  () => route.query.id,
  (newId) => {
    // 基础校验
    if (route.path === "/article" && newId) {
      const currentId = String(newId);
      const now = Date.now();

      // 1. 核心内容：立即加载，保证用户第一秒看到文字
      init(currentId);

      // 2. 统计逻辑：判断并延迟执行
      const isDifferentArticle = currentId !== lastArticleId;
      const isTimeExceeded = now - lastVisitTime > 10 * 60 * 1000; // 10分钟

      if (isDifferentArticle || isTimeExceeded) {
        // 使用延时，避开首屏网络竞争
        // 建议设为 2s，此时用户已经开始阅读内容，统计更真实
        setTimeout(() => {
          addArticleViews(currentId);
          
          // 更新状态
          lastArticleId = currentId;
          lastVisitTime = now;
        }, 2000); 
      }
    }
  },
  { immediate: true }
);


</script>

<template>
  <PageHeader :article="articleForm" :loading="loading" />
  <div class="article article-center">
    <el-row class="article_box">
      <el-col :xs="24" :sm="24" :md="24" class="content-col">
        <el-skeleton v-if="loading" :loading="loading" :rows="8" animated />
        <el-card v-else class="md-preview">
          <MdPreview
            class="md-preview-v3"
            v-model="mdState.text"
            :editorId="mdState.id"
            :preview-theme="previewTheme"
            :code-theme="codeTheme"
            :theme="mainTheme ? 'dark' : 'light'"
            @onGetCatalog="onCatalog"
          ></MdPreview>
          <div class="article-info">
            <div class="article-info-inner">
              <div>
                <span>文章作者：</span>
                <a class="to_pointer" href="">{{
                  articleForm.authorName
                }}</a>
              </div>
              <div>
                <span>类型：</span>
                <el-tag>{{
                  articleForm.type == 1 ? "原创" : articleForm.type == 2 ? "转载" : "翻译"
                }}</el-tag>
              </div>
              <div v-if="articleForm.type != 1">
                <span>原文链接：</span>
                <a class="to_pointer" :href="articleForm.originUrl">{{
                  articleForm.originUrl
                }}</a>
              </div>
              <div v-else>
                <span>本文链接：</span>
                <a class="to_pointer" v-copy="currentUrl">{{ currentUrl }}</a>
              </div>
              <p>声明: 此文章版权归 Sg Luo 所有，如有转载，请注明来自原作者</p>
            </div>
          </div>
          <div :class="['like', isLike ? 'is-like' : '']" @click="like">
            <i class="iconfont icon-icon mr-5px"></i>
            <GsapCount
              :class="[isLike ? 'is-like' : '']"
              v-if="articleForm.thumbsUpTimes - 0 < 1000"
              :value="articleForm.thumbsUpTimes"
            />
            <span v-else :class="[isLike ? 'is-like' : '']">
              {{ articleForm.thumbsUpTimes }}
            </span>
          </div>
          <div class="recommend flex_r_between">
            <div class="recommend-box" @click="goToArticle(previousArticleForm.id)">
              <el-image
                class="recommend-box-img animate__animated animate__fadeInDown"
                fit="cover"
                :src="previousArticleForm.article_cover"
              >
                <template #error>
                  <svg-icon name="image404" :width="10" :height="5"></svg-icon>
                </template>
              </el-image>
              <span class="recommend-box-item prev">
                <span class="flex_r_around">
                  <i class="iconfont icon-arrowleft"></i>
                  <span class="font-semibold">上一篇</span>
                </span>
                <Tooltip
                  width="60%"
                  color="#fff"
                  :weight="600"
                  :name="previousArticleForm.article_title"
                  align="left"
                ></Tooltip>
              </span>
            </div>
            <div class="recommend-box" @click="goToArticle(nextArticleForm.id)">
              <el-image
                class="recommend-box-img animate__animated animate__fadeInDown"
                fit="cover"
                :src="nextArticleForm.article_cover"
              >
                <template #error>
                  <svg-icon name="image404" :width="10" :height="5"></svg-icon>
                </template>
              </el-image>
              <span class="recommend-box-item next">
                <span class="flex_r_around">
                  <span class="font-semibold">下一篇</span>
                  <i class="iconfont icon-arrowright"></i>
                </span>
                <Tooltip
                  width="60%"
                  color="#fff"
                  :weight="600"
                  :name="nextArticleForm.article_title"
                  align="right"
                ></Tooltip>
              </span>
            </div>
          </div>
          <!-- 移动端推荐文章 -->
          <div class="mobile-recommend">
            <el-row style="padding: 2rem">
              <div class="recommend-title">推荐文章</div>
              <el-col
                :span="12"
                v-for="item in recommendArticleListForm"
                :key="item.id"
                @click="goToArticle(item.id)"
              >
                <el-card class="card card-hover">
                  <template #header>
                    <span :title="item.article_title" class="title">{{ item.article_title }}</span>
                  </template>
                  <el-image
                    class="image animate__animated animate__fadeInDown"
                    fit="cover"
                    :src="item.article_cover"
                  >
                    <template #error>
                      <svg-icon name="image404" :width="10" :height="5"></svg-icon>
                    </template>
                  </el-image>
                </el-card>
              </el-col>
            </el-row>
          </div>
          <div class="p-2rem comment-box">
            <Comment
              ref="commentRef"
              class="w-100"
              type="post"
              :id="Number(route.query.id ?? 0)"
              :author-id="articleForm.authorId"
            />
          </div>
        </el-card>

        <!-- 文章右边界：悬浮出现折叠/展开把手 -->
        <div class="edge-zone no-print" @mouseenter="onEdgeIn" @mousemove="onEdgeMove" @mouseleave="edgeHover = false">
          <button
            :class="['edge-handle', { show: edgeHover }]"
            :style="{ top: edgeTop + 'px' }"
            title="目录"
            @click="toggleDrawer"
          >
            »
          </button>
        </div>
      </el-col>
    </el-row>
    <!-- 目录面板：统一从右侧滑出（桌面把手 / 手机箭头 均打开此面板） -->
    <el-drawer
      title="目录"
      v-model="drawerShow"
      direction="rtl"
      :before-close="toggleDrawer"
      :append-to-body="true"
      :size="tocSize"
      class="no-print"
    >
      <el-card class="command card-hover" header="推荐文章" shadow="never">
        <div class="command-box">
          <div
            class="command-box-item"
            v-for="(item, index) in recommendArticleListForm"
            :key="index"
            @click="goToArticle(item.id)"
          >
            <el-image
              class="command-box-item__img animate__animated animate__fadeInDown"
              fit="cover"
              width="50"
              :src="item.article_cover"
            >
              <template #error>
                <svg-icon name="image404" :width="5" :height="5"></svg-icon>
              </template>
            </el-image>
            <Tooltip width="35%" weight="600" size="1rem" :name="item.article_title" />
            <Tooltip width="35%" size="0.8rem" :name="item.createdAt" />
          </div>
        </div>
      </el-card>
      <div class="drawer-sub drawer-sub-gap">目录</div>
      <TocTree v-if="drawerShow && !loading" :items="catalogItems" />
    </el-drawer>
  </div>
</template>

<style lang="scss" scoped>

.md-preview {
  padding: 20px;  /* 内边距，内容离边框有点距离 */
}

/* 抽屉内分区标题 */
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

/* 文章右边界：悬浮打开目录面板的把手 */
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
.mr-5px {
  margin-right: 5px !important;
}
.font-semibold {
  font-weight: 600;
}
.p-2rem {
  padding: 2rem !important;
}
.w-100 {
  width: 100%;
}
.article {
  &-info {
    padding: 2rem 2rem;

    &-inner {
      padding: 1rem;
      color: var(--font-color);
      border: 1px solid rgba(255, 255, 255, 0.3);
    }
  }
}

.catalogue-card {
  margin-top: 1rem;
  padding: 1rem 0.5rem;

  &__box {
    scrollbar-width: none;
    overflow: auto;
    max-height: calc(100vh - 23.1rem);
    cursor: pointer;
  }
}

.mobile-catalog {
  padding: 2rem;
  max-height: 400px;
  scrollbar-width: none;
  overflow-y: auto;
  cursor: pointer;
}

.theme-card {
  padding: 1rem 0.5rem;
}

.command {
  padding: 1rem 0.5rem;

  &-box {
    max-height: 160px;
    scrollbar-width: none;
    overflow-y: auto;
    cursor: pointer;

    &::-webkit-scrollbar {
      display: none;
      /* Chrome Safari */
    }

    &-item {
      display: flex;
      justify-content: flex-start;
      align-items: center;
      padding: 0.3rem;
      color: var(--font-color);

      &__img {
        margin-right: 1rem;
        width: 50px;
        height: 40px;
      }
    }
  }
}

.icon-sort {
  font-size: 1.8rem;
  color: var(--font-color);
}

.recommend {
  box-sizing: border-box;
  position: relative;
  padding: 2rem;

  &-box {
    display: flex;
    justify-content: space-between;
    align-items: center;
    position: relative;
    width: 50%;
    height: 100%;
    overflow: hidden;
    color: var(--global-white);
    transition: scale 0.5s;
    cursor: pointer;

    &:hover {
      .recommend-box-img {
        scale: 1.2;
      }
      .recommend-box-item {
        background-color: var(--mask-bg);
      }
    }

    &-img {
      transition: all 0.5s;
      width: 100%;
      height: 100%;
    }

    &-item {
      position: absolute;
      display: flex;
      flex-direction: column;
      justify-content: center;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      font-size: 1.2rem;
      line-height: 1.8;
      transition: all 0.5s;
      background-color: rgba(0, 0, 0, 0.7);

      i {
        font-size: 1.4rem;
      }
    }

    .prev {
      padding-left: 2rem;
      align-items: flex-start;

      div {
        box-sizing: border-box;
        max-width: 10rem;
        font-size: 1rem;
        margin-left: 1rem;
      }
    }

    .next {
      padding-right: 2rem;
      align-items: flex-end;

      div {
        box-sizing: border-box;
        max-width: 10rem;
        font-size: 1rem;
        margin-right: 1rem;
      }
    }
  }
}

.like {
  margin: 1rem;
  display: flex;
  justify-content: center;
  align-items: center;

  .icon-icon {
    font-size: 1.8rem;
    transition: all 0.3s;
    &:hover {
      scale: 1.1;
    }
  }
}

.is-like {
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--primary);
  .icon-icon {
    font-size: 1.8rem;
    color: var(--primary);
  }
}

.mobile-recommend {
  position: relative;
  .recommend-title {
    position: absolute;
    top: 0;
    left: 2.2rem;
    font-size: 1.2rem;
    font-weight: 600;
    color: var(--font-color);
  }
  .card {
    width: 100%;
    height: 8rem;
    overflow: hidden;
  }
  .title {
    display: inline-block;
    width: 80%;
    height: 2rem;
    padding: 0.3rem 0 0 0.3rem;
    font-size: 1rem;
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
  }
  .image {
    width: 100%;
    height: 6rem;
  }
}

:deep(.el-card__header) {
  font-size: 1.6rem;
  padding: 0 !important;
  font-weight: bold;
  line-height: 1.8;
  color: var(--font-color);
}

a {
  text-decoration: underline;
}
@media screen and (min-width: 768px) {
  .mobile-recommend {
    display: none;
  }
}
</style>
