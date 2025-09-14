<script setup lang="ts">
import { useRouter } from "vue-router";
import SkeletonItem from "../SkeletonItem/skeleton-item.vue";
import Pagination from "../Pagination/pagination.vue";
import SvgIcon from "@/components/SvgIcon/index.vue";
// 每篇文章类型
interface Article {
  id: number;
  article_title: string;
  article_cover: string;
  createdAt: string;
}

// 每个归档分组类型
interface ArchiveGroup {
  year: number;
  articleList: Article[];
}

// 分页参数类型
interface PaginationParam {
  size: number;
  current: number;
}

// Props 类型
const props = defineProps<{
  archives: ArchiveGroup[];
  loading: boolean;
  param: PaginationParam;
  total: number;
}>();

// Emits 类型
const emit = defineEmits<{
  (e: "pagination", page: { current: number }): void;
}>();

// 分页布局
let layout = " prev, pager, next";

// 分页事件
const pagination = (page: { current: number }) => {
  emit("pagination", page);
};

// 路由
const router = useRouter();

// 跳转到文章
const goToArticle = (article: Article) => {
  router.push({ path: "/article", query: { id: article.id } });
};
</script>

<template>
  <el-timeline class="my-timeline">
    <!-- 骨架屏 -->
    <el-skeleton v-if="loading" :loading="loading" animated class="skeleton">
      <template #template>
        <SkeletonItem variant="text" width="4rem" height="2rem" />
        <SkeletonItem class="skeleton-left" variant="text" width="0.5rem" height="60rem" />
        <div class="flex_r_start skeleton-right" v-for="i in 10" :key="i">
          <SkeletonItem variant="image" width="8rem" height="8rem" />
          <div class="flex_c_center skeleton-right__item">
            <SkeletonItem variant="text" width="4rem" height="25px" />
            <SkeletonItem variant="text" width="6rem" top="1rem" height="15px" />
          </div>
        </div>
      </template>
    </el-skeleton>

    <!-- 时间轴 -->
    <template v-else>
      <div v-for="item in archives" :key="item.year">
        <div class="year to_pointer">{{ item.year }}</div>
        <el-timeline-item
          v-for="article in item.articleList"
          :key="article.id"
          size="large"
          :hollow="true"
          hide-timestamp
          :center="true"
          class="my-timeline-item border-orange"
        >
          <div class="flex_r_start timeline">
            <div 
                class="timeline-cover scale" 
                v-image="article.article_cover"
                >
                <el-image
                    class="cover-img"
                    fit="cover"
                    :src="article.article_cover"
                    @click="goToArticle(article)"
                >
                    <template #error>
                    <svg-icon name="image404" :width="5" :height="5"></svg-icon>
                    </template>
                </el-image>
            </div>

            <div class="timeline-info" @click="goToArticle(article)">
              <div class="timeline-info__title">
                {{ article.article_title }}
              </div>
              <div class="timeline-info__time">
                {{ article.createdAt }}
              </div>
            </div>
          </div>
        </el-timeline-item>
      </div>
    </template>
  </el-timeline>

  <!-- 分页组件 -->
  <Pagination
    :size="param.size"
    :current="param.current"
    :layout="layout"
    :total="total"
    @pagination="pagination"
  />
</template>

<style lang="scss" scoped>
.my-timeline-item {
  margin-bottom: 20px; /* 每个时间轴节点的间隔 */
}

.timeline {
  gap: 12px; /* 图片和文字之间的间隔 */
}

.timeline-cover {
  width: 120px;
  height: 120px;
  flex-shrink: 0; /* 避免图片被压缩 */
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover; /* 保持比例裁切 */
}

.timeline-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
}



</style>