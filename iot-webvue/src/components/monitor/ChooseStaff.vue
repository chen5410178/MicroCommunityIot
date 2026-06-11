<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="选择员工"
    width="800px"
    @close="handleClose"
  >
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="员工名称">
          <el-input v-model="queryParams.staffName" placeholder="请输入员工名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-table
      :data="list"
      v-loading="loading"
      border
      stripe
      highlight-current-row
      style="margin-top: 12px"
      @row-click="handleRowClick"
    >
      <el-table-column prop="userId" label="员工编码" min-width="150" />
      <el-table-column prop="userName" label="员工名称" min-width="150" />
      <el-table-column prop="cellphone" label="手机号" min-width="130" />
      <el-table-column prop="sex" label="性别" width="80">
        <template slot-scope="{ row }">
          {{ row.sex === '1' ? '男' : row.sex === '0' ? '女' : '' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="80" align="center">
        <template slot-scope="{ row }">
          <el-button
            size="mini"
            type="primary"
            @click="handleSelect(row)"
          >选中</el-button>
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
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { listStaffs } from '@/api/lamp/lampApi'

export default {
  name: 'ChooseStaff',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10,
        staffName: ''
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.fetchList()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listStaffs(this.queryParams)
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
    handleSizeChange(val) {
      this.queryParams.row = val
      this.fetchList()
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.fetchList()
    },
    handleRowClick(row) {
      this.$emit('selected', row)
      this.handleClose()
    },
    handleSelect(row) {
      this.$emit('selected', row)
      this.handleClose()
    },
    handleClose() {
      this.dialogVisible = false
    }
  }
}
</script>

<style scoped>
.search-card { margin-bottom: 0; }
.pagination-container { margin-top: 12px; text-align: right; }
</style>
