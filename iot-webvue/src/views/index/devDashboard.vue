<template>
  <div class="dev-dashboard">
    <div class="vc-index-nav">
      <span><i class="el-icon-s-home"></i> 首页</span>
      <span class="margin-left-sm margin-right-sm">/</span>
      <span>开发中心</span>
    </div>

    <el-row :gutter="16" class="stat-row">
      <el-col :span="4" v-for="(item, index) in statDatas" :key="index" class="stat-col">
        <div class="stat-card">
          <div class="stat-value" :style="{ color: item.color }">{{ item.value }}</div>
          <div class="stat-name">{{ item.name }}</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="bottom-row">
      <el-col :span="12">
        <div class="quick-panel">
          <div class="panel-title">常用功能</div>
          <el-row :gutter="8" class="quick-row">
            <el-col :span="8" v-for="(item, index) in quickPages" :key="index" class="quick-col">
              <div class="quick-item" @click="$router.push(item.url)">
                <img :src="item.icon" class="quick-icon" />
                <div class="quick-name">{{ item.name }}</div>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-container">
          <div ref="appServiceChart" class="chart-box"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { queryDevCount, queryAppServiceData } from '@/api/index/indexApi'

const COLORS = ['#1acda1', '#ffae11', '#ff7911', '#3a68f2']

export default {
  name: 'DevDashboard',
  data() {
    return {
      statDatas: [],
      quickPages: [
        { name: '应用', url: '/pages/dev/appManage', icon: '/img/app.png' },
        { name: '服务', url: '/pages/dev/serviceManage', icon: '/img/api.png' },
        { name: '服务注册', url: '/pages/dev/serviceRegisterManage', icon: '/img/register.png' },
        { name: '菜单维护', url: '/pages/dev/menuManage', icon: '/img/menu.png' },
        { name: '配置中心', url: '/pages/dev/mappingManage', icon: '/img/config.png' },
        { name: '刷新缓存', url: '/pages/dev/cacheManage', icon: '/img/cache.png' },
        { name: '定时任务', url: '/pages/dev/jobManage', icon: '/img/job.png' }
      ],
      appServiceChart: null
    }
  },
  mounted() {
    this.loadStatData()
    this.loadAppServiceData()
  },
  beforeDestroy() {
    if (this.appServiceChart) this.appServiceChart.dispose()
  },
  methods: {
    loadStatData() {
      queryDevCount({ platform: 'admin' }).then(res => {
        const data = res.data || res
        if (Array.isArray(data)) {
          this.statDatas = data.map((item, index) => ({
            ...item,
            color: COLORS[index % COLORS.length]
          }))
        }
      }).catch(() => {})
    },
    loadAppServiceData() {
      queryAppServiceData({ platform: 'admin' }).then(res => {
        const data = res.data || res
        if (Array.isArray(data)) {
          this.$nextTick(() => {
            this.initAppServiceChart(data)
          })
        }
      }).catch(() => {})
    },
    initAppServiceChart(data) {
      if (this.appServiceChart) this.appServiceChart.dispose()
      const dom = this.$refs.appServiceChart
      if (!dom) return
      const source = [['product', '接口数']]
      data.forEach(item => {
        source.push([item.name, item.serviceCount])
      })
      const myChart = echarts.init(dom)
      myChart.setOption({
        legend: {},
        tooltip: {},
        title: { show: true, text: '应用接口数' },
        color: ['#66CDAA'],
        dataset: { source },
        xAxis: { type: 'category' },
        yAxis: {},
        series: [{ type: 'bar' }]
      })
      this.appServiceChart = myChart
      window.addEventListener('resize', () => { myChart.resize() })
    }
  }
}
</script>

<style scoped>
.dev-dashboard {
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
.bottom-row {
  margin-bottom: 0;
}
.quick-panel {
  background: #fff;
  border: 1px solid #e0e5eb;
  border-radius: 4px;
  padding: 16px;
}
.panel-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e0e5eb;
}
.quick-row {
  padding: 8px 0;
}
.quick-col {
  margin-bottom: 16px;
}
.quick-item {
  text-align: center;
  cursor: pointer;
  padding: 8px;
  border-radius: 4px;
  transition: background 0.2s;
}
.quick-item:hover {
  background: #f5f7fa;
}
.quick-icon {
  width: 48px;
  height: 48px;
}
.quick-name {
  margin-top: 8px;
  font-size: 13px;
  color: #333;
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
