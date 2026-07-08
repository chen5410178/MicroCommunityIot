<template>
  <div class="monitor-manufactor-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.mmName" placeholder="请输入厂家名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>监控平台</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="mmName" label="厂家名称" min-width="150" />
        <el-table-column prop="mmUrl" label="系统地址" min-width="200" />
        <el-table-column prop="mmBeanName" label="厂家类型" width="150" />
        <el-table-column prop="appId" label="APP_ID" width="150" />
        <el-table-column prop="appSecure" label="秘钥" width="150" />
        <el-table-column label="备注" width="100" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="viewRemark(row.remark)">查看</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template slot-scope="{ row }">
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
    <AddMonitorManufactor :visible.sync="addVisible" @success="handleSuccess" />
    <EditMonitorManufactor :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteMonitorManufactor :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listMonitorManufactors } from '@/api/monitor/monitorApi'
import AddMonitorManufactor from '@/components/monitor/AddMonitorManufactor.vue'
import EditMonitorManufactor from '@/components/monitor/EditMonitorManufactor.vue'
import DeleteMonitorManufactor from '@/components/monitor/DeleteMonitorManufactor.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'MonitorManufactor',
  components: { AddMonitorManufactor, EditMonitorManufactor, DeleteMonitorManufactor },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        mmName: '',
        communityId: ''
      }
    }
  },
  mounted() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        this.queryParams.communityId = communityId
        const res = await listMonitorManufactors(this.queryParams)
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
      this.queryParams = { page: 1, row: 10, mmName: '', communityId: '' }
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
    viewRemark(remark) {
      this.$alert(remark || '暂无备注', '备注', { confirmButtonText: '确定' })
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
