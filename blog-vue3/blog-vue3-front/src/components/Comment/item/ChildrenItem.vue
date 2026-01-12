<script setup lang="ts">
import { ref } from "vue";
import { useUserStore } from "@/stores/index";
import { containHTML } from "@/utils/tool";
import TextOverflow from "@/components/TextOverflow/index.vue";
import CommentInput from "./CommentInput.vue";
import type { CommentItem, CommentType } from "@/types/comment";

const userStore = useUserStore();
const showApplyInput = ref(false);
const replyInputText = ref(""); // 用于子评论输入框的 v-model

const emits = defineEmits<{
  (e: 'like', comment: CommentItem): void;
  (e: 'delete', commentId: number | string): void;
  (e: 'publish', data: any): void; // 用于子评论的发布
}>();

const props = defineProps<{
  comment: CommentItem;
  type: CommentType;
  authorId: number | string;
}>();
const handleLike = () => {
  emits('like', props.comment);
};

const handleDelete = () => {
  emits('delete', props.comment.id);
};

const handleApply = () => {
  showApplyInput.value = !showApplyInput.value
};

const handleClose = () => {
  showApplyInput.value = false;
  replyInputText.value = "";
};

// 提交回复
const submitReply = () => {
  emits('publish', {
    content: replyInputText.value,
    forId: props.comment.id,
    replyUserName: props.comment.userName,
    replyUserId: props.comment.userId
  });
  // 清空输入框内容
  replyInputText.value = "";
  showApplyInput.value = false;
};
</script>

<template>
  <div class="comment-item">
    <div class="flex justify-start items-start">
      <div class="avatar-box">
        <el-avatar class="avatar" :src="comment.from_avatar">{{ comment.userName }}</el-avatar>
      </div>
      <div class="right !w-[100%]">
        <div class="cursor-pointer">
          {{ comment.userName }}
          <span v-if="comment.userId == 1" class="up">UP</span>
          <span v-if="comment.replyUserName" class="reply-to">
            回复 <span class="reply-name">{{ comment.replyUserName }}</span>
          </span>
        </div>
        <div id="comment-content" class="!mt-[1rem]">
          <span v-if="containHTML(comment.content)" v-html="comment.content"></span>
          <TextOverflow
            v-else
            class="content"
            :key="comment.id"
            :text="comment.content"
            :maxLines="3"
            :font-size="16"
          >
            <template v-slot:default="{ clickToggle, expanded }">
              <span @click="clickToggle" class="btn">
                {{ expanded ? "收起" : "展开" }}
              </span>
            </template>
          </TextOverflow>
        </div>
        <div class="!mt-[0.5rem]">
          <span class="!mr-[1rem] ip">{{ `IP: ${comment.ipAddress}` }}</span>
          <span
            :class="[
              'thumbs',
              '!mr-[1rem]',
              'iconfont',
              'icon-icon',
              comment.is_like ? 'like-active' : '',
            ]"
            @click="handleLike"
          >
            <span class="!ml-[0.5rem]">{{ comment.thumbs_up }}</span>
          </span>
          <span class="!mr-[1rem] apply cursor-pointer" @click="handleApply">回复</span>
          <span
            v-if="showApplyInput"
            class="!mr-[1rem] close cursor-pointer"
            @click="handleClose"
          >关闭</span>
          <span
            class="!mr-[1rem] delete cursor-pointer"
            v-if="userStore.getUserInfo.id == comment.userId || userStore.getUserInfo.role == 1"
            @click="handleDelete"
          >删除</span>
        </div>
        <div class="!mt-[0.5rem]">{{ comment.createdAt }}</div>

        <div class="!mt-[1rem]" v-if="showApplyInput">
          <CommentInput
            v-model:inputText="replyInputText"
            :showPublishButton="true"
            :placeholder="comment.userName"
            :parent="false"
            @publish="submitReply"
          />
        </div>

        <div v-if="comment.childComments && comment.childComments.length > 0" class="children-list !ml-[2rem] !mt-[1rem]">
          <ChildrenItem
            v-for="childComment in comment.childComments"
            :key="childComment.id"
            :comment="childComment"
            :type="type"
            :author-id="authorId"
            @like="emits('like', $event)"
            @delete="handleDelete"
            @publish="emits('publish', $event)"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.comment-parent {
  width: 100%;
}

.active {
  color: var(--primary);
}
.icon-icon {
  cursor: pointer;
}
.thumbs {
  word-break: keep-all;
  font-size: 1rem;
}
.apply {
  word-break: keep-all;
  font-size: 1rem;
  color: var(--md-active);
}
.apply:hover {
  color: var(--primary);
}

.close {
  color: var(--top);
  word-break: keep-all;
  font-size: 1rem;
  &:hover {
    color: var(--hot-color);
  }
}

.content-apply {
  font-size: 0.8rem;
}

.delete {
  word-break: keep-all;
  font-size: 1rem;
  color: var(--top);

  &:hover {
    color: var(--hot-color);
  }
}

.ip {
  font-size: 0.8rem;
  display: inline-block;
}

.show-more {
  cursor: pointer;
  margin-top: 3px;
  font-size: 0.8rem;
  &:hover {
    color: var(--primary);
  }
}

.like-active {
  color: var(--primary);
  transform: scale(1.05);
}

.up {
  margin-left: 0.5rem;
  font-size: 0.8rem;
  font-weight: 800;
  color: var(--hot-color);
}

.btn {
  color: var(--primary);
  cursor: pointer;
}

@media screen and (max-width: 768px) {
  .right {
    margin-left: 10px;
  }

  .avatar-box {
    width: 32px;
    height: 32px;
  }
  .avatar {
    width: 32px;
    height: 32px;
  }
}

@media screen and (min-width: 768px) {
  .avatar-box {
    width: 45px;
    height: 45px;
  }
  .avatar {
    width: 45px;
    height: 45px;
  }
  .right {
    margin-left: 10px;
  }
}
</style>