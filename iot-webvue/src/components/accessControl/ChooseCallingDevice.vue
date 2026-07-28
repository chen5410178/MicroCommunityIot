<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="选择设备"
    width="700px"
    append-to-body
    @close="handleClose"
  >
    <div style="margin-bottom: 12px;">
      <el-input
        v-model="searchName"
        placeholder="输入设备名称"
        style="width: 200px; margin-right: 8px"
        clearable
        @keyup.enter.native="handleSearch"
      />
      <el-button type="primary" @click="handleSearch">查询</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="deviceId" label="设备ID" min-width="80" />
      <el-table-column prop="deviceName" label="设备名称" min-width="120" />
      <el-table-column prop="callingNum" label="呼叫号码" min-width="100" />
      <el-table-column prop="platformName" label="平台名称" min-width="100" />
      <el-table-column label="操作" width="80" align="center">
        <template slot-scope="{ row }">
          <el-button size="small" type="primary" @click="handleChoose(row)">选择</el-button>
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
      style="margin-top: 12px; text-align: right"
    />
  </el-dialog>
</template>

<script>
import { listCallingDevice } from '@/api/accessControl/accessControlApi'

export default {
  name: 'ChooseCallingDevice',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      list: [],
      total: 0,
      searchName: '',
      queryParams: {
        page: 1,
        row: 10
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.searchName = ''
        this.queryParams.page = 1
        this.fetchList()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listCallingDevice({
          ...this.queryParams,
          deviceName: this.searchName
        })
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
    handleChoose(row) {
      this.$emit('choose', row)
      this.dialogVisible = false
    },
    handleClose() {
      this.dialogVisible = false
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
