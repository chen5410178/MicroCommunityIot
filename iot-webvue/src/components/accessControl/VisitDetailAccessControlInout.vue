<template>
  <div class="visit-detail-inout">
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>进出记录</span>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column label="人脸" width="90" align="center">
          <template slot-scope="{ row }">
            <el-image v-if="row.facePath" :src="row.facePath" style="width: 60px; height: 60px; border-radius: 4px" fit="cover" :preview-src-list="[row.facePath]" />
            <img v-else src="/img/noPhoto.jpg" style="width: 60px; height: 60px; border-radius: 4px" />
          </template>
        </el-table-column>
        <el-table-column prop="inoutId" label="编号" min-width="100" />
        <el-table-column prop="machineName" label="设备名称" min-width="120" />
        <el-table-column prop="machineCode" label="设备编码" min-width="120" />
        <el-table-column prop="name" label="用户名称" min-width="100" />
        <el-table-column label="开门方式" width="90" align="center">
          <template slot-scope="{ row }">
            {{ row.openTypeCd == '1000' ? '人脸' : '其他' }}
          </template>
        </el-table-column>
        <el-table-column prop="tel" label="手机号" min-width="120">
          <template slot-scope="{ row }">{{ row.tel || '-' }}</template>
        </el-table-column>
        <el-table-column prop="idCard" label="身份证" min-width="160">
          <template slot-scope="{ row }">{{ row.idCard || '-' }}</template>
        </el-table-column>
        <el-table-column prop="similar" label="相似度" width="80" align="center" />
        <el-table-column label="开门状态" width="100" align="center">
          <template slot-scope="{ row }">
            {{ row.state == 'C' ? '开门成功' : '开门失败' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="开门时间" min-width="160" />
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
  name: 'VisitDetailAccessControlInout',
  props: {
    phoneNumber: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10
      }
    }
  },
  watch: {
    phoneNumber: {
      immediate: true,
      handler() {
        this.queryParams.page = 1
        this.fetchList()
      }
    }
  },
  methods: {
    async fetchList() {
      if (!this.phoneNumber) return
      this.loading = true
      try {
        const communityInfo = getCurrentCommunity() || {}
        const res = await listAccessControlInout({
          ...this.queryParams,
          tel: this.phoneNumber,
          communityId: communityInfo.communityId
        })
        this.list = res.data || []
        this.total = res.total || 0
      } catch (e) {
        console.error('加载进出记录失败', e)
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
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
