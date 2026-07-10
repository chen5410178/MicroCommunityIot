<template>
  <div class="room-detail-access-control-inout">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column label="人脸" width="80" align="center">
        <template slot-scope="{ row }">
          <img v-if="row.facePath" :src="row.facePath" style="width: 60px; height: 60px; border-radius: 4px; cursor: pointer" @click="viewFace(row.facePath)" />
          <span v-else style="color: #999">无</span>
        </template>
      </el-table-column>
      <el-table-column prop="inoutId" label="编号" />
      <el-table-column prop="machineName" label="设备名称" />
      <el-table-column prop="machineCode" label="设备编码" />
      <el-table-column prop="name" label="用户名称" />
      <el-table-column label="开门方式" align="center">
        <template slot-scope="{ row }">{{ row.openTypeCd === '1000' ? '人脸' : '其他' }}</template>
      </el-table-column>
      <el-table-column prop="tel" label="手机号" />
      <el-table-column prop="idCard" label="身份证" />
      <el-table-column prop="similar" label="相似度" align="center" />
      <el-table-column label="开门状态" align="center">
        <template slot-scope="{ row }">
          <el-tag :type="row.state === 'C' ? 'success' : 'danger'" size="small">
            {{ row.state === 'C' ? '开门成功' : '开门失败' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="开门时间" />
    </el-table>
    <el-pagination
      :current-page="page" :page-size="row" :total="total"
      :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange" @current-change="handlePageChange"
      class="pagination-container"
    />
  </div>
</template>

<script>
import { listAccessControlInout } from '@/api/property/accessControlApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'RoomDetailAccessControlInout',
  props: { roomId: { type: String, default: '' } },
  data() {
    return { loading: false, list: [], page: 1, row: 10, total: 0 }
  },
  watch: {
    roomId: { immediate: true, handler() { this.fetchData() } }
  },
  methods: {
    async fetchData() {
      if (!this.roomId) { this.list = []; return }
      this.loading = true
      try {
        const communityInfo = getCurrentCommunity() || {}
        const res = await listAccessControlInout({ communityId: communityInfo.communityId, roomId: this.roomId, page: this.page, row: this.row })
        this.list = res.data || []
        this.total = res.records || 0
      } catch (e) { this.list = [] }
      finally { this.loading = false }
    },
    viewFace(url) { window.open(url) },
    handleSizeChange(val) { this.row = val; this.page = 1; this.fetchData() },
    handlePageChange(val) { this.page = val; this.fetchData() }
  }
}
</script>

<style scoped>
.pagination-container { margin-top: 16px; text-align: right; }
</style>
