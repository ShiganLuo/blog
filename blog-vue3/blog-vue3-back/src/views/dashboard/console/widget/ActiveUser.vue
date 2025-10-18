<template>
  <div class="region active-user art-custom-card">
    <el-skeleton :rows="5" animated v-if="isChartLoading" class="chart"/>
    <div class="chart" ref="chartRef" v-else></div>
    <div class="text">
      <h3 class="box-title">用户概述</h3>
      <p class="subtitle" v-if="isAddLoading">比上周 <el-skeleton :rows="0" animated class="inline-skeleton" /></p>
      <p class="subtitle" v-else>比上周 <span class="text-success">{{userAddPercentage}}</span></p>
      <p class="subtitle">我们为您创建了多个选项，可将它们组合在一起并定制为像素完美的页面</p>
    </div>
    <div class="list" v-if="isListLoading">
      <div v-for="n in 4" :key="n">
        <el-skeleton :rows="1" animated />
      </div>
    </div>
    <div class="list" v-else>
      <div v-for="(item, index) in listData" :key="index">
        <p>{{ item.num }}</p>
        <p class="subtitle">{{ item.name }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, onMounted, onUnmounted } from 'vue'
  import * as echarts from 'echarts'
  import { useECharts } from '@/utils/echarts/useECharts'
  import { useSettingStore } from '@/store/modules/setting'
  import { ElSkeleton } from 'element-plus'
  import { getCssVariable } from '@/utils/colors'
  import { dashBoardService } from '@/api/website/dashBoradApi'
  import { WeekChartObject,FiledCountList } from '@/types/website/dashBoard'
  const chartRef = ref<HTMLDivElement | null>(null);
  const { setOptions, removeResize, resize } = useECharts(chartRef as Ref<HTMLDivElement>);
  const settingStore = useSettingStore();
  const menuOpen = computed(() => settingStore.menuOpen);
  const isChartLoading = ref(true);
  const isAddLoading = ref(true);
  const isListLoading = ref(true);
  const userWeekChart = reactive<WeekChartObject>({
    days: [],
    counts: []
  });
  const userAddPercentage = ref<string>("+0.00%");
  const listData = ref<FiledCountList>([]);
  const fetchUserAddData = async ()=> {
    try {
      const responseData = await dashBoardService.getUserAddComparedToLastWeek();
      userAddPercentage.value = responseData.result;
      isAddLoading.value = false;
    } catch {
      isAddLoading.value = true;
      ElMessage({
            message: '用户增长数据加载失败，请检查网络或联系管理员。',
            type: 'error', // 使用 'error' 类型，显示为红色
            duration: 3000 // 消息显示 3 秒
        });
    }
  }
  const fetchListData = async ()=> {
    try {
      const responseData = await dashBoardService.getAccessAndUserCondition();
      listData.value = responseData.result;
      isListLoading.value = false;
    }catch {
      isListLoading.value = true;
      ElMessage({
            message: '用户增长和访问统计数据加载失败，请检查网络或联系管理员。',
            type: 'error', // 使用 'error' 类型，显示为红色
            duration: 3000 // 消息显示 3 秒
      });

    }
  }
  const fetchData = async ()=> {
    try {
      const responseData = await dashBoardService.getUserAddInLastWeek();
      userWeekChart.days = responseData.result.days;
      userWeekChart.counts = responseData.result.counts;
      isChartLoading.value = false;
    } catch {
      isChartLoading.value = true;
        userWeekChart.counts.length = 0;
        userWeekChart.days.length = 0;
          ElMessage({
            message: '用户增长图表数据加载失败，请检查网络或联系管理员。',
            type: 'error', // 使用 'error' 类型，显示为红色
            duration: 3000 // 消息显示 3 秒
        });
    }
  }
  // 收缩菜单时，重新计算图表大小
  watch(menuOpen, () => {
    const delays = [100, 200, 300]
    delays.forEach((delay) => {
      setTimeout(resize, delay)
    })
  })

  const store = useSettingStore()
  const isDark = computed(() => store.isDark)

  // const list = [
  //   { name: '总用户量', num: '32k' },
  //   { name: '总访问量', num: '128k' },
  //   { name: '日访问量', num: '1.2k' },
  //   { name: '周同比', num: '+5%' }
  // ]

  const createChart = () => {
    setOptions({
      grid: {
        left: '0',
        right: '4%',
        bottom: '0%',
        top: '5px',
        containLabel: true
      },
      yAxis: {
        type: 'value',
        axisLabel: {
          show: true,
          color: '#999',
          fontSize: 13
        },
        splitLine: {
          show: true,
          lineStyle: {
            color: isDark.value ? 'rgba(255, 255, 255, 0.1)' : '#EFF1F3',
            width: 1,
            type: 'dashed'
          }
        },
        axisLine: {
          show: true,
          lineStyle: {
            color: isDark.value ? 'rgba(255, 255, 255, 0.1)' : '#EFF1F3',
            width: 1
          }
        }
      },
      xAxis: {
        type: 'category',
        data: userWeekChart.days,
        boundaryGap: [0, 0.01],
        splitLine: {
          show: false
        },
        axisLine: {
          show: true,
          lineStyle: {
            color: isDark.value ? 'rgba(255, 255, 255, 0.1)' : '#EFF1F3',
            width: 1
          }
        },
        axisLabel: {
          show: true,
          color: '#999',
          fontSize: 13
        }
      },
      series: [
        {
          data: userWeekChart.counts,
          type: 'bar',
          barMaxWidth: 36,
          itemStyle: {
            borderRadius: [6, 6, 6, 6],
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                offset: 0,
                color: getCssVariable('--el-color-primary-light-4')
              },
              {
                offset: 1,
                color: getCssVariable('--el-color-primary')
              }
            ])
          }
        }
      ]
    })
  }

  onMounted(async () => {
    await fetchData();
    await fetchUserAddData();
    await fetchListData();
    if (chartRef.value) {
      createChart();
    }
  })

  onUnmounted(() => {
    removeResize()
  })
</script>

<style lang="scss" scoped>
  .dark {
    .region {
      .chart {
        background: none;
      }
    }
  }
  .region {
    box-sizing: border-box;
    width: 42%;
    height: 420px;
    padding: 16px;

    .chart {
      box-sizing: border-box;
      width: 100%;
      height: 220px;
      padding: 20px 0 20px 20px;
      // 跟随系统主色
      // background-image: linear-gradient(
      //   90deg,
      //   var(--el-color-primary-light-1),
      //   var(--el-color-primary-light-3),
      //   var(--el-color-primary-light-1)
      // );
      border-radius: calc(var(--custom-radius) / 2 + 4px) !important;
    }

    .text {
      margin-left: 3px;

      h3 {
        margin-top: 20px;
        font-size: 18px;
        font-weight: 500;
      }

      p {
        margin-top: 5px;
        font-size: 14px;

        &:last-of-type {
          height: 42px;
          margin-top: 5px;
        }
      }
      p.subtitle {
      // skeleton 占位符样式
        .inline-skeleton {
          display: inline-block;
          width: 3em;
          height: 1em;
          vertical-align: middle;
        }
      }
    }

    .list {
      display: flex;
      justify-content: space-between;
      margin-left: 3px;

      > div {
        flex: 1;

        p {
          font-weight: 400;

          &:first-of-type {
            font-size: 18px;
            color: var(--art-gray-900);
          }

          &:last-of-type {
            font-size: 13px;
          }
        }
      }
    }
  }

  @media screen and (max-width: $device-phone) {
    .dark {
      .active-user {
        .chart {
          padding: 15px 0 0 !important;
        }
      }
    }
  }
</style>
