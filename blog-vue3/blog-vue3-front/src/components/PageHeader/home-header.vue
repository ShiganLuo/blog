<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, computed, type Ref } from "vue";
import { useRoute } from "vue-router";
import { HomeService } from "@/api/homeApi";
import { useStaticData } from "@/stores/index";
import TypeWriter from "@/components/TypeWriter/index.vue";
import Waves from "@/components/WelcomeComps/waves.vue";
import { debounce } from "@/utils/tool";
import gsap from "gsap";
import { ScrollTrigger } from "gsap/dist/ScrollTrigger";
import { storeToRefs } from "pinia";
import { ConfigService } from "@/api/configApi";

// API 返回类型
interface SentenceResponse {
  success: boolean;
  data: {
    content: string;
  };
}


// 页面头图类型
interface PageHeader {
  route_name: string;
  bgUrl: string;
}
const websiteChineseName = ref('')
const route = useRoute();
const staticStore = useStaticData();
const { getPageHeaderList } = storeToRefs(staticStore);

const saying: Ref<string[]> = ref([]);
const showScrollBottom = ref(true);

gsap.registerPlugin(ScrollTrigger);

const scrollToBottom = () => {
  const homeElement = document.querySelector("#home") as HTMLElement | null;

  if (homeElement) {
    window.scrollTo({
      top: homeElement.offsetHeight - 90,
      behavior: "smooth",
    });
  }
};

const scrollListener = debounce(() => {
  showScrollBottom.value = document.documentElement.scrollTop <= 50;
}, 50);

// https://github.com/vv314/quotes
const initOneSentence = async () => {

    const res = await HomeService.getOneStentence();
    if (res.code === 200) {
      saying.value = [res.result];
    }
    else {
      saying.value = ["今天也要元气满满"];
    }
};

const initScrollEvent = () => {
  window.addEventListener("scroll", scrollListener);
};

const getBgCover = computed(() => {
  const bgList: PageHeader[] = getPageHeaderList.value;
  const defaultUrl = "http://img.mrzym.top/FvmVKfygxBKoJbFVXJwzjgAASL9S";
  const index = bgList.findIndex((bg) => bg.route_name === route.name);
  return index === -1 ? defaultUrl : bgList[index].bgUrl;
});
const getWebsiteChineseName = async () => {
  const res = await ConfigService.getSomeFrontInformation();
  if (res.code == 200) {
    websiteChineseName.value = res.result.websiteChineseName
  }
}
onMounted(() => {
  initOneSentence();
  initScrollEvent();
  getWebsiteChineseName();
  gsap.to(".bg", {
    scrollTrigger: {
      trigger: "#home",
      scrub: true,
      start: "top top",
      end: "bottom",
    },
    ease: "power1.inOut",
    scale: 1.3,
  });
});

onBeforeUnmount(() => {
  window.removeEventListener("scroll", scrollListener);
});
</script>

<template>
  <div id="home">
    <el-image class="bg full-size" fit="cover" :src="getBgCover"></el-image>
    <div class="font">{{ websiteChineseName }}</div>
    <TypeWriter class="type-writer" size="1.2em" :typeList="saying" :timeSpace="200" :wordPrintTime="100" color="#ffffff"/>
    <Waves />
    <div v-if="showScrollBottom" class="scroll-bottom">
      <i @click="scrollToBottom" class="iconfont icon-arrowdown"></i>
    </div>
  </div>
</template>

<style lang="scss" scoped>
#home {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  background: var(--home-bg);
  background-position: center center;
  background-size: cover;
  background-repeat: no-repeat;

  .bg {
    z-index: 1;
  }

  .full-size {
  width: 100% !important;
  height: 100% !important;
  }

  &::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    display: block;
    width: 100%;
    height: 100%;
    z-index: 2;
    background-color: var(--home-mask-color);
  }

  .scroll-bottom {
    position: absolute;
    cursor: pointer;
    bottom: 70px;
    left: 50%;
    color: var(--menu-color);
    transform: translate(-50%, -50%);
    z-index: 1000;

    .icon-arrowdown {
      display: inline-block;
      font-size: 1.5em;
      animation: bounce 2s infinite;
    }
  }
}

.font {
  position: absolute;
  top: 45%;
  left: 50%;
  z-index: 999;
  transform: translate(-50%, -50%);
  font-size: clamp(1em, 4vmin, 20em);
  color: var(--global-white);
  padding: 0.5rem;
  cursor: pointer;
}
.font:hover {
  animation: anime 0.5s cubic-bezier(0.445, 0.05, 0.55, 0.95) alternate forwards;
}

@keyframes anime {
  from {
    font-variation-settings:
      "wght" 300,
      "slnt" 15;
    text-shadow: none;
  }
  to {
    font-variation-settings:
      "wght" 800,
      "slnt" 0;
    text-shadow:
      1px 1px 0px #00e6e6,
      2px 2px 0px #01cccc,
      5px 5px 5px #dda121;
  }
}

@keyframes bounce {
  0%,
  100% {
    transform: translateY(20px);
    opacity: 0.4;
  }
  50% {
    transform: translateY(0);
    opacity: 1;
  }
}

.type-writer {
  position: absolute;
  top: 60%;
  left: 50%;
  z-index: 999;
  transform: translate(-50%, -50%);
}
</style>
