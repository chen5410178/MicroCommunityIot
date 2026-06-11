<template>
  <div class="list-business-databus-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.databusId" placeholder="请输入ID" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.businessTypeCd" placeholder="请输入业务类型" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.beanName" placeholder="请输入适配器" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>Databus 列表</span>
        <el-button type="primary" size="small" @click="handleAdd">添加 Databus</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="databusId" label="ID" width="120" />
        <el-table-column prop="databusName" label="名称" min-width="150" />
        <el-table-column prop="businessTypeCd" label="业务类型" min-width="120" />
        <el-table-column prop="beanName" label="适配器" min-width="200" />
        <el-table-column prop="seq" label="顺序" width="80" align="center" />
        <el-table-column label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            {{ stateMap[row.state] || row.state }}
          </template>
        </el-table-column>
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
    <AddBusinessDatabus :visible.sync="addVisible" @success="handleSuccess" />
    <EditBusinessDatabus :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteBusinessDatabus :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { queryBusinessDatabus } from '@/api/dev/devApi'
import AddBusinessDatabus from '@/components/dev/AddBusinessDatabus.vue'
import EditBusinessDatabus from '@/components/dev/EditBusinessDatabus.vue'
import DeleteBusinessDatabus from '@/components/dev/DeleteBusinessDatabus.vue'

export default {
  name: 'ListBusinessDatabusManage',
  components: { AddBusinessDatabus, EditBusinessDatabus, DeleteBusinessDatabus },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      stateMap: { '1001': '在用', '2002': '停用' },
      queryParams: {
        page: 1,
        row: 10,
        databusId: '',
        businessTypeCd: '',
        beanName: ''
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
        const res = await queryBusinessDatabus(this.queryParams)
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
      this.queryParams = { page: 1, row: 10, databusId: '', businessTypeCd: '', beanName: '' }
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
