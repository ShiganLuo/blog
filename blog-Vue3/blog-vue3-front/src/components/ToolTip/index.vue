<script lang="ts" setup>
import { ref } from "vue";

// Define props with type annotations
const props = defineProps({
  name: {
    type: String,
    default: "",
  },
  width: {
    type: [String, Number],
    default: "100%",
  },
  size: {
    type: [String, Number],
    default: "1rem",
  },
  color: {
    type: String,
    default: "#676767",
  },
  weight: {
    type: [String, Number],
    default: "400",
  },
  align: {
    type: String,
    default: "left",
  },
  lineHeight: {
    type: Number,
    default: 1,
  },
});

// Tooltip state
const tooltipDisabled = ref<boolean>(true);

// Mouse enter event handler
const onMouseEnter = (): void => {
  const nameNode = document.querySelector<HTMLElement>(`[data-name="${props.name}"]`);
  if (nameNode) {
    // If the element's scrollWidth is greater than offsetWidth, show the tooltip
    tooltipDisabled.value = nameNode.offsetWidth >= nameNode.scrollWidth;
  }
};
</script>

<template>
    <el-tooltip :content="name" placement="top" effect="light" :disabled="tooltipDisabled">
      <span
        class="tooltip-text-overflow animate__animated animate__fadeIn"
        :style="`width: ${width}; font-size: ${size}; color: ${color}; font-weight: ${weight}; text-align: ${align}; line-height: ${lineHeight}`"
        :data-name="name"
        @mouseenter="onMouseEnter"
      >
        {{ name }}
      </span>
    </el-tooltip>
  </template>
  

  
  <style lang="scss" scoped>
  .tooltip-text-overflow {
    display: inline-block;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    cursor: pointer;
  }
  </style>
  