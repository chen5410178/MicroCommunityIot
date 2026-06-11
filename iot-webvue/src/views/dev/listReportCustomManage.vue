<template>
  <div class="list-report-custom-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="报表编号">
          <el-input v-model="queryParams.customId" placeholder="请输入报表编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="组编号">
          <el-input v-model="queryParams.groupId" placeholder="请输入组编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="选项标题">
          <el-input v-model="queryParams.title" placeholder="请输入选项标题" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>报表信息</span>
        <el-button type="primary" size="small" @click="handleAdd">添加报表</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="customId" label="报表编号" width="150" />
        <el-table-column prop="groupName" label="报表组" min-width="150" />
        <el-table-column prop="title" label="选项标题" min-width="150" />
        <el-table-column prop="seq" label="排序" width="80" />
        <el-table-column prop="remark" label="描述" min-width="150" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleRelComponent(row)">关联组件</el-button>
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
    <AddReportCustom :visible.sync="addVisible" @success="handleSuccess" />
    <EditReportCustom :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteReportCustom :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listReportCustoms } from '@/api/dev/devApi'
import AddReportCustom from '@/components/dev/AddReportCustom.vue'
import EditReportCustom from '@/components/dev/EditReportCustom.vue'
import DeleteReportCustom from '@/components/dev/DeleteReportCustom.vue'

export default {
  name: 'ListReportCustomManage',
  components: { AddReportCustom, EditReportCustom, DeleteReportCustom },
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
        customId: '',
        groupId: '',
        title: ''
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
        const res = await listReportCustoms(this.queryParams)
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
      this.queryParams = { page: 1, row: 10, customId: '', groupId: '', title: '' }
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
    handleRelComponent(row) {
      this.$router.push(`/pages/dev/listReportCustomComponentRelManage?customId=${row.customId}&title=${row.title}`)
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
