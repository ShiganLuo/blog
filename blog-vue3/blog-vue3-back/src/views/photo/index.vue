<template>
  <div class="page-content">
    <el-row>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-button @click="handleAdd" v-ripple>新增相册</el-button>
      </el-col>
    </el-row>

    <el-empty v-if="!loading && albumList.length === 0" description="暂无相册" align="center" />

    <el-row class="album-container" :gutter="12" v-loading="loading">
      <el-col v-for="item of albumList" :key="item.id" :xs="12" :sm="8" :md="6">
        <div class="album-item">
          <div class="album-opreation">
            <el-dropdown placement="bottom-end" @command="(cmd: string) => handleCommand(cmd, item)">
              <i class="iconfont-sys" v-html="'&#xe839;'" style="color: #ffffff" />
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="edit">
                    <el-icon><Edit /></el-icon>编辑
                  </el-dropdown-item>
                  <el-dropdown-item command="delete">
                    <el-icon><Delete /></el-icon>删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <div class="album-photo-count">
            <div>{{ item.photoCount || 0 }} 张</div>
          </div>
          <el-image fit="cover" class="album-cover" :src="getCoverUrl(item.albumCover)">
            <template #error>
              <div class="w-[100%] h-[100%] grid place-items-center" style="background: #f5f7fa">
                <el-icon :size="32" color="#c0c4cc"><Picture /></el-icon>
              </div>
            </template>
          </el-image>
          <div class="album-name">{{ item.albumName }}</div>
        </div>
      </el-col>
    </el-row>

    <!-- 添加或修改相册对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="albumRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="相册名" prop="albumName">
          <el-input v-model="form.albumName" placeholder="请输入相册名" />
        </el-form-item>
        <el-form-item label="相册描述">
          <el-input v-model="form.description" placeholder="请输入相册描述" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="相册封面">
          <div class="upload-container">
            <el-upload
              class="cover-uploader"
              :http-request="imageUpload"
              :show-file-list="false"
              :before-upload="beforeUpload"
            >
              <div v-if="!form.albumCover" class="upload-placeholder">
                <el-icon class="upload-icon"><Plus /></el-icon>
                <div class="upload-text">点击上传封面</div>
              </div>
              <img v-else :src="getCoverUrl(form.albumCover)" class="cover-image" />
            </el-upload>
            <el-button class="el-top" type="primary" link @click="showImagePicker = true">从素材库选择</el-button>
          </div>
        </el-form-item>
        <ImagePicker v-model="showImagePicker" @select="handleImageSelect" />
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import ImagePicker from '@/components/Widgets/ImagePicker/index.vue'
import PhotoAlbumService from '@/api/photo/photoAlbumApi'
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type UploadRequestOptions } from 'element-plus'
import type { FormInstance } from 'element-plus'
import type { PhotoAlbumResult } from '@/types/photo/photo'
import { PhotoService } from '@/api/photo/photoApi'
import { Plus, Edit, Delete, Picture } from '@element-plus/icons-vue'
import EmojiText from '@/utils/emojo'

const albumList = ref<PhotoAlbumResult[]>([])
const open = ref(false)
const loading = ref(true)
const title = ref('')
const albumRef = ref<FormInstance>()
const showImagePicker = ref(false)

const initialFormState = {
  id: null as number | null,
  albumName: '',
  description: '',
  albumCover: ''
}

const form = reactive({ ...initialFormState })

const rules = reactive({
  albumName: [{ required: true, message: '相册名称不能为空', trigger: 'blur' }]
})

// 获取封面完整 URL
const getCoverUrl = (cover: string): string => {
  if (!cover) return ''
  if (cover.startsWith('http://') || cover.startsWith('https://')) return cover
  const base = import.meta.env.VITE_MINIO_URL || 'http://localhost:9007'
  const path = cover.startsWith('/') ? cover : `/${cover}`
  return `${base}${path}`
}

