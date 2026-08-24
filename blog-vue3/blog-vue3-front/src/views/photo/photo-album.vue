<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/index";
import { PhotoService } from "@/api/photoApi";
import { ConfigService } from "@/api/configApi";
import { type Album } from "@/types/photo";
import SkeletonItem from "@/components/SkeletonItem/skeleton-item.vue";
import PageHeader from "@/components/PageHeader/index.vue";

const userStore = useUserStore();
const router = useRouter();
const albumList = ref<Album[]>([]);
const loading = ref(false);
const bgUrl = ref("");

// 获取相册封面完整 URL
const getCoverUrl = (cover: string): string => {
  if (!cover) return "";
  if (cover.startsWith("http://") || cover.startsWith("https://")) return cover;
  const base = import.meta.env.VITE_MINIO_URL || "http://localhost:9007";
  const path = cover.startsWith("/") ? cover : `/${cover}`;
  return `${base}${path}`;
};

// 跳转到相册详情
const goToPhotos = (item: Album) => {
  router.push({
    path: "/photos",
    query: {
      id: String(item.id),
      pageTitle: item.albumName,
    },
  });
};

// 获取全部相册
const getAll = async () => {
  loading.value = true;
  const res = await PhotoService.getAllAlbum();
  if (res.code === 200) {
    albumList.value = res.result || [];
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
  getAll();
  getFrontBackground();
});
</script>

<template>
  <PageHeader :bg-url="bgUrl" />
  <div class="albumList">
    <el-row class="center_box">
      <el-col :span="24">
        <el-card class="albumList-card">
          <el-empty v-if="!loading && albumList.length === 0" description="暂无相册" />
          <el-row v-else-if="loading">
            <el-col :xs="12" :sm="6" v-for="item in 8" :key="item">
              <div class="flex_center">
                <el-skeleton animated>
                  <template #template>
                    <SkeletonItem variant="image" width="100%" height="8rem" />
                  </template>
                </el-skeleton>
              </div>
            </el-col>
          </el-row>
          <el-row v-else>
            <el-col :xs="12" :sm="6" v-for="item in albumList" :key="item.id">
              <div
                class="albumList-box flex_center"
                @click="goToPhotos(item)"
              >
                <div class="albumList-box__mask">
                  <span class="name text_overflow">{{ item.albumName }}</span>
                  <span class="desc text_overflow">{{ item.description }}</span>
                  <span class="count" v-if="item.photoCount">{{ item.photoCount }} 张</span>
                </div>
                <el-image
                  class="albumList-box__image"
                  :src="getCoverUrl(item.albumCover)"
                  fit="cover"
                  lazy
                >
                  <template #error>
                    <div class="w-[100%] h-[100%] grid place-items-center">
                      <el-icon :size="32" color="#c0c4cc"><Picture /></el-icon>
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

<script lang="ts">
import { Picture } from "@element-plus/icons-vue";
export default { components: { Picture } };
</script>

<style lang="scss" scoped>
.albumList {
  &-card {
    padding: 10px;
    min-height: 12em;
    cursor: pointer;
  }
  &-box {
    position: relative;
    width: 100%;
    height: 10rem;
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
      bottom: 40%;
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
.albumList-box:hover {
  filter: saturate(2) drop-shadow(0 0 5px rgba(0, 0, 0, 0.66));
  transform: translateY(-5px);
}

@media screen and (max-width: 768px) {
  .albumList-box {
    height: 8rem;
  }
}
</style>
