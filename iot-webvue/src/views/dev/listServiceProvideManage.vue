<template>
  <div class="list-service-provide-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="服务编码">
          <el-input v-model="queryParams.serviceCode" placeholder="请输入服务编码" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="服务名称">
          <el-input v-model="queryParams.name" placeholder="请输入服务名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="实现方式">
          <el-select v-model="queryParams.queryModel" placeholder="请选择实现方式" clearable style="width: 180px">
            <el-option label="全部" value="" />
            <el-option label="sql方式" value="1" />
            <el-option label="存储过程方式" value="2" />
            <el-option label="java方式" value="3" />
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
        <span>服务提供信息</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="id" label="提供ID" width="150" />
        <el-table-column prop="name" label="服务名称" min-width="150" />
        <el-table-column prop="serviceCode" label="服务编码" min-width="150" />
        <el-table-column prop="params" label="参数" min-width="150" />
        <el-table-column label="实现方式" width="130" align="center">
          <template slot-scope="{ row }">{{ queryModelMap[row.queryModel] || row.queryModel }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right" align="center">
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
    <AddServiceProvide :visible.sync="addVisible" @success="handleSuccess" />
    <EditServiceProvide :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteServiceProvide :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listServiceProvides } from '@/api/dev/devApi'
import AddServiceProvide from '@/components/dev/AddServiceProvide.vue'
import EditServiceProvide from '@/components/dev/EditServiceProvide.vue'
import DeleteServiceProvide from '@/components/dev/DeleteServiceProvide.vue'

export default {
  name: 'ListServiceProvideManage',
  components: { AddServiceProvide, EditServiceProvide, DeleteServiceProvide },
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
        serviceCode: '',
        name: '',
        queryModel: ''
      },
      queryModelMap: {
        '1': 'sql方式',
        '2': '存储过程方式',
        '3': 'java方式'
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
        const res = await listServiceProvides(this.queryParams)
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
      this.queryParams = { page: 1, row: 10, serviceCode: '', name: '', queryModel: '' }
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
