<script setup lang="ts">
import { nextTick, reactive, ref, watch, onMounted } from "vue";
import gsap from "gsap";
import { ScrollTrigger } from "gsap/dist/ScrollTrigger";

// 定义 props 类型
interface Props {
  value?: number;
  duration?: number;
}

// 使用类型定义 props
const props = withDefaults(defineProps<Props>(), {
  value: 0,
  duration: 1
});

// 定义响应式变量类型
const id = ref<string | null>(null);

// 定义 reactive 对象类型
interface NumData {
  num: number;
}

const d = reactive<NumData>({
  num: 0
});

// 实现动画的方法
function animateToValue(): void {
  // 在执行动画前，确保id和DOM元素都已准备好
  if (!id.value) return;

  const targetSelector = `.num-${id.value}`;
  const targetElement = document.querySelector(targetSelector);

  if (targetElement) {
    // 确保 ScrollTrigger 只注册一次
    gsap.registerPlugin(ScrollTrigger);

    gsap.to(d, {
      scrollTrigger: targetSelector,
      duration: props.duration,
      num: props.value,
      onUpdate: () => {
        d.num = Math.floor(d.num);
      }
    });
  }
}

// 使用 watch 监听 props.value 的变化
watch(
  () => props.value,
  () => {
    // 在 props.value 变化时，如果 id 已经存在，则执行动画
    if (id.value) {
      nextTick(() => {
        animateToValue();
      });
    }
  },
  { immediate: true } // 这里的 immediate 是安全的
);

onMounted(() => {
  // 在组件挂载时生成唯一的id
  id.value = Math.random().toString(16).slice(2);

  // 确保在 id 和 DOM 渲染完成后执行动画
  nextTick(() => {
    animateToValue();
  });
});

</script>

<template>
  <span :class="'num-' + id">
    {{ Math.floor(d.num) }}
  </span>
</template>