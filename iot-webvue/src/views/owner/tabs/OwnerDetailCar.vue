<template>
  <div class="tab-list">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="carNum" label="车牌号" min-width="120" />
      <el-table-column prop="memberCarCount" label="成员车辆" width="90" align="center">
        <template slot-scope="{ row }">{{ row.memberCarCount || 0 }}</template>
      </el-table-column>
      <el-table-column label="车牌类型" min-width="100">
        <template slot-scope="{ row }">
          <span v-if="row.leaseType === 'T'">临时车</span>
          <span v-else>{{ row.leaseTypeName || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="车辆类型" min-width="100">
        <template slot-scope="{ row }">{{ row.carTypeName || '-' }}</template>
      </el-table-column>
      <el-table-column label="颜色" width="80">
        <template slot-scope="{ row }">{{ row.carColor || '-' }}</template>
      </el-table-column>
      <el-table-column label="业主" min-width="120">
        <template slot-scope="{ row }">{{ row.ownerName }}({{ row.link }})</template>
      </el-table-column>
      <el-table-column label="车位" min-width="100">
        <template slot-scope="{ row }">
          <span v-if="row.areaNum && row.state == '1001'">{{ row.areaNum }}-{{ row.num }}</span>
          <span v-else>车位已释放</span>
        </template>
      </el-table-column>
      <el-table-column label="有效期" min-width="160">
        <template slot-scope="{ row }">
          <span v-if="row.leaseType === 'H'">{{ row.startTime }}<br />~{{ row.endTime }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="stateName" label="状态" width="90" />
    </el-table>
    <el-pagination :current-page="page" :page-size="row" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handlePageChange" class="pagination-container" />
  </div>
</template>

<script>
import { queryOwnerCars } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'OwnerDetailCar',
  props: {
    ownerId: { type: String, default: '' },
    memberId: { type: String, default: '' },
    ownerName: { type: String, default: '' },
    link: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      page: 1,
      row: 10
    }
  },
  mounted() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const community = getCurrentCommunity()
        const res = await queryOwnerCars({
          ownerId: this.ownerId,
          communityId: community ? community.communityId : '',
          page: this.page,
          row: this.row
        })
        this.list = res.data || []
        this.total = res.records || 0
      } finally { this.loading = false }
    },
    handleSizeChange(val) { this.row = val; this.page = 1; this.fetchList() },
    handlePageChange(val) { this.page = val; this.fetchList() }
  }
}
</script>

<style scoped>
.pagination-container { margin-top: 16px; text-align: right; }
</style>
