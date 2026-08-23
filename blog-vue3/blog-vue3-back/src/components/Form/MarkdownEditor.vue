<template>
  <div
    class="md-editor-wrapper"
    :style="{ height }"
  >
    <!-- 编辑器 -->
    <MdEditor
      v-if="mode !== 'preview'"
      ref="editorRef"
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

    <!-- 素材库弹窗 -->
    <ImagePicker v-model="showImagePicker" @select="handleImageSelect" />
  </div>
</template>
<script setup lang="ts">
import { computed, ref, onMounted, nextTick, watch } from 'vue'
import { MdEditor, MdPreview } from 'md-editor-v3'
import type { ToolbarNames } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import EmojiText from '@/utils/emojo'
import ImagePicker from '@/components/Widgets/ImagePicker/index.vue'

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

/** 内部值 */
const innerValue = computed({
  get: () => modelValue.value,
  set: v => (modelValue.value = v)
})

/** 暗黑模式 */
const isDark = computed(() =>
  document.documentElement.classList.contains('dark')
)

/** 工具栏 */
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

/** 图片素材库 */
const showImagePicker = ref(false)
const editorRef = ref()

const handleImageSelect = (image: { url: string; id: number }) => {
  const text = `![](${image.url})`
  modelValue.value += text
}

// 在图片下拉菜单中注入"从素材库选择"选项
const injectLibraryOption = () => {
  const editorEl = editorRef.value?.$el
  if (!editorEl) return

  // 找到图片下拉菜单中的所有 menu-item-image
  const menuItems = editorEl.querySelectorAll('.md-editor-menu-item-image')
  if (!menuItems.length) return

  // 检查是否已注入
  const parent = menuItems[0].parentElement
  if (parent?.querySelector('.md-editor-menu-item-library')) return

  // 创建新菜单项
  const li = document.createElement('li')
  li.className = 'md-editor-menu-item md-editor-menu-item-image md-editor-menu-item-library'
  li.textContent = '从素材库选择'
  li.setAttribute('role', 'menuitem')
  li.setAttribute('tabindex', '0')
  li.style.cursor = 'pointer'
  li.addEventListener('click', () => {
    showImagePicker.value = true
  })

  parent?.appendChild(li)
}

// 监听编辑器渲染完成后注入
watch(() => editorRef.value, () => {
  nextTick(() => {
    injectLibraryOption()
  })
})

onMounted(() => {
  nextTick(() => {
    setTimeout(injectLibraryOption, 500)
  })
})

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
        Authorization: `Bearer ${accessToken}`
      },
      body: formData
    }).then(r => r.json())

    if (res.code === 200) {
      callback([res.result.imageUrl])
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
