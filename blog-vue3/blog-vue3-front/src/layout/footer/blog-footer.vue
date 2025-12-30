<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import { ConfigService } from "@/api/configApi";
const route = useRoute();
const icpFillingNumber = ref('');
const websiteChinseName = ref('')

const getICPFillingNumber = async (): Promise<void> =>{
  const res = await ConfigService.getSomeFrontInformation();
  if (res.code == 200) {
    icpFillingNumber.value = res.result.icpFillingNumber;
    websiteChinseName.value = res.result.websiteChineseName
  }
}
onMounted(async () => {
  await getICPFillingNumber();
});
</script>

<template>
  <div
    v-if="route.path !== '/message/chat'" class="footer_box"
  >
    <!-- eslint-disable-next-line -->
    <div class="footer-color">&copy {{ websiteChinseName }} 2025 ICP备案号 {{ icpFillingNumber }}</div>
    <div class="footer-color m-5px flex-wrap">
      <a class="p-3px" href="https://imzbf.github.io/md-editor-v3/docs/index" target="_blank">
        <img
          src="https://img.shields.io/badge/MdEditorV3-MD%E7%BC%96%E8%BE%91%E5%99%A8-159957"
          alt=""
      /></a>
      <a class="p-3px" href="https://butterfly.js.org/" target="_blank">
        <img src="https://img.shields.io/badge/HEXO-BUTTERFLY-blue" alt="" />
      </a>
      <a class="p-3px" href="https://min.io/" target="_blank">
        <img
          src="https://img.shields.io/badge/minio-%E5%AF%B9%E8%B1%A1%E5%AD%98%E5%82%A8%E7%B3%BB%E7%BB%9F-purple
"
          alt=""
        />
      </a>
    </div>
  </div>
</template>



<style lang="scss" scoped>
.footer-color {
  color: var(--font-color);
  text-align: center;
}
.footer_box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  width: 100%;
  z-index: 2009;
  height: 10rem;
  overflow: hidden;
  backdrop-filter: saturate(100%) blur(3px);
  overflow-x: hidden;
  box-sizing: border-box;
}
.m-5px {
  margin-top: 5px;
}
.p-3px {
  padding: 3px;
}
.flex-wrap {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
}
.change-color {
  text-decoration: none;
  transition: all 0.3s;

  &:hover {
    color: var(--global-black);
  }
}
</style>
