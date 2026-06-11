<template>
  <div class="list-service-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="服务名称">
          <el-input v-model="queryParams.serviceName" placeholder="请输入服务名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="服务编码">
          <el-input v-model="queryParams.serviceCode" placeholder="请输入服务编码" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="调用地址">
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
        <span>服务列表</span>
        <el-button type="primary" size="small" @click="handleAdd">添加服务</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="serviceId" label="服务ID" width="120" />
        <el-table-column prop="name" label="服务名称" min-width="150" />
        <el-table-column prop="serviceCode" label="服务编码" min-width="120" />
        <el-table-column prop="url" label="调用地址" min-width="200" />
        <el-table-column prop="method" label="调用方式" width="100" />
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleEdit(row)">编辑</el-button>
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
    <AddService :visible.sync="addVisible" @success="handleSuccess" />
    <EditService :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteService :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listServices } from '@/api/dev/devApi'
import AddService from '@/components/dev/AddService.vue'
import EditService from '@/components/dev/EditService.vue'
import DeleteService from '@/components/dev/DeleteService.vue'

export default {
  name: 'ListServiceManage',
  components: { AddService, EditService, DeleteService },
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
        serviceName: '',
        serviceCode: '',
        serviceUrl: ''
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
        const res = await listServices(this.queryParams)
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
      this.queryParams = { page: 1, row: 10, serviceName: '', serviceCode: '', serviceUrl: '' }
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
