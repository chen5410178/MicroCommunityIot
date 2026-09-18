<template>
  <div class="lock-detail-inout">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="inoutId" label="进出编号" min-width="140" />
      <el-table-column prop="machineName" label="门锁名称" min-width="150" />
      <el-table-column prop="machineCode" label="门锁编码" min-width="150" />
      <el-table-column prop="userName" label="操作人名称" min-width="120" />
      <el-table-column prop="tel" label="操作人手机号" min-width="130" />
      <el-table-column label="开门方式" min-width="100" align="center">
        <template slot-scope="{ row }">
          <span v-if="row.openTypeCd === '1001'">密码开锁</span>
          <span v-else-if="row.openTypeCd === '2002'">蓝牙开锁</span>
          <span v-else-if="row.openTypeCd === '3003'">远程开锁</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="开门状态" width="100" align="center">
        <template slot-scope="{ row }">
          <el-tag v-if="row.state === 'C'" type="success" size="small">开门成功</el-tag>
          <el-tag v-else-if="row.state === 'F'" type="danger" size="small">开门失败</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
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
import { listLockInouts } from '@/api/lock/lockApi'
import { getCommunityId } from '@/utils/auth'

export default {
  name: 'LockDetailInout',
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
        const res = await listLockInouts({
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
        this.$message.error(e.message || '获取开门记录失败')
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
