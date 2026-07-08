<template>
  <div class="charge-statistics">
    <el-row :gutter="16">
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <div ref="orderCountChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <div ref="orderMoneyChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <div ref="monthCountChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <div ref="monthMoneyChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import echarts from 'echarts'
import { queryOrderCountStatistics, queryOrderMoneyStatistics, queryMonthCountStatistics, queryMonthMoneyStatistics } from '@/api/charge/chargeApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ChargeStatistics',
  data() {
    return {
      charts: {
        orderCount: null,
        orderMoney: null,
        monthCount: null,
        monthMoney: null
      }
    }
  },
  computed: {
    currentCommunityId() {
      return (getCurrentCommunity() || {}).communityId || ''
    }
  },
  watch: {
    currentCommunityId: {
      immediate: true,
      handler(val) {
        if (val) {
          this.$nextTick(() => {
            this.initCharts()
            this.fetchStatistics()
          })
        }
      }
    }
  },
  beforeDestroy() {
    Object.values(this.charts).forEach(chart => {
      if (chart) chart.dispose()
    })
  },
  methods: {
    initCharts() {
      this.charts.orderCount = echarts.init(this.$refs.orderCountChart)
      this.charts.orderMoney = echarts.init(this.$refs.orderMoneyChart)
      this.charts.monthCount = echarts.init(this.$refs.monthCountChart)
      this.charts.monthMoney = echarts.init(this.$refs.monthMoneyChart)
    },
    fetchStatistics() {
      const params = { communityId: this.currentCommunityId, machineId: '' }
      this.fetchOrderCount(params)
      this.fetchOrderMoney(params)
      this.fetchMonthCount(params)
      this.fetchMonthMoney(params)
    },
    async fetchOrderCount(params) {
      try {
        const res = await queryOrderCountStatistics(params)
        this.setChartOption(this.charts.orderCount, res.data || [], '充电订单统计')
      } catch (e) { /* empty */ }
    },
    async fetchOrderMoney(params) {
      try {
        const res = await queryOrderMoneyStatistics(params)
        this.setChartOption(this.charts.orderMoney, res.data || [], '充电金额统计')
      } catch (e) { /* empty */ }
    },
    async fetchMonthCount(params) {
      try {
        const res = await queryMonthCountStatistics(params)
        this.setChartOption(this.charts.monthCount, res.data || [], '月卡订单统计')
      } catch (e) { /* empty */ }
    },
    async fetchMonthMoney(params) {
      try {
        const res = await queryMonthMoneyStatistics(params)
        this.setChartOption(this.charts.monthMoney, res.data || [], '月卡金额统计')
      } catch (e) { /* empty */ }
    },
    setChartOption(chart, data, title) {
      if (!chart) return
      const createTimes = (data || []).map(item => item.createTime)
      const totals = (data || []).map(item => item.totalCount)
      chart.setOption({
        title: { text: title },
        tooltip: { trigger: 'axis' },
        legend: { data: createTimes },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        toolbox: { feature: { saveAsImage: {} } },
        xAxis: { type: 'category', boundaryGap: false, data: createTimes },
        yAxis: { type: 'value' },
        series: [{ name: '数量', type: 'line', stack: 'Total', data: totals }]
      })
    }
  }
}
</script>

<style scoped>
.chart-card { margin-bottom: 16px; }
.chart-card .chart-container { width: 100%; height: 300px; }
</style>
