<template>
  <div class="tab-component">
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams" size="small">
        <el-form-item>
          <el-date-picker v-model="queryParams.startTime" type="date" placeholder="开始时间" value-format="yyyy-MM-dd" style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-date-picker v-model="queryParams.endTime" type="date" placeholder="结束时间" value-format="yyyy-MM-dd" style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never">
      <div ref="chartDom" style="width: 100%; height: 350px"></div>
    </el-card>
  </div>
</template>

<script>
import { listCarInoutPaymentSummary } from '@/api/car/carApi'
import * as echarts from 'echarts'

export default {
  name: 'ParkingAreaControlPaymentSummary',
  props: { paId: { type: String, default: '' } },
  data() {
    return {
      queryParams: { startTime: '', endTime: '' },
      chart: null
    }
  },
  mounted() {
    this.loadData()
  },
  beforeDestroy() {
    if (this.chart) {
      this.chart.dispose()
      this.chart = null
    }
  },
  methods: {
    async loadData() {
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listCarInoutPaymentSummary({
          page: 1, row: 100,
          paId: this.paId,
          communityId,
          startTime: this.queryParams.startTime,
          endTime: this.queryParams.endTime
        })
        const data = res.data || []
        this.initChart(data)
      } catch (e) {
        console.error('获取收费统计失败', e)
      }
    },
    handleSearch() { this.loadData() },
    initChart(data) {
      if (!this.$refs.chartDom) return
      if (this.chart) { this.chart.dispose() }
      this.chart = echarts.init(this.$refs.chartDom)

      const createTimes = data.map(item => item.createTime)
      const realChargeTotals = data.map(item => item.realChargeTotal)

      this.chart.setOption({
        title: { text: '每日收费统计' },
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        toolbox: { feature: { saveAsImage: {} } },
        xAxis: { type: 'category', boundaryGap: false, data: createTimes },
        yAxis: { type: 'value' },
        series: [{
          name: '收入金额',
          type: 'line',
          data: realChargeTotals
        }]
      })
    }
  }
}
</script>

<style scoped>
.tab-component { padding: 0; }
.search-card { margin-bottom: 16px; }
</style>
