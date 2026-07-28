<template>
  <div class="list-service-register-manage">
    <div class="page-container">
      <div class="left-panel">
        <el-card shadow="never" class="app-card">
          <div class="app-search">
            <el-input v-model="appSearchName" placeholder="请输入应用名称" clearable size="small" @keyup.enter.native="handleAppSearch" />
          </div>
          <div class="app-list">
            <div
              v-for="item in appList"
              :key="item.appId"
              class="app-item"
              :class="{ active: selectedApp && selectedApp.appId === item.appId }"
              @click="handleAppSelect(item)"
            >
              {{ item.name }}
            </div>
          </div>
        </el-card>
      </div>
      <div class="right-panel">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.serviceName" placeholder="请输入服务名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.serviceCode" placeholder="请输入服务编码" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.serviceUrl" placeholder="请输入调用地址" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>服务注册列表</span>
            <el-button type="primary" size="small" :disabled="!selectedApp" @click="handleServiceBinding">服务绑定</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="id" label="绑定ID" width="100" />
            <el-table-column prop="appId" label="应用ID" width="120" />
            <el-table-column prop="appName" label="应用名称" min-width="150" />
            <el-table-column prop="serviceId" label="服务ID" width="120" />
            <el-table-column prop="serviceName" label="服务名称" min-width="150" />
            <el-table-column prop="serviceCode" label="服务编码" min-width="120" />
            <el-table-column prop="orderTypeCd" label="订单类型" width="100" />
            <el-table-column prop="serviceUrl" label="调用地址" min-width="200" />
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
      </div>
    </div>
    <EditServiceRegister :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteServiceRegister :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listServiceRegisters, listApps } from '@/api/dev/devApi'
import EditServiceRegister from '@/components/dev/EditServiceRegister.vue'
import DeleteServiceRegister from '@/components/dev/DeleteServiceRegister.vue'

export default {
  name: 'ListServiceRegisterManage',
  components: { EditServiceRegister, DeleteServiceRegister },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      appList: [],
      appSearchName: '',
      selectedApp: null,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        appId: '',
        serviceName: '',
        serviceCode: '',
        serviceUrl: ''
      }
    }
  },
  mounted() {
    const appId = this.$route.query.appId
    if (appId) {
      this.queryParams.appId = appId
      this.fetchAppAndSelect(appId)
    }
    this.fetchAppList()
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listServiceRegisters(this.queryParams)
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    async fetchAppList() {
      try {
        const res = await listApps({ page: 1, row: 100, name: this.appSearchName })
        this.appList = res.data || []
      } catch { /* ignore */ }
    },
    async fetchAppAndSelect(appId) {
      try {
        const res = await listApps({ page: 1, row: 1, appId })
        if (res.data && res.data.length > 0) {
          this.selectedApp = res.data[0]
        }
      } catch { /* ignore */ }
    },
    handleAppSearch() {
      this.fetchAppList()
    },
    handleAppSelect(item) {
      this.selectedApp = item
      this.queryParams.appId = item.appId
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
        appId: this.selectedApp ? this.selectedApp.appId : '',
        serviceName: '',
        serviceCode: '',
        serviceUrl: ''
      }
      this.fetchList()
    },
    handleServiceBinding() {
      if (!this.selectedApp) return
      this.$router.push({
        path: '/pages/dev/addServiceBind',
        query: {
          appId: this.selectedApp.appId,
          appName: this.selectedApp.name
        }
      })
    },
    handleEdit(row) {
      this.currentRow = { ...row }
      this.editVisible = true
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
.app-search { margin-bottom: 8px; }
.app-list { }
.app-item { padding: 10px 12px; cursor: pointer; border-radius: 4px; margin-bottom: 5px; text-align: center; font-size: 13px; transition: all 0.3s; }
.app-item:hover { background-color: #f5f7fa; }
.app-item.active { background-color: #409eff; color: white; font-weight: 600; }
.search-card { margin-bottom: 16px; }
.search-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.table-card { margin-bottom: 0; }
.table-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
