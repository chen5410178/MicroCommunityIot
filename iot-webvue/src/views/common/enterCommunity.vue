<template>
  <div class="enter-community">
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>我的小区</span>
        <!-- <el-button size="small" @click="handleVisual">可视化</el-button> -->
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="name" label="小区名称" min-width="150" />
        <el-table-column prop="communityId" label="小区编码" width="180" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="tel" label="联系方式" width="120" />
      </el-table>
      <el-pagination
        v-if="total > 0"
        style="margin-top: 15px; text-align: right"
        :current-page="queryParams.page"
        :page-sizes="[10, 20, 50]"
        :page-size="queryParams.row"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
      <div class="row margin-top-xs">
        <div class="col-sm-9">
          <div>温馨提示：小区和物业公司管理，请联系管理员或者登录运营账号管理；</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { listMyEnteredCommunitys } from '@/api/community/communityApi'

export default {
  name: 'EnterCommunity',
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
  mounted() {
    this.fetchList()
  },
  methods: {
    handleSizeChange(val) {
      this.queryParams.row = val
      this.queryParams.page = 1
      this.fetchList()
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.fetchList()
    },
    async fetchList() {
      this.loading = true
      try {
        const res = await listMyEnteredCommunitys(this.queryParams)
        this.list = res.data || []
        this.total = res.total || 0
      } catch { /* ignore */ } finally {
        this.loading = false
      }
    },
    handleVisual() {
      window.open('/3d.html#/pages/3d/community3dMap')
    }
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
