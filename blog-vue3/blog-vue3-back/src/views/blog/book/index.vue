<template>
  <div class="page-content">
    <el-row>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-button type="primary" @click="handleAdd" v-ripple>新增书</el-button>
      </el-col>
    </el-row>

    <el-empty v-if="!loading && bookList.length === 0" description="暂无书，点击左上角创建" align="center" />

    <el-table v-loading="loading" :data="bookList" style="width: 100%; margin-top: 16px">
      <el-table-column label="封面" width="90">
        <template #default="{ row }">
          <el-image
            fit="cover"
            style="width: 70px; height: 46px; border-radius: 4px"
            :src="getCoverUrl(row.coverImage)"
          >
            <template #error>
              <div class="cover-fallback"><el-icon :size="20" color="#c0c4cc"><Picture /></el-icon></div>
            </template>
          </el-image>
        </template>
      </el-table-column>
      <el-table-column label="书名" prop="title" min-width="160" show-overflow-tooltip>
        <template #default="{ row }">
          <div>{{ row.title }}</div>
          <div class="sub-text" v-if="row.subtitle">{{ row.subtitle }}</div>
        </template>
      </el-table-column>
      <el-table-column label="章节数" prop="chapterCount" width="80" align="center" />
      <el-table-column label="状态" width="90" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '已上架' : '草稿' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" width="170">
        <template #default="{ row }">{{ formatTime(row.updatedAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="260" align="center">
        <template #default="{ row }">
          <el-button link type="primary" @click="goToEdit(row)">章节编排</el-button>
          <el-button link type="primary" @click="handleExport(row)">导出</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增书对话框 -->
    <el-dialog title="新增书" v-model="open" width="560px" append-to-body>
      <el-form ref="bookRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="书名" prop="title">
          <el-input v-model="form.title" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="副标题">
          <el-input v-model="form.subtitle" placeholder="选填" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="选填" />
        </el-form-item>
        <el-form-item label="封面">
          <div class="upload-container">
            <el-upload class="cover-uploader" :http-request="imageUpload" :show-file-list="false" :before-upload="beforeUpload">
              <div v-if="!form.coverImage" class="upload-placeholder">
                <el-icon class="upload-icon"><Plus /></el-icon>
                <div class="upload-text">点击上传封面</div>
              </div>
              <img v-else :src="getCoverUrl(form.coverImage)" class="cover-image" />
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
import BookAdminService, { BookListItem } from '@/api/blog/bookApi'
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type UploadRequestOptions } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { Plus, Picture } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { PhotoService } from '@/api/photo/photoApi'
import { downloadExcel } from '@/utils/utils'
import EmojiText from '@/utils/emojo'

const bookList = ref<BookListItem[]>([])
const open = ref(false)
const loading = ref(true)
const bookRef = ref<FormInstance>()
const router = useRouter()
const showImagePicker = ref(false)

const initialFormState = {
  title: '',
  subtitle: '',
  description: '',
  coverImage: ''
}
const form = reactive({ ...initialFormState })
const rules = reactive({
  title: [{ required: true, message: '书名不能为空', trigger: 'blur' }]
})

const getCoverUrl = (cover?: string): string => {
  if (!cover) return ''
  if (cover.startsWith('http://') || cover.startsWith('https://')) return cover
  const base = import.meta.env.VITE_MINIO_URL || 'http://localhost:9007'
  const path = cover.startsWith('/') ? cover : `/${cover}`
  return `${base}${path}`
}

const formatTime = (t?: string): string => {
  if (!t) return '-'
  return t.replace('T', ' ').slice(0, 16)
}

const getList = async () => {
  loading.value = true
  const res = await BookAdminService.listBook()
  if (res.code === 200) {
    bookList.value = (res.result as BookListItem[]) || []
  }
  loading.value = false
}

const handleImageSelect = (image: { url: string; id: number }) => {
  form.coverImage = image.url
}

const imageUpload = async (options: UploadRequestOptions) => {
  const formData = new FormData()
  formData.append('file', options.file)
  try {
    const res = await PhotoService.uploadPhoto(formData)
    if (res.code === 200) {
      form.coverImage = res.result.imageUrl
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
  Object.assign(form, initialFormState)
}

const handleAdd = () => {
  Object.assign(form, initialFormState)
  open.value = true
}

const submitForm = async () => {
  if (!bookRef.value) return
  await bookRef.value.validate(async (valid) => {
    if (valid) {
      const res = await BookAdminService.addBook({ ...form })
      if (res.code === 200) {
        ElMessage.success(res.message)
        open.value = false
        getList()
      }
    }
  })
}

const goToEdit = (row: BookListItem) => {
  router.push({ path: '/blog/book/edit/' + row.id })
}

const handleExport = (row: BookListItem) => {
  downloadExcel(BookAdminService.exportBook(row.id), `${row.title || 'book'}.zip`)
}

const handleDelete = async (row: BookListItem) => {
  const confirm = await ElMessageBox.confirm(`确认删除《${row.title}》？书中的章节（文章）不会被删除。`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消'
  })
  if (confirm === 'confirm') {
    const res = await BookAdminService.deleteBook(row.id)
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
.sub-text { font-size: 12px; color: var(--el-text-color-secondary); }
.cover-fallback { display: grid; place-items: center; width: 70px; height: 46px; background: #f5f7fa; border-radius: 4px; }
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
</style>
