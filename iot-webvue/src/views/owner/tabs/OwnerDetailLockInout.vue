<template>
  <div class="tab-list">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="inoutId" label="进出编号" min-width="120" />
      <el-table-column prop="machineName" label="门锁名称" min-width="120" />
      <el-table-column prop="machineCode" label="门锁编码" min-width="120" />
      <el-table-column prop="userName" label="业主名称" min-width="100" />
      <el-table-column prop="tel" label="业主手机号" min-width="120" />
      <el-table-column label="开门方式" width="100">
        <template slot-scope="{ row }">
          <span v-if="row.openTypeCd === '1001'">密码开锁</span>
          <span v-else-if="row.openTypeCd === '2002'">蓝牙开锁</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="开门状态" width="90">
        <template slot-scope="{ row }">
          <span v-if="row.state === 'C'">开门成功</span>
          <span v-else-if="row.state === 'F'">开门失败</span>
          <span v-else>{{ row.state || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" />
    </el-table>
    <el-pagination :current-page="page" :page-size="row" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handlePageChange" class="pagination-container" />
  </div>
</template>

<script>
import { listLockInouts } from '@/api/lock/lockApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'OwnerDetailLockInout',
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
        const res = await listLockInouts({
          personId: this.ownerId,
          communityId: community ? community.communityId : '',
          page: this.page,
          row: this.row
        })
        this.list = res.data || []
        this.total = res.total || 0
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
