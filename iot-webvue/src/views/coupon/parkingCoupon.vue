<template>
  <div class="list-parking-coupon">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.name" placeholder="请输入名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.typeCd" placeholder="请选择类型" clearable style="width: 180px">
            <el-option label="时长减免" value="1001" />
            <el-option label="金额减免" value="2002" />
            <el-option label="折扣减免" value="3003" />
            <el-option label="全免" value="4004" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>停车券</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="couponId" label="编号" width="120" align="center" />
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column prop="typeCdName" label="类型" width="100" align="center" />
        <el-table-column prop="paName" label="停车场" min-width="120" />
        <el-table-column label="面值" width="120" align="center">
          <template slot-scope="{ row }">
            <span v-if="row.typeCd === '1001'">{{ row.value }}分钟</span>
            <span v-else-if="row.typeCd === '2002'">{{ row.value }}元</span>
            <span v-else-if="row.typeCd === '3003'">{{ row.value }}折</span>
            <span v-else-if="row.typeCd === '4004'">全免</span>
            <span v-else>{{ row.value }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="valuePrice" label="售价（元）" width="100" align="center" />
        <el-table-column label="操作" width="180" fixed="right" align="center">
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

    <AddParkingCoupon :visible.sync="addVisible" @success="handleSuccess" />
    <EditParkingCoupon :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteParkingCoupon :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listParkingCoupons } from '@/api/coupon/couponApi'
import AddParkingCoupon from '@/components/coupon/AddParkingCoupon.vue'
import EditParkingCoupon from '@/components/coupon/EditParkingCoupon.vue'
import DeleteParkingCoupon from '@/components/coupon/DeleteParkingCoupon.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListParkingCoupon',
  components: { AddParkingCoupon, EditParkingCoupon, DeleteParkingCoupon },
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
        name: '',
        typeCd: ''
      }
    }
  },
  computed: {
    communityId() {
      return (getCurrentCommunity() || {}).communityId || ''
    }
  },
  watch: {
    communityId: {
      immediate: true,
      handler(val) {
        if (val) {
          this.fetchList()
        }
      }
    }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listParkingCoupons({
          ...this.queryParams,
          communityId: this.communityId
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
    handleReset() {
      this.queryParams = { page: 1, row: 10, name: '', typeCd: '' }
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
