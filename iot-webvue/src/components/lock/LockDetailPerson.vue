<template>
  <div class="lock-detail-person">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="lpId" label="授权编号" min-width="140" />
      <el-table-column prop="machineName" label="门锁名称" min-width="150" />
      <el-table-column prop="machineCode" label="门锁编码" min-width="150" />
      <el-table-column prop="name" label="业主名称" min-width="120" />
      <el-table-column label="开锁方式" width="100" align="center">
        <template slot-scope="{ row }">
          <span v-if="row.openModel === '1001'">密码开锁</span>
          <span v-else-if="row.openModel === '2002'">蓝牙开锁</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="授权状态" width="100" align="center">
        <template slot-scope="{ row }">
          <span v-if="row.state === '1001'">可用</span>
          <span v-else-if="row.state === '2002'">冻结</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="startTime" label="授权开始时间" min-width="160" />
      <el-table-column prop="endTime" label="授权结束时间" min-width="160" />
      <el-table-column prop="createTime" label="创建时间" min-width="160" />
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
  </div>
</template>

<script>
import { listLockPersons } from '@/api/lock/lockApi'
import { getCommunityId } from '@/utils/auth'

export default {
  name: 'LockDetailPerson',
  props: {
    machineId: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: { page: 1, row: 10 }
    }
  },
  watch: {
    machineId: {
      immediate: true,
      handler() {
        if (!this.machineId) {
          this.list = []
          this.total = 0
          return
        }
        this.queryParams.page = 1
        this.fetchList()
      }
    }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listLockPersons({
          machineId: this.machineId,
          communityId: getCommunityId(),
          page: this.queryParams.page,
          row: this.queryParams.row
        })
        this.list = res.data || []
        this.total = res.total || 0
      } catch (e) {
        this.list = []
        this.total = 0
        this.$message.error(e.message || '获取门锁授权失败')
      } finally {
        this.loading = false
      }
    },
    handleSizeChange(val) {
      this.queryParams.row = val
      this.queryParams.page = 1
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
