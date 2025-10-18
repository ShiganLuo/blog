<template>
  <div class="region sales-overview art-custom-card">
    <div class="card-header">
      <div class="title">
        <h4 class="box-title">文章发表</h4>
        <p class="subtitle" v-if="isAddLoading">比上周 <el-skeleton :rows="0" animated class="inline-skeleton" /></p>
        <p class="subtitle" v-else>今年增长<span class="text-success">{{articleAdd}}</span></p>
      </div>
    </div>
    <el-skeleton :rows="8" animated v-if="isChartLoading" class="chart"/>
    <div class="chart" ref="chartRef" v-else></div>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
  import echarts from '@/plugins/echarts'
  import { useECharts } from '@/utils/echarts/useECharts'
  import { hexToRgba } from '@/utils/colors'
  import { useSettingStore } from '@/store/modules/setting'
  import { SystemThemeEnum } from '@/enums/appEnum'
  import { getCssVariable } from '@/utils/colors'
  import { ElSkeleton } from 'element-plus'
  import { MonthChartObject } from '@/types/website/dashBoard'
  import {  dashBoardService } from '@/api/website/dashBoradApi'

  const chartRef = ref<HTMLDivElement>()
  const { setOptions, removeResize, resize } = useECharts(chartRef as Ref<HTMLDivElement>);

  const store = useSettingStore();
  const theme = computed(() => store.systemThemeType);
  const isLight = computed(() => theme.value === SystemThemeEnum.LIGHT);
  const settingStore = useSettingStore();
  const menuOpen = computed(() => settingStore.menuOpen);
  const isAddLoading = ref(true);
  const isChartLoading = ref(true);
  const articleAdd = ref<string>("+0.00%");;
  const artilceMonthsChart = reactive<MonthChartObject>({
    months: [],
    counts: []
  });
  // 收缩菜单时，重新计算图表大小
  watch(menuOpen, () => {
    const delays = [100, 200, 300]
    delays.forEach((delay) => {
      setTimeout(resize, delay)
    })
  })

  const fetchChartData = async ()=> {
    try {
      const responseData = await  dashBoardService.getArticleAddInThisYear();
      artilceMonthsChart.months = responseData.result.months;
      artilceMonthsChart.counts = responseData.result.counts;
      isChartLoading.value = false;
    } catch {
      isChartLoading.value = true;
      ElMessage({
            message: '文章增长表格数据加载失败，请检查网络或联系管理员。',
            type: 'error', // 使用 'error' 类型，显示为红色
            duration: 3000 // 消息显示 3 秒
        });
    }
  }

  const fetchAddData = async ()=> {
    try {
      const responseData = await dashBoardService.getArticleAddCompareToLastWeek();
      articleAdd.value = responseData.result
      isAddLoading.value = false;
    } catch {
      isAddLoading.value = true;
      ElMessage({
            message: '文章增长数据加载失败，请检查网络或联系管理员。',
            type: 'error', // 使用 'error' 类型，显示为红色
            duration: 3000 // 消息显示 3 秒
      });
    }
  }
  onMounted(async () => {
    await fetchChartData();
    await fetchAddData();
    if (chartRef.value) {
      createChart();
    }
  })

  onUnmounted(() => {
    removeResize()
    
  })
  const createChart = () => {
    setOptions({
      grid: {
        left: '2.2%',
        right: '3%',
        bottom: '0%',
        top: '5px',
        containLabel: true
      },
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: artilceMonthsChart.months,
        axisLabel: {
          show: true,
          color: '#999',
          margin: 20,
          interval: 0,
          fontSize: 13
        },
        axisLine: {
          show: false
        }
      },
      yAxis: {
        type: 'value',
        axisLabel: {
          show: true,
          color: '#999',
          fontSize: 13
        },
        axisLine: {
          show: isLight.value ? true : false,
          lineStyle: {
            color: '#E8E8E8',
            width: 1
          }
        },
        splitLine: {
          show: true,
          lineStyle: {
            color: isLight.value ? '#e8e8e8' : '#444',
            width: 1,
            type: 'dashed'
          }
        }
      },
      series: [
        {
          name: '文章',
          color: getCssVariable('--main-color'),
          type: 'line',
          stack: '总量',
          data: artilceMonthsChart.counts,
          smooth: true,
          symbol: 'none',
          lineStyle: {
            width: 2.6
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              {
                offset: 0,
                color: hexToRgba(getCssVariable('--el-color-primary'), 0.2).rgba
              },
              {
                offset: 1,
                color: hexToRgba(getCssVariable('--el-color-primary'), 0.01).rgba
              }
            ])
          }
        }
      ]
    })
  }
</script>

<style lang="scss" scoped>
  .region {
    box-sizing: border-box;
    width: calc(58% - var(--console-margin));
    height: 420px;
    padding: 20px 0 30px;

    .card-header {
      padding: 0 18px !important;
      .title {
        p.subtitle {
        // skeleton 占位符样式
          .inline-skeleton {
            display: inline-block;
            width: 3em;
            height: 1em;
            // vertical-align: middle;
          }
        }
      }

    }

    .chart {
      width: 100%;
      height: calc(100% - 80px);
      margin-top: 30px;
    }
  }
</style>
