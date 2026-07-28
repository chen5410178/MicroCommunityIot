<template>
  <div class="lamp-machine-detail">
    <el-card shadow="never" class="info-card">
      <div slot="header" class="card-header">
        <span>路灯信息</span>
        <el-button size="small" @click="handleBack">返回</el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="info-item"><span class="info-label">路灯ID：</span>{{ info.machineId }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">名称：</span>{{ info.machineName }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">设备编号：</span>{{ info.machineCode }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">厂家名称：</span>{{ info.factoryName }}</div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="info-row">
        <el-col :span="6">
          <div class="info-item"><span class="info-label">位置：</span>{{ info.locationName }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">状态：</span>{{ info.stateName }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">在线状态：</span>{{ info.isOnlineStateName }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">心跳时间：</span>{{ info.heartbeatTime }}</div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="info-row">
        <el-col :span="6">
          <div class="info-item"><span class="info-label">创建时间：</span>{{ info.createTime }}</div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="info-row" v-if="info.lampMachineParamDtoList && info.lampMachineParamDtoList.length > 0">
        <el-col :span="6" v-for="(item, index) in info.lampMachineParamDtoList" :key="index">
          <div class="info-item"><span class="info-label">{{ item.paramName }}：</span>{{ item.paramValue }}</div>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="never" class="tab-card">
      <el-tabs v-model="currentTab" @tab-click="handleTabClick">
        <el-tab-pane label="操作日志" name="operation">
          <div v-loading="operationLoading">
            <el-table :data="operationList" border stripe>
              <el-table-column prop="operationId" label="日志ID" min-width="150" />
              <el-table-column prop="machineName" label="路灯名称" min-width="120" />
              <el-table-column prop="machineCode" label="设备编号" min-width="120" />
              <el-table-column prop="operationTypeName" label="操作类型" min-width="100" />
              <el-table-column prop="stateName" label="状态" min-width="100" />
              <el-table-column prop="userName" label="操作人" min-width="100" />
              <el-table-column prop="createTime" label="创建时间" min-width="160" />
            </el-table>
            <el-pagination
              :current-page="operationPage.page"
              :page-size="operationPage.row"
              :total="operationTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleOperationSizeChange"
              @current-change="handleOperationPageChange"
              class="pagination-container"
            />
          </div>
        </el-tab-pane>
        <el-tab-pane label="下发日志" name="log">
          <div v-loading="logLoading">
            <el-table :data="logList" border stripe>
              <el-table-column prop="logId" label="日志ID" min-width="150" />
              <el-table-column prop="machineName" label="路灯名称" min-width="120" />
              <el-table-column prop="machineCode" label="设备编号" min-width="120" />
              <el-table-column prop="logAction" label="日志动作" min-width="100" />
              <el-table-column prop="stateName" label="状态" min-width="100" />
              <el-table-column prop="userName" label="操作人" min-width="100" />
              <el-table-column prop="createTime" label="创建时间" min-width="160" />
            </el-table>
            <el-pagination
              :current-page="logPage.page"
              :page-size="logPage.row"
              :total="logTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleLogSizeChange"
              @current-change="handleLogPageChange"
              class="pagination-container"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { listLampMachine, listLampMachineOperation, listLampMachineLog } from '@/api/lamp/lampApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'LampMachineDetail',
  data() {
    return {
      machineId: '',
      currentTab: 'operation',
      info: {},
      operationLoading: false,
      operationList: [],
      operationTotal: 0,
      operationPage: { page: 1, row: 10 },
      logLoading: false,
      logList: [],
      logTotal: 0,
      logPage: { page: 1, row: 10 }
    }
  },
  mounted() {
    this.machineId = this.$route.query.machineId || ''
    if (!this.machineId) {
      this.$router.go(-1)
      return
    }
    this.loadMachineInfo()
    this.loadOperationData()
  },
  methods: {
    async loadMachineInfo() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listLampMachine({ machineId: this.machineId, page: 1, row: 1, communityId })
        if (res.data && res.data.length > 0) {
          this.info = res.data[0]
        }
      } catch (e) {
        console.error('加载路灯信息失败', e)
      }
    },
    async loadOperationData() {
      this.operationLoading = true
      try {
        const res = await listLampMachineOperation({
          machineId: this.machineId,
          page: this.operationPage.page,
          row: this.operationPage.row
        })
        this.operationList = res.data || []
        this.operationTotal = res.total || 0
      } catch (e) {
        console.error('加载操作日志失败', e)
      } finally {
        this.operationLoading = false
      }
    },
    async loadLogData() {
      this.logLoading = true
      try {
        const res = await listLampMachineLog({
          machineId: this.machineId,
          page: this.logPage.page,
          row: this.logPage.row
        })
        this.logList = res.data || []
        this.logTotal = res.total || 0
      } catch (e) {
        console.error('加载下发日志失败', e)
      } finally {
        this.logLoading = false
      }
    },
    handleTabClick(tab) {
      if (tab.name === 'log' && this.logList.length === 0) {
        this.loadLogData()
      }
    },
    handleBack() {
      this.$router.go(-1)
    },
    handleOperationSizeChange(val) { this.operationPage.row = val; this.loadOperationData() },
    handleOperationPageChange(val) { this.operationPage.page = val; this.loadOperationData() },
    handleLogSizeChange(val) { this.logPage.row = val; this.loadLogData() },
    handleLogPageChange(val) { this.logPage.page = val; this.loadLogData() }
  }
}
</script>

<style scoped>
.info-card { margin-bottom: 16px; }
.tab-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.info-item { line-height: 32px; font-size: 14px; }
.info-label { color: #909399; }
.info-row { margin-top: 8px; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
