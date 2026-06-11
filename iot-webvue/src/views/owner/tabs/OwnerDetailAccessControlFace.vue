<template>
  <div class="tab-list">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column label="人脸" width="100" align="center">
        <template slot-scope="{ row }">
          <img v-if="row.facePath" :src="row.facePath" style="width: 60px; height: 60px; border-radius: 4px;" @error="row.facePath = ''" />
          <img v-else src="/img/noPhoto.jpg" style="width: 60px; height: 60px; border-radius: 4px;" />
        </template>
      </el-table-column>
      <el-table-column prop="machineName" label="门禁名称" min-width="120" />
      <el-table-column label="人员类型" width="90">
        <template slot-scope="{ row }">{{ row.personType === 'OWNER' ? '人员' : '员工' }}</template>
      </el-table-column>
      <el-table-column prop="name" label="用户名称" min-width="100" />
      <el-table-column prop="startTime" label="开始时间" width="160" />
      <el-table-column prop="endTime" label="结束时间" width="160" />
      <el-table-column label="身份证" min-width="160">
        <template slot-scope="{ row }">{{ row.idNumber || '-' }}</template>
      </el-table-column>
      <el-table-column prop="stateName" label="状态" width="90" />
      <el-table-column prop="message" label="消息" min-width="120" />
      <el-table-column label="门禁卡号" min-width="140">
        <template slot-scope="{ row }">{{ row.cardNumber || '-' }}</template>
      </el-table-column>
      <el-table-column prop="comeTypeName" label="来自类型" min-width="100" />
    </el-table>
    <el-pagination :current-page="page" :page-size="row" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handlePageChange" class="pagination-container" />
  </div>
</template>

<script>
import { listAccessControlFace } from '@/api/accessControl/accessControlApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'OwnerDetailAccessControlFace',
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
        const res = await listAccessControlFace({
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
