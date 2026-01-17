<template>
  <el-card class="main-card">
    <div class="title">{{ route.meta.title }}</div>
    <div class="operation">
      <div class="all-check">
        <el-checkbox
          :indeterminate="isIndeterminate"
          v-model="checkAll"
          @change="handleCheckAllChange"
        >
          全选
        </el-checkbox>
        <div class="check-count">已选择{{ selectPhotoIds.length }}张</div>
      </div>
      <el-button @click="updatePhotoDelete()" :disabled="selectPhotoIds.length == 0">
        批量恢复
      </el-button>
      <el-button @click="batchDeletePhoto()" :disabled="selectPhotoIds.length == 0">
        批量删除
      </el-button>
    </div>

    <el-empty v-if="photos.length == 0" description="暂无照片" />

    <div class="photo-container">
      <el-checkbox-group v-model="selectPhotoIds" @change="handleCheckedPhotoChange">
        <el-checkbox :label="item.id" v-for="item of photos" :key="item.id">
          <div class="photo-item">
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
      @size-change="sizeChange"
      @current-change="currentChange"
      :current-page="current"
      :page-size="size"
      :total="count"
      layout="prev, pager, next"
    />
  </el-card>
</template>

<script setup>
  import { ref, onMounted } from 'vue'
  import { useRoute } from 'vue-router'
  import { ElMessage } from 'element-plus'
  import { PhotoService } from '@/api/photo/photoApi'

  const route = useRoute()
  const loading = ref(true)
  const isIndeterminate = ref(false)
  const checkAll = ref(false)
  const photos = ref([])
  const photoIds = ref([])
  const selectPhotoIds = ref([])
  const current = ref(1)
  const size = ref(18)
  const count = ref(0)

  const listPhotos = async () => {
    const res = await PhotoService.listPhoto({
      pageNum: current.value,
      pageSize: size.value,
      isDelete: 1
    })
    if (res.code == 200) {
      photos.value = res.result.list
      count.value = res.result.total
      loading.value = false
    }
  }

  const sizeChange = (newSize) => {
    size.value = newSize
    listPhotos()
  }

  const currentChange = (newCurrent) => {
    current.value = newCurrent
    listPhotos()
  }

  watch(
    () => photos.value,
    (newPhotos) => {
      photoIds.value = newPhotos.map((item) => item.id)
    }
  )

  const updatePhotoDelete = async () => {
    const res = await PhotoService.updatePhotoDelete({ ids: selectPhotoIds.value, isDelete: 0 })
    if (res.code === 200) {
      ElMessage.success(res.msg)
      listPhotos()
    }
  }

  const batchDeletePhoto = async () => {
    const Tr = await ElMessageBox.confirm('是否确认彻底删除照片数据？')
    if (Tr) {
      const res = await PhotoService.deletePhoto(selectPhotoIds.value)
      if (res.code === 200) {
        ElMessage.success(res.msg)
        listPhotos()
      }
    }
  }

  const handleCheckAllChange = (val) => {
    selectPhotoIds.value = val ? photoIds.value : []
    isIndeterminate.value = false
  }

  const handleCheckedPhotoChange = (value) => {
    const checkedCount = value.length
    checkAll.value = checkedCount === photoIds.value.length
    isIndeterminate.value = checkedCount > 0 && checkedCount < photoIds.value.length
  }

  onMounted(() => {
    listPhotos()
  })
</script>

<style scoped>
  .operation {
    display: flex;
    justify-content: flex-end;
    margin-top: 2.25rem;
    margin-bottom: 2rem;
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
  .photo-item {
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
  .photo-container :deep(.el-checkbox .el-checkbox__input) {
    display: none;
  }
</style>
