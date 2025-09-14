<script setup lang="ts">
import { ref, watch, onMounted } from "vue";
import { debounce } from "@/utils/tool";
import IconList from "./IconList.vue";
import { getCurrentIndex } from "@/utils/tool";

const emit = defineEmits<{
  (e: "update:inputText", val: string): void
  (e: "publish"): void
}>();

const props = defineProps<{
  inputText: string
  showPublishButton: boolean
  placeholder: string
  parent: boolean
}>();

const inputCommentRef = ref<HTMLDivElement | null>(null);
const showPublish = ref<boolean>(false);
const currentIndex = ref<number>(0);

// 输入完成
const inputComment = debounce(() => {
  if (inputCommentRef.value) {
    emit("update:inputText", inputCommentRef.value.innerHTML);
  }
}, 100);

const keepIndex = () => {
  currentIndex.value = getCurrentIndex();
};

const focusComment = () => {
  showPublish.value = true;
};

const selectIcon = (val: string) => {
  const text = val;
  if (!inputCommentRef.value) return;

  const innerHTML = inputCommentRef.value.innerHTML;
  if (currentIndex.value >= innerHTML.length) {
    inputCommentRef.value.innerHTML += text;
  } else {
    const start = innerHTML.slice(0, currentIndex.value);
    const end = innerHTML.slice(currentIndex.value);
    inputCommentRef.value.innerHTML = start + text + end;
  }

  currentIndex.value += 2;
  emit("update:inputText", inputCommentRef.value.innerHTML);
};

const publish = () => {
  emit("publish");
  showPublish.value = false;
};

const clear = () => {
  if (!inputCommentRef.value) return;
  currentIndex.value = getCurrentIndex();
  inputCommentRef.value.innerHTML = "";
  emit("update:inputText", "");
};

watch(
  () => props.showPublishButton,
  (newV) => {
    showPublish.value = newV;
  }
);

onMounted(() => {
  if (inputCommentRef.value) {
    inputCommentRef.value.innerHTML = props.inputText;
  }
});

defineExpose({
  clear,
});
</script>

<template>
  <div :class="parent ? 'parent-input' : 'children-input'">
    <div class="header">
      <div v-if="placeholder" class="placeholder">@ {{ placeholder }}</div>
      <div
        ref="inputCommentRef"
        contenteditable="true"
        :class="[
          'comment-content',
          parent ? 'parent-input-inputText' : 'children-input-inputText',
          'input-inputText',
          '!mt-[5px]',
        ]"
        :style="{ '--size': parent ? '1.2rem' : '1rem' }"
        rows="3"
        cols="20"
        @input="inputComment()"
        @focus="focusComment"
        @blur="keepIndex"
        @click="keepIndex"
        placeholder="期待能留下你的脚印~"
      ></div>
    </div>
    <div
      v-if="showPublish"
      class="!mt-[0.5rem] flex justify-between items-center animate__animated animate__fadeIn"
    >
      <div class="cursor-pointer">
        <IconList @select-icon="selectIcon" />
      </div>
      <div v-if="inputText">
        <el-button type="danger" class="clear-btn" @click="clear">清空</el-button>
        <el-button type="primary" class="publish-btn" @click="publish">发布</el-button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.input-inputText {
  background-color: var(--global-shadow-white);
  border-radius: 8px;
  padding: 12px;
  box-sizing: border-box;
  color: var(--font-color);
  font-size: 1.2rem;
  font-weight: 700;
  transition: all 0.3s;
  word-break: break-all;
  box-shadow: 0 3px 6px 3px rgba(7, 17, 27, 0.06);
  &:hover {
    box-shadow: 0 3px 6px 3px rgba(7, 17, 27, 0.15);
  }
}

.comment-content:empty::before {
  content: attr(placeholder);
  font-size: var(--size);
  color: var(--comment-grey);
  font-weight: 700;
}

.publish-btn {
  background-color: var(--primary);
  border: none;
  font-weight: 700;
}

.clear-btn {
  background-color: var(--top);
  font-weight: 700;
  background-color: #efbcda;
  border: none;
}

// pc
@media screen and (min-width: 768px) {
  .parent-input {
    width: 100%;
  }

  .children-input {
    width: calc(100% - 10px);
  }

  .parent-input-inputText {
    width: 100%;
    min-height: 120px;
  }
  .children-input-inputText {
    font-size: 1.2rem;
    width: 100%;
    min-height: 80px;
  }

  .publish-btn {
    font-size: 1rem;
    height: 1.8rem;
    padding: 0 1rem;
  }
  .clear-btn {
    font-size: 1rem;
    height: 1.8rem;
    padding: 0 1rem;
  }
}
// mobile
@media screen and (max-width: 768px) {
  .parent-input {
    width: 100%;
  }

  .children-input {
    width: 100%;
  }

  .parent-input-inputText {
    width: 100%;
    min-height: 100px;
  }
  .children-input-inputText {
    font-size: 1rem;
    width: 100%;
    min-height: 60px;
  }

  .publish-btn {
    font-size: 1rem;
    height: 1.5rem;
    padding: 0 0.5rem;
  }
  .clear-btn {
    font-size: 1rem;
    height: 1.5rem;
    padding: 0 0.5rem;
  }
}
</style>
