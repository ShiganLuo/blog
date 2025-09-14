<script setup lang="ts">
import { useRouter } from "vue-router";
import { ref, onBeforeUnmount } from "vue";
import SvgIcon from "@/components/SvgIcon/index.vue";
const router = useRouter();

// type 可以是 'back' 或 undefined（当直接点击“返回首页”时）
const goBack = (arg?: MouseEvent | 'back') => {
  if (arg === 'back') {
    router.go(-1);
  } else {
    router.push('/');
  }
};

// ref<number> 明确类型
const time = ref<number>(0);

// 在 Node 和浏览器中 setInterval 返回值类型不同，这里用 ReturnType 处理
const timer: ReturnType<typeof setInterval> = setInterval(() => {
  time.value++;
  if (time.value >= 5) {
    goBack();
    clearInterval(timer);
  }
}, 1000);

onBeforeUnmount(() => {
  clearInterval(timer);
});
</script>

<template>
  <div class="not-found">
    <div class="flex items-center">
      <el-button type="primary" size="small" @click="goBack('back')">返回上一页</el-button>
      <span
        class="!ml-[5px] cursor-pointer text-sm hover:text-primary-blue"
        @click="goBack"
      >返回首页</span>
    </div>
    <p class="!mt-[5px]">{{ 5 - time }} 秒后自动返回首页......</p>

    <svg-icon name="404" :width="30" :height="30"></svg-icon>
  </div>
</template>

<style lang="scss" scoped>
.not-found {
  height: calc(100vh - 140px);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
</style>
