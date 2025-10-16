<script setup lang="ts">
import { ref, h, onActivated, type Ref } from "vue";
import { useRouter } from "vue-router";
import { useUserStore, useStaticData } from "@/stores/index";
import { storeToRefs } from "pinia";

import TypeWriter from "@/components/TypeWriter/index.vue";
import CardMessage from "./components/card-message.vue";
import DanmuMessage from "./components/danmu-message.vue";
import PageHeader from "@/components/PageHeader/index.vue";
import { ElNotification } from "element-plus";

import { MessageService } from "@/api/messageApi";
import SvgIcon from "@/components/SvgIcon/index.vue";




interface MessageForm {
  id: number | string;
  content: string;
  type: string;
  from_id: number | string;
  color: string;
  font_size: number;
  font_weight: number;
  bg_color: string;
  bg_url: string;
  nick_name: string;
  avatar: string;
  isNew: boolean;
}
const randomFontColor = (): string => {
  return `rgb(${Math.random() * 180 + 30},${Math.random() * 180 + 30},${Math.random() * 180 + 30})`;
};

const randomFontSize = (): number => {
  return Math.random() * 1.6 + 0.6;
};
const randomFontWeight = (): number => {
  return Math.random() * 100;
};

interface AddMessageResponse {
  code: number;
  [key: string]: any;
}

interface MessageComponent {
  addDanmu: (form: MessageForm) => void;
  hideSearchInput: () => void;
  init: () => void;
}

const router = useRouter();
const { getUserInfo } = storeToRefs(useUserStore());
const { getMessageTypeIsCard } = storeToRefs(useStaticData());

const messageRef = ref<MessageComponent | null>(null);
const message = ref<string>("");
const showMessageInput = ref<boolean>(false);
const isLoaded = ref<boolean>(false);

const toggle = (): void => {
  useStaticData().setMessageTypeIsCard(!getMessageTypeIsCard.value);
};

const userAddMessage = (): void => {
  console.log("-------1--------")
  if (getMessageTypeIsCard.value) {
    console.log("-------2--------")
    router.push({ path: "/message/publish", query: { type: "add" } });
  } else {
    console.log("-------3--------")
    if (!message.value) {
      ElNotification({
        offset: 60,
        title: "温馨提示",
        duration: 3000,
        message: h(
          "div",
          { style: "color: #e6c081; font-weight: 600;" },
          "请输入留言内容"
        ),
      });
      return;
    }
    console.log(getUserInfo.value)
    const form: MessageForm = {
      id: 0,
      content: message.value,
      type: "message",
      from_id: getUserInfo.value.id || 0,
      color: randomFontColor(),
      font_size: randomFontSize(),
      font_weight: randomFontWeight(),
      bg_color: "transparent",
      bg_url: "",
      nick_name: getUserInfo.value.nick_name || "游客",
      avatar: getUserInfo.value.avatar || "游客",
      isNew: true,
    };
    console.log("form", form);
    // 保存弹幕
    MessageService.addMessage(form).then((res: AddMessageResponse) => {
      if (res.code === 200) {
        messageRef.value?.addDanmu(form);
        message.value = "";
        ElNotification({
          offset: 60,
          title: "提示",
          message: h(
            "div",
            { style: "color: #7ec050; font-weight: 600;" },
            form.id ? "修改成功" : "留言成功"
          ),
        });
      }
    });
  }
};

const hideSearchInput = (): void => {
  if (getMessageTypeIsCard.value) {
    messageRef.value?.hideSearchInput();
  }
};

onActivated(() => {
  if (isLoaded.value && !getMessageTypeIsCard.value) {
    messageRef.value?.init();
  }
  isLoaded.value = true;
});

