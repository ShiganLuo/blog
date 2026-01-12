<script setup lang="ts">
import { ref, reactive, onMounted, h } from "vue";
import { useRouter, useRoute } from "vue-router";
import type { LocationQueryValue } from "vue-router";
import { ElNotification } from "element-plus";
import { useUserStore } from "@/stores/index";
import { storeToRefs } from "pinia";

import PageHeader from "@/components/PageHeader/index.vue";
import { MessageService } from "@/api/messageApi";
import { CommentSerivce } from "@/api/blog/commentApi";
import { _getLocalItem, _removeLocalItem, _setLocalItem, debounce } from "@/utils/tool";


// -------------------- 类型声明 --------------------
interface BgFile {
  id?: number;
  name?: string;
  url: string;
}

interface MessageForm {
  id: number | string;
  content: string;
  type: string;
  color: string;
  font_size: number;
  font_weight: number;
  bg_color: string;
  userId?: number | string;
  nickName: string;
  avatar?: string;
}

interface TabItem {
  key: number;
  label: string;
}

interface ApiResponse<T> {
  code: number;
  message?: string;
  result: T;
}

// -------------------- 初始化 --------------------
const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const { getUserInfo } = storeToRefs(userStore);

const loading = ref(false);
const tabList = ref<TabItem[]>([]);
const hasImgChange = ref(false);

const form = reactive<MessageForm>({
  id: 0,
  content: "",
  type: "message",
  color: "#676767",
  font_size: 16,
  font_weight: 500,
  bg_color: "transparent",
  userId: getUserInfo.value.id,
  nickName: getUserInfo.value.nick_name || "游客",
  avatar: getUserInfo.value.avatar || "游客",
});

const primaryForm: MessageForm = { ...form };
const inputCommentRef = ref<HTMLElement | null>(null);

// -------------------- 工具方法 --------------------
function keepLastIndex(dom: HTMLElement) {
  let range;
  if (window.getSelection) {
    dom.focus();
    range = window.getSelection();
    if (range) {
      range.selectAllChildren(dom);
      range.collapseToEnd();
    }
  } else if ((document as any).selection) {
    range = (document as any).selection.createRange();
    range.moveToElementText(dom);
    range.collapse(false);
    range.select();
  }
}

const inputComment = debounce(() => {
  if (inputCommentRef.value) {
    form.content = inputCommentRef.value.innerHTML;
  }
}, 100);

const focusCommentInput = () => {
  if (inputCommentRef.value) {
    if (inputCommentRef.value.innerHTML === "留下点什么再走吧~") {
      inputCommentRef.value.innerHTML = "";
    }
    keepLastIndex(inputCommentRef.value);
  }
};

// -------------------- 提交留言 --------------------
const leaveMessage = async () => {
  if (!form.content) {
    ElNotification({
      offset: 60,
      title: "温馨提示",
      message: h("div", { style: "color: #e6c081; font-weight: 600;" }, "留言内容不能为空"),
    });
    return;
  }
  if (form.content.length > 555) {
    ElNotification({
      offset: 60,
      title: "温馨提示",
      message: h("div", { style: "color: #e6c081; font-weight: 600;" }, "留言内容过长 请删减"),
    });
    return;
  }
  if (!form.id) {
    form.userId = getUserInfo.value.id;
  }

  loading.value = true;

  let res: ApiResponse<any>;
  if (form.id) {
    res = await CommentSerivce.addComment(form);
  } else {
    res = await MessageService.addMessage(form);
  }

  if (res && res.code === 200) {
    ElNotification({
      offset: 60,
      title: "提示",
      message: h("div", { style: "color: #7ec050; font-weight: 600;" }, form.id ? "修改成功" : "留言成功"),
    });
    Object.assign(form, primaryForm);
    loading.value = false;
    _removeLocalItem("blog-message-item");
    _setLocalItem("message-refresh", true);
    _setLocalItem("message-need-scroll", true);
    router.go(-1);
  } else {
    ElNotification({
      offset: 60,
      title: "错误提示",
      message: h("div", { style: "color: #f56c6c; font-weight: 600;" }, res.message || "未知错误"),
    });
  }
};

const getHotMessageTag = async () => {
  const res = await MessageService.getMessageTag();
  if (res.code === 200) {
    tabList.value = Array.isArray(res.result)
      ? res.result.map((v, i) => ({ key: i + 1, label: v.tag }))
      : [];
  }
};


