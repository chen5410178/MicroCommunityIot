<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="选择停车位"
    width="900px"
    @close="handleClose"
  >
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="停车场">
          <el-select v-model="queryParams.paId" placeholder="请选择停车场" clearable style="width: 180px">
            <el-option v-for="item in parkingAreas" :key="item.paId" :label="item.num" :value="item.paId" />
          </el-select>
        </el-form-item>
        <el-form-item label="车位编号">
          <el-input v-model="queryParams.num" placeholder="请输入停车位编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="paNum" label="停车场编码" min-width="120" />
      <el-table-column prop="num" label="停车位编码" min-width="120" />
      <el-table-column label="停车场类型" min-width="100">
        <template slot-scope="{ row }">
          {{ row.typeCd === '1001' ? '地上停车位' : '地下停车位' }}
        </template>
      </el-table-column>
      <el-table-column prop="parkingTypeName" label="车位类型" min-width="100" />
      <el-table-column prop="stateName" label="车位状态" min-width="100" />
      <el-table-column prop="area" label="面积" min-width="100" />
      <el-table-column label="操作" width="80" fixed="right" align="center">
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
      class="pagination-container"
    />
  </el-dialog>
</template>

<script>
import { listParkingSpaces } from '@/api/car/carApi'
import { listParkingAreas } from '@/api/car/carApi'

export default {
  name: 'SearchParkingSpace',
  props: {
    visible: { type: Boolean, default: false },
    parkingSpaceFlag: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      list: [],
      total: 0,
      parkingAreas: [],
      queryParams: {
        page: 1,
        row: 10,
        num: '',
        paId: ''
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.queryParams.num = ''
        this.fetchList()
        this.fetchParkingAreas()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listParkingSpaces({
          ...this.queryParams,
          communityId,
          state: this.parkingSpaceFlag
        })
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    async fetchParkingAreas() {
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listParkingAreas({ page: 1, row: 100, communityId })
        this.parkingAreas = res.data || []
      } catch { /* ignore */ }
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
    handleChoose(row) {
      this.$emit('choose', row)
      this.dialogVisible = false
    },
    handleClose() {
      this.dialogVisible = false
    }
  }
}
</script>

<style scoped>
.search-card { margin-bottom: 16px; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
