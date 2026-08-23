<template>
  <el-dialog
    v-model="visible"
    title="图片素材库"
    width="80%"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="image-picker">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索文件名..."
          clearable
          @keyup.enter="handleSearch"
          style="width: 300px"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-upload
          class="upload-btn"
          :show-file-list="false"
          :http-request="handleUpload"
          :before-upload="beforeUpload"
          accept="image/*"
        >
          <el-button type="success">上传新图片</el-button>
        </el-upload>
      </div>

      <!-- 图片网格 -->
      <div class="image-grid" v-loading="loading">
        <div
          v-for="image in imageList"
          :key="image.id"
          class="image-item"
          :class="{ selected: selectedImage?.id === image.id }"
          @click="handleSelect(image)"
        >
          <el-image
            :src="getImageDisplayUrl(image.filePath)"
            fit="cover"
            class="image-thumb"
            :preview-src-list="[getImageDisplayUrl(image.filePath)]"
            preview-teleported
          >
            <template #error>
              <div class="image-error">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
          <div class="image-info">
            <el-tooltip :content="image.fileName" placement="top">
              <span class="image-name">{{ image.fileName }}</span>
            </el-tooltip>
            <span class="image-size">{{ formatSize(image.fileSize) }}</span>
          </div>
          <div class="image-check" v-if="selectedImage?.id === image.id">
            <el-icon><Check /></el-icon>
          </div>
        </div>
        <el-empty v-if="!loading && imageList.length === 0" description="暂无图片" />
      </div>
    </div>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleConfirm" :disabled="!selectedImage">
        确认选择
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { Search, Picture, Check } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { PhotoService } from '@/api/photo/photoApi'
import type { UploadRequestOptions } from 'element-plus'

interface ImageItem {
  id: number
  filePath: string
  fileName: string
  fileSize: number
  mimeType: string
  createdBy: string
  createdAt: string
  updatedAt: string
}

const props = defineProps<{
  modelValue: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'select', image: { url: string; id: number }): void
}>()

const visible = ref(false)
const loading = ref(false)
const searchKeyword = ref('')
const imageList = ref<ImageItem[]>([])
const selectedImage = ref<ImageItem | null>(null)

// 监听 modelValue 变化
watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val) {
    loadImageList()
  }
})

// 监听 visible 变化
watch(visible, (val) => {
  emit('update:modelValue', val)
})

// 加载图片列表
const loadImageList = async () => {
  loading.value = true
  try {
    const res = await PhotoService.listImages(searchKeyword.value || undefined)
    if (res.code === 200) {
      imageList.value = (res.result || []) as ImageItem[]
    }
  } catch (error) {
    console.error('加载图片列表失败:', error)
    ElMessage.error('加载图片列表失败')
  } finally {
    loading.value = false
  }
}

// 获取图片显示 URL（相对路径拼接 MinIO 地址）
const getImageDisplayUrl = (filePath: string): string => {
  if (!filePath) return ''
  if (filePath.startsWith('http://') || filePath.startsWith('https://')) {
    return filePath
  }
  const minioUrl = import.meta.env.VITE_MINIO_URL || 'http://localhost:9007'
  // filePath 可能是 my-bucket/xxx.jpg 或 /my-bucket/xxx.jpg
  const path = filePath.startsWith('/') ? filePath : `/${filePath}`
  return `${minioUrl}${path}`
}

// 格式化文件大小
const formatSize = (kb: number): string => {
  if (kb < 1024) return `${kb} KB`
  return `${(kb / 1024).toFixed(1)} MB`
}

// 搜索
const handleSearch = () => {
  loadImageList()
}

// 重置搜索
const handleReset = () => {
  searchKeyword.value = ''
  loadImageList()
}

// 选择图片
const handleSelect = (image: ImageItem) => {
  selectedImage.value = image
}

// 确认选择（返回数据库中的相对路径）
const handleConfirm = () => {
  if (selectedImage.value) {
    emit('select', {
      url: getImageDisplayUrl(selectedImage.value.filePath),
      id: selectedImage.value.id
    })
    handleClose()
  }
}

// 关闭弹窗
const handleClose = () => {
  visible.value = false
  selectedImage.value = null
  searchKeyword.value = ''
}

// 上传前校验
const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

// 上传图片
const handleUpload = async (options: UploadRequestOptions) => {
  const formData = new FormData()
  formData.append('file', options.file)
  try {
    const res = await PhotoService.uploadPhoto(formData)
    if (res.code === 200) {
      ElMessage.success('上传成功')
      loadImageList() // 刷新列表
    } else {
      ElMessage.error(`上传失败: ${res.message}`)
    }
  } catch (error) {
    ElMessage.error('上传失败')
  }
}
</script>

<style lang="scss" scoped>
.image-picker {
  .search-bar {
    display: flex;
    gap: 10px;
    margin-bottom: 16px;
    align-items: center;

    .upload-btn {
      margin-left: auto;
    }
  }

  .image-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
    gap: 12px;
    max-height: 500px;
    overflow-y: auto;
    padding: 4px;

    .image-item {
      position: relative;
      border: 2px solid transparent;
      border-radius: 8px;
      overflow: hidden;
      cursor: pointer;
      transition: all 0.2s;

      &:hover {
        border-color: var(--el-color-primary-light-5);
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }

      &.selected {
        border-color: var(--el-color-primary);
        box-shadow: 0 0 0 2px var(--el-color-primary-light-5);
      }

      .image-thumb {
        width: 100%;
        height: 140px;
        display: block;
      }

      .image-error {
        width: 100%;
        height: 140px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: var(--el-fill-color-light);
        color: var(--el-text-color-placeholder);
        font-size: 32px;
      }

      .image-info {
        padding: 8px;
        background: var(--el-fill-color-blank);

        .image-name {
          display: block;
          font-size: 12px;
          color: var(--el-text-color-regular);
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .image-size {
          display: block;
          font-size: 11px;
          color: var(--el-text-color-placeholder);
          margin-top: 2px;
        }
      }

      .image-check {
        position: absolute;
        top: 8px;
        right: 8px;
        width: 24px;
        height: 24px;
        background: var(--el-color-primary);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 14px;
      }
    }
  }
}
</style>
