<template>
  <div class="ac-inout-statistics">
    <el-row :gutter="16">
      <el-col :span="3">
        <el-card shadow="never" class="left-device-card">
          <div class="device-list">
            <div
              v-for="item in accessControls"
              :key="item.machineId"
              class="device-item"
              :class="{ 'device-active': selectedMachineId === item.machineId }"
              @click="switchDevice(item)"
            >
              {{ item.machineName }}
            </div>
            <div v-if="accessControls.length === 0" class="empty-tip">暂无设备</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="21">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-card shadow="never" class="chart-card">
              <div ref="inoutChart" class="chart-container"></div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="never" class="chart-card">
              <div ref="logChart" class="chart-container"></div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="never" class="chart-card">
              <div ref="visitChart" class="chart-container"></div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="never" class="chart-card">
              <div ref="faceChart" class="chart-container"></div>
            </el-card>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import echarts from 'echarts'
import { listAccessControl, queryAcInoutStatistics, queryAcLogStatistics, queryAcVisitStatistics, queryAcFaceStatistics } from '@/api/accessControl/accessControlApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AcInoutStatistics',
  data() {
    return {
      accessControls: [],
      selectedMachineId: '',
      charts: {
        inout: null,
        log: null,
        visit: null,
        face: null
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
          this.loadAccessControls()
        }
      }
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initCharts()
    })
  },
  beforeDestroy() {
    Object.values(this.charts).forEach(chart => {
      if (chart) chart.dispose()
    })
  },
  methods: {
    initCharts() {
      this.charts.inout = echarts.init(this.$refs.inoutChart)
      this.charts.log = echarts.init(this.$refs.logChart)
      this.charts.visit = echarts.init(this.$refs.visitChart)
      this.charts.face = echarts.init(this.$refs.faceChart)
    },
    async loadAccessControls() {
      try {
        const res = await listAccessControl({ page: 1, row: 100, communityId: this.currentCommunityId })
        this.accessControls = res.data || []
        if (this.accessControls.length > 0) {
          this.selectedMachineId = this.accessControls[0].machineId
          this.fetchStatistics()
        }
      } catch (e) {
        this.accessControls = []
      }
    },
    switchDevice(item) {
      this.selectedMachineId = item.machineId
      this.fetchStatistics()
    },
    fetchStatistics() {
      const params = { communityId: this.currentCommunityId, machineId: this.selectedMachineId }
      this.fetchAcInoutStatistics(params)
      this.fetchAcLogStatistics(params)
      this.fetchAcVisitStatistics(params)
      this.fetchAcFaceStatistics(params)
    },
    async fetchAcInoutStatistics(params) {
      try {
        const res = await queryAcInoutStatistics(params)
        this.setChartOption(this.charts.inout, res.data || [], '人员进出统计表')
      } catch (e) { /* empty */ }
    },
    async fetchAcLogStatistics(params) {
      try {
        const res = await queryAcLogStatistics(params)
        this.setChartOption(this.charts.log, res.data || [], '人员下发统计表')
      } catch (e) { /* empty */ }
    },
    async fetchAcVisitStatistics(params) {
      try {
        const res = await queryAcVisitStatistics(params)
        this.setChartOption(this.charts.visit, res.data || [], '访客统计表')
      } catch (e) { /* empty */ }
    },
    async fetchAcFaceStatistics(params) {
      try {
        const res = await queryAcFaceStatistics(params)
        this.setChartOption(this.charts.face, res.data || [], '人脸审核统计表')
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
.left-device-card { height: calc(100vh - 120px); overflow-y: auto; }
.left-device-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.left-device-card .device-list { padding: 0; }
.left-device-card .device-item {
  padding: 10px 12px;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 5px;
  text-align: center;
  transition: all 0.3s;
  font-size: 13px;
  color: #303133;
}
.left-device-card .device-item:hover { background-color: #f5f7fa; }
.left-device-card .device-item.device-active { background-color: #409eff; color: white; font-weight: bold; }
.left-device-card .empty-tip { padding: 20px; text-align: center; color: #909399; font-size: 13px; }

.chart-card { margin-bottom: 16px; }
.chart-card .chart-container { width: 100%; height: 300px; }
</style>
