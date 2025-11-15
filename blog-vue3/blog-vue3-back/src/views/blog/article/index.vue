<template>
  <div class="page-content">
    <table-bar
      :showTop="false"
      @search="handleQuery"
      @reset="resetForm(queryRef)"
      @changeColumn="changeColumn"
      :columns="columns"
    >
      <template #top>
        <el-form :model="queryParams" ref="queryRef" label-width="82px">
          <el-row :gutter="20">
            <form-input
              label="文章标题"
              prop="articleTitle"
              v-model="queryParams.keywords"
              @keyup.enter="handleQuery"
            />
            <form-select
              label="文章分类"
              prop="categoryId"
              v-model="queryParams.categoryId"
              :options="categoryOption"
            />
            <form-select
              label="文章标签"
              prop="status"
              v-model="queryParams.tagId"
              :options="tagOption"
            />
            <form-select
              label="文章类型"
              prop="type"
              v-model="queryParams.type"
              :options="article_type"
            />
            <el-col :xs="24" :sm="12" :lg="6">
              <el-form-item label="&nbsp;" />
            </el-col>
          </el-row>
        </el-form>
      </template>
      <template #bottom>
        <div class="button-group">
          <el-button @click="handleAdd" v-auth="['blog:article:add']" v-ripple>新增 </el-button>
          <el-button
            @click="handleDelete"
            :disabled="multiple"
            v-auth="['blog:article:remove']"
            v-ripple
            >删除
          </el-button>
          <el-button @click="handleExport" v-auth="['blog:article:export']" v-ripple
            >导出
          </el-button>
          <div class="center-radio-group">
            <el-radio-group v-model="queryParams.status" @change="handleStatusChange">
              <el-radio-button value="">全部</el-radio-button>
              <el-radio-button value="1">公开</el-radio-button>
              <el-radio-button value="2">私密</el-radio-button>
              <el-radio-button value="3">草稿</el-radio-button>
              <el-radio-button value="4">回收站</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>
    </table-bar>

    <art-table
      v-loading="loading"
      :data="articleListForm"
      selection
      :total="total"
      :current-page="queryParams.current"
      :page-size="queryParams.size"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      @selection-change="handleSelectionChange"
      row-key="id"
    >
      <template #default>
        <el-table-column label="文章作者" align="center" prop="authorName" v-if="columns[0].show" />
        <el-table-column label="文章分类" align="center" v-if="columns[1].show">
          <template #default="{ row }">
            <div
              style="display: flex; flex-wrap: wrap; justify-content: center;"
            >
              <el-tooltip
                v-for="(name, idx) in row.categoryNameList"
                :key="idx"
                :content="name"
                placement="top"
              >
                <el-tag
                  type="info"
                  style="margin: 2px; max-width: 100px; overflow: hidden; text-overflow: ellipsis;"
                >
                  {{ name }}
                </el-tag>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="缩略图" align="center" prop="articleCover" v-if="columns[2].show">
          <template #default="scope">
            <el-image
              class="article-cover"
              :src="scope.row.articleCover ? scope.row.articleCover : defaultAvatar"
            />
          </template>
        </el-table-column>
        <el-table-column
          label="文章标题"
          align="center"
          prop="articleTitle"
          v-if="columns[3].show"
        />

        <el-table-column
          label="摘要"
          align="center"
          prop="articleAbstract"
          v-if="columns[4].show"
        />
        <el-table-column
          label="文章内容"
          align="center"
          prop="articleContent"
          v-if="columns[5].show"
        />
        <el-table-column label="置顶" align="center" prop="isTop" v-if="columns[6].show">
          <template #default="scope">
            <el-switch
              :disabled="scope.row.isDeleted == 1"
              v-model="scope.row.isTop"
              :active-value="1"
              :inactive-value="0"
              @change="updateTopOrFeaturedChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="推荐" align="center" prop="isFeatured" v-if="columns[7].show">
          <template #default="scope">
            <el-switch
              :disabled="scope.row.isDeleted == 1"
              v-model="scope.row.isFeatured"
              :active-value="1"
              :inactive-value="0"
              @change="updateTopOrFeaturedChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="删除" align="center" prop="isDeleted" v-if="columns[8].show" />
        <el-table-column label="状态" align="center" prop="status" v-if="columns[9].show" />
        <el-table-column label="类型" align="center" prop="type" v-if="columns[10].show">
          <template #default="scope">
            <dict-tag :options="article_type" :value="scope.row.type" />
          </template>
        </el-table-column>
        <el-table-column
          label="原文链接"
          align="center"
          prop="originalUrl"
          v-if="columns[11].show"
        />
        <el-table-column label="浏览量" align="center" prop="viewsCount" v-if="columns[12].show" />
        <el-table-column label="创建时间" align="center" prop="createdAt" v-if="columns[13].show">
          <template #default="scope">
            {{ parseTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="文章标签" align="center" v-if="columns[14].show">
          <template #default="{ row }">
            <div
              style="display: flex; flex-wrap: wrap; justify-content: center;"
            >
              <el-tooltip
                v-for="(name, idx) in row.tagNameList"
                :key="idx"
                :content="name"
                placement="top"
              >
                <el-tag
                  type="info"
                  style="margin: 2px; max-width: 100px; overflow: hidden; text-overflow: ellipsis;"
                >
                  {{ name }}
                </el-tag>
              </el-tooltip>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center">
          <template #default="scope">
            <button-table
              v-if="!deleteBut"
              type="edit"
              v-auth="['blog:article:edit']"
              @click="handleUpdate(scope.row)"
            />
            <button-table v-else type="add" icon="&#xe708;" @click="handleRestore(scope.row)" />
            <button-table
              type="delete"
              v-auth="['blog:article:remove']"
              @click="handleDelete(scope.row)"
            />
          </template>
        </el-table-column>
      </template>
    </art-table>
  </div>
</template>
<script setup lang="ts">
  import { ArticleService } from '@/api/blog/articleApi'
  import { ref, reactive } from 'vue'
  import { resetForm } from '@/utils/utils'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { CategoryService } from '@/api/blog/categoryApi'
  import { TagService } from '@/api/blog/tagApi'
  import { parseTime } from '@/utils/utils'
  import defaultAvatar from '@/assets/img/avatar/default-avatar.png'
  
  const loading = ref(true)
  const ids = ref([])
  const multiple = ref(true)
  const total = ref(0)
  const queryRef = ref()
  const deleteBut = ref(false)
  const queryParams = reactive({
    current: 1,
    size: 10,
    keywords: '',
    categoryId: 0,
    tagId: 0,
    type: '',
    status: '',
    isDeleted: 0
  })
  interface initialArticleFormState {
    id: number,
    authorName: string,
    articleCover: string,
    articleTitle: string,
    categoryNameList: string[],
    articleAbstract: string,
    articleContent: string,
    isTop: number,
    isFeatured: number,
    isDeleted: number,
    status: string,           // 默认公开
    type: string,             // 默认原创
    originalUrl: string,
    viewsCount: number,
    createdAt: string,
    tagNameList: string[],
  }
  const articleListForm = ref<initialArticleFormState[]>([])
  /** 查询文章列表 */
  const getList = async () => {
    loading.value = true
    const query = { ...queryParams }
    if (query.status === '4') {
      query.status = ''
    }
    const res = await ArticleService.listArticle(query)
    if (res.code === 200) {
      Object.assign(articleListForm.value, res.result.list)
      total.value = res.result.total;
      loading.value = false;
    }
  }

  const columns = reactive([
    { name: '文章作者', show: true },
    { name: '文章分类', show: true },
    { name: '缩略图', show: true },
    { name: '文章标题', show: true },
    { name: '摘要', show: false },
    { name: '文章内容', show: false },
    { name: '置顶', show: true },
    { name: '推荐', show: true },
    { name: '删除', show: false },
    { name: '状态', show: false },
    { name: '类型', show: true },
    { name: '原文链接', show: false },
    { name: '浏览量', show: true },
    { name: '创建时间', show: true },
    { name: '文章标签', show: true }
  ])

  const changeColumn = (list: any) => {
    columns.values = list
  }

  /** 搜索按钮操作 */
  const handleQuery = () => {
    queryParams.current = 1
    getList()
  }

  /** 每页条数改变 */
  const handleSizeChange = (size: number) => {
    queryParams.size = size
    getList()
  }

  /** 当前页改变 */
  const handleCurrentChange = (page: number) => {
    queryParams.current = page
    getList()
  }

  // 多选框选中数据
  const handleSelectionChange = (selection: any) => {
    ids.value = selection.map((item: any) => item.id)
    multiple.value = !selection.length
  }

  import { useRouter } from 'vue-router'
  const router = useRouter()
  /** 新增按钮操作 */
  const handleAdd = () => {
    router.push({ path: '/blog/article-publish/index/0', query: { current: queryParams.current } })
  }

  /** 修改按钮操作 */
  const handleUpdate = async (row: any) => {
    router.push({
      path: '/blog/article-publish/index/' + row.id
    })
  }

  /** 删除按钮操作 */
  const handleDelete = async (row: any) => {
    const _ids = row.id ? [row.id] : ids.value
    const Tr = await ElMessageBox.confirm(
      `是否确认${deleteBut.value ? ' （彻底） ' : ''}删除文章编号为"${_ids}"的数据项？`
    )
    if (Tr && !deleteBut.value) {
      const res = await ArticleService.updateArticle({ ids: _ids, isDeleted: 1 })
      if (res.code === 200) {
        getList()
        ElMessage.success(res.message)
      }
    }
    if (Tr && deleteBut.value) {
      const res = await ArticleService.deleteArticle(_ids)
      if (res.code === 200) {
        getList()
        ElMessage.success(res.message)
      }
    }
  }

  /** 恢复按钮操作 */
  const handleRestore = async (row: any) => {
    const _ids = row.id ? [row.id] : ids.value
    const Tr = await ElMessageBox.confirm('是否确认恢复文章编号为"' + _ids + '"的数据项？')
    if (Tr) {
      const res = await ArticleService.updateArticle({ ids: _ids, isDeleted: 0 })
      if (res.code === 200) {
        getList()
        ElMessage.success(res.message)
      }
    }
  }

  // 文章分类
  const categoryOption = ref<any>([])
  const searchCategories = async () => {
    const res = await CategoryService.searchCategories('')
    if (res.code === 200) {
      categoryOption.value = res.result.map((item: any) => {
        return {
          label: item.categoryName,
          value: item.id
        }
      })
    }
  }

  // 文章标签
  const tagOption = ref<any>([])
  const searchTags = async () => {
    const res = await TagService.searchTags('')
    if (res.code === 200) {
      tagOption.value = res.result.map((item: any) => {
        return {
          label: item.tagName,
          value: item.id
        }
      })
    }
  }

  // 获取文章类型
  import { useDict, DictType } from '@/utils/dict'
  const article_type = ref<DictType[]>([]) // 系统字典数据
  const getuseDict = async () => {
    const { articleType } = await useDict('articleType')
    article_type.value = articleType;
  }

  /** 状态切换操作 */
  const handleStatusChange = () => {
    // 如果选择回收站，设置isDeleted为1
    if (queryParams.status === '4') {
      deleteBut.value = true
      queryParams.isDeleted = 1
    } else {
      deleteBut.value = false
      queryParams.isDeleted = 0
    }
    queryParams.current = 1
    getList()
  }

  /** 置顶/推荐切换操作 */
  const updateTopOrFeaturedChange = async (row: any) => {
    const res = await ArticleService.updateTopOrFeatured({
      id: row.id,
      isTop: row.isTop,
      isFeatured: row.isFeatured
    })
    if (res.code === 200) {
      ElMessage.success(res.message)
      getList()
    }
  }

  /** 导出按钮操作 */
  const handleExport = () => {
    // 暂时未开发
    ElMessage.warning('暂未开发')
  }

  import { useRoute } from 'vue-router'
  import { ar } from 'element-plus/es/locale'
  const route = useRoute()
  onActivated(() => {
    if (route.query.current) {
      queryParams.current == Number(route.query.current)
    }
  })

  // 初始化
  onMounted(() => {
    searchCategories()
    searchTags()
    getuseDict()
    getList()
  })
</script>
<style scoped>
  .button-group {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
  }

  .center-radio-group {
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    padding: 0 16px;
  }
  .article-cover {
    position: relative;
    width: 100%;
    height: 90px;
    border-radius: 4px;
  }
</style>
