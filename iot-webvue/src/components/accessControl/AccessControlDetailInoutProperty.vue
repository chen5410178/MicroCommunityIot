<template>
  <div class="access-control-detail-inout-property">
    <el-card shadow="never" class="table-card">
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column label="人脸" width="90" align="center">
          <template slot-scope="{ row }">
            <el-image v-if="row.facePath" :src="row.facePath" style="width: 60px; height: 60px; border-radius: 4px" fit="cover" />
            <img v-else src="/img/noPhoto.jpg" style="width: 60px; height: 60px; border-radius: 4px" />
          </template>
        </el-table-column>
        <el-table-column prop="inoutId" label="编号" min-width="100" />
        <el-table-column prop="machineName" label="设备名称" min-width="120" />
        <el-table-column prop="machineCode" label="设备编码" min-width="120" />
        <el-table-column prop="name" label="用户名称" min-width="100" />
        <el-table-column label="开门方式" width="90" align="center">
          <template slot-scope="{ row }">
            {{
              row.openTypeCd == '1000' ? '人脸' :
              row.openTypeCd == '2000' ? '卡片' :
              row.openTypeCd == '3000' ? '二维码' :
              row.openTypeCd == '4000' ? '密码' :
              row.openTypeCd == '5000' ? '远程' : '其他'
            }}
          </template>
        </el-table-column>
        <el-table-column prop="tel" label="手机号" width="130" />
        <el-table-column prop="idCard" label="用户身份证" min-width="160" />
        <el-table-column prop="similar" label="相似度" width="80" align="center" />
        <el-table-column label="开门状态" width="90" align="center">
          <template slot-scope="{ row }">
            {{ row.state == 'C' ? '开门成功' : '开门失败' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="开门时间" min-width="150" />
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
  </div>
</template>

<script>
import { listAccessControlInout } from '@/api/accessControl/accessControlApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AccessControlDetailInoutProperty',
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
        if (this.machineId) {
          this.queryParams.page = 1
          this.fetchList()
        }
      }
    }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const community = getCurrentCommunity()
        const res = await listAccessControlInout({
          machineId: this.machineId,
          communityId: community ? community.communityId : '',
          page: this.queryParams.page,
          row: this.queryParams.row
        })
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
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
.table-card { margin-bottom: 0; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
