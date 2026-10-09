<template>
  <div class="app-container">
    <div class="info-card">
      <h4 class="form-header h4">基本信息</h4>
      <el-form :model="form" label-width="80px">
        <el-row>
          <el-col :span="8" :offset="2">
            <el-form-item label="用户昵称" prop="nickName">
              <el-input v-model="form.nickName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8" :offset="2">
            <el-form-item label="登录账号" prop="userName">
              <el-input v-model="form.userName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="20" :offset="2">
            <el-form-item label="角色信息">
              <el-tag
                v-for="(role, index) in form.roles"
                :key="index"
                style="margin-right: 8px"
              >
                {{ role }}
              </el-tag>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="role-card">
      <h4 class="form-header h4">角色信息</h4>
      <art-table
        v-loading="loading"
        :data="roles.slice((pageNum - 1) * pageSize, pageNum * pageSize)"
        :total="total"
        :current-page="pageNum"
        :page-size="pageSize"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        row-key="roleId"
        class="role-table"
      >
        <el-table-column label="选择" width="70" align="center">
          <template #default="scope">
            <el-checkbox v-model="scope.row.flag" />
          </template>
        </el-table-column>
        <el-table-column label="角色名称" prop="roleName" align="center" />
        <el-table-column label="角色描述" prop="roleDesc" align="center" />
        <el-table-column label="权限" align="center">
          <template #default="scope">
            <template v-if="(scope.row.permissions || []).length">
              <el-tag
                v-for="(p, i) in scope.row.permissions.slice(0, 4)"
                :key="i"
                size="small"
                style="margin: 2px"
                >{{ p }}</el-tag
              >
              <el-tag v-if="scope.row.permissions.length > 4" size="small" type="info" style="margin: 2px">
                +{{ scope.row.permissions.length - 4 }}
              </el-tag>
            </template>
            <span v-else>—</span>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createdAt" align="center" />
      </art-table>
    </div>
    <div class="button-container" style="display: flex; justify-content: center; margin-top: 20px">
      <el-button type="primary" class="gradient-btn" @click="submitForm">保存</el-button>
      <el-button class="outline-btn" @click="close">关闭</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { UserService } from '@/api/system/userApi'
  import { ElMessage } from 'element-plus'
  import { useRouter } from 'vue-router'
  import { ref } from 'vue'
  import { RoleType } from '@/types/system/user'

  const router = useRouter()
  const route = useRoute()

  const loading = ref(true)
  const total = ref(0)
  const pageNum = ref(1)
  const pageSize = ref(10)
  const roles = ref<RoleType[]>([])
  const form = ref({
    nickName: '',
    userName: '',
    userId: 0,
    roles: []
  })

  /** 关闭按钮 */
  const close = () => {
    router.push({ path: '/system/user-auth/role/index'})
  }

  /** 每页条数改变 */
  const handleSizeChange = (size: number) => {
    pageSize.value = size
  }

  /** 当前页改变 */
  const handleCurrentChange = (page: number) => {
    pageNum.value = page
  }

  /** 提交按钮 */
  const submitForm = async () => {
    const userId = form.value.userId
    // 勾选状态直接读行上的 flag（不再依赖表格内部 selection）
    const checked = roles.value.filter((row: any) => row.flag).map((row: any) => row.roleName)
    const res = await UserService.updateAuthRole({ userId: userId, roleNames: checked })
    if (res.code === 200) {
      ElMessage.success((res as any).result || res.message)
      close()
    }
  }

  /** 页面初始化：每次激活都执行（组件被 keep-alive 缓存时 setup 只跑一次） */
  const initPage = async () => {
    const userId = route.params && route.params.userId
    if (!userId) {
      // 不带 userId 直接访问（如从菜单点进）：回列表，避免空白页
      loading.value = false
      ElMessage.warning('请从用户列表选择用户进行角色分配')
      router.replace({ path: '/system/user-auth/role/index' })
      return
    }
    loading.value = true
    try {
      const res = await UserService.getAuthRole(userId)
      if (res.code === 200) {
        form.value = res.result.user
        roles.value = res.result.roles || []
        total.value = roles.value.length
        pageNum.value = 1
        // 预勾选当前用户已拥有的角色
        roles.value.forEach((row: any) => {
          row.flag = ((form.value.roles as string[]) || []).includes(row.roleName)
        })
      }
    } finally {
      loading.value = false
    }
  }

  // 首次挂载 setup 直接跑；keep-alive 缓存后靠 onActivated 复跑
  let booted = false
  initPage()
  onActivated(() => {
    if (!booted) {
      booted = true
      return
    }
    initPage()
  })
</script>

<style scoped>
  .app-container {
    padding: 20px;
  }

  .info-card,
  .role-card {
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    padding: 20px;
    margin-bottom: 20px;
    transition: all 0.3s ease;
  }

  .form-header {
    color: #303133;
    font-weight: 600;
    margin-bottom: 20px;
    position: relative;
    padding-left: 10px;
  }

  .form-header::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 4px;
    height: 16px;
    background: linear-gradient(to bottom, #409eff, #36d1dc);
    border-radius: 2px;
  }

  .role-table {
    margin-top: 10px;
  }

  .gradient-btn:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
  }

  .outline-btn {
    transition: all 0.3s ease;
  }
</style>
