<template>
  <div class="monitor-machine-model-manage">
    <el-card shadow="never" class="header-card">
      <div slot="header" class="card-header">
        <span>监控分析</span>
        <el-button size="small" @click="$router.push('/pages/monitor/monitorModelManage')">返回</el-button>
      </div>
    </el-card>
    <el-row :gutter="16" class="content-row">
      <el-col :span="4">
        <el-card shadow="never" class="left-card">
          <div slot="header" class="card-header">
            <span>监控模型</span>
          </div>
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
      </el-col>
      <el-col :span="20">
        <el-card shadow="never" class="search-card">
          <el-form :inline="true" :model="queryParams">
            <el-form-item label="监控名称">
              <el-input v-model="queryParams.machineName" placeholder="请输入监控名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item label="监控位置">
              <el-input v-model="queryParams.locationName" placeholder="请输入监控位置" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item label="监控区域">
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
            <span>设备列表</span>
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
      </el-col>
    </el-row>
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
        this.total = res.records || 0
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
.header-card { margin-bottom: 16px; }
.content-row { margin-bottom: 0; }
.left-card { min-height: 300px; }
.model-list { list-style: none; padding: 0; margin: 0; }
.model-list li {
  padding: 10px 12px;
  cursor: pointer;
  border-bottom: 1px solid #ebeef5;
  font-size: 14px;
  color: #606266;
  transition: all 0.2s;
}
.model-list li:hover { background-color: #f5f7fa; }
.model-list li.active { background-color: #ecf5ff; color: #409eff; font-weight: 600; border-left: 3px solid #409eff; }
.model-list li.empty-tip { cursor: default; color: #c0c4cc; text-align: center; padding: 20px; }
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
