<template>
  <div class="page-content">
    <el-row>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-form :model="queryParams" ref="queryRef">
          <el-input placeholder="请输入标签名称" v-model="queryParams.keywords" @keyup.enter="handleQuery" />
        </el-form>
      </el-col>
      <div style="width: 12px"></div>
      <el-col :xs="24" :sm="12" :lg="6" class="el-col2">
        <el-button @click="handleQuery" v-ripple>搜索 </el-button>
        <el-button @click="handleAdd" v-auth="['server:tag:add']" v-ripple>新增 </el-button>
        <el-button
          @click="handleDelete"
          :disabled="multiple"
          v-auth="['server:tag:remove']"
          v-ripple
          >删除
        </el-button>
      </el-col>
    </el-row>

    <art-table
      v-loading="loading"
      :data="tagList"
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
        <el-table-column label="标签名称" align="center" prop="tagName" />
        <el-table-column label="文章量" align="center" prop="articleCount" />
        <el-table-column label="创建时间" align="center" prop="createTime">
          <template #default="scope">
            {{ parseTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center">
          <template #default="scope">
            <button-table type="edit" v-auth="['blog:tag:edit']" @click="handleUpdate(scope.row)" />
            <button-table
              type="delete"
              v-auth="['blog:tag:remove']"
              @click="handleDelete(scope.row)"
            />
          </template>
        </el-table-column>
      </template>
    </art-table>

    <!-- 添加或修改标签对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="tagRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标签名称" prop="tagName">
          <el-input v-model="form.tagName" placeholder="请输入标签名称" />
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
  import { TagService } from '@/api/blog/tagApi'
  import { ref, reactive } from 'vue'
  import { ElMessage, ElMessageBox } from 'element-plus'
  import { FormInstance } from 'element-plus'
  import { TagResult } from '@/types/blog/tag'
  import { parseTime } from '@/utils/utils'
  const tagList = ref<TagResult[]>([])
  const open = ref(false)
  const loading = ref(true)
  const ids = ref([])
  const multiple = ref(true)
  const total = ref(0)
  const title = ref('')
  const queryRef = ref()
  const tagRef = ref<FormInstance>()
  // 定义初始表单状态
  const initialFormState = {
    id: null,
    tagName: null
  }
  const form = reactive({ ...initialFormState })
  const queryParams = reactive({
    current: 1,
    size: 10,
    keywords: ''
  })
  const rules = reactive({
    tagName: [
      {
        required: true,
        message: '标签名称不能为空',
        trigger: 'blur'
      }
    ]
  })

  /** 查询标签列表 */
  const getList = async () => {
    loading.value = true
    const res = await TagService.listTag(queryParams)
    if (res.code === 200) {
      tagList.value = res.result.list
      total.value = res.result.total
      loading.value = false
    }
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
    queryParams.size = page
    getList()
  }

  // 多选框选中数据
  const handleSelectionChange = (selection: any) => {
    ids.value = selection.map((item: any) => item.id)
    multiple.value = !selection.length
  }

  /** 新增按钮操作 */
  const handleAdd = () => {
    reset()
    open.value = true
    title.value = '添加标签'
  }

  /** 修改按钮操作 */
  const handleUpdate = async (row: any) => {
    reset()
    Object.assign(form, row)
    open.value = true
    title.value = '修改标签'
  }

  /** 提交按钮 */
  const submitForm = async () => {
    if (!tagRef.value) return
    await tagRef.value.validate(async (valid) => {
      if (valid) {
        if (form.id != null) {
          const res = await TagService.updateTag(form)
          if (res.code === 200) {
            ElMessage.success(res.message)
            open.value = false
            getList()
          }
        } else {
          const res = await TagService.addTag(form)
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
    const _ids = row ? [row.id] : ids.value
    const Tr = await ElMessageBox.confirm('是否确认删除标签编号为"' + _ids + '"的数据项？')
    if (Tr) {
      const res = await TagService.deleteTag(_ids)
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
