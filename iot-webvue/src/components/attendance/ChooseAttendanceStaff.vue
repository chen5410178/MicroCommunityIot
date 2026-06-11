<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="选择员工"
    width="700px"
    append-to-body
    @close="handleClose"
  >
    <el-form :inline="true" :model="searchForm">
      <el-form-item>
        <el-input v-model="searchForm.staffName" placeholder="请输入员工名称" clearable style="width: 200px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="staffList" v-loading="loading" border stripe max-height="400">
      <el-table-column prop="userId" label="员工编码" min-width="120" />
      <el-table-column prop="name" label="员工名称" min-width="120" />
      <el-table-column prop="tel" label="手机号" min-width="120" />
      <el-table-column label="性别" width="80" align="center">
        <template slot-scope="{ row }">
          {{ row.sex == 0 ? '男' : row.sex == 1 ? '女' : '' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template slot-scope="{ row }">
          <el-button size="small" type="text" @click="chooseStaff(row)">选择</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      :current-page="page"
      :page-size="row"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handlePageChange"
      class="pagination-container"
    />
  </el-dialog>
</template>

<script>
import { listStaffs } from '@/api/lamp/lampApi'

export default {
  name: 'ChooseAttendanceStaff',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      staffList: [],
      total: 0,
      page: 1,
      row: 10,
      searchForm: {
        staffName: ''
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.page = 1
        this.staffName = ''
        this.searchForm.staffName = ''
        this.fetchList()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listStaffs({
          page: this.page,
          row: this.row,
          staffName: this.searchForm.staffName
        })
        this.staffList = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.page = 1
      this.fetchList()
    },
    handleSizeChange(val) {
      this.row = val
      this.fetchList()
    },
    handlePageChange(val) {
      this.page = val
      this.fetchList()
    },
    chooseStaff(row) {
      this.$emit('selected', {
        staffId: row.userId,
        staffName: row.userName || row.name
      })
      this.dialogVisible = false
    },
    handleClose() {
      this.searchForm.staffName = ''
      this.staffList = []
    }
  }
}
</script>

<style scoped>
.pagination-container { margin-top: 16px; text-align: right; }
</style>
