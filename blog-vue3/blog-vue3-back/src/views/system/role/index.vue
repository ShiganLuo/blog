<template>
  <div class="page-content">
    <!-- 角色管理 -->
    <table-bar
      :showTop="false"
      @search="search"
      @reset="handleReset"
      @changeColumn="changeColumn"
      :columns="columns"
    >
      <template #top>
        <el-form :model="queryParams" ref="searchFormRef" label-width="82px">
          <el-row :gutter="20">
            <form-input
              label="角色名称"
              prop="roleName"
              @keyup.enter="search"
              v-model="queryParams.roleName"
            />
          </el-row>
        </el-form>
      </template>
      <template #bottom>
        <el-button @click="showDialog('add')" v-auth="['system:role:add']" v-ripple>新增</el-button>
      </template>
    </table-bar>

    <!-- 角色列表 -->
    <art-table :data="roleList">
      <template #default>
        <el-table-column label="角色名称" prop="roleName" v-if="columns[0].show">
          <template #default="scope">
            {{ scope.row.roleName }}
            <el-tag v-if="scope.row.roleName === 'ADMIN'" size="small" type="danger" style="margin-left: 4px">
              内置
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="描述" prop="roleDesc" v-if="columns[1].show" />
        <el-table-column label="权限数" prop="permCount" width="90" align="center" v-if="columns[2].show" />
        <el-table-column label="用户数" prop="userCount" width="90" align="center" v-if="columns[3].show" />
        <el-table-column label="创建时间" prop="createdAt" v-if="columns[4].show" />
        <el-table-column fixed="right" label="操作" width="170px">
          <template #default="scope">
            <el-tooltip content="编辑角色" placement="top" v-auth="['system:role:edit']">
              <button-table type="edit" @click="showDialog('edit', scope.row)" />
            </el-tooltip>
            <el-tooltip
              content="删除角色"
              placement="top"
              v-auth="['system:role:remove']"
              v-if="scope.row.roleName !== 'ADMIN'"
            >
              <button-table type="delete" @click="deleteRole(scope.row)" />
            </el-tooltip>
          </template>
        </el-table-column>
      </template>
    </art-table>

    <!-- 新增/编辑角色 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增角色' : '编辑角色'"
      width="520px"
      append-to-body
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input
            v-model="form.roleName"
            placeholder="请输入角色名称"
            :disabled="dialogType === 'edit' && form.roleName === 'ADMIN'"
          />
        </el-form-item>
        <el-form-item label="描述" prop="roleDesc">
          <el-input v-model="form.roleDesc" type="textarea" :rows="2" placeholder="请输入角色描述" />
        </el-form-item>
        <el-form-item label="权限配置">
          <div style="margin-bottom: 6px">
            <el-checkbox v-model="permExpand" @change="toggleExpand">展开/折叠</el-checkbox>
            <el-checkbox v-model="permAll" @change="toggleAll">全选/全不选</el-checkbox>
          </div>
          <el-tree
            ref="treeRef"
            class="perm-tree"
            :data="permissionTree"
            show-checkbox
            node-key="id"
            :props="{ label: 'name', children: 'children' }"
            empty-text="加载中，请稍候"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
        <el-button @click="dialogVisible = false">取消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
  import { ElMessage, ElMessageBox } from 'element-plus'
  import type { CheckboxValueType, FormInstance, FormRules } from 'element-plus'
  import { RoleService } from '@/api/system/roleApi'
  import { resetForm } from '@/utils/utils'

  interface RoleRow {
    id: number
    roleName: string
    roleDesc: string
    createdAt: string
    permCount: number
    userCount: number
  }

  interface PermNode {
    id: number
    name: string
    permission: string
    type: string
    children?: PermNode[]
  }

  const dialogVisible = ref(false)
  const dialogType = ref<'add' | 'edit'>('add')
  const roleList = ref<RoleRow[]>([])
  const permissionTree = ref<PermNode[]>([])
  const treeRef = ref()
  const permExpand = ref(false)
  const permAll = ref(false)

  const queryParams = reactive({ roleName: '' })
  const searchFormRef = ref<FormInstance>()
  const formRef = ref<FormInstance>()

  const rules = reactive<FormRules>({
    roleName: [
      { required: true, message: '角色名称不能为空', trigger: 'blur' },
      { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
    ]
  })

  const columns = reactive([
    { name: '角色名称', show: true },
    { name: '描述', show: true },
    { name: '权限数', show: true },
    { name: '用户数', show: true },
    { name: '创建时间', show: true }
  ])

  const changeColumn = (list: any) => {
    columns.values = list
  }

  const form = reactive({ id: 0, roleName: '', roleDesc: '' })

  // 角色数量是个位数，一次取全量，不做分页
  const getList = async () => {
    const res = await RoleService.listRole({
      roleName: queryParams.roleName,
      pageNum: 1,
      pageSize: 100
    })
    if (res.code === 200) {
      roleList.value = res.result.list
    }
  }

  const loadPermissionTree = async () => {
    if (permissionTree.value.length) return
    const res = await RoleService.permissionTree()
    if (res.code === 200) {
      permissionTree.value = res.result
    }
  }

  const search = () => {
    getList()
  }

  const handleReset = () => {
    resetForm(searchFormRef.value)
    getList()
  }

  /** 收集权限树全部节点id（全选用） */
  const collectIds = (nodes: PermNode[]): number[] =>
    nodes.flatMap((n) => [n.id, ...(n.children ? collectIds(n.children) : [])])

  const toggleAll = (val: CheckboxValueType) => {
    treeRef.value?.setCheckedKeys(val ? collectIds(permissionTree.value) : [])
  }

  const toggleExpand = (val: CheckboxValueType) => {
    const map = (treeRef.value as any)?.store?.nodesMap || {}
    Object.values(map).forEach((node: any) => {
      node.expanded = val
    })
  }

  /** 打开新增/编辑对话框 */
  const showDialog = async (type: 'add' | 'edit', row?: RoleRow) => {
    dialogType.value = type
    await loadPermissionTree()
    if (type === 'add') {
      form.id = 0
      form.roleName = ''
      form.roleDesc = ''
      permAll.value = false
      dialogVisible.value = true
      nextTick(() => treeRef.value?.setCheckedKeys([]))
      return
    }
    if (!row) return
    const res = await RoleService.getRole(row.id)
    if (res.code === 200) {
      form.id = res.result.id
      form.roleName = res.result.roleName
      form.roleDesc = res.result.roleDesc || ''
      permAll.value = false
      dialogVisible.value = true
      nextTick(() => treeRef.value?.setCheckedKeys(res.result.permissionIds || []))
    }
  }

  /** 提交（新增/编辑 + 权限整体保存） */
  const handleSubmit = async () => {
    if (!formRef.value) return
    try {
      await formRef.value.validate()
    } catch {
      return
    }
    const permissionIds: number[] = treeRef.value?.getCheckedKeys() || []
    const payload = {
      id: form.id || undefined,
      roleName: form.roleName,
      roleDesc: form.roleDesc,
      permissionIds
    }
    const res =
      dialogType.value === 'add' ? await RoleService.addRole(payload) : await RoleService.updateRole(payload)
    if (res.code === 200) {
      ElMessage.success(res.result || res.message)
      dialogVisible.value = false
      getList()
    }
  }

  /** 删除角色 */
  const deleteRole = (row: RoleRow) => {
    ElMessageBox.confirm(`确认删除角色“${row.roleName}”吗？持有该角色的用户将被解除关联。`, '提示', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
      .then(async () => {
        const res = await RoleService.delRole(row.id)
        if (res.code === 200) {
          ElMessage.success(res.result || res.message)
          getList()
        }
      })
      .catch(() => {})
  }

  onMounted(() => {
    getList()
  })
</script>

<style scoped>
  .perm-tree {
    width: 100%;
    max-height: 320px;
    overflow: auto;
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 6px;
    padding: 6px;
  }
</style>
