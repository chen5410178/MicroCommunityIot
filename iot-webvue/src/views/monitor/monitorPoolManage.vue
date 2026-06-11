<template>
  <div class="monitor-pool-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-select v-model="queryParams.machineId" placeholder="请选择监控设备" clearable filterable style="width: 180px">
            <el-option v-for="item in machineList" :key="item.machineId" :label="item.machineName" :value="item.machineId" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.modelId" placeholder="请选择监控模型" clearable style="width: 180px">
            <el-option v-for="item in modelList" :key="item.modelId" :label="item.modelName" :value="item.modelId" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.monitorType" placeholder="请选择监控类型" clearable style="width: 180px">
            <el-option v-for="item in monitorTypeList" :key="item.statusCd" :label="item.name" :value="item.statusCd" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-date-picker v-model="queryParams.queryStartTime" type="datetime" placeholder="请选择开始时间" value-format="yyyy-MM-dd HH:mm:ss" style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-date-picker v-model="queryParams.queryEndTime" type="datetime" placeholder="请选择结束时间" value-format="yyyy-MM-dd HH:mm:ss" style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>监控告警</span>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="mpId" label="编号" width="120" />
        <el-table-column prop="machineName" label="监控名称" min-width="150" />
        <el-table-column prop="machineCode" label="监控编码" min-width="120" />
        <el-table-column prop="modelName" label="监控模型" min-width="120" />
        <el-table-column prop="monitorTypeName" label="监控类型" width="100" />
        <el-table-column label="监控图片" width="100" align="center">
          <template slot-scope="{ row }">
            <el-image :src="row.monitorPhoto" style="width:50px;height:50px;border-radius:10px" fit="cover">
              <div slot="error" class="image-slot"><i class="el-icon-picture-outline"></i></div>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="描述" width="100" align="center">
          <template slot-scope="{ row }">
            <el-button type="text" @click="viewRemark(row.remark)">查看</el-button>
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
  </div>
</template>

<script>
import { listMonitorPools, listMonitorMachines, listMonitorModels } from '@/api/monitor/monitorApi'
import { listDicts } from '@/api/dev/devApi'

export default {
  name: 'MonitorPoolManage',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      machineList: [],
      modelList: [],
      monitorTypeList: [],
      queryParams: {
        page: 1,
        row: 10,
        machineId: '',
        modelId: '',
        monitorType: '',
        queryStartTime: '',
        queryEndTime: '',
        communityId: ''
      }
    }
  },
  mounted() {
    this.fetchMachines()
    this.fetchModels()
    this.fetchMonitorTypes()
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        this.queryParams.communityId = communityId
        const res = await listMonitorPools(this.queryParams)
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    async fetchMachines() {
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listMonitorMachines({ page: 1, row: 100, communityId })
        this.machineList = res.data || []
      } catch (e) {
        console.error('获取监控设备下拉失败', e)
      }
    },
    async fetchModels() {
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listMonitorModels({ page: 1, row: 100, communityId })
        this.modelList = res.data || []
      } catch (e) {
        console.error('获取监控模型下拉失败', e)
      }
    },
    async fetchMonitorTypes() {
      try {
        const res = await listDicts({ tableName: 'monitor_pool', columnName: 'monitor_type' })
        this.monitorTypeList = res.data || []
      } catch (e) {
        console.error('获取监控类型字典失败', e)
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
        modelId: '',
        monitorType: '',
        queryStartTime: '',
        queryEndTime: '',
        communityId: ''
      }
      this.fetchList()
    },
    viewRemark(remark) {
      this.$alert(remark || '暂无备注', '备注', { confirmButtonText: '确定' })
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
.image-slot { display: flex; justify-content: center; align-items: center; background: #f5f7fa; width: 50px; height: 50px; border-radius: 10px; font-size: 20px; color: #c0c4cc; }
</style>
