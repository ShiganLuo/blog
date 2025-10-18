<template>
  <el-card class="main-card">
    <div class="title">{{ route.meta.title }}</div>
    <div class="album-info">
      <el-image fit="cover" class="album-cover" :src="albumInfo.albumCover" />
      <div class="album-detail">
        <div style="margin-bottom: 0.6rem">
          <span class="album-name">{{ albumInfo.albumName }}</span>
          <span class="photo-count">{{ albumInfo.photoCount }}张</span>
        </div>
        <div>
          <span v-if="albumInfo.albumDesc" class="album-desc">
            {{ albumInfo.albumDesc }}
          </span>
          <el-button @click="uploadPhoto = true" v-ripple> 上传照片 </el-button>
        </div>
      </div>
      <div class="operation">
        <div class="all-check">
          <el-checkbox
            :indeterminate="isIndeterminate"
            v-model="checkAll"
            @change="handleCheckAllChange"
          >
            全选
          </el-checkbox>
          <div class="check-count">已选择{{ selectphotoIds.length }}张</div>
        </div>
        <el-button @click="movePhoto = true" :disabled="selectphotoIds.length == 0" v-ripple>
          移动到
        </el-button>
        <el-button @click="batchDeletePhoto()" :disabled="selectphotoIds.length == 0" v-ripple>
          批量删除
        </el-button>
      </div>
    </div>

    <el-empty v-if="photos.length == 0" description="暂无照片" />

    <div class="photo-container">
      <el-checkbox-group v-model="selectphotoIds" @change="handleCheckedPhotoChange">
        <el-checkbox
          :value="item.id"
          v-for="item of photos"
          :key="item.id"
          style="height: 150px; margin: 20px"
        >
          <div class="photo-item">
            <div class="photo-opreation">
              <el-dropdown @command="handleCommand">
                <i class="iconfont-sys" v-html="'&#xe839;'" style="color: royalblue" />
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item :command="JSON.stringify(item)">
                      <i class="iconfont-sys" v-html="'&#xe642;'" />编辑
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
            <el-image
              fit="cover"
              class="photo-img"
              :src="item.photoSrc"
              :preview-photoSrc-list="photos"
            />
            <div class="photo-name">{{ item.photoName }}</div>
          </div>
        </el-checkbox>
      </el-checkbox-group>
    </div>

    <el-pagination
      :hide-on-single-page="true"
      class="pagination-container"
      @current-change="currentChange"
      :current-page="current"
      :page-size="size"
      :total="count"
      layout="prev, pager, next"
    />

    <el-dialog title="上传照片" v-model="uploadPhoto" width="70%" top="10vh">
      <div class="upload-container">
        <el-upload
          v-show="uploads.length > 0"
          :action="uploadImageUrl"
          list-type="picture-card"
          v-model:file-list="uploads"
          multiple
          :headers="uploadHeaders"
          :before-upload="beforeUpload"
          @error="onError"
        >
          <el-icon><Plus /></el-icon>
          <template #file="{ file }">
            <div>
              <img class="el-upload-list__item-thumbnail" :src="file.url" alt="" />
              <span class="el-upload-list__item-actions">
                <span class="el-upload-list__item-delete" @click="handleRemove(file)">
                  <el-icon><Delete /></el-icon>
                </span>
              </span>
            </div>
          </template>
        </el-upload>
        <div class="upload">
          <el-upload
            v-show="uploads.length == 0"
            drag
            :action="uploadImageUrl"
            multiple
            :headers="uploadHeaders"
            :before-upload="beforeUpload"
            @success="onSuccess"
            @error="onError"
            :show-file-list="false"
          >
            <template #default>
              <el-icon class="el-icon--upload"><upload-filled /></el-icon>
              <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            </template>
            <template #tip>
              <div class="el-upload__tip">支持上传jpg/png文件</div>
            </template>
          </el-upload>
        </div>
      </div>
      <template #footer>
        <div class="upload-footer">
          <div class="upload-count">共上传{{ uploads.length }}张照片</div>
          <div style="margin-left: auto">
            <el-button @click="uploadPhoto = false">取 消</el-button>
            <el-button @click="savePhotos" type="primary" :disabled="uploads.length == 0">
              开始上传
            </el-button>
          </div>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="修改信息" v-model="editPhoto" width="30%">
      <el-form ref="photoFormRef" label-width="80px" :rules="photoFormRules" :model="photoForm">
        <el-form-item label="照片名称" prop="photoName">
          <el-input v-model="photoForm.photoName" />
        </el-form-item>
        <el-form-item label="照片描述" prop="photoDesc">
          <el-input v-model="photoForm.photoDesc" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editPhoto = false">取 消</el-button>
        <el-button type="primary" @click="updatePhoto"> 确 定 </el-button>
      </template>
    </el-dialog>

    <el-dialog title="移动照片" v-model="movePhoto" width="30%">
      <el-empty v-if="albumList.length < 2" description="暂无其他相册" />
      <el-form v-else label-width="80px" :model="photoForm">
        <el-radio-group v-model="albumId">
          <div class="album-check-item">
            <template v-for="item of albumList">
              <el-radio
                v-if="item.id != albumInfo.id"
                :key="item.id"
                :value="item.id"
                style="margin-bottom: 1rem"
              >
                <div class="album-check">
                  <el-image fit="cover" class="album-check-cover" :src="item.albumCover" />
                  <div style="margin-left: 0.5rem">{{ item.albumName }}</div>
                </div>
              </el-radio>
            </template>
          </div>
        </el-radio-group>
      </el-form>
      <template #footer>
        <el-button @click="movePhoto = false">取 消</el-button>
        <el-button :disabled="albumId == null" type="primary" @click="updatePhotoAlbum">
          确 定
        </el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
  import { ref, reactive, onMounted } from 'vue'
  import { useRoute } from 'vue-router'
  import { ElMessage, ElNotification } from 'element-plus'
  import { PhotoAlbumResult } from '@/types/photo/photo'
  import { useUserStore } from '@/store/modules/user'
  import PhotoAlbumService from '@/api/photo/photoAlbumApi'
  import PhotoService from '@/api/photo/photoApi'

  const route = useRoute()

  // 响应式状态
  const loading = ref<boolean>(true)
  const isIndeterminate = ref<boolean>(false)
  const checkAll = ref<boolean>(false)
  const uploadPhoto = ref<boolean>(false)
  const editPhoto = ref<boolean>(false)
  const movePhoto = ref<boolean>(false)
  const uploads = ref<any[]>([])
  const photos = ref<any[]>([])
  const selectphotoIds = ref<number[]>([])
  const albumList = ref<PhotoAlbumResult[]>([])
  const current = ref<number>(1)
  const size = ref<number>(18)
  const count = ref<number>(0)
  const albumId = ref<number>(0)
  const photoIds = ref<number[]>([])

  const userStore = useUserStore()
  let { accessToken } = userStore
  // 上传路径
  const uploadImageUrl = `${import.meta.env.VITE_API_BASE_URL}/blog/photo/admin/photos/upload`

  const uploadHeaders = { Authorization: accessToken }

  // 表单数据
  const albumInfo = reactive<{
    id: number | null
    albumName: string
    albumDesc: string
    albumCover: string
    photoCount: number
  }>({
    id: null,
    albumName: '',
    albumDesc: '',
    albumCover: '',
    photoCount: 0
  })

  const photoForm = reactive<{
    id: number | null
    photoName: string
    photoDesc: string
  }>({
    id: null,
    photoName: '',
    photoDesc: ''
  })

  const photoFormRules = reactive({
    photoName: [{ required: true, message: '请输入照片名称', trigger: 'blur' }]
  })

  import { FormInstance } from 'element-plus'
  const photoFormRef = ref<FormInstance>()

  import EmojiText from '@/utils/emojo'
  // 上传成功后的处理函数
  const onSuccess = (response: any) => {
    uploads.value.push({ url: response.msg })
    ElMessage.success(`图片上传成功 ${EmojiText[200]}`)
  }

  // 上传失败后的处理函数
  const onError = () => {
    ElMessage.error(`图片上传失败 ${EmojiText[500]}`)
  }

  // API调用
  const getAlbumInfo = async () => {
    albumId.value = Number(route.params.albumId)
    const res = await PhotoAlbumService.detailPhotoAlbum(albumId.value)
    if (res.code === 200) {
      Object.assign(albumInfo, res.result)
    }
  }

  const listPhotos = async () => {
    const res = await PhotoService.listPhoto({
      current: current.value,
      size: size.value,
      albumId: Number(route.params.albumId),
      isDelete: 0
    })
    if (res.code === 200) {
      photos.value = res.result.list
      count.value = res.result.total
      loading.value = false
    }
  }

  // 事件处理
  const currentChange = (currentPage: number) => {
    current.value = currentPage
    listPhotos()
  }

  const savePhotos = async () => {
    const res = await PhotoService.savePhoto({
      albumId: albumId.value,
      photoUrls: uploads.value.map((photo: any) => photo.url)
    })
    if (res.code === 200) {
      ElMessage.success(res.message)
      uploadPhoto.value = false
      uploads.value = []
      listPhotos()
    }
  }

  const updatePhoto = async () => {
    if (!photoFormRef.value) return
    await photoFormRef.value.validate(async (valid) => {
      if (valid) {
        const res = await PhotoService.updatePhoto(photoForm)
        if (res.code === 200) {
          ElMessage.success(res.message)
          editPhoto.value = false
        }
      }
    })
  }

  const updatePhotoAlbum = async () => {
    const res = await PhotoService.movePhotoAlbum({
      albumId: albumId.value,
      photoIds: selectphotoIds.value
    })
    if (res.code === 200) {
      ElMessage.success(res.message)
      movePhoto.value = false
      listPhotos()
    }
  }

  const handleRemove = (file: any) => {
    const index = uploads.value.indexOf(file)
    uploads.value.splice(index, 1)
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

  const handleCommand = (command: any) => {
    Object.assign(photoForm, JSON.parse(command))
    editPhoto.value = true
  }

  const handleCheckAllChange = (val: any) => {
    selectphotoIds.value = val ? photoIds.value : []
    isIndeterminate.value = false
  }

  watch(
    () => photos.value,
    (newPhotos) => {
      photoIds.value = newPhotos.map((item) => item.id)
    }
  )

  function handleCheckedPhotoChange(value: any) {
    const checkedCount = value.length
    checkAll.value = checkedCount === photoIds.value.length
    isIndeterminate.value = checkedCount > 0 && checkedCount < photoIds.value.length
  }

  const listAlbums = async () => {
    const res = await PhotoAlbumService.photoAlbumList()
    if (res.code === 200) {
      albumList.value = res.result
    }
  }

  const batchDeletePhoto = async () => {
    const Tr = await ElMessageBox.confirm('是否确认删除照片数据？')
    if (Tr) {
      const res = await PhotoService.updatePhotoDelete({ ids: selectphotoIds.value, isDelete: 1 })
      if (res.code === 200) {
        ElMessage.success(res.message)
        listPhotos()
      }
    }
  }

  // 生命周期钩子
  onMounted(() => {
    getAlbumInfo()
    listPhotos()
    listAlbums()
  })
</script>
<style scoped>
  .album-info {
    display: flex;
    margin-top: 2.25rem;
    margin-bottom: 2rem;
  }
  .album-cover {
    border-radius: 4px;
    width: 5rem;
    height: 5rem;
  }
  .album-check-cover {
    border-radius: 4px;
    width: 4rem;
    height: 4rem;
  }
  .album-detail {
    padding-top: 0.4rem;
    margin-left: 0.8rem;
  }
  .album-desc {
    font-size: 14px;
    margin-right: 0.8rem;
  }
  .operation {
    padding-top: 1.5rem;
    margin-left: auto;
    display: flex;
    align-items: center;
  }
  .all-check {
    display: inline-flex;
    align-items: center;
    margin-right: 1rem;
  }
  .check-count {
    margin-left: 1rem;
    font-size: 12px;
  }
  .album-name {
    font-size: 1.25rem;
  }
  .photo-count {
    font-size: 12px;
    margin-left: 0.5rem;
  }
  .photo-item {
    width: 100%;
    position: relative;
    cursor: pointer;
    margin-bottom: 1rem;
  }
  .photo-img {
    width: 100%;
    height: 7rem;
    border-radius: 4px;
  }
  .photo-name {
    font-size: 14px;
    margin-top: 0.3rem;
    text-align: center;
  }
  .upload-container {
    height: 400px;
  }
  .upload {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .upload-footer {
    display: flex;
    align-items: center;
  }
  .photo-opreation {
    position: absolute;
    z-index: 1000;
    top: 0.3rem;
    right: 0.5rem;
  }
  .photo-checkbox {
    position: absolute;
    z-index: 1000;
    left: 0.5rem;
  }
  .album-check {
    display: flex;
    align-items: center;
  }
  :deep(.el-upload-dragger) {
    background-color: #fff;
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    box-sizing: border-box;
    width: 360px;
    height: 180px;
    text-align: center;
    cursor: pointer;
    overflow: hidden;
  }
  .photo-container :deep(.el-checkbox .el-checkbox__input) {
    display: none;
  }
</style>
