<template>
  <el-dialog
    :visible.sync="innerVisible"
    title="选择路灯设备"
    width="700px"
    @close="handleClose"
  >
    <el-form :inline="true" :model="queryParams">
      <el-form-item>
        <el-input v-model="queryParams.machineName" placeholder="请输入名称" clearable style="width: 160px" />
      </el-form-item>
      <el-form-item>
        <el-input v-model="queryParams.machineCode" placeholder="请输入设备编号" clearable style="width: 160px" />
      </el-form-item>
      <el-form-item>
        <el-input v-model="queryParams.locationName" placeholder="请输入位置" clearable style="width: 160px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
      </el-form-item>
    </el-form>
    <el-table
      ref="tableRef"
      :data="list"
      v-loading="loading"
      border
      stripe
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50" />
      <el-table-column prop="machineName" label="名称" min-width="120" />
      <el-table-column prop="machineCode" label="设备编号" min-width="120" />
      <el-table-column prop="locationName" label="位置" min-width="120" />
      <el-table-column prop="factoryName" label="厂家" min-width="100" />
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
      <el-button type="primary" @click="handleConfirm" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { listLampMachine } from '@/api/lamp/lampApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ChooseLampMachine',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      innerVisible: false,
      loading: false,
      list: [],
      total: 0,
      selectedRows: [],
      queryParams: {
        page: 1,
        row: 10,
        machineName: '',
        machineCode: '',
        locationName: ''
      }
    }
  },
  watch: {
    visible(val) {
      this.innerVisible = val
      if (val) {
        this.fetchList()
      }
    },
    innerVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listLampMachine({ ...this.queryParams, communityId })
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
    handleSelectionChange(val) {
      this.selectedRows = val
    },
    handleConfirm() {
      this.$emit('confirm', this.selectedRows)
      this.innerVisible = false
    },
    handleClose() {
      this.innerVisible = false
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
.pagination-container { margin-top: 16px; text-align: right; }
</style>
