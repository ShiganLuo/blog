<script setup lang="ts">
import { ref, computed, nextTick, watch } from "vue";

interface Props {
  text?: string;
  maxLines?: number;
  width?: number;
  color?: string;
  fontSize?: number;
}

const props = defineProps<Props>();

const expanded = ref(false);
const showSlotNode = ref(false);

const textOverflowRef = ref<HTMLElement | null>(null);
const slotRef = ref<HTMLElement | null>(null);
const overEllipsisRef = ref<HTMLElement | null>(null);

const boxStyle = computed(() => {
  return {
    color: props.color ?? "",
    fontSize: (props.fontSize ?? 14) + "px",
    whiteSpace: "pre-line",
    overflow: "hidden",
    textOverflow: "ellipsis",
    display: "-webkit-box",
    WebkitBoxOrient: "vertical",
    WebkitLineClamp: expanded.value ? "unset" : (props.maxLines ?? 3),
  } as const;
});

function calcIsOverflow() {
  if (!overEllipsisRef.value) return;
  showSlotNode.value = overEllipsisRef.value.scrollHeight > overEllipsisRef.value.clientHeight;
}

function toggle() {
  expanded.value = !expanded.value;
}

watch(
  () => props.text,
  () => {
    nextTick(() => {
      calcIsOverflow();
    });
  },
  { immediate: true }
);
</script>

<template>
  <div
    class="text-box"
    ref="textOverflowRef"
    :style="{ marginBottom: showSlotNode ? '25px' : '0' }"
  >
    <span
      class="keep"
      ref="overEllipsisRef"
      :title="props.text"
      :style="boxStyle"
    >
      {{ props.text }}
    </span>

    <span class="slot-box" ref="slotRef" v-if="showSlotNode">
      <slot :click-toggle="toggle" :expanded="expanded" />
    </span>
  </div>
</template>

<style lang="scss" scoped>
.text-box {
  position: relative;
  margin-bottom: 25px;
}

.slot-box {
  display: inline-block;
  position: absolute;
  left: 0;
  bottom: -25px;
}
</style>
