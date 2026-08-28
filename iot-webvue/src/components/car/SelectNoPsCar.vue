<template>
  <el-dialog :title="title" :visible.sync="dialogVisible" width="800px" append-to-body @close="handleClose">
    <el-form :inline="true" style="margin-bottom: 12px">
      <el-form-item label="车牌号">
        <el-input
          v-model="carQuery.carNum"
          clearable
          placeholder="请输入车牌号"
          style="width: 220px"
          @keyup.enter.native="searchCars"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="searchCars">搜索</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="carLoading" :data="carList" border highlight-current-row @row-click="selectCar">
      <el-table-column prop="carNum" label="车牌号" align="center" />
      <el-table-column prop="ownerName" label="业主" align="center">
        <template slot-scope="{ row }">{{ row.ownerName || '-' }}</template>
      </el-table-column>
      <el-table-column prop="link" label="联系电话" align="center">
        <template slot-scope="{ row }">{{ row.link || '-' }}</template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="120">
        <template slot-scope="{ row }">
          <el-button type="text" @click.stop="selectCar(row)">选择</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      style="margin-top: 12px; text-align: right"
      :current-page="carPagination.page"
      :page-size="carPagination.row"
      :total="carPagination.total"
      layout="total, prev, pager, next"
      @current-change="handleCarPageChange"
    />
  </el-dialog>
</template>

<script>
import { queryCarsWithoutParkingSpace } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'SelectNoPsCar',
  props: {
    visible: { type: Boolean, default: false },
    title: { type: String, default: '选择车辆' }
  },
  data() {
    return {
      dialogVisible: false,
      carLoading: false,
      carQuery: { carNum: '' },
      carList: [],
      carPagination: { page: 1, row: 10, total: 0 }
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) {
      this.$emit('update:visible', val)
      if (val) {
        this.open()
      }
    }
  },
  methods: {
    open() {
      this.carQuery.carNum = ''
      this.carPagination.page = 1
      this.loadCars()
    },
    searchCars() {
      this.carPagination.page = 1
      this.loadCars()
    },
    loadCars() {
      this.carLoading = true
      const communityId = (getCurrentCommunity() || {}).communityId || ''
      const params = {
        page: this.carPagination.page,
        row: this.carPagination.row,
        communityId
      }
      if (this.carQuery.carNum) {
        params.carNumLike = this.carQuery.carNum
      }
      queryCarsWithoutParkingSpace(params).then(res => {
        this.carList = res.data || []
        this.carPagination.total = res.total || 0
      }).catch(error => {
        this.$message.error(error.message || '查询车辆列表失败')
      }).finally(() => {
        this.carLoading = false
      })
    },
    selectCar(row) {
      this.$emit('select', row)
      this.dialogVisible = false
    },
    handleCarPageChange(page) {
      this.carPagination.page = page
      this.loadCars()
    },
    handleClose() {
      this.carQuery.carNum = ''
      this.carPagination.page = 1
      this.carPagination.total = 0
    }
  }
}
</script>
