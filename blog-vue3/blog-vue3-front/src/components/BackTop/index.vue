<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, reactive, watch, computed } from "vue";
import { storeToRefs } from "pinia";
import { useStaticData } from "@/stores/index";
import { debounce } from "@/utils/tool";
import SvgIcon from "@/components/SvgIcon/index.vue";

const { mainTheme } = storeToRefs(useStaticData());
const isFirst = ref(true);

interface Props {
  bottom?: string | number;
  right?: string | number;
  svgName?: string;
  svgWidth?: number;
  animation?: boolean;
  rotateDeg?: number;
}

const props = defineProps<Props>();

const backTopProps = reactive({
  bottom: "",
  right: "",
  width: 3,
});

watch(
  () => props,
  () => {
    backTopProps.bottom = /^[\d|.]*$/g.test(String(props.bottom))
      ? props.bottom + "rem"
      : String(props.bottom);
    backTopProps.right = /^[\d|.]*$/g.test(String(props.right))
      ? props.right + "rem"
      : String(props.right);
  },
  {
    immediate: true,
    deep: true,
  }
);

const svgThemeName = computed(() => {
  return mainTheme.value ? "dark" + props.svgName : "light" + props.svgName;
});

const backTopShow = ref(false);

const scroll = debounce(() => {
  const scrollTop =
    window.scrollY || document.documentElement.scrollTop || document.body.scrollTop;
  if (scrollTop > 200) {
    backTopShow.value = true;
    isFirst.value = false;
  } else {
    backTopShow.value = false;
  }
}, 10);

const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
};

onMounted(() => {
  window.addEventListener("scroll", scroll);
});

onBeforeUnmount(() => {
  window.removeEventListener("scroll", scroll);
});
</script>

<template>
  <div
    v-if="!isFirst"
    :class="[backTopShow ? 'back-top-show' : 'back-top-hidden', 'back-top']"
    :style="`bottom: ${backTopProps.bottom};right:${backTopProps.right};`"
  >
    <svg-icon
      :style="{ transform: `rotateZ(${props.rotateDeg}deg)` }"
      :name="svgThemeName"
      :width="props.svgWidth"
      @click="scrollToTop"
    />
  </div>
</template>

<style lang="scss" scoped>
.back-top {
  position: fixed;
  overflow: hidden;
  transition: all ease-in-out 0.3s;
  &-show {
    animation: show 0.8s ease-in-out forwards;
  }
  &-hidden {
    animation: hide 0.8s ease-in-out forwards;
  }
}
@keyframes show {
  0% {
    transform: translateX(0);
    transform: translateY(100px);
    opacity: 0;
  }
  80% {
    transform: translateY(0px);
    opacity: 1;
  }
}
@keyframes hide {
  0% {
    opacity: 1;
    transform: translateX(0px);
  }
  80% {
    opacity: 0;
    transform: translateY(300px);
  }
  100% {
    transform: translateX(300px);
  }
}
</style>
