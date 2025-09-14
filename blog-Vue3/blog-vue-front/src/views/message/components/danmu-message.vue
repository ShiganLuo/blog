<script setup lang="ts">
import { ref, onMounted, nextTick, watch } from "vue";
import { getMessageList } from "@/api/message";
import VueDanmaku from "vue3-danmaku";

// 定义弹幕数据类型
interface Danmu {
  avatar: string;
  nick_name: string;
  content: string;
  isNew?: boolean;
}

// vue-danmaku 组件实例类型
interface VueDanmakuInstance {
  add: (danmu: Danmu) => void;
  play: () => void;
}

const vueDanmakuRef = ref<VueDanmakuInstance | null>(null);

// 用于存储从后端获取的原始弹幕数据
const originalDanmus = ref<Danmu[]>([]);

// 用于控制 v-if 渲染的弹幕数组
const currentDanmus = ref<Danmu[]>([]);

const loop = ref(false);

// 添加新弹幕
const addDanmu = (danmu: Danmu) => {
  if (vueDanmakuRef.value) {
    vueDanmakuRef.value.add(danmu);
  }
};

// 加载并播放弹幕
const loadAndPlayDanmu = async () => {
  try {
    const res: any = await getMessageList({
      current: 1,
      size: 200,
      type: "message",
    });
    const data = res.result.list as Danmu[];
    
    originalDanmus.value = data; // 存储原始数据
    currentDanmus.value = data; // 用于初始渲染

    if (originalDanmus.value.length > 100) {
      loop.value = true;
    }

    // Vue 会根据 currentDanmus 的变化来渲染组件
    // 无需手动调用 play()，因为 isSuspend="true" 的特性
    // 而是通过 watch 监听 currentDanmus，当它变化时自动播放
    // 这种模式更健壮
  } catch (error) {
    console.error("Failed to load messages:", error);
  }
};

// 监听弹幕数据，当数据加载后自动播放
watch(
  () => currentDanmus.value,
  (newVal) => {
    if (newVal.length > 0) {
      nextTick(() => {
        if (vueDanmakuRef.value) {
          vueDanmakuRef.value.play();
        }
      });
    }
  },
  { immediate: true }
);

// 当弹幕播放结束时，重新加载并播放
const handlePlayEnd = () => {
  // 清空 currentDanmus，触发组件销毁
  currentDanmus.value = [];
  
  // 使用 nextTick 确保组件完全销毁后再重新赋值
  nextTick(() => {
    // 重新赋值，触发组件重建和播放
    currentDanmus.value = [...originalDanmus.value];
  });
};

onMounted(() => {
  loadAndPlayDanmu();
});

defineExpose({
  addDanmu,
  init: loadAndPlayDanmu,
});
</script>

<template>
  <vue-danmaku
    v-if="currentDanmus.length"
    ref="vueDanmakuRef"
    class="danmaku-container"
    v-model:danmus="currentDanmus"
    :loop="loop"
    useSlot
    :speeds="60"
    :isSuspend="true"
    @play-end="handlePlayEnd"
  >
    <template #dm="{ danmu }">
      <div class="danmu-item">
        <el-avatar :src="danmu.avatar">{{ danmu.nick_name }}</el-avatar> :
        <span class="new-item" v-if="danmu.isNew">{{ danmu.content }}</span>
        <span v-else>{{ danmu.content }}</span>
      </div>
    </template>
  </vue-danmaku>
  <div class="shooting_stars !z-[1]">
    <div class="night">
      <span v-for="i in 20" :key="i" class="shooting_star"></span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use "sass:math";
.danmaku-container {
  position: absolute !important;
  top: 60px !important;
  left: 0;
  width: 100%;
  z-index: 2 !important;
  height: calc(100vh - 60px);
}

.danmu-item {
  display: flex;
  align-items: center;
  font-size: 20px;
  color: #fff;

  .new-item {
    position: relative;
    display: inline-block;
    padding: 3px 60px 3px 3px;
    border-radius: 8px;

    &::after {
      content: "new";
      position: absolute;
      top: -5px;
      right: 0;
      color: #fff;
      background: #ff1900;
      padding: 2px 3px;
      border-radius: 8px;
    }
  }
}

.shooting_stars {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  background: radial-gradient(ellipse at bottom, #1b2735 0%, #090a0f 100%);
  height: 100vh;
  overflow: hidden;
  display: flex;
  font-family: "Anton", sans-serif;
  justify-content: center;
  align-items: center;
}

.night {
  position: relative;
  width: 100%;
  height: 100%;
  transform: rotateZ(45deg);
}

.shooting_star {
  position: absolute;
  left: 10%;
  top: 20%;
  height: 2px;
  background: linear-gradient(-45deg, rgba(95, 145, 255, 1), rgba(0, 0, 255, 0));
  border-radius: 999px;
  filter: drop-shadow(0 0 6px rgba(105, 155, 255, 1));
  animation:
    tail 5s ease-in-out infinite,
    shooting 5s ease-in-out infinite;

  &::before {
    content: "";
    position: absolute;
    top: calc(50% - 1px);
    right: 0;
    height: 2px;
    background: linear-gradient(
      -45deg,
      rgba(0, 0, 255, 0),
      rgba(95, 145, 255, 1),
      rgba(0, 0, 255, 0)
    );
    transform: translateX(50%) rotateZ(45deg);
    border-radius: 100%;
    animation: shining 3000 ease-in-out infinite;
  }

  &::after {
    content: "";
    position: absolute;
    top: calc(50% - 1px);
    right: 0;
    height: 2px;
    background: linear-gradient(
      -45deg,
      rgba(0, 0, 255, 0),
      rgba(95, 145, 255, 1),
      rgba(0, 0, 255, 0)
    );
    transform: translateX(50%) rotateZ(45deg);
    border-radius: 100%;
    animation: shining 3000 ease-in-out infinite;
    transform: translateX(50%) rotateZ(-45deg);
  }

  @for $i from 1 through 20 {
    &:nth-child(#{$i}) {
      $delay: math.random(9999) + 0ms;
      top: calc(50% - #{math.random(400) - 200px});
      left: calc(30% - #{math.random(300) + 0px});
      animation-delay: $delay;
      // opacity: math.random(50) / 100 + 0.5;

      &::before,
      &::after {
        animation-delay: $delay;
      }
    }
  }
}

@keyframes tail {
  0% {
    width: 0;
  }

  30% {
    width: 100px;
  }

  100% {
    width: 0;
  }
}

@keyframes shining {
  0% {
    width: 0;
  }

  50% {
    width: 30px;
  }

  100% {
    width: 0;
  }
}

@keyframes shooting {
  0% {
    transform: translateX(0);
  }

  100% {
    transform: translateX(800px);
  }
}

@keyframes sky {
  0% {
    transform: rotate(45deg);
  }

  100% {
    transform: rotate(45 + 360deg);
  }
}
</style>