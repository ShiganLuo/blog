<template>
  <el-card class="region art-custom-card">
    <div class="title">留言管理</div>
    <div class="status-menu">
      <el-button @click="handleAdd" v-ripple>新增 </el-button>
      <el-radio-group v-model="status" @change="changeStatus" style="float: right">
        <el-radio-button :value="''">全部</el-radio-button>
        <el-radio-button :value="1">公开</el-radio-button>
        <el-radio-button :value="2">私密</el-radio-button>
      </el-radio-group>
    </div>

    <el-empty v-if="talks == null" description="暂无留言" />

    <div class="talk-item" v-for="item of talks" :key="item.id">
      <div class="user-info-wrapper">
        <el-avatar class="user-avatar" :src="AvatarImga(item.avatar)" :size="36" />
        <div class="user-detail-wrapper">
          <div class="user-nickname">
            <div>{{ item.nickname }}</div>
            <el-dropdown trigger="click" @command="handleCommand">
              <i class="iconfont-sys" v-html="'&#xe839;'" style="color: grey" />
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item :command="'1,' + item.id">
                    <el-icon><Edit /></el-icon>编辑
                  </el-dropdown-item>
                  <el-dropdown-item :command="'2,' + item.id">
                    <el-icon><Delete /></el-icon>删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <div class="time">
            {{ parseTime(item.createTime) }}
            <span class="top" v-if="item.isTop == 1">
              <i class="iconfont el-icon-myzhiding" /> 置顶
            </span>
            <span class="secret" v-if="item.status == 2">
              <i class="iconfont el-icon-mymima" /> 私密
            </span>
          </div>
          <div class="talk-content" v-html="item.content" />
          <el-row :gutter="4" class="talk-images" v-if="item.imgs">
            <el-col :md="8" :cols="6" v-for="(img, index) of item.imgs" :key="index">
              <el-image class="images-items" :src="img" :preview-src-list="previews" />
            </el-col>
          </el-row>
        </div>
      </div>
    </div>

    <el-pagination
      :hide-on-single-page="false"
      class="pagination-container"
      @size-change="sizeChange"
      @current-change="currentChange"
      :current-page="current"
      :page-size="size"
      :total="count"
      layout="prev, pager, next"
    />

    <el-dialog :title="title" v-model="open" width="80%" append-to-body>
      <div class="talk-container">
        <Editor v-model="talk.content" style="max-height: 350px" />
        <div class="operation-wrapper">
          <div class="left-wrapper">
            <el-upload
              v-show="uploads.length == 0"
              :action="uploadImageUrl"
              multiple
              :headers="uploadHeaders"
              :before-upload="beforeUpload"
              :on-success="onSuccess"
              :show-file-list="false"
            >
              <i class="iconfont-sys" v-html="'&#xe634;'" style="font-size: 32px" />
            </el-upload>
          </div>
          <div class="right-wrapper">
            <el-switch
              style="margin-right: 16px"
              v-model="talk.isTop"
              inactive-text="置顶"
              :active-value="1"
              :inactive-value="0"
            />
            <el-dropdown
              trigger="click"
              @command="
                (command) => {
                  talk.status = command
                }
              "
              style="margin-right: 16px"
            >
              <span class="el-dropdown-link">
                {{ dropdownTitle }}
                <el-icon class="el-icon--right">
                  <arrow-down />
                </el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item
                    v-for="(item, index) of statuses"
                    :key="index"
                    :command="item.status"
                  >
                    {{ item.desc }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button type="primary" @click="submitForm" :disabled="talk.content == '<p><br></p>'">
              发布
            </el-button>
          </div>
        </div>
        <el-upload
          class="talk-image-upload"
          v-show="uploads.length > 0"
          :action="uploadImageUrl"
          list-type="picture-card"
          multiple
          :headers="uploadHeaders"
          v-model:file-list="uploads"
          :before-upload="beforeUpload"
        >
          <el-icon><Plus /></el-icon>
          <template #file="{ file }">
            <div>
              <img class="el-upload-list__item-thumbnail" :src="file.url" alt="" />
              <span class="el-upload-list__item-actions">
                <span class="el-upload-list__item-delete" @click="handleRemoveUpload(file)">
                  <el-icon><Delete /></el-icon>
                </span>
              </span>
            </div>
          </template>
        </el-upload>
      </div>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
  import { ref, reactive } from 'vue'
  import TalkService from '@/api/message/talkApi'
  import { useUserStore } from '@/store/modules/user'
  import EmojiText from '@/utils/emojo'
  import { parseTime, AvatarImga } from '@/utils/utils'

  // 响应式数据
  const current = ref(0)
  const size = ref(5)
  const count = ref(0)
  const status = ref('')
  const talks = ref<any[]>([])
  const previews = ref<string[]>([])
  const open = ref(false)
  const title = ref('')
  const uploads = ref<any[]>([])
  const statuses = ref([
    { status: 1, desc: '公开' },
    { status: 2, desc: '私密' }
  ])
  const initialFormState = {
    id: null,
    content: '<p><br></p>',
    isTop: 0,
    status: 1,
    images: ''
  }
  const talk = reactive({ ...initialFormState })

  const userStore = useUserStore()
  let { accessToken } = userStore
  // 上传路径
  const uploadImageUrl = `${import.meta.env.VITE_API_BASE_URL}/blog/talk/admin/talks/images`

  const uploadHeaders = { Authorization: accessToken }

  // 方法
  const handleCommand = (command: string) => {
    const [action, id] = command.split(',')
    switch (action) {
      case '1':
        handleUpdate(id)
        break
      case '2':
        handleRemove(id)
        break
    }
  }

  const listTalks = async () => {
    const res = await TalkService.listBackTalks({
      current: current.value,
      size: size.value,
      status: status.value
    })
    if (res.code == 200) {
      talks.value = res.result.list
      talks.value?.forEach((item) => {
        if (item.imgs) {
          previews.value.push(...item.imgs)
        }
      })
      count.value = res.result.total
    }
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

  // 上传成功后的处理函数
  const onSuccess = (response: any) => {
    uploads.value.push({ url: response.msg })
    ElMessage.success(`图片上传成功 ${EmojiText[200]}`)
  }

  const sizeChange = (newSize: number) => {
    previews.value = []
    size.value = newSize
    listTalks()
  }

  const currentChange = (newCurrent: number) => {
    previews.value = []
    current.value = newCurrent
    listTalks()
  }

  const changeStatus = () => {
    current.value = 1
    previews.value = []
    listTalks()
  }

  const handleRemoveUpload = (file: any) => {
    const index = uploads.value.indexOf(file)
    uploads.value.splice(index, 1)
  }

  // 表单重置
  const reset = () => {
    // 重置表单数据到初始状态
    Object.assign(talk, initialFormState)
    uploads.value = []
  }

  /** 新增按钮操作 */
  const handleAdd = () => {
    reset()
    open.value = true
    title.value = '发布留言'
  }

  /** 删除按钮操作 */
  const handleRemove = async (id: any) => {
    const Tr = await ElMessageBox.confirm('是否确认删除留言数据编号为"' + id + '"的数据项？')
    if (Tr == 'confirm') {
      const res = await TalkService.deleteTalks([id])
      if (res.code == 200) {
        ElMessage.success(res.message)
        listTalks()
      }
    }
  }

  /** 修改按钮操作 */
  const handleUpdate = async (id: any) => {
    reset()
    open.value = true
    title.value = '修改留言'
    const res = await TalkService.getBackTalkById(id)
    if (res.code == 200) {
      Object.assign(talk, res.result)
      if (res.result.imgs) {
        uploads.value = res.result.imgs.map((item: any) => ({ url: item }))
      }
    }
  }

  const dropdownTitle = computed(() => {
    const currentStatus = statuses.value.find((item) => item.status === talk.status)
    return currentStatus?.desc || ''
  })

  const submitForm = async () => {
    if (talk.content.trim() == '<p><br></p>') {
      ElMessage.error('说说内容不能为空')
      return false
    }
    if (uploads.value.length > 0) {
      const img: any = []
      uploads.value.forEach((item) => {
        img.push(item.url)
      })
      talk.images = JSON.stringify(img)
    }
    const res = await TalkService.saveOrUpdateTalk(talk)
    if (res.code == 200) {
      ElMessage.success(res.message)
      open.value = false
      listTalks()
    }
  }

  onMounted(() => {
    listTalks()
  })
</script>
<style scoped>
  .region {
    box-sizing: border-box;
    background: var(--art-main-bg-color);
    border-radius: calc(var(--custom-radius) / 2 + 4px) !important;
  }
  .pagination-container {
    float: right;
    margin-top: 20px;
  }
  .status-menu {
    font-size: 14px;
    margin-top: 40px;
    color: #999;
  }
  .status-menu span {
    margin-right: 24px;
  }
  .status {
    cursor: pointer;
  }
  .active-status {
    cursor: pointer;
    color: #333;
    font-weight: bold;
  }
  .talk-item:not(:first-child) {
    margin-top: 20px;
  }
  .talk-item {
    padding: 16px 20px;
    border-radius: 10px;
    background: rgba(255, 255, 255, 0.1);
    box-shadow: 0 3px 8px 6px rgb(7 17 27 / 6%);
    transition: all 0.3s ease 0s;
  }
  .talk-item:hover {
    box-shadow: 0 5px 10px 8px rgb(7 17 27 / 16%);
    transform: translateY(-3px);
  }
  .user-info-wrapper {
    width: 100%;
    display: flex;
  }
  .user-avatar {
    border-radius: 50%;
  }
  .user-avatar {
    transition: all 0.5s;
  }
  .user-avatar:hover {
    transform: rotate(360deg);
  }
  .user-detail-wrapper {
    margin-left: 10px;
    width: 100%;
  }
  .user-nickname {
    font-size: 15px;
    font-weight: bold;
    display: flex;
    justify-content: space-between;
    cursor: pointer;
  }
  .user-sign {
    margin-left: 4px;
  }
  .time {
    color: #999;
    margin-top: 2px;
    font-size: 12px;
  }
  .top {
    color: #ff7242;
    margin-left: 10px;
  }
  .secret {
    color: #999;
    margin-left: 10px;
  }
  .talk-content {
    margin-top: 8px;
    font-size: 14px;
    line-height: 26px;
    white-space: pre-line;
    word-wrap: break-word;
    word-break: break-all;
  }
  .talk-images {
    margin-top: 8px;
  }
  .images-items {
    cursor: pointer;
    object-fit: cover;
    height: 200px;
    width: 100%;
    border-radius: 4px;
  }
  .tupian {
    margin-left: 3px;
  }
  .editor-wrapper {
    min-height: 150px;
  }
  .operation-wrapper {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 16px;
  }
  .operation-btn {
    cursor: pointer;
    color: #838383;
    font-size: 20px;
    margin-right: 12px;
  }
  .talk-status {
    cursor: pointer;
    font-size: 12px;
    color: #999;
  }
  .emoji {
    user-select: none;
    margin: 0.25rem;
    display: inline-block;
    vertical-align: middle;
  }
  .emoji-item {
    cursor: pointer;
    display: inline-block;
  }
  .emoji-item:hover {
    transition: all 0.2s;
    border-radius: 0.25rem;
    background: #dddddd;
  }
  .left-wrapper {
    display: flex;
    width: 50%;
  }
  .talk-image-upload {
    margin-top: 8px;
  }
  .right-wrapper {
    display: flex;
    align-items: center;
  }
</style>
