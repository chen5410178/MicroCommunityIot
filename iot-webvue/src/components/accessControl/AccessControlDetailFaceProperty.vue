<template>
  <div class="access-control-detail-face-property">
    <el-card shadow="never" class="table-card">
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column label="人脸" width="90" align="center">
          <template slot-scope="{ row }">
            <el-image v-if="row.facePath" :src="row.facePath" style="width: 60px; height: 60px; border-radius: 4px" fit="cover" />
            <img v-else src="/img/noPhoto.jpg" style="width: 60px; height: 60px; border-radius: 4px" />
          </template>
        </el-table-column>
        <el-table-column prop="machineName" label="门禁" min-width="120" />
        <el-table-column label="人员类型" width="90" align="center">
          <template slot-scope="{ row }">
            {{ row.personType == 'OWNER' ? '人员' : '员工' }}
          </template>
        </el-table-column>
        <el-table-column prop="name" label="用户名称" min-width="100" />
        <el-table-column prop="startTime" label="开始时间" min-width="150" />
        <el-table-column prop="endTime" label="结束时间" min-width="150" />
        <el-table-column prop="idNumber" label="身份证" min-width="160" />
        <el-table-column prop="stateName" label="状态" width="80" align="center" />
        <el-table-column prop="message" label="消息" min-width="100" />
        <el-table-column prop="cardNumber" label="门禁卡号" min-width="140" />
        <el-table-column prop="comeTypeName" label="来自类型" width="100" align="center" />
        <el-table-column prop="createTime" label="创建时间" min-width="150" />
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
import { listAccessControlFace } from '@/api/accessControl/accessControlApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AccessControlDetailFaceProperty',
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
        const res = await listAccessControlFace({
          machineId: this.machineId,
          communityId: community ? community.communityId : '',
          page: this.queryParams.page,
          row: this.queryParams.row
        })
        this.list = res.data || []
        this.total = res.total || 0
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
