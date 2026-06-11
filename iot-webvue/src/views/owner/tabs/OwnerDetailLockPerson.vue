<template>
  <div class="tab-list">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="lpId" label="授权编号" min-width="120" />
      <el-table-column prop="machineName" label="门锁名称" min-width="120" />
      <el-table-column prop="machineCode" label="门锁编码" min-width="120" />
      <el-table-column prop="name" label="业主名称" min-width="100" />
      <el-table-column label="开锁方式" width="100">
        <template slot-scope="{ row }">
          <span v-if="row.openModel === '1001'">密码开锁</span>
          <span v-else-if="row.openModel === '2002'">蓝牙开锁</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="授权状态" width="120">
        <template slot-scope="{ row }">
          <span v-if="row.state === '1001'">可用</span>
          <span v-else-if="row.state === '2002'">冻结</span>
          <span v-else>{{ row.stateName || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="startTime" label="开始时间" width="160" />
      <el-table-column prop="endTime" label="结束时间" width="160" />
      <el-table-column prop="createTime" label="创建时间" width="160" />
    </el-table>
    <el-pagination :current-page="page" :page-size="row" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handlePageChange" class="pagination-container" />
  </div>
</template>

<script>
import { listLockPersons } from '@/api/lock/lockApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'OwnerDetailLockPerson',
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
        const res = await listLockPersons({
          personId: this.ownerId,
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
