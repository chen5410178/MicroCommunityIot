<template>
  <div class="list-lift-ride-log">
    <el-row :gutter="16">
      <el-col :span="3">
        <el-card shadow="never" class="left-machine-card">
          <div class="machine-list">
            <div
              v-for="item in machines"
              :key="item.machineId"
              class="machine-item"
              :class="{ 'machine-active': selectedMachineId === item.machineId }"
              @click="handleSelectMachine(item)"
            >
              {{ item.machineName }}
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="21">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.acMachineName" placeholder="请输入门禁名称" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.acMachineCode" placeholder="请输入门禁编号" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-select v-model="queryParams.rideType" placeholder="请选择乘坐类型" clearable style="width: 160px">
                <el-option label="全部" value="" />
                <el-option label="人脸" value="1001" />
                <el-option label="刷卡" value="2002" />
                <el-option label="二维码" value="3003" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.name" placeholder="请输入用户名称" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-select v-model="queryParams.personType" placeholder="请选择人员类型" clearable style="width: 160px">
                <el-option label="全部" value="" />
                <el-option label="访客" value="100" />
                <el-option label="员工" value="200" />
                <el-option label="业主" value="300" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>乘坐记录</span>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="facePath" label="人脸" min-width="100" />
            <el-table-column prop="logId" label="编号" min-width="100" />
            <el-table-column prop="machineName" label="电梯名称" min-width="120" />
            <el-table-column prop="acName" label="门禁名称" min-width="120" />
            <el-table-column prop="acCode" label="门禁编号" min-width="120" />
            <el-table-column prop="rideType" label="乘坐类型" min-width="100">
              <template slot-scope="scope">
                {{ rideTypeMap[scope.row.rideType] || scope.row.rideTypeName || scope.row.rideType }}
              </template>
            </el-table-column>
            <el-table-column prop="userName" label="用户名称" min-width="100" />
            <el-table-column prop="userTel" label="手机号" min-width="120" />
            <el-table-column prop="personType" label="人员类型" min-width="100">
              <template slot-scope="scope">
                {{ personTypeMap[scope.row.personType] || scope.row.personWayName || scope.row.personType }}
              </template>
            </el-table-column>
            <el-table-column prop="roomName" label="房号" min-width="120" />
            <el-table-column prop="curLayer" label="楼层" min-width="80" align="center" />
            <el-table-column prop="createTime" label="乘坐时间" min-width="160" />
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
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { listLiftMachines, listLiftRideLogs } from '@/api/lift/liftApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'LiftRideLog',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      machines: [],
      selectedMachineId: '',
      rideTypeMap: { '1001': '人脸', '2002': '刷卡', '3003': '二维码' },
      personTypeMap: { '100': '访客', '200': '员工', '300': '业主' },
      queryParams: {
        page: 1,
        row: 10,
        communityId: '',
        machineId: '',
        acMachineName: '',
        acMachineCode: '',
        rideType: '',
        name: '',
        personType: ''
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
        this.queryParams.communityId = val
        if (val) {
          this.fetchMachines()
          this.fetchList()
        }
      }
    }
  },
  methods: {
    async fetchMachines() {
      try {
        const res = await listLiftMachines({ page: 1, row: 100, communityId: this.queryParams.communityId })
        const data = res.data || []
        this.machines = [{ machineId: '', machineName: '全部' }, ...data]
      } catch (e) {
        this.machines = [{ machineId: '', machineName: '全部' }]
      }
    },
    async fetchList() {
      this.loading = true
      try {
        const res = await listLiftRideLogs(this.queryParams)
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    handleSelectMachine(item) {
      this.selectedMachineId = item.machineId
      this.queryParams.machineId = item.machineId
      this.queryParams.page = 1
      this.fetchList()
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = {
        page: 1,
        row: 10,
        communityId: this.queryParams.communityId,
        machineId: this.queryParams.machineId,
        acMachineName: '',
        acMachineCode: '',
        rideType: '',
        name: '',
        personType: ''
      }
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
.left-machine-card { margin-bottom: 0; }
.left-machine-card .machine-list { padding: 0; }
.left-machine-card .machine-item {
  padding: 12px 16px;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 4px;
  transition: all 0.2s;
  font-size: 14px;
  color: #303133;
}
.left-machine-card .machine-item:hover { background-color: #f5f7fa; }
.left-machine-card .machine-item.machine-active { background-color: #409eff; color: white; font-weight: 600; }
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
