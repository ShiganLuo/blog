<script setup lang="ts" name="Message">
import { ref, reactive, onMounted, onBeforeUnmount, h, onActivated, nextTick } from "vue";
import { useRouter } from "vue-router";
import { storeToRefs } from "pinia";
import { Edit, Delete, Search } from "@element-plus/icons-vue";
import { ElNotification, ElMessageBox } from "element-plus";

import { gsapTransXScale } from "@/utils/transform";
import { MessageService } from "@/api/messageApi";
import { LikeService } from "@/api/likeApi";
import { type MessageItem, type MessageItemListResponse } from "@/types/message";
import svgIcon from "@/components/SvgIcon/index.vue";
import Loading from "@/components/Loading/index.vue";
import {
  returnTime,
  _setLocalItem,
  containHTML,
  filterMessage,
  _getLocalItem,
  _removeLocalItem,
} from "@/utils/tool";
import { useUserStore } from "@/stores/index";

const router = useRouter();
const userStore = useUserStore();
const { getUserInfo } = storeToRefs(userStore);

const messageList = ref<MessageItem[]>([]);
const total = ref<number>(0);
const loading = ref<boolean>(false);
const scrollLoading = ref<boolean>(false);
let observe: IntersectionObserver | null = null;
let box: Element | null = null;

// 定义请求参数的类型
interface Params {
  current: number;
  size: number;
  type: string;
  content: string;
  userId?: number | string;
}

const param = reactive<Params>({
  current: 1,
  size: 10,
  type: "message",
  content: "",
  userId: getUserInfo.value.id, // 确保 userId 有默认值
});
const primaryParam = reactive<Params>({ ...param });

// 定义标签项的类型
interface TabItem {
  key: number;
  label: string;
}
const activeTab = ref<number>(0);
const tabList = ref<TabItem[]>([]);
const showSearch = ref<boolean>(false);
const searchTag = ref<string>("");
const likePending = ref<boolean>(false);

const changeTab = (key: number, label: string): void => {
  activeTab.value = key;
  searchTag.value = "";
  param.current = 1;
  pageGetMessageList();
};

const hideSearchInput = (): void => {
  showSearch.value = false;
};

const observeBox = (): void => {
  // 获取要监听的元素，并进行类型断言
  box = document.querySelector(".observer");
  if (box) {
    observe = new IntersectionObserver(
      (entries) => {
        entries.forEach(async (e) => {
          if (e.isIntersecting && e.intersectionRatio > 0) {
            if (total.value > messageList.value.length && !loading.value) {
              param.current++;
              await pageGetMessageList();
            }
          }
        });
      },
      { rootMargin: "0px 0px 300px 0px" }
    );
    observe.observe(box);
  }
};

// --- 新增随机值生成函数 ---
/**
 * @description: 生成随机十六进制颜色
 * @return {string}
 */
const getRandomColor = (): string => {
  return '#' + Math.floor(Math.random() * 0xffffff).toString(16).padEnd(6, '0');
};
/**
 * @description: 生成随机字体大小
 * @return {number}
 */
const getRandomFontSize = (): number => {
  return Math.floor(Math.random() * (22 - 16 + 1)) + 16; // 字体大小范围为 16px 到 22px
};
/**
 * @description: 生成随机字体粗细
 * @return {number}
 */
const getRandomFontWeight = (): number => {
  const weights = [400, 500, 600, 700];
  return weights[Math.floor(Math.random() * weights.length)];
};
// --- 随机值生成函数结束 ---

