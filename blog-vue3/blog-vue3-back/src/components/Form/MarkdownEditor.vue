<template>
  <div
    class="md-editor-wrapper"
    :style="{ height }"
  >
    <!-- 编辑器 -->
    <MdEditor
      v-if="mode !== 'preview'"
      v-model="innerValue"
      :theme="isDark ? 'dark' : 'light'"
      :toolbars="toolbars"
      :preview="false"
      :onUploadImg="onUploadImg"
      class="editor"
    />

    <!-- 预览 -->
    <MdPreview
      v-if="mode !== 'edit'"
      class="preview"
      :modelValue="innerValue"
      :theme="isDark ? 'dark' : 'light'"
      :code-theme="isDark ? 'atom' : 'github'"
    />
  </div>
</template>
<script setup lang="ts">
import { computed } from 'vue'
import { MdEditor, MdPreview } from 'md-editor-v3'
import type { ToolbarNames } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import EmojiText from '@/utils/emojo'

/** v-model */
const modelValue = defineModel<string>({
  required: true
})

/** 组件参数 */
const props = withDefaults(
  defineProps<{
    action?: string
    mode?: 'edit' | 'preview' | 'both'
    height?: string
  }>(),
  {
    mode: 'both',
    height: '600px'
  }
)

/** 内部值（避免直接改 props） */
const innerValue = computed({
  get: () => modelValue.value,
  set: v => (modelValue.value = v)
})

/** 暗黑模式 */
const isDark = computed(() =>
  document.documentElement.classList.contains('dark')
)

/** 工具栏（类型安全） */
const toolbars: ToolbarNames[] = [
  'bold',
  'italic',
  'underline',
  'strikeThrough',
  'title',
  'quote',
  'unorderedList',
  'orderedList',
  'task',
  'code',
  'table',
  'link',
  'image',
  'preview',
  'fullscreen'
]

/** 图片上传 */
const userStore = useUserStore()
const { accessToken } = userStore

const onUploadImg = async (
  files: File[],
  callback: (urls: string[]) => void
) => {
  if (!props.action) return

  const formData = new FormData()
  formData.append('file', files[0])

  try {
    const res = await fetch(props.action, {
      method: 'POST',
      headers: {
        Authorization: accessToken
      },
      body: formData
    }).then(r => r.json())

    if (res.errno === 0) {
      callback([res.data.url])
      ElMessage.success(`图片上传成功 ${EmojiText[200]}`)
    } else {
      throw new Error(res.message)
    }
  } catch (e) {
    console.error(e)
    ElMessage.error(`图片上传失败 ${EmojiText[500]}`)
  }
}
</script>
<style scoped lang="scss">
.md-editor-wrapper {
  display: grid;
  grid-template-columns: 1fr 1fr;
  border: 1px solid var(--art-border-color);
  border-radius: 8px;
  overflow: hidden;
}

.editor {
  height: 100%;
  border-right: 1px solid var(--art-border-color);
}

/* md-editor 内部必须 deep 才能撑满 */
.editor :deep(.md-editor),
.editor :deep(.md-editor-content) {
  height: 100%;
}

.preview {
  padding: 20px;
  overflow-y: auto;
  background-color: var(--art-gray-50);
}
</style>
