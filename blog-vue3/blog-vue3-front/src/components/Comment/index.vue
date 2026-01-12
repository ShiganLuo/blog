<script setup lang="ts">
import { ref, onMounted, h, watch } from "vue";
import { useUserStore } from "@/stores/index";
import { CommentSerivce } from "@/api/blog/commentApi";
import ParentItem from "./item/ParentItem.vue";
import CommentInput from "./item/CommentInput.vue";
import { ElNotification } from "element-plus";
import { numberFormate } from "@/utils/tool";

// 定义 emits 类型
const emit = defineEmits<{
  (e: "refresh"): void;
}>();

// 定义 props 类型
interface Props {
  type: "post" | "comment" | "talk";
  id: number | string;
  authorId: number | string;
  expand?: boolean;
  isShowToggle?: boolean;
}
const props = withDefaults(defineProps<Props>(), {
  expand: false,
  isShowToggle: true,
});

const userStore = useUserStore();
const activeOrder = ref<"hot" | "new">("hot");
const total = ref<number>(0);
const isExpand = ref<boolean>(false);
const showPublish = ref<boolean>(false);

const commentInputRef = ref<InstanceType<typeof CommentInput> | null>(null);
const parentItemRef = ref<InstanceType<typeof ParentItem> | null>(null);
const commentText = ref<string>("");

// 切换展开
const toggleExpand = (): void => {
  isExpand.value = !isExpand.value;
};

// 更改排序方式
const changeOrder = (type: "hot" | "new"): void => {
  activeOrder.value = type;
};

// 登录跳转
const toLogin = (): void => {
  if (userStore.getUserInfo.id) return;
  userStore.setShowLogin(true);
};

// 评论文章，由ParentItem触发
const publish = async (): Promise<void> => {
  if (!userStore.getUserInfo.id) {
    ElNotification({
      offset: 60,
      title: "温馨提示",
      message: h("div", { style: "color: #e6c081; fontWeight: 600" }, "请先登录"),
    });
    return;
  }

  const data: any = {
    userId: userStore.getUserInfo.id,
    content: commentText.value,
    forId: props.id,
    type: props.type, 
    authorId: props.authorId, // 文章作者的id用于消息推送
    rootId: props.id
  };
  const res: any = await CommentSerivce.addComment(data);
  if (res.code === 200) {
    commentText.value = "";
    ElNotification({
      offset: 60,
      title: "提示",
      message: h("div", { style: "color: #7ec050; fontWeight: 600" }, "评论成功"),
    });

    parentItemRef.value?.getComment("clear");
    isExpand.value = true;
    showPublish.value = false;
    commentInputRef.value?.clear();

    refresh();
  } else {
    ElNotification({
      offset: 60,
      title: "错误提示",
      message: h("div", { style: "color: #f56c6c; fontWeight: 600" }, res.message),
    });
  }
};

// 获取评论条数,不为0才展示comment-header
const getTotal = (val: number): void => {
  total.value = val;
};

// 获取评论总数（后端接口）
const getCommentTotal = async (): Promise<void> => {
  const res: any = await CommentSerivce.frontGetCommentTotal(props.id);
  if (res && res.code === 200) {
    getTotal(Number(res.result));
  } else {
    ElNotification({
      offset: 60,
      title: "错误提示",
      message: h("div", { style: "color: #f56c6c; fontWeight: 600" }, res.message),
    });
  }
};

// 刷新评论区
const refresh = (): void => {
  getCommentTotal();
  emit("refresh");
};

// 监听 expand
watch(
  () => props.expand,
  (newVal) => {
    isExpand.value = newVal;
  },
  { immediate: true }
);

// 初始化
onMounted(() => {
  getCommentTotal();
});

defineExpose({
  toggleExpand,
});
</script>

<template>
  <div class="comment">
    <div class="comment-header">
      <div class="flex justify-start items-center">
        <span v-if="total" class="total-value" @click="toggleExpand">
          评论 {{ numberFormate(total) }}
        </span>
        <div v-if="total && isExpand" class="flex items-center">
          <span
            :class="['comment-tab', activeOrder == 'hot' ? 'active-order' : '']"
            @click="changeOrder('hot')"
            >最热</span
          >
          <span class="comment-tab">|</span>
          <span
            :class="['comment-tab', activeOrder == 'new' ? 'active-order' : '']"
            @click="changeOrder('new')"
            >最新</span
          >
        </div>
      </div>
      <template v-if="isShowToggle">
        <span v-if="total" class="more" @click="toggleExpand">
          {{ isExpand ? "收起" : "查看更多" }}</span
        >
        <span v-else class="more" @click="toggleExpand">
          {{ isExpand ? "取消发布" : "求评论呀~" }}
        </span>
      </template>
      <template v-else-if="!isShowToggle && isExpand">
        <span v-if="total" class="more" @click="toggleExpand"> 收起</span>
        <span v-else class="more" @click="toggleExpand"> 取消发布 </span>
      </template>
    </div>
    <div v-if="isExpand">
      <div id="commentInput" class="!mt-[1rem] w-[100%] flex justify-start items-start">
        <div class="avatar-box">
          <el-avatar class="avatar" :src="userStore.getUserInfo.avatar" @click="toLogin">{{
            userStore.getUserInfo.username || "登录"
          }}</el-avatar>
        </div>
        <div class="!w-[100%] !ml-[10px]">
          <CommentInput
            ref="commentInputRef"
            v-model:inputText="commentText"
            :show-publish-button="false"
            placeholder="说点什么吧~"
            :parent="true"
            @publish="publish"
          />
        </div>
      </div>
      <div class="comment-list">
        <ParentItem
          v-if="isExpand"
          ref="parentItemRef"
          :active="activeOrder"
          :type="type"
          :id="id"
          :author-id="authorId"
          @refresh="refresh"
        />
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.comment {
  width: 100%;

  &-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .total {
      &-value {
        cursor: pointer;
        font-size: 0.8rem;
      }
    }

    .comment-tab {
      cursor: pointer;
      margin-left: 0.8rem;
      font-size: 0.8rem;

      &:hover {
        color: var(--primary);
      }
    }
  }
}

.active-order {
  color: var(--primary) !important;
}

.avatar {
  cursor: pointer;
}

.more {
  font-size: 0.8rem;
  font-weight: 600;
  cursor: pointer;
  color: var(--font-color);
  &:hover {
    color: var(--primary);
  }
}

// pc
@media screen and (min-width: 768px) {
  .avatar-box {
    width: 45px;
    height: 45px;
  }
  .avatar {
    width: 45px;
    height: 45px;
  }

  .input-text {
    width: 100%;
    background-color: var(--global-shadow-white);
    border-radius: 8px;
    padding: 8px;
    min-height: 80px;
    box-sizing: border-box;
  }

  .publish-btn {
    background-color: var(--primary);
    font-size: 1rem;
    height: 2rem;
    width: 5rem;
    padding: 0 1rem;
  }
}
// mobile
@media screen and (max-width: 768px) {
  .avatar-box {
    width: 32px;
    height: 32px;
  }
  .avatar {
    width: 32px;
    height: 32px;
  }

  .input-text {
    width: 100%;
    background-color: var(--global-shadow-white);
    border-radius: 8px;
    padding: 8px;
    box-sizing: border-box;
  }

  .publish-btn {
    background-color: var(--primary);
    font-size: 0.8rem;
    height: 1.4rem;
    width: 3rem;
    padding: 0 1rem;
  }
}
</style>
