<template>
  <div class="lamp-machine-operation-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-select v-model="queryParams.machineId" placeholder="请选择路灯设备" clearable style="width: 180px">
            <el-option
              v-for="item in machineList"
              :key="item.machineId"
              :label="item.machineName"
              :value="item.machineId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.personId" placeholder="请选择操作人员" clearable style="width: 180px">
            <el-option
              v-for="item in personList"
              :key="item.userId"
              :label="item.name"
              :value="item.userId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.type" placeholder="请选择操作类型" clearable style="width: 180px">
            <el-option
              v-for="item in operateTypes"
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
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择开始时间"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="queryParams.queryEndTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择结束时间"
            clearable
            style="width: 180px"
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
        <span>操作日志</span>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="lmoId" label="操作日志ID" width="150" />
        <el-table-column prop="machineName" label="设备名称" min-width="150" />
        <el-table-column prop="personName" label="操作人员" min-width="120" />
        <el-table-column prop="typeName" label="操作类型" width="120" />
        <el-table-column prop="createTime" label="操作时间" width="180" />
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
  </div>
</template>

<script>
import { listLampMachineOperation, listLampMachine, listCommunityStaffs } from '@/api/lamp/lampApi'
import { listDicts } from '@/api/system/systemApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'LampMachineOperationManage',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      machineList: [],
      personList: [],
      operateTypes: [],
      queryParams: {
        page: 1,
        row: 10,
        machineId: '',
        personId: '',
        type: '',
        queryStartTime: '',
        queryEndTime: ''
      }
    }
  },
  mounted() {
    this.loadDictionaries()
    this.fetchList()
  },
  methods: {
    async loadDictionaries() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const [machineRes, personRes, dictRes] = await Promise.all([
          listLampMachine({ communityId, page: -1, row: 100 }),
          listCommunityStaffs({ communityId }),
          listDicts({ tableName: 'lamp_machine_operation', tableColumns: 'type' })
        ])
        this.machineList = machineRes.data || []
        this.personList = personRes.data || []
        this.operateTypes = dictRes || []
      } catch { /* ignore */ }
    },
    async fetchList() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listLampMachineOperation({ ...this.queryParams, communityId })
        this.list = res.data || []
        this.total = res.total || 0
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
        machineId: '',
        personId: '',
        type: '',
        queryStartTime: '',
        queryEndTime: ''
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
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
