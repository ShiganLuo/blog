<script lang="ts" setup>
import { ref, watch, onBeforeUnmount, nextTick } from "vue";

interface LoopItem {
  target: string;
  delay: number;
}

const props = defineProps<{
  typeList: string[];
  size: string;
  timeSpace: number;
  wordPrintTime: number;
  color: string;
}>();

const loopList = ref<LoopItem[]>([]);
const arr: ReturnType<typeof setTimeout>[] = [];
const interArr: (ReturnType<typeof setInterval> | null)[] = [];

const reset = () => {
  arr.length &&
    arr.forEach((a) => {
      if (a) clearTimeout(a);
    });

  interArr.length &&
    interArr.forEach((a) => {
      if (a) clearInterval(a);
    });

  // 清空数组内容
  arr.length = 0;
  interArr.length = 0;
  loopList.value = [];
};

watch(
  () => props.typeList,
  () => {
    nextTick(() => {
      reset();

      if (!props.typeList.length) return;

      let lastTime = 0;

      props.typeList.forEach((v, index) => {
        if (!v.length) {
          console.error(`第${index + 1}条语句为空，不能打印`);
          return;
        }

        const loop: LoopItem = {
          target: v,
          delay: lastTime,
        };
        loopList.value.push(loop);

        // 计算下一句的延迟时间
        lastTime = Math.round((lastTime + v.length * props.wordPrintTime + props.timeSpace) * 10) / 10;
      });

      loopList.value.forEach((loop) => {
        const timer = setTimeout(() => {
          const writers = document.getElementById("writer");
          if (!writers) return;

          let num = 0;
          let str = "";

          let interTimer = setInterval(() => {
            str += loop.target.charAt(num);
            writers.innerHTML = str;

            if (num < loop.target.length) {
              num++;
            } else {
              if (interTimer) clearInterval(interTimer);
            }
          }, props.wordPrintTime * 1000);

          interArr.push(interTimer);
        }, loop.delay * 1000);

        arr.push(timer);
      });
    });
  },
  {
    deep: true,
    immediate: true,
  }
);

onBeforeUnmount(() => {
  reset();
});
</script>

<template>
  <div :style="{ color: props.color }" class="type-writer">
    <span id="writer" :style="{ fontSize: props.size }"></span>
    <span class="space" :style="{ fontSize: props.size }">|</span>
  </div>
</template>

<style lang="scss" scoped>
.type-writer {
  width: 100%;
  color: var(--global-white);
  font-size: 1em;
  cursor: pointer;
  text-align: center;
}

.space {
  vertical-align: text-bottom;
  animation: showInfinite 0.8s infinite both;
}

@keyframes showInfinite {
  0% {
    opacity: 1;
  }

  100% {
    opacity: 0;
  }
}
</style>
