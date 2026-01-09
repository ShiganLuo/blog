<script setup lang="ts">
import { computed, nextTick, ref, watch } from "vue";
import { useRoute } from "vue-router";
import { useStaticData } from "@/stores/index";
import { storeToRefs } from "pinia";

import { numberFormate } from "@/utils/tool";
import { gsapTransFont } from "@/utils/transform";

import Tooltip from "../ToolTip/index.vue";
import GsapCount from "@/components/GsapCount/index.vue";
import HomeHeader from "./home-header.vue";
import Waves from "@/components/WelcomeComps/waves.vue";

// =====================
// props 类型定义
// =====================
interface Article {
  articleCover?: string;
  articleTitle?: string;
  createdAt?: string;
  updatedAt?: string;
  categoryNameList?: string[];
  tagNameList?: string[];
  thumbsUpTimes?: number;
  viewTimes?: number;
}

interface Props {
  loading?: boolean;
  article?: Article;
  bgUrl?: string;
  photoAlbumList?: any[];
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  article: () => ({}),
  bgUrl: "",
  photoAlbumList: () => [],
});
console.log("----1----")
console.log(props.bgUrl)
// =====================
// Store + 路由
// =====================
const staticStore = useStaticData();
const { codeTheme, previewTheme, getPageHeaderList } = storeToRefs(staticStore);
const route = useRoute();

// =====================
// 方法定义（含类型）
// =====================
const toggleMdTheme = (type: "codeTheme" | "previewTheme", theme: string) => {
  staticStore[type] = theme;
};

const finalUrl = ref("");

// 小于 10 补 0
const addZero = (time: number): string => {
  return time > 0 && time < 10 ? `0${time}` : `${time}`;
};

// =====================
// 计算属性
// =====================
const getBgCover = computed(() => {
  const bgList = getPageHeaderList.value;
  let url: string;
  const fallback = "http://10.135.4.3/uploads/f2b516a7-8557-4d31-baf1-2d68d9fe34e7.jpeg";
  // console.log("bgList", bgList);
  if (route.path === "/article") {
    url = props.article.articleCover || fallback;
  } else if (props.bgUrl) {
    url = props.bgUrl;
  } else {
    const index = bgList.findIndex((bg: any) => bg.route_name === route.name);
    url = index === -1 ? fallback : bgList[index].bg_url;
  }

  finalUrl.value = url;
  return `background-image: url(${url});}`;
});

const getTitle = computed(() => {
  const pageTitle = route.query.pageTitle as string | undefined;
  return pageTitle ? `${route.meta.name} - ${pageTitle}` : `${route.meta.name}`;
});

// =====================
// 动效监听
// =====================
watch(
  () => route.path,
  () => {
    nextTick(() => {
      gsapTransFont(".char");
    });
  },
  {
    immediate: true,
  }
);
</script>

