<script setup lang="ts">
import { reactive, onMounted, h, ref } from "vue";
import { storeToRefs } from "pinia";

import { returnTime, _getLocalItem, _setLocalItem, containHTML } from "@/utils/tool";
import { LikeService } from "@/api/likeApi";
import { useUserStore } from "@/stores/index";

import { ElNotification } from "element-plus";
import PageHeader from "@/components/PageHeader/index.vue";
import SvgIcon from "@/components/SvgIcon/index.vue";

// 单条留言类型
interface MessageItem {
  id: number | string;
  content: string;
  type: string;
  userId?: number | string;
  color: string;
  font_size: number;
  font_weight: number;
  user_id: number;
  likes: number;
  nickName: string;
  avatar: string;
  isLiked: boolean;
  createdAt?: string; // 时间可能存在
}

const userStore = useUserStore();
const { getUserInfo } = storeToRefs(userStore);

const message = reactive<MessageItem>({
  id: 0,
  content: "",
  type:"message",
  userId: getUserInfo.value.id,
  color: "",
  font_size: 16,
  font_weight: 500,
  user_id: 0,
  likes: 0,
  nickName: "",
  avatar: "",
  isLiked: false,
});

const likePending = ref(false);

const like = async (item: MessageItem) => {
  if (likePending.value) return;
  likePending.value = true;

  if (item.isLiked) {
    // 取消点赞
    const res = await LikeService.cancelLike({ for_id: item.id, type: "message", user_id: getUserInfo.value.id });
    if (res.code === 200) {
      item.likes--;
      item.isLiked = false;
      likePending.value = false;

      ElNotification({
        offset: 60,
        title: "提示",
        message: h("div", { style: "color: #7ec050; font-weight: 600;" }, "已取消点赞"),
      });
    }
  } else {
    // 点赞
    const res = await LikeService.addLike({ for_id: item.id, type: "message", user_id: getUserInfo.value.id });
    if (res.code === 200) {
      item.likes++;
      item.isLiked = true;
      likePending.value = false;

      ElNotification({
        offset: 60,
        title: "提示",
        message: h("div", { style: "color: #7ec050; font-weight: 600;" }, "点赞成功"),
      });
    }
  }

  _setLocalItem("message-refresh", true);
  _setLocalItem("blog-message-item", item);
};

const commentRefresh = () => {
  _setLocalItem("message-refresh", true);
};

onMounted(() => {
  const item = _getLocalItem("blog-message-item") as MessageItem | null;
  if (item) {
    Object.assign(message, item);
  }
});
</script>
<template>
  <PageHeader>
    <template #route> 留言详情 </template>
  </PageHeader>
  <div class="message">
    <div class="center_box">
      <el-card>
        <div
          class="message-card animate__animated animate__fadeIn"
        >
          <div
            class="top">
            <div class="top-header">
              <div class="header-items">
                <el-avatar class="left-avatar" :src="message.avatar">
                  {{ message.nickName }}
                </el-avatar>
                <span class="nick-name"> {{ message.nickName }}</span>
              </div>
            </div>
            <div
              class="content"
              v-if="containHTML(message.content)"
              v-html="message.content"
              :style="{
                color: message.color,
                fontSize: message.font_size + 'px',
                fontWeight: message.font_weight,
              }"
            ></div>
            <div
              class="content"
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
              <div class="left bottom-items">
                <div class="time">{{ returnTime(message.createdAt) }}前</div>
              </div>
              <div class="right bottom-items">
                <svg-icon
                  :name="message.isLiked ? 'redHeart' : 'greyHeart'"
                  :width="1.5"
                  @click="like(message)"
                ></svg-icon>
                <span :style="{ color: message.isLiked ? '#f00' : '' }" class="like-count">
                  {{ message.likes}}
                </span>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.message {
  .header-items {
    display: flex;
    align-items: center;
  }
  .bottom-items {
    display: flex;
    justify-content: flex-start;
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
  .like-count {
    margin-left: 5px;
  }
  .comment-container {
    width: 100%;
    margin-top: 1rem;
  }
}
.message-card {
  height: 22rem;
  background-position: center center;
  background-size: cover;
  background-repeat: no-repeat;
}
.content {
  word-break: break-all;
  height: 15rem;
  overflow: auto;
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
.top-header {
  height: 4rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.index-tag {
  color: var(--global-white);
  font-size: 12px;
}
.bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 2rem;
  padding: 8px;
}
.option {
  color: var(--global-white);
  padding: 1px 5px;
  border-radius: 8px;
  background-color: rgba(0, 0, 0, 0.2);
}
.center_box {
  min-height: calc(100vh - 128px);
}
// pc
@media screen and (min-width: 768px) {
  .center_box {
    max-width: 600px !important;
  }
  .canter-top {
    .left-avatar {
      width: 48px;
      height: 48px;
    }
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
}
</style>