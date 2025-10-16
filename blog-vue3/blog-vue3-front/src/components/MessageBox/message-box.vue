<script setup lang="ts">
import { ref, onMounted, reactive, h } from "vue";
import { useRouter } from "vue-router";
import type { Ref } from "vue";

import { NotifyService } from "@/api/notifyApi";
import { containHTML } from "@/utils/tool";
import { type MessageCompoentItem, type MessageCompoentItemListResponse } from "@/types/message";
import { ElNotification } from "element-plus";
import { Delete, Compass } from "@element-plus/icons-vue";
import Loading from "@/components/Loading/index.vue";


interface ListParams {
  current: number;
  size: number;
  userId: number | string;
}

const router = useRouter();

const props = defineProps({
  type: {
    type: String,
    default: "",
  },
  userId: {
    type: [Number, String],
    default: 0,
  },
});

const drawerShow: Ref<boolean> = ref(false);
const messageList: Ref<MessageCompoentItem[]> = ref([]);
const loadMore: Ref<boolean> = ref(false);
const messageTotal: Ref<number> = ref(0);
const redTotal: Ref<number> = ref(0);

const params: ListParams = reactive({
  current: 1,
  size: 10,
  userId: 0,
});

const toggleDrawer = async (): Promise<void> => {
  drawerShow.value = !drawerShow.value;
  params.current = 1;

  if (drawerShow.value) {
    await getMessageList();
  }
};

const handleClose = async (): Promise<void> => {
  params.current = 1;
  await getMessageList();
  drawerShow.value = false;
};

const readMessage = async (id: number | string): Promise<void> => {
  await NotifyService.updateNotify(id);
};

const jump = async (item: MessageCompoentItem): Promise<void> => {
  item.isView == 1 && (await readMessage(item.id));

  switch (item.type + "") {
    case "1": // 文章
      router.push({ path: "/article", query: { id: item.to_id } });
      break;
    case "2": // 说说
      router.push({ path: "/talk", query: { id: item.to_id } });
      break;
    case "3": // 留言
      router.push("/message/list");
      break;
    case "4": // 友情链接
      window.open("http://mrzym.top/admin");
      break;
  }

  handleClose();
};

const deleteMessage = async (confirm: MessageCompoentItem): Promise<void> => {
  const res = await NotifyService.deleteNotify(confirm.id);
  if (res && res.code == 200) {
    ElNotification({
      offset: 60,
      title: "提示",
      message: h("div", { style: "color: #7ec050; font-weight: 600;" }, "删除成功"),
    });
    params.current = 1;
    await getMessageList();
  } else {
    ElNotification({
      offset: 60,
      title: "错误提示",
      message: h("div", { style: "color: #f56c6c; font-weight: 600;" }, res.message),
    });
  }
};

const getMore = (): void => {
  params.current++;
  getMessageList();
};

const getMessageList = async (): Promise<void> => {
  try {
    redTotal.value = 0;
    params.userId = props.userId;
    if (params.current > 1) {
      loadMore.value = true;
    }
    const res = await NotifyService.getNotifylist(params);
    if (res.code == 200) {
      const { list, total } = res.result;
      messageList.value = params.current == 1 ? list : messageList.value.concat(list);
      messageTotal.value = total;
      Array.isArray(messageList.value) &&
        messageList.value.forEach((v) => {
          if (v.isView == 1) {
            redTotal.value++;
          }
        });
    }
  } finally {
    loadMore.value = false;
  }
};

onMounted(async (): Promise<void> => {
  await getMessageList();
});
</script>

<template>
  <!-- Template remains the same -->
  <div class="message-drawer">
    <i class="iconfont icon-xiaoxi" @click="toggleDrawer">
      <el-badge class="red-total" v-if="redTotal" :value="redTotal" :max="9"> </el-badge>
    </i>
    <el-drawer
      class="message-drawer__body"
      v-model="drawerShow"
      :size="type == 'pc' ? '40%' : '80%'"
      title="我的消息"
      direction="ltr"
      :before-close="handleClose"
      :append-to-body="true"
      :z-index="9999"
    >
      <el-card class="p-[15px] mt-[10px] card-hover" v-for="item in messageList" :key="item.id">
        <div class="flex items-center" @click="jump(item)">
          <el-badge class="w-[90%]" v-if="item.isView == 1" :value="'new'">
            <span v-if="containHTML(item.message)" v-html="item.message"></span>
            <span v-else>{{ item.message }}</span>
          </el-badge>
          <div v-else class="grey w-[90%]">
            <span v-if="containHTML(item.message)" v-html="item.message"></span>
            <span v-else>{{ item.message }}</span>
          </div>
        </div>
        <div class="flex items-center justify-end">
          <el-icon @click="jump(item)"
            ><Compass :class="['jump', item.isView != 1 ? 'grey' : '']"
          /></el-icon>
          <el-popconfirm
            width="120"
            confirm-button-text="确认"
            cancel-button-text="取消"
            icon-color="#626AEF"
            title="确定删除吗?"
            @confirm="deleteMessage(item)"
          >
            <template #reference>
              <el-icon class="ml-[5px]"><Delete class="delete" /></el-icon>
            </template>
          </el-popconfirm>
        </div>
      </el-card>
      <div class="observer" @click="getMore">
        <Loading :size="32" v-if="loadMore" />
        <template v-else>
          {{ messageList.length >= messageTotal ? "已经到底啦" : "点击加载更多～" }}
        </template>
      </div>
    </el-drawer>
  </div>
</template>

<style lang="scss" scoped>
/* Styles remain the same */
.message-drawer {
  .icon-xiaoxi {
    position: relative;
    font-size: 1.6rem;
    color: var(--menu-color);
  }

  .red-total {
    position: absolute;
    bottom: 6px;
    left: 12px;
  }
}

.grey {
  color: #ccc !important;
}

.delete {
  cursor: pointer;
  color: var(--hot-color);
  transition: all 0.5s;
  &:hover {
    color: red;
    transform: scale(1.1);
  }
}

.jump {
  cursor: pointer;
  color: var(--primary);
  transition: all 0.5s;
  &:hover {
    color: var(--md-active);
    transform: scale(1.1);
  }
}

.observer {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 1rem;
  color: var(--primary);
  margin-top: 8px;
  letter-spacing: 1px;
  cursor: pointer;
}
</style>