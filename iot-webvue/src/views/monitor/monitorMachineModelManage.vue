<template>
  <div class="monitor-machine-model-manage">
    <div class="page-container">
      <div class="left-panel">
        <el-card shadow="never" class="left-card">
          <ul class="model-list">
            <li
              v-for="item in monitorModelList"
              :key="item.modelId"
              :class="{ active: queryParams.modelId === item.modelId }"
              @click="handleModelClick(item)"
            >
              <span>{{ item.modelName }}</span>
            </li>
            <li v-if="monitorModelList.length === 0" class="empty-tip">暂无模型数据</li>
          </ul>
        </el-card>
      </div>
      <div class="right-panel">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.machineName" placeholder="请输入监控名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.locationName" placeholder="请输入监控位置" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-select v-model="queryParams.maId" placeholder="请选择区域" clearable style="width: 180px">
                <el-option
                  v-for="item in monitorAreaList"
                  :key="item.maId"
                  :label="item.maName"
                  :value="item.maId"
                />
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
            <span>监控分析</span>
            <el-button type="primary" size="small" @click="$router.push('/pages/monitor/addMonitorMachineModel')">添加</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="modelName" label="监控模型" min-width="150" />
            <el-table-column prop="machineName" label="监控名称" min-width="150" />
            <el-table-column prop="machineCode" label="监控编号" min-width="150" />
            <el-table-column prop="locationName" label="监控位置" min-width="150" />
            <el-table-column prop="maName" label="监控区域" min-width="150" />
            <el-table-column label="操作" width="100" fixed="right" align="center">
              <template slot-scope="{ row }">
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
      </div>
    </div>
    <DeleteMonitorMachineModel :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listMonitorModels, listMonitorMachineModels, listMonitorAreas } from '@/api/monitor/monitorApi'
import DeleteMonitorMachineModel from '@/components/monitor/DeleteMonitorMachineModel.vue'

export default {
  name: 'MonitorMachineModelManage',
  components: { DeleteMonitorMachineModel },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      delVisible: false,
      currentRow: null,
      monitorModelList: [],
      monitorAreaList: [],
      queryParams: {
        page: 1,
        row: 10,
        modelId: '',
        machineName: '',
        locationName: '',
        maId: ''
      }
    }
  },
  mounted() {
    if (this.$route.query.modelId) {
      this.queryParams.modelId = this.$route.query.modelId
    }
    this.fetchMonitorModels()
    this.fetchMonitorAreas()
    this.fetchList()
  },
  methods: {
    async fetchMonitorModels() {
      try {
        const res = await listMonitorModels({ page: 1, row: 100 })
        this.monitorModelList = res.data || []
      } catch { /* ignore */ }
    },
    async fetchMonitorAreas() {
      try {
        const res = await listMonitorAreas({ page: 1, row: 100 })
        this.monitorAreaList = res.data || []
      } catch { /* ignore */ }
    },
    async fetchList() {
      this.loading = true
      try {
        const res = await listMonitorMachineModels(this.queryParams)
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleModelClick(item) {
      if (this.queryParams.modelId === item.modelId) {
        this.queryParams.modelId = ''
      } else {
        this.queryParams.modelId = item.modelId
      }
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
        modelId: '',
        machineName: '',
        locationName: '',
        maId: ''
      }
      this.fetchList()
    },
    handleDelete(row) {
      this.currentRow = { ...row }
      this.delVisible = true
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
.page-container { display: flex; gap: 16px; }
.left-panel { width: 200px; flex-shrink: 0; }
.right-panel { flex: 1; min-width: 0; }
.left-card { height: 100%; }
.model-list { list-style: none; padding: 0; margin: 0; max-height: calc(100vh - 200px); overflow-y: auto; }
.model-list li {
  padding: 10px 12px;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 5px;
  font-size: 13px;
  color: #303133;
  transition: all 0.3s;
}
.model-list li:hover { background-color: #f5f7fa; }
.model-list li.active { background-color: #409eff; color: white; font-weight: 600; }
.model-list li.empty-tip { cursor: default; color: #c0c4cc; text-align: center; padding: 20px; }
.search-card { margin-bottom: 16px; }
.search-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.table-card { margin-bottom: 0; }
.table-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