const pageGetMessageList = async (): Promise<void> => {
  if (param.current === 1) {
    loading.value = true;
  } else {
    scrollLoading.value = true;
  }
  try {
    let res = await MessageService.getMessageList(param);
    if (res.code === 200 && res.result) {
      const { list, sum } = res.result;
      // --- 新增：为每个留言项设置随机值 ---
      list.forEach((item: MessageItem) => {
        item.color = getRandomColor();
        item.font_size = getRandomFontSize();
        item.font_weight = getRandomFontWeight();
      });
      // --- 新增结束 ---

      messageList.value = param.current === 1 ? list : [...messageList.value, ...list];
      let classList = res.result.list.map((item: any, index: number) => {
        return ".message" + (messageList.value.length - list.length + index);
      });
      total.value = sum;
      const elementList = classList
        .map(selector => document.querySelector(selector))
        .filter((el): el is HTMLElement => el !== null);
      nextTick(() => {
        gsapTransXScale(elementList, 0, 1.2);
      });
    }
  } finally {
    loading.value = false;
    scrollLoading.value = false;
  }
};

const like = async (item: MessageItem, index: number): Promise<void> => {
  if (likePending.value) return;
  likePending.value = true;

  if (!getUserInfo.value.id) {
    ElNotification({
      offset: 60,
      title: "温馨提示",
      message: h("div", { style: "color: #e6c081; font-weight: 600;" }, "请先登录"),
    });
    likePending.value = false;
    return;
  }

  try {
    let res;
    // 取消点赞
    if (item.isLiked) {
      res = await LikeService.cancelLike({ for_id: item.id, type: "message", userId: getUserInfo.value.id });
    }
    // 点赞
    else {
      res = await LikeService.addLike({ for_id: item.id, type: "message", userId: getUserInfo.value.id });
    }

    if (res && res.code === 200) {
      if (item.isLiked) {
        messageList.value[index].likes--;
        messageList.value[index].isLiked = false;
        ElNotification({
          offset: 60,
          title: "提示",
          message: h("div", { style: "color: #7ec050; font-weight: 600;" }, "已取消点赞"),
        });
      } else {
        messageList.value[index].likes++;
        messageList.value[index].isLiked = true;
        ElNotification({
          offset: 60,
          title: "提示",
          message: h("div", { style: "color: #7ec050; font-weight: 600;" }, "点赞成功"),
        });
      }
    }
  } finally {
    likePending.value = false;
  }
};

const comment = (item: MessageItem): void => {
  if (item) {
    _setLocalItem("blog-message-item", item);
  }
  router.push({ path: "/message/detail" });
};

const handleEditMessage = (item: MessageItem): void => {
  if (item) {
    _setLocalItem("blog-message-item", item);
  }
  router.push({ path: "/message/publish", query: { type: "edit" } });
};

const handleDeleteMessage = (item: MessageItem): void => {
  ElMessageBox.confirm("确认删除留言吗", "提示", {
    confirmButtonText: "确认",
    cancelButtonText: "取消",
  })
    .then(async () => {
      const res = await MessageService.deleteMessage({ id: item.id });
      if (res && res.code === 200) {
        ElNotification({
          offset: 60,
          title: "提示",
          message: h("div", { style: "color: #7ec050; font-weight: 600;" }, "删除成功"),
        });
        if (observe && box) {
          observe.unobserve(box);
          observe = null;
        }
        param.current = 1;
        await pageGetMessageList();
        nextTick(() => {
          observeBox();
        });
      } else {
        ElNotification({
          offset: 60,
          title: "错误提示",
          message: h("div", { style: "color: #f56c6c; font-weight: 600;" }, res.message),
        });
      }
    })
    .catch(() => { });
};

const getHotMessageTag = async (): Promise<void> => {
  tabList.value = [];
  const res = await MessageService.getMessageTag();
  if (res.code === 200 && res.result) {
    tabList.value = Array.isArray(res.result)
      ? res.result.map((v, i) => ({
        key: i + 1,
        label: v.tag,
      }))
      : [];
    tabList.value.unshift({ key: 0, label: "全部" });
  }
};

const showSearchInput = (): void => {
  showSearch.value = true;
};

const changeSearch = (val: string): void => {
  param.content = val;
  param.current = 1; // 搜索时重置页码
  pageGetMessageList();
};

onMounted(async () => {
  _removeLocalItem("message-refresh");
  await getHotMessageTag();
  await pageGetMessageList();
  if (total.value > messageList.value.length) {
    observeBox();
  }
});