// 获取相册列表
const getList = async () => {
  loading.value = true
  const res = await PhotoAlbumService.listPhotoAlbum()
  if (res.code === 200) {
    albumList.value = (res.result as PhotoAlbumResult[]) || []
  }
  loading.value = false
}

// 下拉菜单操作
const handleCommand = (cmd: string, item: PhotoAlbumResult) => {
  if (cmd === 'edit') {
    handleUpdate(item)
  } else if (cmd === 'delete') {
    handleDelete(item.id)
  }
}

// 素材库选择
const handleImageSelect = (image: { url: string; id: number }) => {
  form.albumCover = image.url
}

// 图片上传
const imageUpload = async (options: UploadRequestOptions) => {
  const formData = new FormData()
  formData.append('file', options.file)
  try {
    const res = await PhotoService.uploadPhoto(formData)
    if (res.code === 200) {
      form.albumCover = res.result.imageUrl
      ElMessage.success(`图片上传成功 ${EmojiText[200]}`)
    } else {
      ElMessage.error(`图片上传失败 ${res.message} ${EmojiText[500]}`)
    }
  } catch (err) {
    ElMessage.error('图片上传失败')
  }
}

const beforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) { ElMessage.error('只能上传图片文件!'); return false }
  if (!isLt5M) { ElMessage.error('图片大小不能超过 5MB!'); return false }
  return true
}

const cancel = () => {
  open.value = false
  reset()
}

const reset = () => {
  Object.assign(form, initialFormState)
}

const handleAdd = () => {
  reset()
  open.value = true
  title.value = '新增相册'
}

const handleUpdate = (row: PhotoAlbumResult) => {
  reset()
  Object.assign(form, { id: row.id, albumName: row.albumName, description: row.description, albumCover: row.albumCover })
  open.value = true
  title.value = '修改相册'
}

const submitForm = async () => {
  if (!albumRef.value) return
  await albumRef.value.validate(async (valid) => {
    if (valid) {
      let res
      if (form.id) {
        res = await PhotoAlbumService.updateAlbum({ id: form.id, albumName: form.albumName, description: form.description, albumCover: form.albumCover })
      } else {
        res = await PhotoAlbumService.addAlbum({ albumName: form.albumName, description: form.description, albumCover: form.albumCover })
      }
      if (res.code === 200) {
        ElMessage.success(res.message)
        open.value = false
        getList()
      }
    }
  })
}

const handleDelete = async (id: number) => {
  const confirm = await ElMessageBox.confirm('确认删除该相册？', '提示', { confirmButtonText: '确认', cancelButtonText: '取消' })
  if (confirm === 'confirm') {
    const res = await PhotoAlbumService.deleteAlbum(id)
    if (res.code === 200) {
      ElMessage.success(res.message)
      getList()
    }
  }
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.upload-container {
  .cover-uploader {
    position: relative;
    overflow: hidden;
    cursor: pointer;
    border-radius: 6px;
    transition: var(--el-transition-duration);
    &:hover { border-color: var(--el-color-primary); }
    .upload-placeholder {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      width: 260px;
      height: 160px;
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      .upload-icon { font-size: 28px; color: #8c939d; }
      .upload-text { margin-top: 8px; font-size: 14px; color: #8c939d; }
    }
    .cover-image { display: block; width: 260px; height: 160px; object-fit: cover; }
  }
}

.album-container { margin: 20px 0; }
.album-cover { position: relative; border-radius: 4px; width: 100%; height: 200px; }
.album-photo-count {
  display: flex; align-items: center; justify-content: space-between;
  font-size: 1rem; z-index: 1000; position: absolute;
  left: 0; right: 0; padding: 0 0.5rem; bottom: 2.6rem; color: #fff;
}
.album-name { text-align: center; margin-top: 0.5rem; }
.album-item { position: relative; cursor: pointer; margin-bottom: 1rem; }
.album-opreation { position: absolute; z-index: 1000; top: 0.5rem; right: 0.8rem; }
</style>
