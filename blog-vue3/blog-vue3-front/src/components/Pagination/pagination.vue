<template>
  <div class="pagination">
    <el-pagination
      background
      :layout="layout"
      :pager-count="pagerCount"
      :page-sizes="pageSizes"
      :page-size="size"
      :total="total"
      :current-page="current"
      @size-change="sizeChange"
      @current-change="currentChange"
      @prev-click="prev"
      @next-click="next"
    ></el-pagination>
  </div>
</template>

<script setup lang="ts">
import { reactive } from "vue";

// 定义 props 类型
interface Props {
  total?: number;
  pagerCount?: number;
  layout?: string;
  pageSizes?: number[];
  current?: number;
  size?: number;
}

// 使用类型定义 props
const props = withDefaults(defineProps<Props>(), {
  total: 0,
  pagerCount: 5,
  layout: "total,sizes, prev, pager, next, jumper, ->, slot",
  pageSizes: () => [10, 20, 50],
  current: 1,
  size: 10  // 修正默认值为10更合理
});

// 定义响应式页面状态
interface PageState {
  size: number;
  current: number;
}

const page = reactive<PageState>({
  size: props.size,
  current: props.current
});

// 定义 emits 类型
const emit = defineEmits<{
  (event: "pagination", payload: PageState): void;
}>();

// 选择每页显示数量
const sizeChange = (val: number): void => {
  page.size = val;
  emit("pagination", page);
};

// 选择某一页
const currentChange = (val: number): void => {
  page.current = val;
  emit("pagination", page);
};

// 上一页
const prev = (val: number): void => {
  page.current = val - 1;
  emit("pagination", page);
};

// 下一页
const next = (val: number): void => {
  page.current = val + 1;
  emit("pagination", page);
};
</script>

<style lang="scss" scoped>
.pagination {
  width: 100%;
  padding: 10px 0;
  text-align: center;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
}

:deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background-color: #00c4b6;
  color: var(--white);
  cursor: default;
  font-weight: 800;
}

:deep(.el-pager li) {
  font-weight: 600;
}
</style>