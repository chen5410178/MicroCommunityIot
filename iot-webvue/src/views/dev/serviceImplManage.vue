<template>
  <div class="list-service-impl-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.businessTypeCd" placeholder="请输入业务类型" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.name" placeholder="请输入业务名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.invokeType" placeholder="请选择调用方式" clearable style="width: 180px">
            <el-option label="微服务POST方式" value="1" />
            <el-option label="webservice方式" value="2" />
            <el-option label="http post方式" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item v-show="showMore">
          <el-form-item>
            <el-input v-model="queryParams.messageTopic" placeholder="请输入kafka主题" clearable style="width: 180px" />
          </el-form-item>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="text" @click="showMore = !showMore">{{ showMore ? '隐藏' : '更多' }}条件</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>服务实现列表</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="serviceBusinessId" label="服务实现ID" width="120" />
        <el-table-column prop="businessTypeCd" label="业务类型" min-width="150" />
        <el-table-column prop="name" label="业务名称" min-width="150" />
        <el-table-column prop="invokeType" label="调用方式" width="150">
          <template slot-scope="{ row }">{{ invokeTypeMap[row.invokeType] || row.invokeType }}</template>
        </el-table-column>
        <el-table-column prop="messageTopic" label="kafka主题" min-width="150" />
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
    <AddServiceImpl :visible.sync="addVisible" @success="handleSuccess" />
    <EditServiceImpl :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteServiceImpl :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listServiceImpls } from '@/api/dev/devApi'
import AddServiceImpl from '@/components/dev/AddServiceImpl.vue'
import EditServiceImpl from '@/components/dev/EditServiceImpl.vue'
import DeleteServiceImpl from '@/components/dev/DeleteServiceImpl.vue'

export default {
  name: 'ListServiceImplManage',
  components: { AddServiceImpl, EditServiceImpl, DeleteServiceImpl },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      showMore: false,
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        businessTypeCd: '',
        name: '',
        invokeType: '',
        messageTopic: ''
      },
      invokeTypeMap: {
        '1': '微服务POST方式',
        '2': 'webservice方式',
        '3': 'http post方式'
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
        const res = await listServiceImpls(this.queryParams)
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
        businessTypeCd: '',
        name: '',
        invokeType: '',
        messageTopic: ''
      }
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
