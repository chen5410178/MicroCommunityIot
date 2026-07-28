<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="选择监控设备"
    width="800px"
    @close="handleClose"
  >
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.machineName" placeholder="请输入设备名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.machineCode" placeholder="请输入设备编号" clearable style="width: 180px" />
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
      @selection-change="handleSelectionChange"
      style="margin-top: 12px"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="machineCode" label="设备编号" min-width="150" />
      <el-table-column prop="machineName" label="设备名称" min-width="150" />
      <el-table-column prop="locationName" label="位置" min-width="120" />
      <el-table-column prop="maName" label="区域" min-width="120" />
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
      <el-button type="primary" @click="handleConfirm" :disabled="selectedRows.length === 0">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { listMonitorMachines } from '@/api/monitor/monitorApi'

export default {
  name: 'ChooseMonitorMachine',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      list: [],
      total: 0,
      selectedRows: [],
      queryParams: {
        page: 1,
        row: 10,
        machineName: '',
        machineCode: ''
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
        const res = await listMonitorMachines(this.queryParams)
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
    handleSizeChange(val) {
      this.queryParams.row = val
      this.fetchList()
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.fetchList()
    },
    handleSelectionChange(val) {
      this.selectedRows = val
    },
    handleConfirm() {
      this.$emit('selected', this.selectedRows)
      this.handleClose()
    },
    handleClose() {
      this.dialogVisible = false
      this.selectedRows = []
    }
  }
}
</script>

<style scoped>
.search-card { margin-bottom: 0; }
.pagination-container { margin-top: 12px; text-align: right; }
</style>
