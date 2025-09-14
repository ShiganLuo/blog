<script setup lang="ts">
import { ElMessage } from 'element-plus'
import { getVisitorLocation,calculateDistance } from '@/utils/tool'
import { SERVER_LOCATION } from '@/config/config'
import { onMounted } from 'vue'

onMounted(async () => {
  const location = await getVisitorLocation()
  if (location) {
    const distance = calculateDistance(
      location.latitude,
      location.longitude,
      SERVER_LOCATION.latitude,
      SERVER_LOCATION.longitude
    )
    
    ElMessage.success({
      message: `👋 欢迎来自 ${location.city}, ${location.country} 的用户！\n我们之间相距约 ${Math.round(distance)} 公里。`,
      duration: 6000, // 6秒后关闭
      showClose: true, // 显示关闭按钮
    })
  } else {
    ElMessage.info('欢迎访问！')
  }
})
</script>

<template>
</template>