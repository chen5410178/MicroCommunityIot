<template>
  <div class="list-asset-import-log-detail">
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>导入日志详情</span>
        <div>
          <el-button type="primary" size="small" @click="fetchList">刷新</el-button>
          <el-button size="small" @click="$router.back()">返回</el-button>
        </div>
      </div>
      <el-row :gutter="16">
        <el-col :span="4">
          <div class="state-filter">
            <div
              v-for="item in stateOptions"
              :key="item.value"
              class="state-item"
              :class="{ active: currentState === item.value }"
              @click="handleStateChange(item.value)"
            >
              {{ item.name }}
            </div>
          </div>
        </el-col>
        <el-col :span="20">
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column
              v-for="col in dynamicColumns"
              :key="col.logProperty"
              :prop="col.logProperty"
              :label="col.logColumn"
              min-width="120"
              show-overflow-tooltip
            />
            <el-table-column prop="createTime" label="导入时间" min-width="160" />
            <el-table-column prop="state" label="状态" min-width="100">
              <template slot-scope="{ row }">
                {{ stateMap[row.state] || row.state }}
              </template>
            </el-table-column>
            <el-table-column prop="message" label="描述" min-width="200" show-overflow-tooltip />
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
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script>
import { listAssetImportLogDetails, listAssetImportLogTypes } from '@/api/log/logApi'

export default {
  name: 'AssetImportLogDetail',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      logId: '',
      logType: '',
      currentState: '',
      dynamicColumns: [],
      queryParams: {
        page: 1,
        row: 10
      },
      stateOptions: [
        { name: '全部', value: '' },
        { name: '待导入', value: 'W' },
        { name: '导入成功', value: 'C' },
        { name: '导入失败', value: 'F' }
      ],
      stateMap: {
        'W': '待导入',
        'C': '成功',
        'F': '失败'
      }
    }
  },
  mounted() {
    this.logId = this.$route.query.logId || ''
    this.logType = this.$route.query.logType || ''
    this.fetchColumns()
  },
  methods: {
    async fetchColumns() {
      try {
        const res = await listAssetImportLogTypes({ logType: this.logType })
        const logTypes = res.data || []
        this.dynamicColumns = logTypes.map(item => ({
          logColumn: item.logColumn,
          logProperty: item.logProperty
        }))
      } catch (e) {
        this.dynamicColumns = []
      }
      this.fetchList()
    },
    async fetchList() {
      this.loading = true
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId
        const params = {
          ...this.queryParams,
          logId: this.logId,
          communityId
        }
        if (this.currentState) {
          params.state = this.currentState
        }
        const res = await listAssetImportLogDetails(params)
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleStateChange(state) {
      this.currentState = state
      this.queryParams.page = 1
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
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
.state-filter { border: 1px solid #e4e7ed; border-radius: 4px; overflow: hidden; }
.state-item { padding: 12px 16px; cursor: pointer; border-bottom: 1px solid #e4e7ed; font-size: 14px; color: #606266; }
.state-item:last-child { border-bottom: none; }
.state-item:hover { background-color: #f5f7fa; }
.state-item.active { color: #409eff; background-color: #ecf5ff; font-weight: bold; }
</style>
