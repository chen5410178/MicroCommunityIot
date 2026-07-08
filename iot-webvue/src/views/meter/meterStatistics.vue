<template>
  <div class="meter-statistics">
    <el-row :gutter="16">
      <el-col :span="3">
        <el-card shadow="never" class="left-type-card">
          <div class="type-list">
            <div
              class="type-item"
              :class="{ 'type-active': selectedMeterType === '' }"
              @click="switchMeterType('')"
            >
              全部
            </div>
            <div
              v-for="item in meterTypes"
              :key="item.typeId"
              class="type-item"
              :class="{ 'type-active': selectedMeterType === item.typeId }"
              @click="switchMeterType(item.typeId)"
            >
              {{ item.typeName }}
            </div>
            <div v-if="meterTypes.length === 0" class="empty-tip">暂无类型</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="21">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-card shadow="never" class="chart-card">
              <div ref="countChart" class="chart-container"></div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="never" class="chart-card">
              <div ref="moneyChart" class="chart-container"></div>
            </el-card>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import echarts from 'echarts'
import { listMeterTypes, queryMeterCountStatistics, queryMeterMoneyStatistics } from '@/api/meter/meterApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'MeterStatistics',
  data() {
    return {
      meterTypes: [],
      selectedMeterType: '',
      charts: {
        count: null,
        money: null
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
            this.loadMeterTypes()
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
      this.charts.count = echarts.init(this.$refs.countChart)
      this.charts.money = echarts.init(this.$refs.moneyChart)
    },
    async loadMeterTypes() {
      try {
        const res = await listMeterTypes({ page: 1, row: 500, communityId: this.currentCommunityId })
        this.meterTypes = res.data || []
      } catch (e) {
        this.meterTypes = []
      }
    },
    switchMeterType(typeId) {
      this.selectedMeterType = typeId
      this.fetchStatistics()
    },
    fetchStatistics() {
      const params = { communityId: this.currentCommunityId, machineId: '', meterType: this.selectedMeterType }
      this.fetchCountStatistics(params)
      this.fetchMoneyStatistics(params)
    },
    async fetchCountStatistics(params) {
      try {
        const res = await queryMeterCountStatistics(params)
        this.setChartOption(this.charts.count, res.data || [], '抄表数量统计')
      } catch (e) { /* empty */ }
    },
    async fetchMoneyStatistics(params) {
      try {
        const res = await queryMeterMoneyStatistics(params)
        this.setChartOption(this.charts.money, res.data || [], '充值金额统计')
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
.left-type-card { height: calc(100vh - 120px); overflow-y: auto; }
.left-type-card .type-list { padding: 0; }
.left-type-card .type-item {
  padding: 10px 12px;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 5px;
  text-align: center;
  transition: all 0.3s;
  font-size: 13px;
  color: #303133;
}
.left-type-card .type-item:hover { background-color: #f5f7fa; }
.left-type-card .type-item.type-active { background-color: #409eff; color: white; font-weight: bold; }
.left-type-card .empty-tip { padding: 20px; text-align: center; color: #909399; font-size: 13px; }

.chart-card { margin-bottom: 16px; }
.chart-card .chart-container { width: 100%; height: 300px; }
</style>