onMounted(async () => {
  await getHotMessageTag();
  const type = route.query.type as LocationQueryValue;
  if (type === "edit") {
    const item = _getLocalItem("blog-message-item") as MessageForm | null;
    if (item) {
      Object.assign(form, item);
    }
    if (inputCommentRef.value) {
      inputCommentRef.value.innerHTML = form.content;
    }
  } else {
    if (inputCommentRef.value) {
      inputCommentRef.value.innerHTML = "留下点什么再走吧~";
    }
    form.nickName = getUserInfo.value.nick_name || "游客";
    form.avatar = getUserInfo.value.avatar || "游客";
  }
});

</script>
<template>
  <PageHeader>
    <template #route>
      {{ form.id ? "编辑留言" : "发布留言" }}
    </template>
  </PageHeader>
  <div class="message">
    <div class="center_box">
      <el-card class="card-container">
        <div class="card-content-height" :style="{ backgroundColor: form.bg_color }">
          <div class="top-header">
            <div class="flex items-center">
              <el-avatar class="left-avatar" :src="form.avatar">{{ form.nickName }} </el-avatar>
              <span class="nick-name"> {{ form.nickName }}</span>
            </div>
          </div>
          <div
            class="content"
            :style="{
              color: form.color,
              fontSize: form.font_size + 'px',
              fontWeight: form.font_weight,
            }"
            ref="inputCommentRef"
            contenteditable="true"
            @input="inputComment()"
            @focus="focusCommentInput"
          ></div>
        </div>
      </el-card>
      <div class="content-container">
        <div class="button-container">
          <el-button
            :disabled="loading"
            :loading="loading"
            class="main-button"
            @click="leaveMessage"
            >{{
              loading ? "努力上传中..." : route.query.type == "edit" ? "保存" : "发布"
            }}</el-button
          >
        </div>
      </div>
    </div>
    <div v-if="loading" class="loading">
      <div class="coffee_cup"></div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.message {
  .top {
    height: 22rem;
    padding: 8px;
    background-position: center center;
    background-size: cover;
    background-repeat: no-repeat;
  }
  .card-container {
    margin-top: 2rem !important;
  }
  .card-content-height {
    height: 22rem !important;
  }
  .content-container {
    margin-top: 10px !important;
    height: 20rem !important;
  }
  .button-container {
    height: 4rem !important;
    padding: 15px !important;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  .main-button {
    width: 200px !important;
  }
  .top-header {
    height: 4rem;
    display: flex;
    justify-content: space-between;
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
  .content {
    height: 15rem;
    width: 100%;
    overflow: auto;
  }
  .bottom {
    height: 2rem;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    padding: 8px;
  }
  .tag {
    font-size: 12px;
    color: var(--global-white);
    background-color: rgba(0, 0, 0, 0.2);
    padding: 3px 8px;
    border-radius: 8px;
    margin-right: 10px;
  }
  &-input {
    width: 100%;
  }
  :deep(.el-textarea__inner) {
    background-color: transparent;
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
    margin-bottom: 1rem;
    background-color: rgba(0, 0, 0, 0.2);
    border-radius: 8px;
    li {
      margin-right: 1rem;
    }
    .tab-li {
      width: 6rem;
      height: 2rem;
      line-height: 2rem;
      text-align: center;
      border-radius: 8px;
    }
    .message-active-tab {
      color: var(--global-white);
      background-image: var(--button-linear-gradient);
    }
  }
}
.center_box {
  min-height: calc(100vh - 128px);
}
.loading {
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
  display: flex;
  justify-content: center;
  align-items: center;
}
.coffee_cup {
  transform: scale(2);
}
.type-writer {
  color: var(--global-black);
}
:deep(.el-upload--picture-card) {
  width: 280px !important;
  height: 140px !important;
  border-radius: 8px !important;
}
:deep(.el-upload-list__item) {
  width: 280px !important;
  height: 140px !important;
  border-radius: 8px !important;
  margin: 0;
}
:deep(.el-upload-list--picture-card) {
  width: 280px !important;
  height: 140px !important;
  border-radius: 8px !important;
}
@media screen and (min-width: 768px) {
  .center-top {
    .left-avatar {
      width: 48px;
      height: 48px;
    }
  }
  .center_box {
    max-width: 600px !important;
  }
}
@media screen and (max-width: 768px) {
  .center-top {
    .left-avatar {
      width: 40px;
      height: 40px;
    }
  }
}
</style>