</script>
<template>
  <PageHeader v-if="getMessageTypeIsCard">
    <template #route>
      <el-popover
        placement="top-start"
        :width="110"
        trigger="hover"
        :content="`切换成${getMessageTypeIsCard ? '弹幕' : '卡片'}模式`"
      >
        <template #reference>
          <div class="message-title cursor-pointer !z-[2000]" @click="toggle">留言板</div>
        </template>
      </el-popover>
    </template>
    <div class="message-header">
      <div class="flex items-center !w-[100%] !h-[1.2rem] !z-[2000]">
        <TypeWriter
          class="type-writer"
          size="1.2rem"
          :typeList="['生活原本沉闷，但跑起来就会有风!']"
          :timeSpace="200"
          :wordPrintTime="50"
          color="#000"
        ></TypeWriter>
      </div>
      <div class="publish flex items-center">
        <el-popover placement="top-start" :width="110" trigger="hover" content="点我去发表留言">
          <template #reference>
            <svg-icon
              class="!z-[2000] cursor-pointer"
              name="publish"
              :width="4"
              :height="4"
              :hidden="false"
              @click="userAddMessage"
              @mouseenter="showMessageInput = true"
            ></svg-icon>
          </template>
        </el-popover>
      </div>
    </div>
  </PageHeader>

  <div v-else class="message-container">
    <el-popover
      placement="top-start"
      :width="110"
      trigger="hover"
      :content="`切换成${getMessageTypeIsCard ? '弹幕' : '卡片'}模式`"
    >
      <template #reference>
        <div class="message-title-popover" @click="toggle">留言</div>
      </template>
    </el-popover>
    <div class="input-container">
      <transition name="down" mode="out-in">
        <el-input
          v-model="message"
          class="message-input"
          placeholder="一定要留下点什么～"
          v-if="showMessageInput"
          :maxlength="555"
          @blur="
            () => {
              showMessageInput = false;
            }
          "
          @keyup.enter="userAddMessage"
        ></el-input>
      </transition>
      <div class="publish-icon-container">
        <el-popover placement="top-start" :width="110" trigger="hover" content="点击发送">
          <template #reference>
            <svg-icon
              class="publish-icon"
              name="publish"
              :width="4"
              :height="4"
              @click="userAddMessage"
              @mouseenter="showMessageInput = true"
            ></svg-icon>
          </template>
        </el-popover>
      </div>
    </div>
  </div>

  <div :class="['message', getMessageTypeIsCard ? 'min-height: 100vh' : 'min-height: 100vh']" @click="hideSearchInput">
    <component ref="messageRef" :is="getMessageTypeIsCard ? CardMessage : DanmuMessage"></component>
  </div>
</template>

<style lang="scss" scoped>
.message {
  .row-height {
    min-height: 22rem;
    transition: height 0.8s;
  }
  &-header {
    position: relative;
    width: 100%;

    .publish {
      position: absolute;
      bottom: -100px;
      left: 50%;
      transform: translateX(-50%);
    }
  }
}

.message-title {
  display: inline-block;
  position: relative;
  font-size: 2.4rem;
  font-weight: 600;
  color: var(--router-color);
}

.type-writer {
  color: var(--router-color);
}

.message-input {
  width: 300px;
  height: 60px;
  border-radius: 60px;
  background: #676767;
}

.down-enter-from,
.down-leave-to {
  opacity: 0;
  transform: translateY(-80px);
}

.down-enter-active,
.down-leave-active {
  transition: all 0.8s;
}

.down-enter-to,
.down-leave-from {
  opacity: 1;
  transform: translateY(0);
}

:deep(.el-input__wrapper) {
  border-radius: 60px;
}
.message-container {
  position: relative;
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.message-title-popover {
  cursor: pointer;
  z-index: 2000;
}

.input-container {
  margin-top: 3rem;
  display: flex;
  align-items: center;
}

.message-input {
  z-index: 2000;
  margin-right: 10px;
}

.publish-icon-container {
  display: flex;
  align-items: center;
}

.publish-icon {
  z-index: 2000;
  cursor: pointer;
}
</style>