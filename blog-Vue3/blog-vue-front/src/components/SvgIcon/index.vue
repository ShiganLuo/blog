<script setup lang="ts">
import { watch, reactive } from 'vue';

interface Props {
  name?: string;
  customClass?: string;
  width?: string | number;
  height?: string | number;
  hidden?: boolean;
}

const props = defineProps<Props>();

interface SvgProps {
  width: string;
  height: string;
  customClass: string;
  name: string;
}

const svgProps = reactive<SvgProps>({
  width: '',
  height: '',
  customClass: '',
  name: ''
});

watch(
  () => props,
  () => {
    const widthStr = String(props.width ?? '3rem');
    const heightStr = String(props.height ?? '3rem');

    const isPureNumber = (val: string) => /^[\d.]+$/.test(val);

    svgProps.width = isPureNumber(widthStr) ? `${widthStr}rem` : widthStr;
    svgProps.height = isPureNumber(heightStr) ? `${heightStr}rem` : heightStr;

    svgProps.name = props.name ? `#icon-${props.name}` : '#icon';
    svgProps.customClass = props.customClass
      ? `svg-icon ${props.customClass}`
      : 'svg-icon';
  },
  {
    immediate: true,
    deep: true
  }
);
</script>

<template>
  <svg
    :class="svgProps.customClass"
    :style="`width: ${svgProps.width}; height: ${svgProps.width || svgProps.height};`"
    :aria-hidden="props.hidden ?? true"
  >
    <use :xlink:href="svgProps.name"></use>
  </svg>
</template>

<style scoped>
.svg-icon {
  vertical-align: -0.15rem;
  fill: currentColor;
  overflow: hidden;
}
</style>
