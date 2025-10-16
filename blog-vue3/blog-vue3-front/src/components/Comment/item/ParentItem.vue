<script setup lang="ts">
import { reactive, ref, watch, h } from "vue";
import { storeToRefs } from "pinia";
import { frontGetComment, addComment, deleteComment } from "@/api/blog/commentApi";
import { addLike, cancelLike } from "@/api/likeApi";
import Loading from "@/components/Loading/index.vue";
import { useUserStore } from "@/stores/index";
import { ElMessageBox, ElNotification } from "element-plus";
import type { CommentType, CommentItem, CommentParams } from "./Comment";
import ChildrenItem from "./ChildrenItem.vue";
import CommentInput from "./CommentInput.vue"; // 引入你提供的评论输入组件

const userStore = useUserStore();
const { getUserInfo } = storeToRefs(userStore);
const emits = defineEmits<{
  (e: 'refresh'): void;
}>();

const props = defineProps<{
  active: string;
  type: CommentType;
  id: number | string;
  authorId: number | string;
}>();

const commentList = ref<CommentItem[]>([]);
const commentTotal = ref(0);
const topCommentInputRef = ref<InstanceType<typeof CommentInput> | null>(null);

const params = reactive<CommentParams>({
  current: 1,
  size: 10,
  user_id: getUserInfo.value.id,
  rootId: props.id,
  loading: false,
});

// 获取所有评论（假设API返回嵌套结构）
const getComment = async (type?: string) => {
  params.loading = true;
  if (type === "clear") params.current = 1;
  const res = await frontGetComment(params);
  if (res && res.code === 200) {
    const { list, total } = res.result;
    commentList.value = params.current === 1 ? list : commentList.value.concat(list);
    commentTotal.value = total;
  } else {
    ElNotification({ offset: 60, title: "错误提示", message: h("div", { style: "color: #f56c6c; font-weight: 600;" }, res.message) });
  }
  params.loading = false;
};

const showMore = () => {
  params.current++;
  getComment();
};

// 处理点赞事件
const handleLike = async (comment: CommentItem) => {
  let res;
  const payload = { for_id: comment.id, type: "comment", user_id: userStore.getUserInfo.id };
  if (comment.is_like) {
    res = await cancelLike(payload);
    if (res?.code === 200) {
      comment.is_like = false;
      comment.thumbs_up--;
      ElNotification({ offset: 60, title: "提示", message: h("div", { style: "color: #7ec050; font-weight: 600;" }, "已取消点赞") });
    }
  } else {
    res = await addLike(payload);
    if (res?.code === 200) {
      comment.is_like = true;
      comment.thumbs_up++;
      ElNotification({ offset: 60, title: "提示", message: h("div", { style: "color: #7ec050; font-weight: 600;" }, "点赞成功") });
    }
  }
};

// 处理删除事件
const handleDelete = (commentId: number | string) => {
  ElMessageBox.confirm("确认删除此条评论吗？子级评论也会被删除哦", "提示", {
    confirmButtonText: "确认",
    cancelButtonText: "取消",
  }).then(async () => {
    const res = await deleteComment(commentId);
    if (res?.code === 200) {
      ElNotification({ offset: 60, title: "提示", message: h("div", { style: "color: #7ec050; font-weight: 600;" }, "删除成功") });
      getComment("clear");
      emits("refresh");
    } else {
      ElNotification({ offset: 60, title: "错误提示", message: h("div", { style: "color: #f56c6c; font-weight: 600;" }, res.message) });
    }
  });
};

// 回复评论，由CommentItem触发
const publish = async (data: { content: string, for_id?: number | string, to_id?: string }) => {
  console.log("这是parent的publish", data)
  const commentData = {
    from_id: userStore.getUserInfo.id,
    content: data.content,
    for_id: data.for_id,
    to_id: data.to_id,
    type: "comment",
    author_id: props.authorId,
    root_id: props.id
  };
  console.log(commentData)
  const res = await addComment(commentData);
  if (res.code === 200) {
    ElNotification({ offset: 60, title: "提示", message: h("div", { style: "color: #7ec050; font-weight: 600;" }, "评论成功") });
    getComment("clear");
    emits("refresh");
    if (!data.for_id) {
      topCommentInputRef.value?.clear(); // 清空顶层评论输入框
    }
  } else {
    ElNotification({ offset: 60, title: "错误提示", message: h("div", { style: "color: #f56c6c; font-weight: 600;" }, res.message) });
  }
};

watch(
  () => props.id,
  () => {
    Object.assign(params, {
      rootId: props.id
    });
    getComment();
  },
  { immediate: true }
);


defineExpose({ getComment });
</script>

<template>
  <div class="comment-parent">
    <template v-if="commentList.length">
      <div
        class="!mt-[0.5rem] animate__animated animate__fadeIn"
        v-for="comment in commentList"
        :key="comment.id"
      >
        <ChildrenItem
          :comment="comment"
          :type="type"
          :author-id="authorId"
          @like="handleLike"
          @delete="handleDelete"
          @publish="publish"
        />
      </div>
    </template>
    <Loading :size="32" v-if="params.loading" />
    <div v-else-if="commentTotal > params.size" class="show-more" @click="showMore">
      展开更多
    </div>
    <div v-else class="h-[48px]"></div>
  </div>
</template>

<style lang="scss" scoped>
.comment-parent {
  width: 100%;
}

.active {
  color: var(--primary);
}
.icon-icon1 {
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
