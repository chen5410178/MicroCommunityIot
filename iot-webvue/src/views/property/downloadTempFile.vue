<template>
  <div class="list-download-temp-file">
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>下载中心</span>
        <el-button type="primary" size="small" @click="handleRefresh">刷新</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="name" label="名称" min-width="150" />
        <el-table-column prop="fileTypeName" label="文件类型" width="120" align="center" />
        <el-table-column prop="downloadUserName" label="下载人" width="120" align="center" />
        <el-table-column prop="createTime" label="下载时间" width="180" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template slot-scope="{ row }">
            <el-tag v-if="row.state === '3003'" type="success" size="small">已完成</el-tag>
            <el-tag v-else-if="row.state === '4004'" type="danger" size="small">失败</el-tag>
            <el-tag v-else type="info" size="small">{{ row.stateName || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" />
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button v-if="row.state === '3003'" size="small" type="text" @click="handleDownload(row)">下载</el-button>
            <el-button v-if="row.state === '3003' || row.state === '4004'" size="small" type="text" style="color: #f56c6c" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
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
    <DeleteDownloadTempFile :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listUserDownloadFile } from '@/api/property/propertyApi'
import { getCurrentCommunity } from '@/utils/auth'
import DeleteDownloadTempFile from '@/components/property/DeleteDownloadTempFile.vue'

export default {
  name: 'ListDownloadTempFile',
  components: { DeleteDownloadTempFile },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      delVisible: false,
      currentRow: null,
      queryParams: { page: 1, row: 10 }
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
        const params = { ...this.queryParams, communityId: community ? community.communityId : '' }
        const res = await listUserDownloadFile(params)
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleDownload(file) {
      if (!file.downloadUrl) {
        this.$message.error('下载地址不存在')
        return
      }
      window.open(file.downloadUrl)
    },
    handleDelete(row) {
      this.currentRow = { ...row }
      this.delVisible = true
    },
    handleSuccess() {
      this.fetchList()
    },
    handleRefresh() {
      this.queryParams.page = 1
      this.fetchList()
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
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
