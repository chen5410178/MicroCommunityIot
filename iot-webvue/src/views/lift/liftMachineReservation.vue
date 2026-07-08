<template>
  <div class="list-lift-machine-reservation">
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
              <el-select v-model="queryParams.resWay" placeholder="请选择预约方式" clearable style="width: 160px">
                <el-option label="全部" value="" />
                <el-option label="室内机" value="1001" />
                <el-option label="业主手机" value="2002" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-select v-model="queryParams.personWay" placeholder="请选择人员类型" clearable style="width: 160px">
                <el-option label="全部" value="" />
                <el-option label="访客" value="1001" />
                <el-option label="业主" value="2002" />
                <el-option label="员工" value="3003" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.personName" placeholder="请输入人员名称" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.roomName" placeholder="请输入预约房号" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-date-picker
                v-model="queryParams.queryStartTime"
                type="datetime"
                placeholder="选择开始时间"
                value-format="yyyy-MM-dd HH:mm:ss"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item>
              <el-date-picker
                v-model="queryParams.queryEndTime"
                type="datetime"
                placeholder="选择结束时间"
                value-format="yyyy-MM-dd HH:mm:ss"
                clearable
                style="width: 200px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>预约记录</span>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="machineName" label="设备名称" min-width="120" />
            <el-table-column prop="resWayName" label="预约方式" min-width="100" />
            <el-table-column prop="roomName" label="预约房号" min-width="120" />
            <el-table-column prop="personName" label="人员" min-width="100" />
            <el-table-column prop="personWayName" label="人员类型" min-width="100" />
            <el-table-column prop="orgLayer" label="原始楼层" min-width="80" />
            <el-table-column prop="targetLayer" label="目标楼层" min-width="80" />
            <el-table-column prop="createTime" label="创建时间" min-width="150" />
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
import { listLiftMachines, listLiftMachineReservations } from '@/api/lift/liftApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'LiftMachineReservation',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      machines: [],
      selectedMachineId: '',
      queryParams: {
        page: 1,
        row: 10,
        communityId: '',
        machineId: '',
        resWay: '',
        personWay: '',
        personName: '',
        roomName: '',
        queryStartTime: '',
        queryEndTime: ''
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
        const res = await listLiftMachineReservations(this.queryParams)
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
        machineId: '',
        resWay: '',
        personWay: '',
        personName: '',
        roomName: '',
        queryStartTime: '',
        queryEndTime: ''
      }
      this.selectedMachineId = ''
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
