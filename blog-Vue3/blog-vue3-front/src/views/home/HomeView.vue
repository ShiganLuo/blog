<script lang="ts" setup>
import { ref, reactive, onMounted, nextTick, type Ref } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/index";

import { homeGetArticleList } from "@/api/article";
import { homeGetConfig } from "@/api/config";
import { getAllTag } from "@/api/tag";
import { homeGetStatistic } from "@/api/home";
import { randomFontColor, numberFormate } from "@/utils/tool";
import PageHeader from "@/components/PageHeader/index.vue";
import HomeArticleList from "@/components/HomeArticle/home-article-list.vue";
import RightSide from "@/components/RightSide/right-side.vue";
import { gsapTransY } from "@/utils/transform";

// 类型定义
interface Article {
  id?: string | number;
  article_cover?: string;
  article_title?: string;
  createdAt?: string;
  updatedAt?: string;
  categoryNameList?: string[];
  tagNameList?: string[];
  thumbs_up_times?: number;
  view_times?: number;
  article_description?: string;
  is_top?: number;
}

interface ConfigDetail {
  blog_avatar?: string;
  createdAt?: string;
  articleCount?: number;
  view_time?: number;
  qq_group?: string;
  we_chat_group?: string;
  blog_notice?: string;
  // 其他配置字段...
  avatar_bg?: string;
  blog_name?: string;
  personal_say?: string;
  categoryCount?: number;
  tagCount?: number;
  git_ee_link?: string;
  bilibili_link?: string;
  github_link?: string;
  we_chat_link?: string;
  qq_link?: string;
}

interface Tag {
  id: number;
  tag_name: string;
}

interface ColoredTag extends Tag {
  color: string;
}

interface PaginationParams {
  current: number;
  size: number;
  loading: boolean;
}

// 初始化逻辑
const userStore = useUserStore();
const router = useRouter();

/** 文章相关逻辑 */
const param: PaginationParams = reactive({
  current: 1, // 当前页
  size: 5, //每页条目数
  loading: true, //加载
});

const articleList: Ref<Article[]> = ref([]);
const articleTotal: Ref<number> = ref(0);

const getHomeArticleList = async (): Promise<void> => {
  try {
    const res = await homeGetArticleList( { pageNum: param.current, pageSize: param.size });
    if (res.code === 200) {
      const { total, list } = res.result;
      articleTotal.value = total;
      articleList.value = list;
    }
  } finally {
    param.loading = false;
  }
};

const pagination = (page: { current: number }): void => {
  param.current = page.current;
  getHomeArticleList();
};

/** 右侧边栏相关逻辑 */
const rightSizeLoading: Ref<boolean> = ref(false);
const runtime: Ref<number> = ref(0);
const configDetail: Ref<ConfigDetail> = ref({});
const tags: Ref<ColoredTag[]> = ref([]);

// 获取网站详细信息
const getConfigDetail = async (): Promise<void> => {
  try {
    const res = await homeGetConfig();
    if (res.code === 200 && typeof res.result !== "string") {
      configDetail.value = res.result as ConfigDetail;
      userStore.setBlogAvatar(res.result.blog_avatar || '');
      calcRuntimeDays(configDetail.value.createdAt || '');
    }
  } finally {
    rightSizeLoading.value = false;
  }
};

// 获取所有标签
const getAllTags = async (): Promise<void> => {
  const res = await getAllTag();

  if (res.code === 200 && Array.isArray(res.result)) {
    tags.value = res.result.map((tag: Tag): ColoredTag => ({
      ...tag,
      color: randomFontColor()
    }));
  } else {
    console.warn('返回数据不符合预期:', res);
  }
};

// 计算网站运行天数
const calcRuntimeDays = (time: string): void => {
  if (time) {
    // ISO-8601 兼容，浏览器能解析 "2025-07-06T14:13:12"
    const now = new Date().getTime();
    const created = new Date(time).getTime();

    if (!isNaN(created)) {
      const days = Math.floor((now - created) / 8.64e7); // 8.64e7 = 1000 * 60 * 60 * 24
      runtime.value = days;
    } else {
      console.warn("无法解析时间格式:", time);
    }
  }
};


const init = async (): Promise<void> => {
  param.loading = true;
  rightSizeLoading.value = true;
  await Promise.all([
    getHomeArticleList(),
    getConfigDetail(),
    getAllTags()
  ]);
};



onMounted(async () => {
  await init();
});
</script>

<template>
  <PageHeader/>
  <div class="home_center_box">
    <el-row>
      <el-col :xs="24" :sm="18">
        <HomeArticleList
          :article-list="articleList"
          :param="param"
          :article-total="articleTotal"
          @page-change="pagination"
        />


      </el-col>

      <el-col :xs="0" :sm="6">
        <RightSide
          :config-detail="configDetail"
          :tags="tags"
          :runtime="runtime"
          :loading="rightSizeLoading"
        />

      </el-col>
    </el-row>
  </div>
</template>

<style lang="scss" scoped>
.group {
  margin-left: 0.3rem;
  width: 100%;
  height: auto;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;

  .img {
    width: 80px;
    height: 80px;
    border-radius: 8px;
    margin-right: 10px;
  }
}
</style>
