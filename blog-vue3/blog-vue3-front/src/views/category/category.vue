<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { CategoryService } from "@/api/blog/categoryApi";
import { type Category, type CategoryResponse } from "@/types/blog/category";
import GsapCount from "@/components/GsapCount/index.vue";
import PageHeader from "@/components/PageHeader/index.vue";

const router = useRouter();

// ref 指定类型
const categoryList = ref<Category[]>([]);
const loading = ref<boolean>(false);

const randomFontColor = (): string => {
  return `rgb(${Math.random() * 180 + 30},${Math.random() * 180 + 30},${Math.random() * 180 + 30})`;
};

const randomFontSize = (): number => {
  return Math.random() * 1.6 + 0.6;
};

const getCategoryList = async (): Promise<void> => {
  loading.value = true;
  const res = await CategoryService.getAllCategory();
  if (res.code === 200 && Array.isArray(res.result)) {
    categoryList.value = (res.result).map((r) => {
      return {
        id: r.id,
        categoryName: r.categoryName,
        fontSize: randomFontSize(),
        fontColor: randomFontColor(),
      };
    });
  }
  loading.value = false;
};

onMounted(() => {
  getCategoryList();
});

const goToArticleList = (item: Category): void => {
  router.push({
    path: "/articleList",
    query: { id: item.id, type: "category", name: item.categoryName },
  });
};
</script>

<template>
  <PageHeader :loading="loading" />
  <div class="category center_box">
    <el-card class="category-card">
      <div class="category-total flex_center">
        分类 -
        <GsapCount :value="categoryList.length" />
      </div>
      <el-row v-if="loading">
        <el-skeleton
          v-if="loading"
          class="category-item"
          :loading="loading"
          :rows="1"
          animated
        />
      </el-row>
      <el-row v-else>
        <el-col :span="24" class="category-item">
          <span
            v-for="(item, i) in categoryList"
            :key="i"
            :style="{ fontSize: item.fontSize + 'rem', color: item.fontColor }"
            class="category-item__label scale animate__animated animate__fadeInDown"
            @click="goToArticleList(item)"
          >
            {{ item.categoryName }}
          </span>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<style lang="scss" scoped>
.category {
  &-card {
    padding: 40px 30px;
  }

  &-total {
    font-size: 1.4rem;
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
