<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/index";
import { BookService, type BookListItem } from "@/api/blog/bookApi";
import { ConfigService } from "@/api/configApi";
import SkeletonItem from "@/components/SkeletonItem/skeleton-item.vue";
import PageHeader from "@/components/PageHeader/index.vue";

const userStore = useUserStore();
const router = useRouter();
const bookList = ref<BookListItem[]>([]);
const loading = ref(false);
const bgUrl = ref("");

// 封面完整 URL（后端已补全域名，这里做兼容兜底）
const getCoverUrl = (cover?: string): string => {
  if (!cover) return "";
  if (cover.startsWith("http://") || cover.startsWith("https://")) return cover;
  const base = import.meta.env.VITE_MINIO_URL || "http://localhost:9007";
  const path = cover.startsWith("/") ? cover : `/${cover}`;
  return `${base}${path}`;
};

// 进入阅读页（不带 articleId，由阅读页解析首章/上次进度）
const goToRead = (item: BookListItem) => {
  router.push({ path: "/book/read", query: { bookId: String(item.id) } });
};

const getBooks = async () => {
  loading.value = true;
  const res = await BookService.getBookList();
  if (res.code === 200) {
    bookList.value = res.result || [];
  }
  loading.value = false;
};

const getFrontBackground = async () => {
  const res = await ConfigService.getFrontBackground(userStore.getUserInfo.id || 1);
  if (res.code === 200) {
    bgUrl.value = res.result.frontHeadBackground;
  }
};

onMounted(() => {
  getBooks();
  getFrontBackground();
});
</script>

<template>
  <PageHeader :bg-url="bgUrl" />
  <div class="bookList">
    <!-- 内容区：显式左右留白，不贴屏幕边缘 -->
    <el-row class="center_box">
      <el-col :span="24">
        <el-card class="bookList-card">
          <el-empty v-if="!loading && bookList.length === 0" description="暂无上架的书" />
          <el-row v-else-if="loading">
            <el-col :xs="12" :sm="6" v-for="item in 4" :key="item">
              <div class="flex_center">
                <el-skeleton animated>
                  <template #template>
                    <SkeletonItem variant="image" width="100%" height="12rem" />
                  </template>
                </el-skeleton>
              </div>
            </el-col>
          </el-row>
          <el-row v-else>
            <el-col :xs="12" :sm="6" v-for="item in bookList" :key="item.id">
              <div class="bookList-box flex_center" @click="goToRead(item)">
                <div class="bookList-box__mask">
                  <span class="name text_overflow">{{ item.title }}</span>
                  <span class="desc text_overflow" v-if="item.subtitle">{{ item.subtitle }}</span>
                  <span class="desc text_overflow" v-else>{{ item.description }}</span>
                  <span class="count">{{ item.chapterCount }} 章</span>
                </div>
                <el-image
                  class="bookList-box__image"
                  :src="getCoverUrl(item.coverImage)"
                  fit="cover"
                  lazy
                >
                  <template #error>
                    <div class="w-[100%] h-[100%] grid place-items-center cover-empty">
                      <i class="iconfont icon-muludaohang" style="font-size: 32px; color: #909399"></i>
                    </div>
                  </template>
                </el-image>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style lang="scss" scoped>
.bookList {
  box-sizing: border-box;
  width: 100%;
  max-width: 1180px;
  margin: 0 auto;
  padding: 0 24px 32px;

  &-card {
    padding: 10px;
    min-height: 12em;
    cursor: pointer;
    border-radius: 10px;
  }
  &-box {
    position: relative;
    width: 100%;
    height: 14rem;
    margin: 5px 0;
    transition: all 0.3s ease-in-out;

    &__image {
      border-radius: 8px;
      vertical-align: middle;
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    &__mask {
      display: block;
      position: absolute;
      top: 0.8rem;
      left: 1rem;
      right: 1rem;
      bottom: 45%;
      border-radius: 8px;
      padding: 5px;
      z-index: 999;
      background: rgba(0, 0, 0, 0.2);
      .name {
        display: block;
        width: 100%;
        color: var(--global-white);
        font-size: 1.4rem;
        font-weight: bold;
      }
      .desc {
        display: block;
        width: 100%;
        color: var(--global-white);
        font-size: 1rem;
      }
      .count {
        display: inline-block;
        margin-top: 2px;
        padding: 1px 6px;
        border-radius: 4px;
        background: rgba(255, 255, 255, 0.3);
        color: var(--global-white);
        font-size: 0.8rem;
      }
    }
  }
}
.cover-empty {
  width: 100%;
  height: 100%;
  background: #f5f7fa;
}
.bookList-box:hover {
  filter: saturate(2) drop-shadow(0 0 5px rgba(0, 0, 0, 0.66));
  transform: translateY(-5px);
}

@media screen and (max-width: 768px) {
  .bookList-box {
    height: 10rem;
  }
}
</style>
