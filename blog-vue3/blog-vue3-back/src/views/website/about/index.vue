<template>
  <div class="article-edit">
    <div>
      <div class="editor-wrap">
        <!-- 标题 -->
        <el-row> 关于我 </el-row>

        <!-- 富文本编辑器 -->
        <Editor class="el-top" v-model="content" :action="uploadImageUrl" />

        <div style="display: flex; justify-content: flex-end">
          <el-button type="primary" @click="submit" style="width: 100px"> 修改 </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref } from 'vue'
  import AboutService from '@/api/website/aboutApi'
  const content = ref('')

  const uploadImageUrl = `${import.meta.env.VITE_API_BASE_URL}/blog/article/admin/articles/images`

  const submit = async () => {
    if (content.value === '<p><br></p>') {
      ElMessage.warning('请输入内容')
      return
    }
    const res = await AboutService.updateAbout({ content: delCodeTrim(content.value) })
    if (res.code === 200) {
      ElMessage.success(res.message)
      getAbout()
    }
  }

  const getAbout = async () => {
    const res = await AboutService.getAbout()
    if (res.code === 200) {
      content.value = res.result.content
    }
  }

  const delCodeTrim = (content: string): string => {
    return content.replace(/(\s*)<\/code>/g, '</code>')
  }

  onMounted(() => {
    getAbout()
  })
</script>

<style lang="scss" scoped>
  .article-edit {
    .editor-wrap {
      padding: 20px;
      background-color: #f9f9f9;
      border-radius: 8px;
      .el-row {
        font-size: 24px;
        font-weight: bold;
        color: #333;
        margin-bottom: 20px;
      }
      .el-top {
        max-height: 650px;
        margin-bottom: 20px;
      }
      .el-button {
        background-color: #409eff;
        border-color: #409eff;
        color: #fff;
        &:hover {
          background-color: #66b1ff;
          border-color: #66b1ff;
        }
      }
    }
  }
</style>
