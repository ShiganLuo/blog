<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { ArticleService } from "@/api/blog/articleApi";
import { type Article, type ArchiveGroup } from "@/types/blog/article"
import PageHeader from "@/components/PageHeader/index.vue";
import TimeLine from "@/components/TimeLine/index.vue";

// 归档数据
const archives = ref<ArchiveGroup[]>([]);

// 分页参数
const param = reactive({
  current: 1, // 当前页
  size: 5,    // 每页条数
});

// 总数
const archivesTotal = ref<number>(0);

// 加载状态
const loading = ref<boolean>(false);

// 分页事件
const pagination = (page: { current: number }) => {
  param.current = page.current;
  getArchives();
};

function groupArticlesByYear(articles: Article[]): ArchiveGroup[] {
  const map = new Map<number, Article[]>();

  articles.forEach(article => {
    const year = new Date(article.createdAt).getFullYear();
    if (!map.has(year)) {
      map.set(year, []);
    }
    map.get(year)!.push(article);
  });

  // 转成 ArchiveGroup[] 并按年份倒序
  return Array.from(map.entries())
    .map(([year, articleList]) => ({ year, articleList }))
    .sort((a, b) => b.year - a.year);
}

// 获取时间轴数据
const getArchives = async () => {
  try {
    loading.value = true;
    const res = await ArticleService.blogTimelineGetArticleList(param);
    if (res.code === 200) {
      const { total, list } = res.result;
      archives.value = groupArticlesByYear(list);
      archivesTotal.value = total;
    }
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  getArchives();
});
</script>

<template>
  <PageHeader :loading="loading" />
  <div class="archives">
    <el-row class="center_box">
      <el-col :span="24">
        <el-card class="archives-card">
          <TimeLine
            :archives="archives"
            :total="archivesTotal"
            :loading="loading"
            :param="param"
            @pagination="pagination"
          />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style lang="scss" scoped>
.archives {
  &-card {
    padding: 40px 50px;
  }
}
</style>
