<script setup lang="ts">
import { onMounted, ref } from "vue";
import { storeToRefs } from "pinia";
import type { Ref } from "vue";

import { _getLocalItem } from "@/utils/tool";
import { useStaticData } from "@/stores/index";

import { Moon, Sunrise } from "@element-plus/icons-vue";

// Define theme type for better type safety
type ThemeMode = "light" | "dark";

const staticStore = useStaticData();
const { mainTheme } = storeToRefs(staticStore);
const currentTheme: Ref<boolean> = ref(false);

onMounted(() => {
  const th = _getLocalItem("mainTheme") as ThemeMode | null;
  
  // Use cached theme if exists
  if (th) {
    const nowTh: ThemeMode = mainTheme.value ? "dark" : "light";
    if (th !== nowTh) {
      changeSwitch();
    }
  } else {
    const now = new Date().getHours();
    // Determine if it's day or night
    if (now >= 8 && now <= 18) {
      // If daytime, switch to light theme if currently dark
      if (mainTheme.value) {
        changeSwitch();
      }
    } else {
      // If nighttime, switch to dark theme if currently light
      if (!mainTheme.value) {
        changeSwitch();
      }
    }
  }
  currentTheme.value = mainTheme.value;
});

const changeSwitch = (): void => {
  staticStore.switchMainTheme();
  // Sync the current theme after switch
  currentTheme.value = mainTheme.value;
};
</script>

<template>
  <el-switch
    v-model="currentTheme"
    size="default"
    :active-icon="Sunrise"
    inline-prompt
    :inactive-icon="Moon"
    @change="changeSwitch"
  />
</template>

<style lang="scss" scoped></style>