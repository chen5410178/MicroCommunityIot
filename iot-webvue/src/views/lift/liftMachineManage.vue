<template>
  <div class="lift-machine-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.machineName" placeholder="请输入名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.machineCode" placeholder="请输入设备编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.factoryId" placeholder="请选择厂家" clearable style="width: 180px">
            <el-option
              v-for="item in factoryList"
              :key="item.factoryId"
              :label="item.factoryName"
              :value="item.factoryId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.state" placeholder="请选择状态" clearable style="width: 180px">
            <el-option
              v-for="item in stateList"
              :key="item.statusCd"
              :label="item.name"
              :value="item.statusCd"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.machineId" placeholder="请输入设备ID" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>电梯信息</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="machineId" label="设备ID" min-width="150" />
        <el-table-column prop="machineName" label="名称" min-width="150" />
        <el-table-column prop="machineCode" label="设备编号" min-width="120" />
        <el-table-column prop="brand" label="品牌" width="100" />
        <el-table-column prop="locationName" label="位置" min-width="150" />
        <el-table-column prop="registrationUnit" label="登记单位" min-width="120" />
        <el-table-column prop="useUnit" label="使用单位" min-width="120" />
        <el-table-column prop="maintenanceUnit" label="维保单位" min-width="120" />
        <el-table-column prop="rescueLink" label="救援电话" width="120" />
        <el-table-column prop="factoryName" label="厂家" min-width="120" />
        <el-table-column label="电梯状态" width="160" align="center">
          <template slot-scope="{ row }">
            <span>{{ row.stateName }}</span>
            <span v-if="row.state === '1001'">
              <el-button type="text" size="small" @click="handleSwitchState(row)">停止</el-button>
            </span>
            <span v-if="row.state === '2002'">
              <el-button type="text" size="small" @click="handleSwitchState(row)">启用</el-button>
            </span>
          </template>
        </el-table-column>
        <el-table-column label="当前楼层" width="80" align="center">
          <template slot-scope="{ row }">{{ row.curLayer }}(层)</template>
        </el-table-column>
        <el-table-column prop="isOnlineStateName" label="在线状态" width="100" align="center" />
        <el-table-column prop="heartbeatTime" label="心跳时间" min-width="160" />
        <el-table-column label="监控视频" width="120" align="center">
          <template slot-scope="{ row }">
            <span>{{ row.monitorName || '-' }}</span>
            <el-button v-if="row.monitorName" type="text" size="small" @click="handleViewVideo(row)">观看</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleDetail(row)">详情</el-button>
            <el-button size="small" type="text" @click="handleReservation(row)">预约</el-button>
            <el-button size="small" type="text" @click="handleEdit(row)">修改</el-button>
            <el-button size="small" type="text" style="color: #f56c6c" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        :current-page="queryParams.page"
        :page-size="queryParams.row"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        class="pagination-container"
      />
    </el-card>
    <AddLiftMachine :visible.sync="addVisible" @success="handleSuccess" />
    <EditLiftMachine :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteLiftMachine :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
    <ReservationLiftMachine :visible.sync="reservationVisible" :machine-data="currentRow" @success="handleSuccess" />
    <PlayMonitorVideo :visible.sync="videoVisible" :video-info="videoInfo" />
  </div>
</template>

<script>
import { listLiftMachines, listLiftMachineFactorys, switchLiftState } from '@/api/lift/liftApi'
import { listDicts } from '@/api/dev/devApi'
import { getCurrentCommunity } from '@/utils/auth'
import AddLiftMachine from '@/components/lift/AddLiftMachine.vue'
import EditLiftMachine from '@/components/lift/EditLiftMachine.vue'
import DeleteLiftMachine from '@/components/lift/DeleteLiftMachine.vue'
import ReservationLiftMachine from '@/components/lift/ReservationLiftMachine.vue'
import PlayMonitorVideo from '@/components/monitor/PlayMonitorVideo.vue'

export default {
  name: 'LiftMachineManage',
  components: { AddLiftMachine, EditLiftMachine, DeleteLiftMachine, ReservationLiftMachine, PlayMonitorVideo },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      factoryList: [],
      stateList: [],
      addVisible: false,
      editVisible: false,
      delVisible: false,
      reservationVisible: false,
      videoVisible: false,
      currentRow: null,
      videoInfo: {},
      queryParams: {
        page: 1,
        row: 10,
        machineName: '',
        machineCode: '',
        factoryId: '',
        state: '',
        machineId: ''
      }
    }
  },
  mounted() {
    this.fetchList()
    this.loadFactorys()
    this.loadStateDict()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const community = getCurrentCommunity()
        const communityId = community ? community.communityId : ''
        const params = { ...this.queryParams, communityId }
        const res = await listLiftMachines(params)
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    async loadFactorys() {
      try {
        const res = await listLiftMachineFactorys({ page: -1, row: 100 })
        this.factoryList = res.data || []
      } catch (e) {
        console.error('获取厂家列表失败', e)
      }
    },
    async loadStateDict() {
      try {
        const res = await listDicts({ tableName: 'lift_machine', columnName: 'state' })
        this.stateList = res.data || []
      } catch (e) {
        console.error('获取状态字典失败', e)
      }
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = {
        page: 1,
        row: 10,
        machineName: '',
        machineCode: '',
        factoryId: '',
        state: '',
        machineId: ''
      }
      this.fetchList()
    },
    handleAdd() {
      this.addVisible = true
    },
    handleEdit(row) {
      this.currentRow = { ...row }
      this.editVisible = true
    },
    handleDelete(row) {
      this.currentRow = { ...row }
      this.delVisible = true
    },
    handleReservation(row) {
      this.currentRow = { ...row }
      this.reservationVisible = true
    },
    handleDetail(row) {
      this.$router.push(`/pages/lift/liftMachineDetail?machineId=${row.machineId}`)
    },
    handleViewVideo(row) {
      this.videoInfo = {
        machineId: row.monitorId,
        machineName: row.monitorName,
        communityId: row.communityId
      }
      this.videoVisible = true
    },
    async handleSwitchState(row) {
      const userInfo = JSON.parse(sessionStorage.getItem('iot_webvue_user') || '{}')
      const data = {
        machineId: row.machineId,
        state: row.state,
        userId: userInfo.userId || '',
        userName: userInfo.name || ''
      }
      if (row.state === '1001') {
        data.logAction = 'stopping'
      } else if (row.state === '2002') {
        data.logAction = 'running'
      } else {
        data.logAction = 'unknown'
      }
      try {
        await switchLiftState(data)
        this.$message.success('操作成功')
        this.fetchList()
      } catch (e) {
        this.$message.error(e.msg || '操作失败')
      }
    },
    handleSuccess() {
      this.fetchList()
    },
    handleSizeChange(val) {
      this.queryParams.row = val
      this.fetchList()
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.fetchList()
    }
  }
}
</script>

<style scoped>
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
