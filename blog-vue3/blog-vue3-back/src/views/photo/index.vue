<template>
  <div class="page-content">
    <el-row>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-form :model="queryParams" ref="queryRef">
          <el-input
            placeholder="请输入相册名称"
            v-model="queryParams.keywords"
            @keyup.enter="handleQuery"
          />
        </el-form>
      </el-col>
      <div style="width: 12px"></div>
      <el-col :xs="24" :sm="12" :lg="6" class="el-col2">
        <el-button @click="handleQuery" v-ripple>搜索 </el-button>
        <el-button @click="handleAdd" v-auth="['server:tag:add']" v-ripple>新增 </el-button>
        <el-button @click="handleDel" v-ripple>回收站 </el-button>
      </el-col>
    </el-row>

    <el-empty v-if="albumList == null" description="暂无相册" align="center" />

    <el-row class="album-container" :gutter="12" v-loading="loading">
      <el-col v-for="item of albumList" :key="item.id" :md="6">
        <div class="album-item" @click="checkPhoto(item)">
          <div class="album-opreation">
            <el-dropdown placement="bottom-end" @command="handleCommand">
              <i class="iconfont-sys" v-html="'&#xe839;'" style="color: #ffffff" />
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="'update' + JSON.stringify(item)">
                    <i class="iconfont-sys" v-html="'&#xe642;'" />编辑
                  </el-dropdown-item>
                  <el-dropdown-item :command="'delete' + item.id">
                    <i class="iconfont-sys" v-html="'&#xe783;'" />删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <div class="album-photo-count">
            <div>{{ item.photoCount }}</div>
            <i
              v-if="item.status == 2"
              class="iconfont-sys"
              v-html="'&#xe817;'"
              style="font-size: 1.5rem"
            />
          </div>
          <el-image fit="cover" class="album-cover" :src="item.albumCover" />
          <div class="album-name">{{ item.albumName }}</div>
        </div>
      </el-col>
    </el-row>

    <el-pagination
      :hide-on-single-page="true"
      class="pagination-container"
      :total="total"
      :current-page="queryParams.current"
      :page-size="queryParams.size"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      layout="prev, pager, next"
    />
    <!-- <art-table
      v-loading="loading"
      :data="albumList"
      :total="total"
      :current-page="queryParams.current"
      :page-size="queryParams.size"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      row-key="id"
    >
      <template #default>
        <el-table-column label="相册名称" align="center" prop="albumName" />
        <el-table-column label="相册封面" align="center" prop="albumCover">
          <template #default="scope">
            <el-image
              class="article-cover"
              :src="scope.row.albumCover ? scope.row.albumCover : defaultAvatar"
            />
          </template>
        </el-table-column>
        <el-table-column label="相册描述" align="center" prop="albumDesc" />
        <el-table-column label="操作" align="center">
          <template #default="scope">
            <button-table
              type="edit"
              v-auth="['website:album:edit']"
              @click="handleUpdate(scope.row)"
            />
            <button-table
              type="delete"
              v-auth="['website:album:remove']"
              @click="handleDelete(scope.row)"
            />
          </template>
        </el-table-column>
      </template>
    </art-table> -->

    <!-- 添加或修改相册管理对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="albumRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="相册名" prop="albumName">
          <el-input v-model="form.albumName" placeholder="请输入相册名" />
        </el-form-item>
        <el-form-item label="相册描述" prop="albumDesc">
          <el-input v-model="form.albumDesc" placeholder="请输入相册描述" />
        </el-form-item>
        <el-form-item label="相册封面" prop="albumCover">
          <div class="el-top upload-container">
            <el-upload
              class="cover-uploader"
              :action="uploadImageUrl"
              :headers="uploadHeaders"
              :show-file-list="false"
              :on-success="onSuccess"
              :on-error="onError"
              :before-upload="beforeUpload"
            >
              <div v-if="!form.albumCover" class="upload-placeholder">
                <el-icon class="upload-icon"><Plus /></el-icon>
                <div class="upload-text">点击上传封面</div>
              </div>
              <img v-else :src="form.albumCover" class="cover-image" />
            </el-upload>
            <div class="el-upload__tip">建议尺寸 16:9，jpg/png 格式</div>
          </div>
        </el-form-item>
        <el-form-item label="发布形式">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">公开</el-radio>
            <el-radio :value="2">私密</el-radio>
          </el-radio-group>
        </el-form-item>
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
  import PhotoAlbumService from '@/api/photo/photoAlbumApi'
  import { ref, reactive } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { FormInstance } from 'element-plus'
  import { PhotoAlbumResult } from '@/types/photo/photo'
  import { useUserStore } from '@/store/modules/user'
  import EmojiText from '@/utils/emojo'
  import { Plus } from '@element-plus/icons-vue'
  const albumList = ref<PhotoAlbumResult[]>([])
  const open = ref(false)
  const loading = ref(true)
  const total = ref(0)
  const title = ref('')
  const queryRef = ref()
  const albumRef = ref<FormInstance>()
  const userStore = useUserStore()
  let { accessToken } = userStore
  // 上传路径
  const uploadImageUrl = `${import.meta.env.VITE_API_BASE_URL}/blog/photo/admin/photos/albums/upload`
  // 传递 token
  const uploadHeaders = { Authorization: accessToken }
  // 定义初始表单状态
  const initialFormState = {
    id: null,
    albumName: '',
    albumDesc: '',
    albumCover: '',
    photoCount: 0,
    status: 1
  }
  const form = reactive({ ...initialFormState })
  const queryParams = reactive({
    current: 1,
    size: 10,
    keywords: ''
  })
  const rules = reactive({
    albumName: [
      {
        required: true,
        message: '相册名称不能为空',
        trigger: 'blur'
      }
    ],
    albumCover: [
      {
        required: true,
        message: '相册封面不能为空',
        trigger: 'blur'
      }
    ],
    albumDesc: [
      {
        required: true,
        message: '相册描述不能为空',
        trigger: 'blur'
      }
    ]
  })

  /** 查询相册管理列表 */
  const getList = async () => {
    loading.value = true
    const res = await PhotoAlbumService.listPhotoAlbum(queryParams)
    if (res.code === 200) {
      albumList.value = res.result.list
      total.value = res.result.total
      loading.value = false
    }
  }

  // 下拉框点击事件
  const handleCommand = (command: any) => {
    const type = command.substring(0, 6)
    const data = command.substring(6)
    if (type == 'delete') {
      handleDelete(data)
    } else {
      handleUpdate(data)
    }
  }

  import { useRouter } from 'vue-router'
  const router = useRouter()
  // 查看相册
  const checkPhoto = (item: any) => {
    router.push({ path: '/photo/photo/index/' + item.id })
  }
  const handleDel = () => {
    router.push({ path: '/photo-delete' })
  }

  // 上传成功后的处理函数
  const onSuccess = (response: any) => {
    form.albumCover = response.msg
    ElMessage.success(`图片上传成功 ${EmojiText[200]}`)
  }

  // 上传失败后的处理函数
  const onError = () => {
    ElMessage.error(`图片上传失败 ${EmojiText[500]}`)
  }

  // 上传前的校验函数
  const beforeUpload = (file: File) => {
    const isImage = file.type.startsWith('image/')
    const isLt2M = file.size / 1024 / 1024 < 2

    if (!isImage) {
      ElMessage.error('只能上传图片文件!')
      return false
    }
    if (!isLt2M) {
      ElMessage.error('图片大小不能超过 2MB!')
      return false
    }
    return true
  }

  // 取消按钮
  const cancel = () => {
    open.value = false
    reset()
  }

  // 表单重置
  const reset = () => {
    // 重置表单数据到初始状态
    Object.assign(form, initialFormState)
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

  /** 新增按钮操作 */
  const handleAdd = () => {
    reset()
    open.value = true
    title.value = '添加相册管理'
  }

  /** 修改按钮操作 */
  const handleUpdate = async (row: any) => {
    reset()
    Object.assign(form, JSON.parse(row))
    open.value = true
    title.value = '修改相册管理'
  }

  /** 提交按钮 */
  const submitForm = async () => {
    if (!albumRef.value) return
    await albumRef.value.validate(async (valid) => {
      if (valid) {
        const res = await PhotoAlbumService.saveOrUpdatePhotoAlbum(form)
        if (res.code === 200) {
          ElMessage.success(res.message)
          open.value = false
          getList()
        }
      }
    })
  }
  const handleDelete = async (_ids: any) => {
    const Tr = await ElMessageBox.confirm('是否确认删除相册管理编号为"' + _ids + '"的数据项？')
    if (Tr) {
      const res = await PhotoAlbumService.deletePhotoAlbum(_ids)
      if (res.code === 200) {
        getList()
        ElMessage.success(res.message)
      }
    }
  }

  // 初始化
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

      &:hover {
        border-color: var(--el-color-primary);
      }

      .upload-placeholder {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        width: 260px;
        height: 160px;
        border: 1px dashed #d9d9d9;
        border-radius: 6px;

        .upload-icon {
          font-size: 28px;
          color: #8c939d;
        }

        .upload-text {
          margin-top: 8px;
          font-size: 14px;
          color: #8c939d;
        }
      }

      .cover-image {
        display: block;
        width: 260px;
        height: 160px;
        object-fit: cover;
      }
    }

    .el-upload__tip {
      margin-top: 8px;
      font-size: 12px;
      color: #666;
    }
  }
  .article-cover {
    position: relative;
    width: 100%;
    height: 90px;
    border-radius: 4px;
  }
  .album-container {
    margin: 20px;
  }
  .album-cover {
    position: relative;
    border-radius: 4px;
    width: 100%;
    height: 200px;
  }
  .album-cover::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
  }
  .album-photo-count {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 1.5rem;
    z-index: 1000;
    position: absolute;
    left: 0;
    right: 0;
    padding: 0 0.5rem;
    bottom: 2.6rem;
    color: #fff;
  }
  .album-name {
    text-align: center;
    margin-top: 0.5rem;
  }
  .album-item {
    position: relative;
    cursor: pointer;
    margin-bottom: 1rem;
  }
  .album-opreation {
    position: absolute;
    z-index: 1000;
    top: 0.5rem;
    right: 0.8rem;
  }
</style>
