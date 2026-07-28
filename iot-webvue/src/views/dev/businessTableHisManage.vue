<template>
  <div class="list-business-table-his-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-select v-model="queryParams.action" placeholder="请选择动作" clearable style="width: 180px">
            <el-option label="ADD" value="ADD" />
            <el-option label="MOD" value="MOD" />
            <el-option label="DEL" value="DEL" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.actionObj" placeholder="请输入表名" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.businessTypeCd" placeholder="请输入业务类型" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>业务轨迹</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="hisId" label="轨迹ID" width="150" />
        <el-table-column prop="businessTypeCd" label="业务类型" min-width="120" />
        <el-table-column label="动作" width="100" align="center">
          <template slot-scope="{ row }">{{ actionMap[row.action] || row.action }}</template>
        </el-table-column>
        <el-table-column prop="actionObj" label="表名" min-width="150" />
        <el-table-column prop="actionObjHis" label="轨迹表名" min-width="150" />
        <el-table-column prop="remark" label="备注" min-width="120" />
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
    <AddBusinessTableHis :visible.sync="addVisible" @success="handleSuccess" />
    <EditBusinessTableHis :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteBusinessTableHis :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listBusinessTableHis } from '@/api/dev/devApi'
import AddBusinessTableHis from '@/components/dev/AddBusinessTableHis.vue'
import EditBusinessTableHis from '@/components/dev/EditBusinessTableHis.vue'
import DeleteBusinessTableHis from '@/components/dev/DeleteBusinessTableHis.vue'

export default {
  name: 'ListBusinessTableHisManage',
  components: { AddBusinessTableHis, EditBusinessTableHis, DeleteBusinessTableHis },
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
        action: '',
        actionObj: '',
        businessTypeCd: ''
      },
      actionMap: {
        'ADD': '添加',
        'MOD': '修改',
        'DEL': '删除'
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
        const res = await listBusinessTableHis(this.queryParams)
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
      this.queryParams = { page: 1, row: 10, action: '', actionObj: '', businessTypeCd: '' }
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
