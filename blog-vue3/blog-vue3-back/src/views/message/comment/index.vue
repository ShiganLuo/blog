<template>
  <div class="page-content">
    <el-row :gutter="12">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-form :model="queryParams" ref="queryRef">
          <el-input
            placeholder="请输入用户名称"
            v-model="queryParams.keywords"
            @keyup.enter="handleQuery"
          />
        </el-form>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-button @click="handleQuery" v-ripple>搜索 </el-button>
        <el-button @click="handleDelete" :disabled="multiple" v-ripple>删除 </el-button>
        <el-button @click="handlePass" :disabled="multiple" v-ripple>审核 </el-button>
      </el-col>
      <el-col :xs="24" :sm="24" :lg="12">
        <el-radio-group v-model="queryParams.status" @change="getList()" style="float: right">
          <el-radio-button :value="0">待审</el-radio-button>
          <el-radio-button :value="1">通过</el-radio-button>
          <el-radio-button :value="2">拒绝</el-radio-button>
        </el-radio-group>
      </el-col>
    </el-row>
    <art-table
      v-loading="loading"
      :data="commentList"
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
        <el-table-column type="selection" />
        <el-table-column prop="avatar" label="头像" align="center">
          <template #default="scope">
            <img :src="AvatarImga(scope.row.avatar)" width="40" height="40" />
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="评论人" align="center" />
        <el-table-column prop="replyUserName" label="回复人" align="center">
          <template #default="scope">
            {{ scope.row.replyUserName }}
          </template>
        </el-table-column>
        <el-table-column prop="articleTitle" label="文章标题" align="center">
          <template #default="scope">
            {{ scope.row.articleTitle }}
          </template>
        </el-table-column>
        <el-table-column prop="commentContent" label="评论内容" align="center">
          <template #default="scope">
            <span v-html="scope.row.commentContent" class="comment-content" />
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="评论时间" align="center">
          <template #default="scope">
            {{ parseTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.status == 0" type="warning">审核中</el-tag>
            <el-tag v-if="scope.row.status == 1" type="success">通过</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="来源" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.type == 1">文章</el-tag>
            <el-tag v-if="scope.row.type == 2" type="danger">留言</el-tag>
            <el-tag v-if="scope.row.type == 3" type="success">关于我</el-tag>
            <el-tag v-if="scope.row.type == 4" type="warning">友链</el-tag>
            <el-tag v-if="scope.row.type == 5" type="warning">说说</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center">
          <template #default="scope">
            <button-table type="delete" @click="handleDelete(scope.row)" />
            <button-table
              type="add"
              icon="&#xe86a;"
              v-if="scope.row.status == 0"
              @click="handlePass(scope.row)"
            >
              通过
            </button-table>
          </template>
        </el-table-column>
      </template>
    </art-table>
  </div>
</template>

<script setup lang="ts">
  import { CommentService } from '@/api/message/commentApi'
  import { ref, reactive } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { CommentResult } from '@/types/message/comment'
  import { AvatarImga, parseTime } from '@/utils/utils'
  const commentList = ref<CommentResult[]>([])
  const loading = ref(true)
  const ids = ref([])
  const multiple = ref(true)
  const total = ref(0)
  const queryRef = ref()
  const queryParams = reactive({
    current: 1,
    size: 10,
    keywords: '',
    status: 0,
    type: ['comment']
  })

  /** 查询评论管理列表 */
  const getList = async () => {
    loading.value = true
    const res = await CommentService.getCommentList(queryParams)
    if (res.code === 200) {
      commentList.value = res.result.list
      total.value = res.result.total
      loading.value = false
    }
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

  /** 删除按钮操作 */
  const handleDelete = async (row: any) => {
    const _ids = row ? [row.id] : ids.value
    const Tr = await ElMessageBox.confirm('是否确认删除评论管理编号为"' + _ids + '"的数据项？')
    if (Tr) {
      const res = await CommentService.deleteComment(_ids)
      if (res.code === 200) {
        getList()
        ElMessage.success(res.message)
      }
    }
  }

  // 审核
  const handlePass = async (row: any) => {
    const _ids = row ? [row.id] : ids.value
    const Tr = await ElMessageBox.confirm('是否确认审核评论管理编号为"' + _ids + '"的数据项？')
    if (Tr) {
      const res = await CommentService.passComment({ ids: _ids, status: 1 })
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
