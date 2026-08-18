<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="选择商铺"
    width="800px"
    append-to-body
    @close="handleClose"
  >
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.shopNum" placeholder="商铺编号" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.ownerName" placeholder="业主姓名" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.state" placeholder="状态" clearable style="width: 120px">
            <el-option label="已出租" value="2006" />
            <el-option label="已出售" value="2007" />
            <el-option label="空置" value="2008" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-table :data="shopList" v-loading="loading" border stripe max-height="350">
      <el-table-column prop="shopNum" label="商铺编号" min-width="110" />
      <el-table-column prop="locationName" label="位置" min-width="120" />
      <el-table-column prop="ownerName" label="业主" min-width="100" />
      <el-table-column label="状态" width="90" align="center">
        <template slot-scope="{ row }">
          {{ _getStateName(row.state) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="70" align="center">
        <template slot-scope="{ row }">
          <el-button size="small" type="text" @click="handleSelect(row)">选择</el-button>
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
import { listBuildingShops } from '@/api/shop/shopApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ShopSelect',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      shopList: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10,
        shopNum: '',
        ownerName: '',
        state: ''
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
    _getStateName(state) {
      if (state == '2006') return '已出租'
      if (state == '2007') return '已出售'
      if (state == '2008') return '空置'
      return '-'
    },
    async fetchList() {
      this.loading = true
      try {
        const community = getCurrentCommunity()
        const params = { ...this.queryParams, communityId: community.communityId }
        const res = await listBuildingShops(params)
        this.shopList = res.data || []
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
      this.queryParams = { page: 1, row: 10, shopNum: '', ownerName: '', state: '' }
      this.fetchList()
    },
    handleSelect(row) {
      this.$emit('confirm', {
        roomId: row.shopId,
        roomName: row.shopNum
      })
      this.dialogVisible = false
    },
    handleSizeChange(val) {
      this.queryParams.row = val
      this.fetchList()
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.fetchList()
    },
    handleClose() {
      this.dialogVisible = false
    }
  }
}
</script>

<style scoped>
.search-card { margin-bottom: 16px; }
.search-card >>> .el-card__body { padding: 12px 16px 0; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
