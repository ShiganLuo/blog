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
              label="用户账号"
              prop="userName"
              v-model="queryParams.userName"
              @keyup.enter="handleQuery"
            />
            <form-input
              label="手机号码"
              prop="phoneNumber"
              v-model="queryParams.phoneNumber"
              @keyup.enter="handleQuery"
            />
            <form-select
              label="状态"
              prop="status"
              v-model="queryParams.status"
              :options="sysNormalDisable"
            />
            <el-col :xs="24" :sm="12" :lg="6">
              <el-form-item label="创建时间">
                <el-date-picker
                  v-model="dateRange"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  type="daterange"
                  range-separator="-"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
                ></el-date-picker>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </template>
      <template #bottom>
        <el-button @click="handleAdd" v-auth="['system:user:add']" v-ripple>新增 </el-button>
        <el-button
          @click="handleDelete"
          :disabled="multiple"
          v-auth="['system:user:remove']"
          v-ripple
          >删除
        </el-button>
        <el-button @click="handleImport" v-auth="['system:user:import']" v-ripple>导入 </el-button>
        <el-button @click="handleExport" v-auth="['system:user:export']" v-ripple>导出 </el-button>
      </template>
    </table-bar>

    <art-table
      v-loading="loading"
      :data="userList"
      selection
      :total="total"
      :current-page="queryParams.pageNum"
      :page-size="queryParams.pageSize"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      @selection-change="handleSelectionChange"
      row-key="userId"
    >
      <template #default>
        <el-table-column label="用户ID" align="center" prop="userId" v-if="columns[0].show" />
        <el-table-column label="用户账号" align="center" prop="userName" v-if="columns[1].show" />
        <el-table-column label="用户头像" align="center" prop="avatarUrl" #default="scope" v-if="columns[2].show" >
          <div class="user" style="display: flex; align-items: center">
            <img class="avatar" :src="AvatarImga(scope.row.avatarUrl) || defaultAvatar" />
          </div>
        </el-table-column>
        <el-table-column label="用户名" align="center" prop="nickName" v-if="columns[3].show">
        </el-table-column>
        <el-table-column label="用户类型" align="center" prop="userType" v-if="columns[4].show" />
        <el-table-column label="用户邮箱" align="center" prop="email" v-if="columns[5].show" />
        <el-table-column
          label="手机号码"
          align="center"
          prop="phoneNumber"
          v-if="columns[6].show"
        />
        <el-table-column label="用户性别" align="center" prop="sex" v-if="columns[7].show">
          <template #default="scope">
            <dict-tag :options="sysUserSex" :value="scope.row.sex" />
          </template>
        </el-table-column>
        <el-table-column label="密码" align="center" prop="password" v-if="columns[8].show" />
        <el-table-column label="帐号状态" align="center" prop="status" v-if="columns[9].show">
          <template #default="scope">
            <dict-tag
              :options="sysNormalDisable"
              :value="scope.row.status"
              class="cursor-pointer"
              @click="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column
          label="创建时间"
          sortable
          align="center"
          prop="createdAt"
          width="180"
          v-if="columns[10].show"
        />
        <el-table-column label="最后登录IP" align="center" prop="loginIp" v-if="columns[11].show" />
        <el-table-column
          label="最后登录时间"
          align="center"
          prop="loginDate"
          width="180"
          v-if="columns[12].show"
        >
          <template #default="scope">
            {{ parseTime(scope.row.loginDate) }}
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" v-if="columns[13].show" />
        <el-table-column label="操作" align="center" width="220px">
          <template #default="scope">
            <button-table
              type="edit"
              v-auth="['system:user:edit']"
              @click="handleUpdate(scope.row)"
            />
            <button-table
              type="delete"
              v-auth="['system:user:remove']"
              @click="handleDelete(scope.row)"
            />
            <button-table
              icon="&#xe889;"
              type="add"
              v-auth="['system:user:resetPwd']"
              @click="handleResetPwd(scope.row)"
            />
            <button-table
              icon="&#xe715;"
              type="add"
              v-auth="['system:user:edit']"
              @click="handleAuthRole(scope.row)"
            />
          </template>
        </el-table-column>
      </template>
    </art-table>

    <!-- 添加或修改用户配置对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form :model="form" :rules="rules" ref="userRef" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户昵称" prop="nickName">
              <el-input v-model="form.nickName" placeholder="请输入用户昵称" maxlength="30" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="手机号码" prop="phoneNumber">
              <el-input v-model="form.phoneNumber" placeholder="请输入手机号码" maxlength="11" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item v-if="form.userId == undefined" label="用户名称" prop="userName">
              <el-input v-model="form.userName" placeholder="请输入用户名称" maxlength="30" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item v-if="form.userId == undefined" label="用户密码" prop="password">
              <el-input
                v-model="form.password"
                placeholder="请输入用户密码"
                type="password"
                maxlength="20"
                show-password
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户性别">
              <el-select v-model="form.sex" placeholder="请选择">
                <el-option
                  v-for="dict in sysUserSex"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in sysNormalDisable" :key="dict.value" :value="dict.value">{{
                  dict.label
                }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 用户导入对话框 -->
    <el-dialog :title="upload.title" v-model="upload.open" width="400px" append-to-body>
      <el-upload
        ref="uploadRef"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip text-center">
            <div class="el-upload__tip">
              <el-checkbox v-model="upload.updateSupport" />是否更新已经存在的用户数据
            </div>
            <span>仅允许导入xls、xlsx格式文件。</span>
            <el-link
              type="primary"
              :underline="false"
              style="font-size: 12px; vertical-align: baseline"
              @click="importTemplate"
              >下载模板</el-link
            >
          </div>
        </template>
      </el-upload>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitFileForm">确 定</el-button>
          <el-button @click="upload.open = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
  import { UserService } from '@/api/system/userApi'
  import { ref, reactive, onMounted } from 'vue'
  import { addDateRange, resetForm, AvatarImga, parseTime, selectDictLabel } from '@/utils/utils'
  import defaultAvatar from '@/assets/img/avatar/default-avatar.png'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { FormInstance } from 'element-plus'
  import { UserResult, UserInfoResult, RoleType, PostType } from '@/types/system/user'
  const userList = ref<UserResult[]>([])
  const open = ref(false)
  const loading = ref(true)
  const ids = ref([])
  const multiple = ref(true)
  const total = ref(0)
  const title = ref('')
  const queryRef = ref()
  const userRef = ref<FormInstance>()
  const dateRange = ref([])
  const postOptions = ref<PostType[]>([])
  const roleOptions = ref<RoleType[]>([])
  const uploadRef = ref()
  // 定义初始表单状态
  const initialFormState = {
    userId: null,
    userName: null,
    nickName: null,
    userType: null,
    email: null,
    phoneNumber: null,
    sex: '0',
    avatarUrl: null,
    password: null,
    status: '0',
    loginIp: null,
    loginDate: '',
    createBy: null,
    createdAt: null,
    updateBy: null,
    updatedAt: null,
    remark: null
  }
  const form = reactive({ ...initialFormState })
  const queryParams = reactive({
    pageNum: 1,
    pageSize: 10,
    deptId: '',
    userName: '',
    phoneNumber: '',
    status: ''
  })
  const rules = reactive({
    userName: [
      {
        required: true,
        message: '用户账号不能为空',
        trigger: 'blur'
      }
    ],
    nickName: [
      {
        required: true,
        message: '用户昵称不能为空',
        trigger: 'blur'
      }
    ]
  })
  import { useUserStore } from '@/store/modules/user'
  const { accessToken } = useUserStore()
  /*** 用户导入参数 */
  const upload = reactive({
    // 是否显示弹出层（用户导入）
    open: false,
    // 弹出层标题（用户导入）
    title: '',
    // 是否禁用上传
    isUploading: false,
    // 是否更新已经存在的用户数据
    updateSupport: 0,
    // 设置上传的请求头部
    headers: { Authorization: 'Bearer ' + accessToken },
    // 上传的地址
    url: import.meta.env.VITE_APP_BASE_API + '/system/user/importData'
  })

  /**文件上传中处理 */
  const handleFileUploadProgress = () => {
    upload.isUploading = true
  }

  /** 文件上传成功处理 */
  const handleFileSuccess = (response: any, file: any) => {
    upload.open = false
    upload.isUploading = false
    uploadRef.value.handleRemove(file)
    ElMessageBox.alert(
      "<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" +
        response.msg +
        '</div>',
      '导入结果',
      { dangerouslyUseHTMLString: true }
    )
    getList()
  }

  /** 下载模板操作 */
  const importTemplate = () => {
    downloadExcel(UserService.importTemplate(), `user_template_${new Date().getTime()}.xlsx`)
  }

  /** 重置密码按钮操作 */
  const handleResetPwd = (row: any) => {
    ElMessageBox.prompt('请输入"' + row.userName + '"的新密码', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      closeOnClickModal: false,
      inputPattern: /^.{5,20}$/,
      inputErrorMessage: '用户密码长度必须介于 5 和 20 之间'
    }).then((value: any) => {
      UserService.resetUserPwd({ userId: row.userId, password: value }).then((response) =>
        ElMessage.success(response.message)
      )
    })
  }

  import { useRouter } from 'vue-router'
  const router = useRouter()
  /** 跳转角色分配 */
  const handleAuthRole = (row: any) => {
    const userId = row.userId
    router.push('/system/user-auth/role/' + userId)
  }

  /** 查询用户信息列表 */
  const getList = async () => {
    loading.value = true
    const res = await UserService.listUser(addDateRange(queryParams, dateRange.value))
    if (res.code === 200) {
      userList.value = res.result.list
      total.value = res.result.total
      loading.value = false
    }
  }

  // 查询用户信息详细
  const getUser = async (id: any): Promise<UserInfoResult> => {
    const res = await UserService.getUser(id)
    if (res.code === 200) {
      return res.result
    }
    return Promise.reject(res.message)
  }

  /** 提交上传文件 */
  const submitFileForm = () => {
    uploadRef.value.submit()
  }

  const columns = reactive([
    { name: '用户ID', show: true },
    { name: '用户头像', show: true },
    { name: '用户账号', show: true },
    { name: '用户名', show: true },
    { name: '用户类型', show: false },
    { name: '用户邮箱', show: false },
    { name: '手机号码', show: true },
    { name: '用户性别', show: true },
    { name: '密码', show: false },
    { name: '帐号状态', show: true },
    { name: '创建时间', show: true },
    { name: '最后登录IP', show: false },
    { name: '最后登录时间', show: false },
    { name: '备注', show: false }
  ])

  const changeColumn = (list: any) => {
    columns.values = list
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
    queryParams.pageNum = 1
    getList()
  }

  /** 每页条数改变 */
  const handleSizeChange = (size: number) => {
    queryParams.pageSize = size
    getList()
  }

  /** 当前页改变 */
  const handleCurrentChange = (page: number) => {
    queryParams.pageNum = page
    getList()
  }

  // 多选框选中数据
  const handleSelectionChange = (selection: any) => {
    ids.value = selection.map((item: any) => item.userId)
    multiple.value = !selection.length
  }

  /** 新增按钮操作 */
  const handleAdd = () => {
    reset()
    getUser(null).then((res) => {
      const { posts, roles } = res
      postOptions.value = posts
      roleOptions.value = roles
    })
    open.value = true
    title.value = '添加用户信息'
  }

  /** 修改按钮操作 */
  const handleUpdate = async (row: any) => {
    reset()
    const _userId = row.userId || ids.value
    getUser(_userId).then((res) => {
      const { posts, roles, data } = res
      postOptions.value = posts
      roleOptions.value = roles
      Object.assign(form, { ...data })
      open.value = true
      title.value = '修改用户信息'
    })
  }

  /** 提交按钮 */
  const submitForm = async () => {
    if (!userRef.value) return
    await userRef.value.validate(async (valid) => {
      if (valid) {
        if (form.userId != null) {
          const res = await UserService.updateUser(form)
          if (res.code === 200) {
            ElMessage.success(res.message)
            open.value = false
            getList()
          }
        } else {
          const res = await UserService.addUser(form)
          if (res.code === 200) {
            ElMessage.success(res.message)
            open.value = false
            getList()
          }
        }
      }
    })
  }

  /** 删除按钮操作 */
  const handleDelete = async (row: any) => {
    const _userIds = row.userId || ids.value
    const Tr = await ElMessageBox.confirm('是否确认删除用户信息编号为"' + _userIds + '"的数据项？')
    if (Tr) {
      const res = await UserService.deleteUser(_userIds)
      if (res.code === 200) {
        getList()
        ElMessage.success(res.message)
      }
    }
  }

  /** 导入按钮操作 */
  const handleImport = () => {
    upload.title = '用户导入'
    upload.open = true
  }

  import { downloadExcel } from '@/utils/utils'

  /** 导出按钮操作 */
  const handleExport = () => {
    downloadExcel(UserService.exportExcel(queryParams))
  }

  /** 修改用户状态 */
  const handleStatusChange = async (row: any) => {
    const text = row.status === '0' ? '停用' : '启用'
    const Tr = await ElMessageBox.confirm(`确认要${text}${row.userName}用户吗？`)
    if (Tr) {
      const res = await UserService.changeUserStatus({
        userId: row.userId,
        status: row.status === '0' ? '1' : '0'
      })
      if (res.code === 200) {
        ElMessage.success(res.message)
        getList()
      }
    }
  }

  import { useDict, DictType } from '@/utils/dict'
  const sysNormalDisable = ref<DictType[]>([]) // 状态字典数据
  const sysUserSex = ref<DictType[]>([]) // 性别字典数据
  const getuseDict = async () => {
    const { userStatus } = await useDict('userStatus')
    sysNormalDisable.value = userStatus
    const { gender } = await useDict('gender')
    sysUserSex.value = gender
  }

  // 初始化
  onMounted(() => {
    getuseDict()
    getList()
  })
</script>
<style lang="scss" scoped>
  .page-content {
    width: 100%;
    height: 100%;

    .user {
      .avatar {
        width: 40px;
        height: 40px;
        border-radius: 6px;
      }

      .user-name {
        font-weight: 500;
        color: var(--art-text-gray-800);
      }
    }

    :deep(.cursor-pointer) {
      cursor: pointer;
    }
  }
</style>
