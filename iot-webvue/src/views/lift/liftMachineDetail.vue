<template>
  <div class="lift-machine-detail">
    <el-card shadow="never" class="info-card">
      <div slot="header" class="card-header">
        <span>电梯信息</span>
        <el-button size="small" @click="handleBack">返回</el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="info-item"><span class="info-label">电梯ID：</span>{{ info.machineId }}</div>
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
          <div class="info-item"><span class="info-label">在线状态：</span>{{ info.isOnlineStateName }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">电梯状态：</span>{{ info.stateName }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">心跳时间：</span>{{ info.heartbeatTime }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">添加时间：</span>{{ info.createTime }}</div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="info-row">
        <el-col :span="6">
          <div class="info-item"><span class="info-label">品牌：</span>{{ info.brand }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">位置：</span>{{ info.locationName }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">登记单位：</span>{{ info.registrationUnit }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">使用单位：</span>{{ info.useUnit }}</div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="info-row">
        <el-col :span="6">
          <div class="info-item"><span class="info-label">当前楼层：</span>{{ info.curLayer }}(层)</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">维保单位：</span>{{ info.maintenanceUnit }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">救援电话：</span>{{ info.rescueLink }}</div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="info-row" v-if="info.liftMachineParamDtoList && info.liftMachineParamDtoList.length > 0">
        <el-col :span="6" v-for="(item, index) in info.liftMachineParamDtoList" :key="index">
          <div class="info-item"><span class="info-label">{{ item.paramName }}：</span>{{ item.paramValue }}</div>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="never" class="tab-card">
      <el-tabs v-model="currentTab" @tab-click="handleTabClick">
        <el-tab-pane label="电梯门禁" name="liftMachineAc">
          <div v-loading="acLoading">
            <div style="margin-bottom: 12px; text-align: right">
              <el-button type="primary" size="small" @click="handleAddAc">添加门禁</el-button>
            </div>
            <el-table :data="acList" border stripe>
              <el-table-column prop="machineName" label="电梯名称" min-width="120" />
              <el-table-column prop="machineCode" label="电梯编号" min-width="120" />
              <el-table-column prop="acName" label="门禁名称" min-width="120" />
              <el-table-column prop="acCode" label="门禁编号" min-width="120" />
              <el-table-column prop="acTypeName" label="门禁类型" min-width="100" />
              <el-table-column prop="createTime" label="创建时间" min-width="160" />
              <el-table-column label="操作" width="100" fixed="right" align="center">
                <template slot-scope="{ row }">
                  <el-button size="small" type="text" style="color: #f56c6c" @click="handleDeleteAc(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-pagination
              :current-page="acPage.page"
              :page-size="acPage.row"
              :total="acTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleAcSizeChange"
              @current-change="handleAcPageChange"
              class="pagination-container"
            />
          </div>
        </el-tab-pane>
        <el-tab-pane label="预约记录" name="liftMachineReservation">
          <div v-loading="reservationLoading">
            <el-table :data="reservationList" border stripe>
              <el-table-column prop="machineName" label="设备名称" min-width="120" />
              <el-table-column prop="resWayName" label="预约方式" min-width="100" />
              <el-table-column prop="roomName" label="预约房号" min-width="120" />
              <el-table-column prop="personName" label="人员" min-width="100" />
              <el-table-column prop="personWayName" label="人员类型" min-width="100" />
              <el-table-column prop="orgLayer" label="原始楼层" min-width="100" align="center" />
              <el-table-column prop="targetLayer" label="目标楼层" min-width="100" align="center" />
              <el-table-column prop="createTime" label="创建时间" min-width="160" />
            </el-table>
            <el-pagination
              :current-page="reservationPage.page"
              :page-size="reservationPage.row"
              :total="reservationTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleReservationSizeChange"
              @current-change="handleReservationPageChange"
              class="pagination-container"
            />
          </div>
        </el-tab-pane>
        <el-tab-pane label="电梯日志" name="liftMachineLog">
          <div v-loading="logLoading">
            <el-table :data="logList" border stripe>
              <el-table-column prop="logId" label="日志ID" min-width="150" />
              <el-table-column prop="machineName" label="电梯名称" min-width="120" />
              <el-table-column prop="machineCode" label="电梯编号" min-width="120" />
              <el-table-column prop="logAction" label="日志动作" min-width="100" />
              <el-table-column prop="stateName" label="状态" min-width="100" />
              <el-table-column prop="userName" label="操作人" min-width="100" />
              <el-table-column prop="createTime" label="创建时间" min-width="160" />
              <el-table-column label="报文查看" width="100" align="center">
                <template slot-scope="{ row }">
                  <el-button v-if="row.logMessage" size="small" type="text" @click="handleViewLog(row)">查看</el-button>
                </template>
              </el-table-column>
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
        <el-tab-pane label="乘坐记录" name="liftRideLog">
          <div v-loading="rideLoading">
            <el-table :data="rideList" border stripe>
              <el-table-column prop="facePath" label="人脸" min-width="120" />
              <el-table-column prop="machineName" label="电梯名称" min-width="120" />
              <el-table-column prop="acName" label="门禁名称" min-width="120" />
              <el-table-column prop="acCode" label="门禁编号" min-width="120" />
              <el-table-column prop="rideTypeName" label="乘坐类型" min-width="100" />
              <el-table-column prop="userName" label="用户名称" min-width="100" />
              <el-table-column prop="userTel" label="手机号" min-width="120" />
              <el-table-column prop="personWayName" label="人员类型" min-width="100" />
              <el-table-column prop="roomName" label="房号" min-width="120" />
              <el-table-column prop="curLayer" label="楼层" min-width="80" align="center" />
              <el-table-column prop="createTime" label="乘坐时间" min-width="160" />
            </el-table>
            <el-pagination
              :current-page="ridePage.page"
              :page-size="ridePage.row"
              :total="rideTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleRideSizeChange"
              @current-change="handleRidePageChange"
              class="pagination-container"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <AddLiftMachineAc :visible.sync="acAddVisible" :machine-data="info" @success="loadAcData" />
    <DeleteLiftMachineAc :visible.sync="acDelVisible" :del-data="acDelRow" @success="loadAcData" />
  </div>
</template>

<script>
import {
  listLiftMachines,
  listLiftMachineAcs,
  listLiftMachineReservations,
  listLiftMachineLogs,
  listLiftRideLogs
} from '@/api/lift/liftApi'
import { getCurrentCommunity } from '@/utils/auth'
import AddLiftMachineAc from '@/components/lift/AddLiftMachineAc.vue'
import DeleteLiftMachineAc from '@/components/lift/DeleteLiftMachineAc.vue'

export default {
  name: 'LiftMachineDetail',
  components: { AddLiftMachineAc, DeleteLiftMachineAc },
  data() {
    return {
      machineId: '',
      currentTab: 'liftMachineAc',
      info: {},
      acAddVisible: false,
      acDelVisible: false,
      acDelRow: null,
      acLoading: false,
      acList: [],
      acTotal: 0,
      acPage: { page: 1, row: 10 },
      reservationLoading: false,
      reservationList: [],
      reservationTotal: 0,
      reservationPage: { page: 1, row: 10 },
      logLoading: false,
      logList: [],
      logTotal: 0,
      logPage: { page: 1, row: 10 },
      rideLoading: false,
      rideList: [],
      rideTotal: 0,
      ridePage: { page: 1, row: 10 }
    }
  },
  mounted() {
    this.machineId = this.$route.query.machineId || ''
    if (!this.machineId) {
      this.$router.go(-1)
      return
    }
    this.loadMachineInfo()
    this.loadAcData()
  },
  methods: {
    async loadMachineInfo() {
      try {
        const community = getCurrentCommunity()
        const communityId = community ? community.communityId : ''
        const res = await listLiftMachines({
          machineId: this.machineId,
          page: 1,
          row: 1,
          communityId
        })
        if (res.data && res.data.length > 0) {
          this.info = res.data[0]
        }
      } catch (e) {
        console.error('加载电梯信息失败', e)
      }
    },
    async loadAcData() {
      this.acLoading = true
      try {
        const res = await listLiftMachineAcs({
          machineId: this.machineId,
          page: this.acPage.page,
          row: this.acPage.row
        })
        this.acList = res.data || []
        this.acTotal = res.records || 0
      } catch (e) {
        console.error('加载门禁列表失败', e)
      } finally {
        this.acLoading = false
      }
    },
    async loadReservationData() {
      this.reservationLoading = true
      try {
        const res = await listLiftMachineReservations({
          machineId: this.machineId,
          page: this.reservationPage.page,
          row: this.reservationPage.row
        })
        this.reservationList = res.data || []
        this.reservationTotal = res.records || 0
      } catch (e) {
        console.error('加载预约记录失败', e)
      } finally {
        this.reservationLoading = false
      }
    },
    async loadLogData() {
      this.logLoading = true
      try {
        const res = await listLiftMachineLogs({
          machineId: this.machineId,
          page: this.logPage.page,
          row: this.logPage.row
        })
        this.logList = res.data || []
        this.logTotal = res.records || 0
      } catch (e) {
        console.error('加载日志失败', e)
      } finally {
        this.logLoading = false
      }
    },
    async loadRideData() {
      this.rideLoading = true
      try {
        const res = await listLiftRideLogs({
          machineId: this.machineId,
          page: this.ridePage.page,
          row: this.ridePage.row
        })
        this.rideList = res.data || []
        this.rideTotal = res.records || 0
      } catch (e) {
        console.error('加载乘坐记录失败', e)
      } finally {
        this.rideLoading = false
      }
    },
    handleTabClick(tab) {
      if (tab.name === 'liftMachineReservation' && this.reservationList.length === 0) {
        this.loadReservationData()
      } else if (tab.name === 'liftMachineLog' && this.logList.length === 0) {
        this.loadLogData()
      } else if (tab.name === 'liftRideLog' && this.rideList.length === 0) {
        this.loadRideData()
      }
    },
    async handleDeleteAc(row) {
      this.acDelRow = row
      this.acDelVisible = true
    },
    handleAddAc() {
      this.acAddVisible = true
    },
    handleViewLog(row) {
      this.$alert(row.logMessage, '报文详情', { confirmButtonText: '确定' })
    },
    handleAcSizeChange(val) { this.acPage.row = val; this.loadAcData() },
    handleAcPageChange(val) { this.acPage.page = val; this.loadAcData() },
    handleReservationSizeChange(val) { this.reservationPage.row = val; this.loadReservationData() },
    handleReservationPageChange(val) { this.reservationPage.page = val; this.loadReservationData() },
    handleLogSizeChange(val) { this.logPage.row = val; this.loadLogData() },
    handleLogPageChange(val) { this.logPage.page = val; this.loadLogData() },
    handleRideSizeChange(val) { this.ridePage.row = val; this.loadRideData() },
    handleRidePageChange(val) { this.ridePage.page = val; this.loadRideData() },
    handleBack() {
      this.$router.go(-1)
    }
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
