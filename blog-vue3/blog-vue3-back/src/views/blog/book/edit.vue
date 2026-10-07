<template>
  <div class="page-content" v-loading="loading">
    <!-- 顶部：书信息 -->
    <el-card shadow="never">
      <div class="book-head">
        <div class="cover-box">
          <el-image fit="cover" class="cover" :src="getCoverUrl(book.coverImage)">
            <template #error>
              <div class="cover-fallback"><el-icon :size="24" color="#c0c4cc"><Picture /></el-icon></div>
            </template>
          </el-image>
        </div>
        <div class="info-form">
          <el-form label-width="70px" size="default">
            <el-row :gutter="12">
              <el-col :span="12">
                <el-form-item label="书名">
                  <el-input v-model="book.title" placeholder="书名" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="副标题">
                  <el-input v-model="book.subtitle" placeholder="选填" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="12">
              <el-col :span="12">
                <el-form-item label="简介">
                  <el-input v-model="book.description" type="textarea" :rows="2" placeholder="选填" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="状态">
                  <el-radio-group v-model="book.status">
                    <el-radio :value="0">草稿</el-radio>
                    <el-radio :value="1">上架</el-radio>
                  </el-radio-group>
                  <el-button type="primary" style="margin-left: 16px" @click="saveBookInfo">保存书信息</el-button>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </div>
      </div>
      <div class="head-actions">
        <el-button @click="goBack">← 返回书架列表</el-button>
        <el-button type="success" @click="handleExport">导出 ZIP</el-button>
      </div>
    </el-card>

    <!-- 章节编排 -->
    <el-row :gutter="16" style="margin-top: 16px">
      <!-- 已选章节 -->
      <el-col :xs="24" :md="14">
        <el-card shadow="never">
          <template #header>
            <span>章节目录（{{ chapters.length }} 章）</span>
            <span class="header-tip">拖序用「上移/下移」，改名不影响文章标题</span>
          </template>
          <el-table :data="chapters" size="default" row-key="articleId">
            <el-table-column type="index" label="#" width="50" :index="index1" />
            <el-table-column label="章节名" prop="chapterTitle" min-width="180" show-overflow-tooltip />
            <el-table-column label="原文标题" min-width="140" show-overflow-tooltip>
              <template #default="{ row }">
                <span v-if="row.articleTitle !== row.chapterTitle" class="sub-text">{{ row.articleTitle }}</span>
                <span v-else class="sub-text">—</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="230" align="center">
              <template #default="{ $index, row }">
                <el-button link type="primary" :disabled="$index === 0" @click="moveChapter($index, -1)">上移</el-button>
                <el-button link type="primary" :disabled="$index === chapters.length - 1" @click="moveChapter($index, 1)">下移</el-button>
                <el-button link type="primary" @click="renameChapter(row)">改名</el-button>
                <el-button link type="danger" @click="removeChapter(row)">移除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="chapters.length === 0" description="还没有章节，从右侧加入文章" :image-size="80" />
        </el-card>
      </el-col>

      <!-- 添加章节 -->
      <el-col :xs="24" :md="10">
        <el-card shadow="never">
          <template #header>
            <span>加入文章</span>
          </template>
          <div class="search-bar">
            <el-input
              v-model="articleQuery.keyword"
              placeholder="按标题搜索文章"
              clearable
              @keyup.enter="searchArticles"
              @clear="searchArticles"
            >
              <template #append>
                <el-button @click="searchArticles"><el-icon><Search /></el-icon></el-button>
              </template>
            </el-input>
          </div>

          <el-table
            ref="articleTableRef"
            :data="articleList"
            size="small"
            v-loading="articleLoading"
            max-height="360"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="40" :selectable="rowSelectable" />
            <el-table-column label="文章标题" prop="articleTitle" min-width="180" show-overflow-tooltip />
            <el-table-column label="状态" width="70" align="center">
              <template #default="{ row }">
                <el-tag size="small" :type="row.status === 1 ? 'success' : 'info'">
                  {{ row.status === 1 ? '公开' : row.status === 3 ? '草稿' : '私密' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>

          <div class="pager">
            <el-pagination
              v-model:current-page="articleQuery.current"
              v-model:page-size="articleQuery.size"
              :total="articleTotal"
              :page-sizes="[5, 10, 20]"
              layout="total, sizes, prev, pager, next"
              @current-change="getArticles"
              @size-change="searchArticles"
            />
          </div>

          <div class="add-bar">
            <el-button type="primary" :disabled="selectedArticles.length === 0" @click="addSelected">
              加入所选（{{ selectedArticles.length }}）
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import BookAdminService, { ChapterItem } from '@/api/blog/bookApi'
import { ArticleService } from '@/api/blog/articleApi'
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { TableInstance } from 'element-plus'
import { Picture, Search } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { downloadExcel } from '@/utils/utils'

const route = useRoute()
const router = useRouter()
const bookId = Number(route.params.bookId)

const loading = ref(false)
const chapters = ref<ChapterItem[]>([])

const book = reactive({
  id: bookId,
  title: '',
  subtitle: '',
  description: '',
  coverImage: '',
  status: 0
})

// 文章候选列表
const articleTableRef = ref<TableInstance>()
const articleList = ref<any[]>([])
const articleLoading = ref(false)
const articleTotal = ref(0)
const selectedArticles = ref<any[]>([])
const articleQuery = reactive({
  current: 1,
  size: 10,
  keyword: '',
  isDeleted: false
})

const index1 = (i: number) => i + 1

const getCoverUrl = (cover?: string): string => {
  if (!cover) return ''
  if (cover.startsWith('http://') || cover.startsWith('https://')) return cover
  const base = import.meta.env.VITE_MINIO_URL || 'http://localhost:9007'
  const path = cover.startsWith('/') ? cover : `/${cover}`
  return `${base}${path}`
}

/** 载入书详情 + 章节 */
const getBook = async () => {
  loading.value = true
  const res = await BookAdminService.getBook(bookId)
  if (res.code === 200) {
    const detail = res.result as any
    book.title = detail.title || ''
    book.subtitle = detail.subtitle || ''
    book.description = detail.description || ''
    book.coverImage = detail.coverImage || ''
    book.status = detail.status ?? 0
    chapters.value = detail.chapters || []
  } else {
    ElMessage.error('书不存在')
    router.push('/blog/book/list')
  }
  loading.value = false
}

/** 保存书信息 */
const saveBookInfo = async () => {
  if (!book.title.trim()) {
    ElMessage.warning('书名不能为空')
    return
  }
  const res = await BookAdminService.updateBook({
    id: book.id,
    title: book.title,
    subtitle: book.subtitle,
    description: book.description,
    coverImage: book.coverImage,
    status: book.status
  })
  if (res.code === 200) {
    ElMessage.success(res.message)
    getBook()
  }
}

/** 文章候选列表 */
const getArticles = async () => {
  articleLoading.value = true
  const res = await ArticleService.listArticle({ ...articleQuery })
  if (res.code === 200) {
    articleList.value = res.result.list
    articleTotal.value = res.result.total
  }
  articleLoading.value = false
}

const searchArticles = () => {
  articleQuery.current = 1
  getArticles()
}

const handleSelectionChange = (rows: any[]) => {
  selectedArticles.value = rows
}

/** 已在书中的文章不可再勾选 */
const rowSelectable = (row: any) => {
  return !chapters.value.some((c) => c.articleId === row.id)
}

/** 加入所选文章 */
const addSelected = async () => {
  const ids = selectedArticles.value.map((a) => a.id)
  const res = await BookAdminService.addArticles({ bookId, articleIds: ids })
  if (res.code === 200) {
    ElMessage.success(res.message)
    articleTableRef.value?.clearSelection()
    getBook()
    getArticles()
  }
}

/** 上移/下移：本地换位后提交整序 */
const moveChapter = async (index: number, dir: number) => {
  const target = index + dir
  if (target < 0 || target >= chapters.value.length) return
  const arr = [...chapters.value]
  ;[arr[index], arr[target]] = [arr[target], arr[index]]
  const res = await BookAdminService.reorder(bookId, arr.map((c) => c.articleId))
  if (res.code === 200) {
    chapters.value = arr
  }
}

/** 重命名章节 */
const renameChapter = async (row: ChapterItem) => {
  try {
    const { value } = await ElMessageBox.prompt('输入新章节名（留空则用文章标题）', '重命名章节', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputValue: row.chapterTitle
    })
    const res = await BookAdminService.renameChapter(bookId, row.articleId, (value || '').trim())
    if (res.code === 200) {
      ElMessage.success(res.message)
      getBook()
    }
  } catch {
    // 用户取消
  }
}

/** 移除章节 */
const removeChapter = async (row: ChapterItem) => {
  const confirm = await ElMessageBox.confirm(`移除章节「${row.chapterTitle}」？文章本身不受影响。`, '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消'
  })
  if (confirm === 'confirm') {
    const res = await BookAdminService.removeArticle(bookId, row.articleId)
    if (res.code === 200) {
      ElMessage.success(res.message)
      getBook()
      getArticles()
    }
  }
}

/** 导出 ZIP */
const handleExport = () => {
  downloadExcel(BookAdminService.exportBook(bookId), `${book.title || 'book'}.zip`)
}

const goBack = () => {
  router.push('/blog/book/list')
}

onMounted(() => {
  getBook()
  getArticles()
})
</script>

<style lang="scss" scoped>
.book-head {
  display: flex;
  gap: 16px;
  .cover {
    width: 150px;
    height: 100px;
    border-radius: 6px;
  }
  .cover-fallback {
    display: grid;
    place-items: center;
    width: 150px;
    height: 100px;
    background: #f5f7fa;
    border-radius: 6px;
  }
  .info-form { flex: 1; }
}
.head-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
.header-tip {
  margin-left: 12px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}
.sub-text { font-size: 12px; color: var(--el-text-color-secondary); }
.search-bar { margin-bottom: 12px; }
.pager { margin-top: 10px; display: flex; justify-content: flex-end; }
.add-bar { margin-top: 12px; display: flex; justify-content: flex-end; }
</style>