onActivated(async () => {
  if (_getLocalItem("message-refresh")) {
    Object.assign(param, primaryParam);
    await getHotMessageTag();
    await pageGetMessageList();
    _removeLocalItem("message-refresh");
  }

  if (_getLocalItem("message-need-scroll")) {
    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
    _removeLocalItem("message-need-scroll");
  }
});

onBeforeUnmount(() => {
  if (observe && box) {
    observe.unobserve(box);
    observe = null;
  }
});

// 定义 expose 的类型
defineExpose<{
  hideSearchInput: () => void;
}>({
  hideSearchInput,
});
</script>

<template>
  <div class="message-body center_box relative">
    <el-row class="row-height" :gutter="16">
      <el-col :span="24">
        <div class="search-tab" @click.stop="showSearchInput">
          <ul class="tab">
            <li v-for="item in tabList" :key="item.key" @click="changeTab(item.key, item.label)">
              <div :class="[item.key === activeTab ? 'message-active-tab' : '', 'tab-li']">
                {{ item.label }}
              </div>
            </li>
          </ul>
          <Transition
            :duration="{ enter: 0, leave: 500 }"
            enter-active-class="animate__animated animate__fadeIn"
            leave-active-class="animate__animated animate__fadeOut"
          >
            <div v-if="showSearch" class="!py-[5px] flex justify-center items-center">
              <el-input
                :prefix-icon="Search"
                class="search"
                v-model="searchTag"
                placeholder="搜索留言内容"
                @change="changeSearch"
                clearable
              ></el-input>
            </div>
          </Transition>
        </div>
      </el-col>
      <el-skeleton :loading="loading" style="height: 100%" animated>
        <template #template>
          <div class="loading">
            <div class="coffee_cup"></div>
          </div>
        </template>
        <el-col
          :class="'message' + index"
          :xs="24"
          :sm="12"
          v-for="(message, index) in messageList"
          :key="message.id"
        >
          <el-card class="card-hover">
            <div
              class="message-card animate__animated animate__fadeIn"
            >
              <div
                class="top"
              >
                <div class="top-header">
                  <div class="flex items-center">
                    <el-avatar class="left-avatar" :src="message.avatar"
                      >{{ message.nickName }}
                    </el-avatar>
                    <span class="nick-name"> {{ message.nickName }}</span>
                  </div>
                  <div
                    class="flex items-center cursor-pointer option-top"
                    v-if="
                      (getUserInfo.id && getUserInfo.id === message.userId) || getUserInfo.role === 1
                    "
                  >
                    <el-icon @click="handleEditMessage(message)"><Edit /></el-icon>
                    <el-icon class="!ml-[10px]" @click="handleDeleteMessage(message)"
                      ><Delete
                    /></el-icon>
                  </div>
                </div>
                <div
                  class="content"
                  v-if="containHTML(filterMessage(message.content))"
                  v-html="filterMessage(message.content)"
                  :style="{
                    color: message.color,
                    fontSize: message.font_size + 'px',
                    fontWeight: message.font_weight,
                  }"
                ></div>
                <div
                  class="content"
                  @click="comment(message)"
                  v-else
                  :style="{
                    color: message.color,
                    fontSize: message.font_size + 'px',
                    fontWeight: message.font_weight,
                  }"
                >
                  {{ message.content }}
                </div>
                <div class="bottom">
                  <div class="left flex items-center">
                    <div class="time">{{ returnTime(message.createdAt) }}前</div>
                  </div>
                  <div class="flex justify-start items-center option">
                    <div
                      class="cursor-pointer scale flex items-center"
                      @click="like(message, index)"
                    >
                      <svg-icon
                        :name="message.isLiked ? 'redHeart' : 'greyHeart'"
                        :width="1.5"
                      ></svg-icon>
                      <span :style="{ color: message.isLiked ? '#f00' : '' }" class="thums_up">{{
                        message.likes
                      }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-skeleton>
    </el-row>
    <div class="observer">
      <Loading :size="32" v-if="scrollLoading" />
      <template v-else>
        {{ messageList.length >= total ? "已经到底了~" : "下拉加载更多～" }}
      </template>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.message {
  &-body {
    min-height: 35rem;
    .search-tab {
      width: 100%;
      min-height: 3rem;
      background-color: rgba(0, 0, 0, 0.1);
      border-radius: 8px;
    }
    .tab {
      width: 100%;
      min-height: 3rem;
      padding: 0.5rem;
      display: flex;
      justify-content: flex-start;
      align-items: center;
      cursor: pointer;
      flex-wrap: wrap;
      font-size: 1.2rem;
      font-weight: 600;

      li {
        margin-right: 1rem;
      }

      .tab-li {
        height: 2rem;
        line-height: 2rem;
        text-align: center;
        padding: 0 0.6rem;
        border-radius: 8px;
      }
      .message-active-tab {
        color: var(--global-white);
        background-image: var(--button-linear-gradient);
      }
    }

    .message-card {
      position: relative;
      height: 22rem;
    }

    .img-loading {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 5rem;
      height: 3rem;
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .nick-name {
      color: var(--global-white);
      margin-left: 1rem;
      letter-spacing: 1px;
      padding: 3px 8px;
      background-color: rgba(0, 0, 0, 0.2);
      border-radius: 8px;
    }

    .left {
      background-color: rgba(0, 0, 0, 0.2);
      border-radius: 8px;
      padding: 3px 8px;
    }

    .time {
      font-size: 12px;
      color: var(--global-white);
      letter-spacing: 1px;
      margin-right: 10px;
    }

    .message-comment {
      font-size: 12px;
      color: var(--global-white);
      letter-spacing: 1px;
      padding: 3px 8px;
    }

    .option-top {
      color: var(--global-white);
      padding: 3px 8px;
      border-radius: 8px;
      background-color: rgba(0, 0, 0, 0.2);
    }

    .option {
      color: var(--global-white);
      padding: 1px 8px;
      border-radius: 8px;
      background-color: rgba(0, 0, 0, 0.2);
    }
    .top-header {
      height: 4rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .top {
      height: 22rem;
      padding: 8px;
      overflow: auto;
      white-space: pre-line;
      scrollbar-width: none;
      -ms-overflow-style: none;
      background-position: center center;
      background-size: cover;
      background-repeat: no-repeat;
      &::-webkit-scrollbar {
        display: none;
      }
    }

    .content {
      word-break: break-all;
      height: 15rem;
      overflow: auto;
    }
    .index-tag {
      font-size: 12px;
      color: var(--global-white);
    }

    .bottom {
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 2rem;
      padding: 8px;
    }
  }

  .apply-tag {
    text-align: center;
    color: var(--font-color);
    font-size: 16px;
  }
}
.likes {
  margin-left: -5px !important;
}
.observer {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 1.2rem;
  color: var(--font-color);
  margin-top: 30px;
  letter-spacing: 1px;
}
.btn {
  margin-left: 3px;
  color: var(--primary);
  cursor: pointer;
}

.scale {
  transition: all 0.3s;
}
.scale:hover {
  transform: scale(1.2);
}

.loading {
  width: 100%;
  height: 22rem;
  display: flex;
  justify-content: center;
  align-items: center;
}

// pc
@media screen and (min-width: 768px) {
  .center-top {
    .left-avatar {
      width: 48px;
      height: 48px;
    }
  }

  .publish-pc {
    cursor: pointer;
  }

  .publish-mobile {
    display: none;
  }
}

.search {
  height: 28px;
  width: 220px;
  :deep(.el-input__wrapper) {
    border-radius: 20px;
  }
}

// mobile
@media screen and (max-width: 768px) {
  .center-top {
    .left-avatar {
      width: 40px;
      height: 40px;
    }
  }

  .publish-mobile {
    cursor: pointer;
    border: none;
  }

  .publish-pc {
    display: none;
  }

  .tab {
    li {
      margin-right: 0 !important;
    }
  }
}
</style>