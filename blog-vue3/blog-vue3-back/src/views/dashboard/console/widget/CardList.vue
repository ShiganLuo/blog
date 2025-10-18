<template>
  <ul class="card-list" v-if="isLoading">
    <li class="art-custom-card" v-for="n in 7" :key="n">
      <el-skeleton :rows="3" animated />
    </li>
  </ul>
  <ul class="card-list" v-else :style="{ marginTop: showWorkTab ? '0' : '10px' }">
    <li class="art-custom-card" v-for="(item, index) in dataList" :key="index">
      <span class="des subtitle">{{ item.des }}</span>
      <CountTo class="number box-title" :startVal = "item.startVal" :endVal="item.endVal" :duration="1000" separator=""></CountTo>
      <div class="change-box">
        <span class="change-text">较上周</span>
        <span
          class="change"
          :class="[item.change.indexOf('+') === -1 ? 'text-danger' : 'text-success']"
        >
          {{ item.change }}
        </span>
      </div>
      <i class="iconfont-sys" v-html="item.icon"></i>
    </li>
  </ul>
</template>

<script setup lang="ts">
  import { useSettingStore } from '@/store/modules/setting'
  import { CountTo } from 'vue3-count-to'
  import { dashBoardService } from '@/api/website/dashBoradApi'
  import { CountToObjectList } from '@/types/website/dashBoard'
  import { ElMessage,ElSkeleton } from 'element-plus'

  const settingStore = useSettingStore();
  const showWorkTab = computed(() => settingStore.showWorkTab);
  const dataList = ref<CountToObjectList>([]); 
  const isLoading = ref(true);

  const fetchData = async () => {
    try {
        // API 应该返回 DashboardStat[] 类型的数据
        const responseData = await dashBoardService.getBlogDetailNumber();
        
        dataList.value = responseData.result;
        isLoading.value = false;
    } catch (error) {
        dataList.value = []
          ElMessage({
            message: '资源仪表盘数据加载失败，请检查网络或联系管理员。',
            type: 'error', // 使用 'error' 类型，显示为红色
            duration: 3000 // 消息显示 3 秒
        });
    }
  };
  // const dataList = reactive([
  //   {
  //     des: '总访问次数',
  //     icon: '&#xe721;', // element-plus 图标
  //     startVal: 0, // 起始值
  //     duration: 1000, // 动画持续时间
  //     num: 9120, // 结束值
  //     change: '+20%' 
  //   },
  //   {
  //     des: '在线访客数',
  //     icon: '&#xe724;',
  //     startVal: 0,
  //     duration: 1000,
  //     num: 182,
  //     change: '+10%'
  //   },
  //   {
  //     des: '点击量',
  //     icon: '&#xe7aa;',
  //     startVal: 0,
  //     duration: 1000,
  //     num: 9520,
  //     change: '-12%'
  //   },
  //   {
  //     des: '新用户',
  //     icon: '&#xe82a;',
  //     startVal: 0,
  //     duration: 1000,
  //     num: 156,
  //     change: '+30%'
  //   }
  // ])
  onMounted(() => {
    fetchData();
  });
</script>

<style lang="scss" scoped>
  .card-list {
    box-sizing: border-box;
    display: flex;
    flex-wrap: wrap;
    width: calc(100% + var(--console-margin));
    margin-top: 0 !important;
    margin-left: calc(0px - var(--console-margin));
    background-color: transparent !important;

    li {
      position: relative;
      box-sizing: border-box;
      display: flex;
      flex-direction: column;
      justify-content: center;
      width: calc(25% - var(--console-margin));
      height: 140px;
      padding: 0 18px;
      margin: 0 0 0 var(--console-margin);
      background: var(--art-main-bg-color);

      $icon-size: 52px;

      .iconfont-sys {
        position: absolute;
        top: 0;
        right: 20px;
        bottom: 0;
        width: $icon-size;
        height: $icon-size;
        margin: auto;
        overflow: hidden;
        font-size: 22px;
        line-height: $icon-size;
        color: var(--el-color-primary) !important;
        text-align: center;
        background-color: var(--el-color-primary-light-9);
        border-radius: 12px;
      }

      .des {
        display: block;
        height: 14px;
        font-size: 14px;
        line-height: 14px;
      }

      .number {
        display: block;
        margin-top: 10px;
        font-size: 28px;
        font-weight: 400;
      }

      .change-box {
        display: flex;
        align-items: center;
        margin-top: 10px;

        .change-text {
          display: block;
          font-size: 13px;
          color: var(--art-text-gray-600);
        }

        .change {
          display: block;
          margin-left: 5px;
          font-size: 13px;
          font-weight: bold;
        }
      }
    }
  }

  .dark {
    .card-list {
      li {
        .iconfont-sys {
          background-color: #232323 !important;
        }
      }
    }
  }
</style>
