<script setup lang="ts">
import { ref, watch, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/stores/index";
import { PhotoService } from "@/api/photoApi";
import { ConfigService } from "@/api/configApi";
import PageHeader from "@/components/PageHeader/index.vue";
import SkeletonItem from "@/components/SkeletonItem/skeleton-item.vue";
import { isMobile } from "@/utils/tool";
import { type Album, type AlbumPhoto, type AlbumDetail } from "@/types/photo";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const bgUrl = ref("");

const photoList = ref<AlbumPhoto[]>([]);
const albumName = ref("");
const photoAlbumList = ref<Album[]>([]);
const loading = ref(false);
const drawerShow = ref(false);

// 获取图片完整 URL
const getFullUrl = (filePath: string): string => {
  if (!filePath) return "";
  if (filePath.startsWith("http://") || filePath.startsWith("https://")) return filePath;
  const base = import.meta.env.VITE_MINIO_URL || "http://localhost:9007";
  const path = filePath.startsWith("/") ? filePath : `/${filePath}`;
  return `${base}${path}`;
};

// 获取相册详情
const getAlbumDetail = async (id: number) => {
  loading.value = true;
  const res = await PhotoService.getAlbumById(id);
  if (res.code === 200) {
    const detail = res.result as AlbumDetail;
    photoList.value = detail.photos || [];
    albumName.value = detail.albumName;
  }
  loading.value = false;
};

// 获取所有相册（用于侧边栏切换）
const getAllAlbums = async () => {
  const res = await PhotoService.getAllAlbum();
  if (res.code === 200) {
    photoAlbumList.value = res.result || [];
  }
};

// 切换相册
const toggleAlbum = (item: Album) => {
  router.push({
    path: "/photos",
    query: {
      id: String(item.id),
      pageTitle: item.albumName,
    },
  });
};

const openDrawer = () => {
  drawerShow.value = true;
};

const getFrontBackground = async () => {
  const res = await ConfigService.getFrontBackground(userStore.getUserInfo.id || 1);
  if (res.code === 200) {
    bgUrl.value = res.result.frontHeadBackground;
  }
};

watch(
  () => route.query.id as string,
  (newV) => {
    if (newV) {
      getAlbumDetail(Number(newV));
    }
  },
  { immediate: true }
);

onMounted(() => {
  getFrontBackground();
  getAllAlbums();
});
</script>

<template>
  <PageHeader :bgUrl="bgUrl" />
  <div class="photoList">
    <div class="center_box">
      <div class="photoList-card">
        <div class="album-title" v-if="albumName">
          <h3>{{ albumName }}</h3>
        </div>
        <el-empty v-if="!loading && photoList.length === 0" description="暂无照片" />
        <el-row v-else-if="loading" class="row-space">
          <el-col class="col-space" :xs="12" :sm="6" v-for="index in 6" :key="index">
            <div class="image-box">
              <el-skeleton animated>
                <template #template>
                  <SkeletonItem variant="image" width="100%" height="10rem" />
                </template>
              </el-skeleton>
            </div>
          </el-col>
        </el-row>
        <el-row v-else class="row-space">
          <el-col
            class="col-space"
            :xs="12"
            :sm="6"
            v-for="(item, index) in photoList"
            :key="item.imageId"
          >
            <div class="image-box flex_center animate__animated animate__fadeIn">
              <el-image
                class="image"
                :src="getFullUrl(item.filePath)"
                fit="cover"
                lazy
                preview-teleported
                :initial-index="index"
                :preview-src-list="photoList.map((v) => getFullUrl(v.filePath))"
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
      </div>
    </div>
  </div>
  <div class="affix">
    <i class="iconfont icon-arrowleft" @click="openDrawer"></i>
  </div>
  <el-drawer
    v-model="drawerShow"
    direction="rtl"
    :before-close="() => (drawerShow = false)"
    :append-to-body="true"
    :size="isMobile() ? '30%' : '15%'"
  >
    <div class="image-list">
      <div
        :class="['album-box !mb-[5px]', Number(route.query.id) === item.id ? 'album-current' : '']"
        v-for="item in photoAlbumList"
        :key="item.id"
      >
        <el-image
          class="album-box__image"
          :src="getFullUrl(item.albumCover)"
          fit="cover"
          lazy
          @click="toggleAlbum(item)"
        >
          <template #error>
            <div class="w-[100%] h-[100%] grid place-items-center text-xs text-gray-400">
              {{ item.albumName }}
            </div>
          </template>
        </el-image>
      </div>
    </div>
  </el-drawer>
</template>

<script lang="ts">
import { Picture } from "@element-plus/icons-vue";
export default { components: { Picture } };
</script>

<style lang="scss" scoped>
.photoList {
  .photoList-card {
    min-height: 8rem;
    border-radius: 8px;
    background-color: var(--shadow-button-bg);
  }

  .album-title {
    padding: 10px 15px;
    h3 {
      margin: 0;
      font-size: 1.2rem;
    }
  }

  .image-box {
    width: 100%;
    height: 100%;
    transition: all 0.3s;
    border-radius: 5px;
    overflow: hidden;
    &:hover {
      transform: translateY(-5px);
      filter: saturate(2) drop-shadow(0 0 5px rgba(0, 0, 0, 0.66));
    }
  }

  .image {
    vertical-align: middle;
    cursor: pointer;
    width: 100%;
    object-fit: cover;
    display: grid;
    place-items: center;
  }
}

.row-space {
  padding: 5px !important;
}

.col-space {
  padding: 5px !important;
}

.image-list {
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  flex-wrap: nowrap;
  padding: 3px;
  &::-webkit-scrollbar {
    display: none;
  }
}

.album-current {
  filter: saturate(2);
  border-right: 2px solid #9face6;
}

.affix {
  position: fixed;
  bottom: 30%;
  right: 0%;
  .icon-arrowleft {
    font-size: 1.8rem;
  }
}

@media screen and (min-width: 769px) {
  .image {
    height: 10rem;
  }

  .album-box {
    width: 100px;
    height: 70px;
    &__image {
      width: 94px;
      height: 70px;
      vertical-align: top;
    }
  }
}

@media screen and (max-width: 768px) {
  .image {
    height: 8rem;
  }

  .album-box {
    width: 70px;
    height: 50px;
    &__image {
      width: 64px;
      height: 50px;
      vertical-align: top;
    }
  }
}
</style>
