
<template>
  <div class="list-mqtt-send-log">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="设备编码">
          <el-input v-model="queryParams.machineCode" placeholder="设备编码" disabled style="width: 220px" />
        </el-form-item>
        <el-form-item label="指令类型">
          <el-select v-model="queryParams.cmdType" placeholder="请选择" clearable style="width: 160px">
            <el-option label="开闸" value="OPEN_DOOR" />
            <el-option label="关闸" value="CLOSE_DOOR" />
            <el-option label="语音" value="VOICE" />
            <el-option label="文字" value="TEXT" />
            <el-option label="抓拍" value="SNAPSHOT" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>下发日志</span>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column label="指令类型" width="100" align="center">
          <template slot-scope="{ row }">
            <el-tag v-if="row.cmdType == 'OPEN_DOOR'" type="success" size="small">开闸</el-tag>
            <el-tag v-else-if="row.cmdType == 'CLOSE_DOOR'" type="info" size="small">关闸</el-tag>
            <el-tag v-else-if="row.cmdType == 'VOICE'" type="warning" size="small">语音</el-tag>
            <el-tag v-else-if="row.cmdType == 'TEXT'" size="small">文字</el-tag>
            <el-tag v-else-if="row.cmdType == 'SNAPSHOT'" type="danger" size="small">抓拍</el-tag>
            <span v-else>{{ row.cmdType }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="carNum" label="车牌号" width="120" />
        <el-table-column prop="topic" label="Topic" min-width="200" show-overflow-tooltip />
        <el-table-column prop="context" label="下发内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="160" />
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
import { listMqttSendLogs } from '@/api/car/carApi'

export default {
  name: 'ListMqttSendLog',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10,
        machineCode: '',
        cmdType: ''
      }
    }
  },
  mounted() {
    this.queryParams.machineCode = this.$route.query.machineCode || ''
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listMqttSendLogs(this.queryParams)
        this.list = res.data || []
        this.total = res.total || 0
      } catch (e) {
        this.$message.error(e.message || '查询失败')
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams.cmdType = ''
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
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
