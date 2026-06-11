<template>
  <div class="admin-dashboard">
    <div class="vc-index-nav">
      <span><i class="el-icon-s-home"></i> 首页</span>
      <span class="margin-left-sm margin-right-sm">/</span>
      <span>平台数据</span>
    </div>

    <el-row :gutter="16" class="stat-row">
      <el-col :span="4" v-for="(item, index) in statDatas" :key="index" class="stat-col">
        <div class="stat-card">
          <div class="stat-value" :style="{ color: item.color }">{{ item.value }}</div>
          <div class="stat-name">{{ item.name }}</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <div class="chart-container">
          <div ref="communityEventChart" class="chart-box"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-container">
          <div ref="communityMonitorPoolChart" class="chart-box"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { queryAdminCount, queryCommunityEvent, queryCommunityMonitorPool } from '@/api/index/indexApi'

const COLORS = ['#1acda1', '#ffae11', '#ff7911', '#3a68f2']

export default {
  name: 'AdminDashboard',
  data() {
    return {
      statDatas: [],
      eventChart: null,
      monitorChart: null
    }
  },
  mounted() {
    this.loadStatData()
    this.loadCommunityEvent()
    this.loadCommunityMonitor()
  },
  beforeDestroy() {
    if (this.eventChart) this.eventChart.dispose()
    if (this.monitorChart) this.monitorChart.dispose()
  },
  methods: {
    loadStatData() {
      queryAdminCount({ platform: 'admin' }).then(res => {
        const data = res.data || res
        if (Array.isArray(data)) {
          this.statDatas = data.map((item, index) => ({
            ...item,
            color: COLORS[index % COLORS.length]
          }))
        }
      }).catch(() => {})
    },
    loadCommunityEvent() {
      queryCommunityEvent({ platform: 'admin' }).then(res => {
        const data = res.data || res
        if (Array.isArray(data)) {
          this.$nextTick(() => {
            this.initEventChart(data)
          })
        }
      }).catch(() => {})
    },
    loadCommunityMonitor() {
      queryCommunityMonitorPool({ platform: 'admin' }).then(res => {
        const data = res.data || res
        if (Array.isArray(data)) {
          this.$nextTick(() => {
            this.initMonitorChart(data)
          })
        }
      }).catch(() => {})
    },
    initEventChart(data) {
      if (this.eventChart) this.eventChart.dispose()
      const dom = this.$refs.communityEventChart
      if (!dom) return
      const source = [['product', '今日小区事件数']]
      data.forEach(item => {
        source.push([item.communityName, item.count])
      })
      const myChart = echarts.init(dom)
      myChart.setOption({
        legend: {},
        tooltip: {},
        title: { show: true, text: '小区事件数' },
        color: ['#FFDAB9', '#66CDAA'],
        dataset: { source },
        xAxis: { type: 'category' },
        yAxis: {},
        series: [{ type: 'bar' }, { type: 'bar' }]
      })
      this.eventChart = myChart
      window.addEventListener('resize', () => { myChart.resize() })
    },
    initMonitorChart(data) {
      if (this.monitorChart) this.monitorChart.dispose()
      const dom = this.$refs.communityMonitorPoolChart
      if (!dom) return
      const source = [['product', '今日监控告警数']]
      data.forEach(item => {
        source.push([item.communityName, item.count])
      })
      const myChart = echarts.init(dom)
      myChart.setOption({
        legend: {},
        tooltip: {},
        title: { show: true, text: '小区监控告警数' },
        color: ['#FFDAB9', '#66CDAA'],
        dataset: { source },
        xAxis: { type: 'category' },
        yAxis: {},
        series: [{ type: 'bar' }, { type: 'bar' }]
      })
      this.monitorChart = myChart
      window.addEventListener('resize', () => { myChart.resize() })
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
  padding: 16px;
}
.vc-index-nav {
  margin-bottom: 16px;
  font-size: 14px;
  color: #666;
}
.margin-left-sm { margin-left: 8px; }
.margin-right-sm { margin-right: 8px; }
.stat-row {
  margin-bottom: 16px;
}
.stat-col {
  margin-bottom: 16px;
}
.stat-card {
  background: #fff;
  border: 1px solid #e0e5eb;
  text-align: center;
  height: 140px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  border-radius: 4px;
}
.stat-value {
  font-size: 38px;
  font-weight: 600;
}
.stat-name {
  margin-top: 16px;
  font-size: 16px;
  font-weight: 300;
  color: #666;
}
.chart-row {
  margin-bottom: 0;
}
.chart-container {
  background: #fff;
  padding: 16px;
  border: 1px solid #e0e5eb;
  border-radius: 4px;
}
.chart-box {
  height: 400px;
  width: 100%;
}
</style>
