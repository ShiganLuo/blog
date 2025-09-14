<script lang="ts" setup>
interface Props {
  icon?: string;
  title?: string;
  size?: string;
  color?: string;
}

const props = withDefaults(defineProps<Props>(), {
  icon: "-",
  title: "-",
  size: "1.2rem",
  color: "#676767",
});
</script>

<template>
  <div class="card-title">
    <i 
      :class="['iconfont', props.icon]" 
      :style="{ 
        fontSize: props.size, 
        color: props.color 
      }"
    ></i>
    <span>{{ props.title }}</span>
  </div>
  <slot></slot>
</template>

<style lang="scss" scoped>
.card-title {
  font-size: 1.2rem;
  line-height: 2.4;
  
  .iconfont {
    font-weight: 900;
  }
  
  span {
    margin-left: 0.3rem;
  }
}
</style>