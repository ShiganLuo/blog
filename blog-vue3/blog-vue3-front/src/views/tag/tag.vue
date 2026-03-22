<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { TagService } from "@/api/blog/tagApi";
import { ConfigService } from "@/api/configApi";
import { type  TagListResponse, type TagItem } from "@/types/blog/tag"
import GsapCount from "@/components/GsapCount/index.vue";
import PageHeader from "@/components/PageHeader/index.vue";


// router
const router = useRouter();
const bgUrl = ref<string>("");

// state
const tagList = ref<TagItem[]>([]);
const total = ref<number>(0);
const loading = ref<boolean>(false);

// 随机字体大小
const randomFontSize = (): number => {
  return Math.random() * 1.4 + 0.6;
};

// 随机字体颜色
const randomFontColor = (): string => {
  return `rgb(${Math.random() * 180 + 30},${Math.random() * 180 + 30},${Math.random() * 180 + 30})`;
};

// 点击跳转文章列表
const goToArticleList = (item: TagItem) => {
  router.push({
    path: "articleList",
    query: { id: String(item.id), type: "tag", name: item.tagName },
  });
};

// 获取标签列表
const getTagList = async () => {
  loading.value = true;
  try {
    const res = await TagService.getAllTag();
    if (res.code === 200) {
      total.value = res.result.length;
      tagList.value = res.result.map((tag) => ({
        id: tag.id,
        tagName: tag.tagName,
        fontSize: randomFontSize(),
        fontColor: randomFontColor(),
      }));
    }
  } finally {
    loading.value = false;
  }
};
const getFrontBackground = async (): Promise<void> => {
  const res = await ConfigService.getFrontBackground();
  if (res.code === 200) {
    bgUrl.value = res.result.frontHeadBackground;
  }
}
// 初始化
onMounted(() => {
  getTagList();
  getFrontBackground();
});
</script>

<template>
  <PageHeader :loading="loading" :bg-url="bgUrl" />
  <div class="tag">
    <el-row class="center_box">
      <el-col :span="24">
        <el-card class="tag-card">
          <div class="tag-total flex_center">
            标签 - <GsapCount :value="total" />
          </div>
          <el-skeleton
            v-if="loading"
            class="tag-item"
            :loading="loading"
            :rows="1"
            animated
          />
          <el-row v-else>
            <el-col :span="24" class="tag-item">
              <span
                v-for="(item, i) in tagList"
                :key="i"
                :style="{ fontSize: item.fontSize + 'rem', color: item.fontColor }"
                class="tag-item__label scale animate__animated animate__fadeInDown"
                @click="goToArticleList(item)"
              >
                {{ item.tagName }}
              </span>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style lang="scss" scoped>
.tag {
  &-card {
    padding: 40px 50px;
  }

  &-total {
    font-size: 2.2rem;
    line-height: 2;
    font-weight: 600;
    color: var(--font-color);
  }

  &-item {
    padding: 10px;
    box-sizing: border-box;
    text-align: center;

    &__label {
      display: inline-block;
      font-weight: bold;
      padding: 0 0.8rem;
    }
  }
}
</style>
