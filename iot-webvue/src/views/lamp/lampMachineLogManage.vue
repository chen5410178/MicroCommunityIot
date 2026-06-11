<template>
  <div class="lamp-machine-log-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-select v-model="queryParams.machineId" placeholder="请选择路灯设备" clearable style="width: 180px">
            <el-option
              v-for="item in lampMachineList"
              :key="item.machineId"
              :label="item.machineName"
              :value="item.machineId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.logAction" placeholder="请选择日志动作" clearable style="width: 180px">
            <el-option
              v-for="item in operateTypes"
              :key="item.statusCd"
              :label="item.name"
              :value="item.statusCd"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.state" placeholder="请选择请求状态" clearable style="width: 180px">
            <el-option
              v-for="item in reqStateList"
              :key="item.statusCd"
              :label="item.name"
              :value="item.statusCd"
            />
          </el-select>
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
          <el-select v-model="queryParams.personId" placeholder="请选择操作人员" clearable style="width: 180px">
            <el-option
              v-for="item in operatePersonList"
              :key="item.userId"
              :label="item.name"
              :value="item.userId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>下发设备日志</span>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="logId" label="编号" min-width="150" />
        <el-table-column prop="machineName" label="设备名称" min-width="150" />
        <el-table-column prop="logActionName" label="日志动作" min-width="100" />
        <el-table-column prop="personName" label="操作人" min-width="120" />
        <el-table-column prop="stateName" label="操作状态" min-width="100" />
        <el-table-column prop="createTime" label="操作时间" min-width="160" />
        <el-table-column label="操作" width="100" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleViewLog(row)">报文</el-button>
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
    <ShowLampMachineLog :visible.sync="logVisible" :log-data="currentLog" />
  </div>
</template>

<script>
import { listLampMachineLog, listLampMachine } from '@/api/lamp/lampApi'
import { listDicts } from '@/api/dev/devApi'
import { listCommunityStaffs } from '@/api/user/userApi'
import ShowLampMachineLog from '@/components/lamp/ShowLampMachineLog.vue'

export default {
  name: 'LampMachineLogManage',
  components: { ShowLampMachineLog },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      logVisible: false,
      currentLog: { reqParam: '', resParam: '' },
      lampMachineList: [],
      operateTypes: [],
      reqStateList: [],
      operatePersonList: [],
      queryParams: {
        page: 1,
        row: 10,
        communityId: '',
        machineId: '',
        logAction: '',
        state: '',
        personId: '',
        queryStartTime: '',
        queryEndTime: ''
      }
    }
  },
  computed: {
    currentCommunityId() {
      const community = this.$store.state.app.currentCommunity || {}
      return community.communityId || ''
    }
  },
  watch: {
    currentCommunityId(v) {
      this.queryParams.communityId = v
      if (v) {
        this.loadDictionaries()
        this.fetchList()
      }
    }
  },
  methods: {
    async loadDictionaries() {
      const communityId = this.currentCommunityId
      try {
        const [machineRes, typesRes, stateRes, staffRes] = await Promise.all([
          listLampMachine({ page: 1, row: 100, communityId }),
          listDicts({ tableName: 'lamp_machine_operation', columnName: 'type' }),
          listDicts({ tableName: 'lift_machine_log', columnName: 'state' }),
          listCommunityStaffs({ communityId })
        ])
        this.lampMachineList = machineRes.data || []
        this.operateTypes = typesRes.data || []
        this.reqStateList = stateRes.data || []
        this.operatePersonList = staffRes.data || []
      } catch { /* ignore */ }
    },
    async fetchList() {
      this.loading = true
      try {
        const res = await listLampMachineLog(this.queryParams)
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
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
        communityId: this.queryParams.communityId,
        machineId: '',
        logAction: '',
        state: '',
        personId: '',
        queryStartTime: '',
        queryEndTime: ''
      }
      this.fetchList()
    },
    handleViewLog(row) {
      this.currentLog = {
        reqParam: row.reqParam || '',
        resParam: row.resParam || ''
      }
      this.logVisible = true
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
