<template>
  <div class="instrument-detail">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>仪表信息</span>
        <el-button size="small" @click="handleBack">返回</el-button>
      </div>
      <el-descriptions :column="4" border>
        <el-descriptions-item label="仪表ID">{{ detail.machineId }}</el-descriptions-item>
        <el-descriptions-item label="仪表名称">{{ detail.machineName }}</el-descriptions-item>
        <el-descriptions-item label="仪表编号">{{ detail.machineCode }}</el-descriptions-item>
        <el-descriptions-item label="厂家名称">{{ detail.factoryName }}</el-descriptions-item>
        <el-descriptions-item label="仪表类型">{{ detail.typeName }}</el-descriptions-item>
        <el-descriptions-item label="剩余电量">{{ detail.charge }}%</el-descriptions-item>
        <el-descriptions-item label="信号强度">{{ detail.sign }}dbm</el-descriptions-item>
        <el-descriptions-item label="上报时间">{{ detail.upMin }}min</el-descriptions-item>
        <el-descriptions-item label="采样时间">{{ detail.checkSec }}sec</el-descriptions-item>
        <el-descriptions-item label="添加时间">{{ detail.createTime }}</el-descriptions-item>
      </el-descriptions>
      <div v-if="detail.instrumentParamList && detail.instrumentParamList.length" style="margin-top: 16px">
        <el-descriptions :column="4" border title="规格参数">
          <el-descriptions-item v-for="item in detail.instrumentParamList" :key="item.specCd" :label="item.specName">{{ item.value }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
    <el-card shadow="never" class="tab-card">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="传感器" name="sensor">
          <el-table :data="detail.instrumentSensorList || []" border stripe>
            <el-table-column prop="sensorType" label="传感器类型" />
            <el-table-column label="参数类型">
              <template slot-scope="{ row }">{{ row.minValue ? '数值型' : '非数值型' }}</template>
            </el-table-column>
            <el-table-column prop="minValue" label="最小值" />
            <el-table-column prop="maxValue" label="最大值" />
            <el-table-column prop="units" label="单位" />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="上报数据" name="log">
          <el-table :data="logList" v-loading="logLoading" border stripe>
            <el-table-column prop="logId" label="日志ID" width="120" />
            <el-table-column prop="value" label="数值" />
            <el-table-column prop="createTime" label="上报时间" width="180" />
          </el-table>
          <el-pagination
            :current-page="logQuery.page"
            :page-size="logQuery.row"
            :total="logTotal"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleLogSizeChange"
            @current-change="handleLogPageChange"
            class="pagination-container"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { listInstruments, listInstrumentLogs } from '@/api/meter/meterApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'InstrumentDetail',
  data() {
    return {
      machineId: '',
      detail: {},
      activeTab: 'sensor',
      logList: [],
      logLoading: false,
      logTotal: 0,
      logQuery: {
        page: 1,
        row: 10
      }
    }
  },
  mounted() {
    this.machineId = this.$route.query.machineId || ''
    if (this.machineId) {
      this.fetchDetail()
    }
  },
  methods: {
    async fetchDetail() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listInstruments({ page: 1, row: 10, communityId, machineId: this.machineId })
        const data = res.data && res.data[0]
        if (data) {
          this.detail = data
        }
      } catch (e) {
        console.log('获取仪表详情失败')
      }
    },
    async fetchLogs() {
      this.logLoading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listInstrumentLogs({ ...this.logQuery, communityId, machineId: this.machineId })
        this.logList = res.data || []
        this.logTotal = res.records || 0
      } catch (e) {
        console.log('获取上报日志失败')
      } finally {
        this.logLoading = false
      }
    },
    handleTabClick(tab) {
      if (tab.name === 'log') {
        this.logQuery.page = 1
        this.fetchLogs()
      }
    },
    handleBack() {
      this.$router.go(-1)
    },
    handleLogSizeChange(val) {
      this.logQuery.row = val
      this.fetchLogs()
    },
    handleLogPageChange(val) {
      this.logQuery.page = val
      this.fetchLogs()
    }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.tab-card { margin-top: 16px; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
