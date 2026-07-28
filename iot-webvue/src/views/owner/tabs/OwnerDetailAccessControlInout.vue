<template>
  <div class="tab-list">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column label="人脸" width="100" align="center">
        <template slot-scope="{ row }">
          <img v-if="row.facePath" :src="row.facePath" style="width: 60px; height: 60px; border-radius: 4px;" @error="row.facePath = ''" />
          <img v-else src="/img/noPhoto.jpg" style="width: 60px; height: 60px; border-radius: 4px;" />
        </template>
      </el-table-column>
      <el-table-column prop="inoutId" label="编号" min-width="120" />
      <el-table-column prop="machineName" label="设备名称" min-width="120" />
      <el-table-column prop="machineCode" label="设备编码" min-width="120" />
      <el-table-column prop="name" label="用户名称" min-width="100" />
      <el-table-column label="开门方式" width="90">
        <template slot-scope="{ row }">
          <span v-if="row.openTypeCd == '1000'">人脸</span>
          <span v-else-if="row.openTypeCd == '2000'">卡片</span>
          <span v-else-if="row.openTypeCd == '3000'">二维码</span>
          <span v-else-if="row.openTypeCd == '4000'">密码</span>
          <span v-else-if="row.openTypeCd == '5000'">远程</span>
          <span v-else>其他</span>
        </template>
      </el-table-column>
      <el-table-column label="手机号" min-width="110">
        <template slot-scope="{ row }">{{ row.tel || '-' }}</template>
      </el-table-column>
      <el-table-column label="身份证" min-width="160">
        <template slot-scope="{ row }">{{ row.idCard || '-' }}</template>
      </el-table-column>
      <el-table-column prop="similar" label="相似度" width="80" />
      <el-table-column label="开门状态" width="90">
        <template slot-scope="{ row }">{{ row.state === 'C' ? '开门成功' : '开门失败' }}</template>
      </el-table-column>
      <el-table-column prop="createTime" label="开门时间" width="160" />
    </el-table>
    <el-pagination :current-page="page" :page-size="row" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handlePageChange" class="pagination-container" />
  </div>
</template>

<script>
import { listAccessControlInout } from '@/api/accessControl/accessControlApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'OwnerDetailAccessControlInout',
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
        const res = await listAccessControlInout({
          personId: this.ownerId,
          tel: this.link,
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