<template>
  <HomeHeader :bgUrl = "bgUrl" class="!w-[100%] !h-[100vh]" v-if="route.path == '/home'" />

  <div v-else class="page-header" :style="getBgCover">
    <div v-if="route.path == '/article'" class="article main-article">
      <div class="loading" v-image="props.article.articleCover"></div>
      <Tooltip
        width="80%"
        weight="500"
        size="2.4rem"
        color="#fff"
        align="center"
        :name="article.articleTitle"
      />
      <div class="!mt-[20px]">
        <span class="to_pointer">
          <i class="iconfont icon-calendar2"></i>
          <span class="meta-label">发表于</span>
          <span class="meta-value">{{ article.createdAt }}</span>
        </span>
        <span class="to_pointer">
          <i class="iconfont icon-schedule"></i>
          <span class="meta-label">更新于</span>
          <span class="meta-value">{{ article.updatedAt }}</span>
        </span>
        <span class="meta-separator"></span>
        <span class="to_pointer">
          <span
            class="meta-value"
            v-for="(categoryName, index) in (article.categoryNameList ?? []).slice(0, 3)"
            :key="index"
            >{{ index == (article.categoryNameList ?? []).length - 1 ? categoryName : categoryName + "、" }}
          </span>
        </span>
        <span class="meta-separator"></span>
        <span class="to_pointer">
          <span
            class="meta-value"
            v-for="(tagName, index) in (article.tagNameList ?? []).slice(0, 3)"
            :key="index"
            >{{ index == (article.tagNameList ?? []).length - 1 ? tagName : tagName + "、" }}
          </span>
        </span>
        <span class="meta-separator"></span>
        <span class="to_pointer">
          <i class="iconfont icon-icon"></i>
          <span class="meta-label">点赞数</span>
          <GsapCount
            class="meta-value"
            v-if="(article.thumbsUpTimes ?? 0) - 0 < 1000"
            :value="article.thumbsUpTimes"
          />
          <span v-else class="meta-value">
            {{ numberFormate(article.thumbsUpTimes) }}
          </span>
        </span>
        <span class="meta-separator"></span>
        <span class="to_pointer">
          <i class="iconfont icon-chakan"></i>
          <span class="meta-label">浏览次数</span>
          <GsapCount
            class="meta-value"
            v-if="(article.viewTimes ?? 0) - 0 < 1000"
            :value="article.viewTimes"
          />
          <span v-else class="meta-value">{{ numberFormate(article.viewTimes) }}</span>
        </span>
      </div>
      <div class="toggle-theme">
        <el-dropdown class="theme-card-dropdown">
          <div class="flex_c_center">
            <span>预览主题</span>
            <span>{{ previewTheme }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item
                v-for="(item, index) in staticStore.previewThemeList"
                :key="index"
                @click="toggleMdTheme('previewTheme', item)"
                >{{ item }}</el-dropdown-item
              >
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-dropdown class="theme-card-dropdown">
          <div class="flex_c_center">
            <span>代码主题</span>
            <span>{{ codeTheme }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item
                v-for="(item, index) in staticStore.codeThemeList"
                :key="index"
                @click="toggleMdTheme('codeTheme', item)"
                >{{ item }}</el-dropdown-item
              >
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
    <div v-else class="route-font">
      <div class="loading !pt-[80px]" v-image="finalUrl"></div>
      <!-- 想修改路由 就修改插槽即可 -->
      <slot name="route">
        <span style="display: inline-block" class="char" v-for="i in getTitle.length" :key="i">
          {{ getTitle.charAt(i - 1) }}
        </span>
      </slot>
      <slot />
    </div>
    <Waves height="4rem" />
  </div>
</template>

<style lang="scss" scoped>
.page-header {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: var(--header-bg);
  background-position: center center;
  background-size: cover;
  background-repeat: no-repeat;
  width: 100%;
  height: 22rem;

  .loading {
    position: absolute;
    top: 0;
    left: 0;
    bottom: 0;
    right: 0;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  &::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    display: block;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.6);
  }

  .route-font {
    font-size: 2.4rem;
    font-weight: 500;
    line-height: 2.4;
    text-align: center;
    color: var(--router-color);
    z-index: 1000;
    cursor: pointer;
    transition: all 0.3s;
  }

  .article {
    z-index: 2000;
    background: transparent;
    font-size: 1.1rem;
    line-height: 1.4;
    margin-top: 5rem;
    color: transparent;
    text-align: center;
    color: var(--global-white);

    .to_pointer {
      padding: 0 0.3rem;
    }

    .iconfont {
      margin-right: 0.3rem;
    }
  }

  .meta {
    .icon-speechbubble {
      font-size: 1rem;
    }

    &-separator {
      margin: 0 0.4rem;
      font-size: 1.1rem;
      position: relative;

      &::after {
        content: "|";
        position: absolute;
        top: -3px;
        right: 0;
      }
    }
    &-value {
      margin-left: 3px;
    }

    i {
      margin: 0 0.2rem 0 0;
    }
  }

  .apply-button {
    display: inline-block;
    padding: 0 20px;
    background-color: transparent;
    box-sizing: border-box;
    border-radius: 20px;
    font-weight: 900;
    font-size: 16px;
    border: 3px solid #dddddd;
    color: #dddddd;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      color: var(--global-white);
      border: 3px solid var(--global-white);
      transform: scale(1.1);
    }
  }

  .toggle-theme {
    position: relative;
    display: flex;
    justify-content: center;
    margin-top: 1rem;

    h3 {
      line-height: 1.4;
    }

    .theme-card-dropdown {
      width: 8rem;
      overflow: auto;
      margin: 0.5rem;
      text-align: center;
      display: block;
      padding: 0.2rem 0;
      background: transparent;
      border: 1px solid var(--global-white);
      color: var(--global-white);
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.5s;

      span {
        &:first-child {
          line-height: 1.2;
        }
      }
      &:hover {
        transform: translateY(-3px);
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .main-article {
    max-width: 90%;
  }
}

@media screen and (min-width: 768px) {
  .main-article {
    max-width: 60%;
  }
}
</style>
