<template>
  <div class="dispatch-log">
    <el-row :gutter="16" class="content-row">
      <el-col :span="4">
        <el-card shadow="never" class="left-card">
          <div slot="header" class="card-header">
            <span>监控设备</span>
          </div>
          <ul class="machine-list">
            <li
              v-for="item in machineList"
              :key="item.machineId"
              :class="{ active: queryParams.machineId === item.machineId }"
              @click="handleMachineClick(item)"
            >
              <span>{{ item.machineName }}</span>
            </li>
            <li v-if="machineList.length === 0" class="empty-tip">暂无设备</li>
          </ul>
        </el-card>
      </el-col>
      <el-col :span="20">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.personName" placeholder="请填写人员名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>布控告警</span>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column label="抓拍" width="100" align="center">
              <template slot-scope="{ row }">
                <img
                  v-if="row.imgUrl"
                  :src="row.imgUrl"
                  style="width: 60px; height: 60px; border-radius: 4px; cursor: pointer"
                  @click="viewImage(row.imgUrl)"
                />
                <img v-else src="/img/noPhoto.jpg" style="width: 60px; height: 60px; border-radius: 4px" />
              </template>
            </el-table-column>
            <el-table-column label="模版" width="100" align="center">
              <template slot-scope="{ row }">
                <img
                  v-if="row.faceUrl"
                  :src="row.faceUrl"
                  style="width: 60px; height: 60px; border-radius: 4px; cursor: pointer"
                  @click="viewImage(row.faceUrl)"
                />
                <img v-else src="/img/noPhoto.jpg" style="width: 60px; height: 60px; border-radius: 4px" />
              </template>
            </el-table-column>
            <el-table-column prop="personName" label="人员名称" min-width="120" />
            <el-table-column prop="personId" label="编号" width="150" />
            <el-table-column prop="communityName" label="项目名称" min-width="150" />
            <el-table-column prop="machineCode" label="设备编号" width="150" />
            <el-table-column prop="machineName" label="设备名称" min-width="150" />
            <el-table-column prop="score" label="相似度" width="100" align="center" />
            <el-table-column label="状态" width="100" align="center">
              <template slot-scope="{ row }">{{ row.state == '1001' ? '识别成功' : '识别失败' }}</template>
            </el-table-column>
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
      </el-col>
    </el-row>
    <el-dialog :visible.sync="imageVisible" title="图片预览" width="500px" append-to-body>
      <img :src="imageUrl" style="width: 100%" />
    </el-dialog>
  </div>
</template>

<script>
import { getDispatchLog } from '@/api/ai/dispatchApi'
import { listMonitorMachines } from '@/api/monitor/monitorApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'DispatchLog',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      machineList: [],
      imageVisible: false,
      imageUrl: '',
      queryParams: {
        page: 1,
        row: 10,
        personName: '',
        machineId: '',
        communityId: ''
      }
    }
  },
  mounted() {
    this.fetchMachines()
    this.fetchList()
  },
  methods: {
    async fetchMachines() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listMonitorMachines({ page: 1, row: 100, communityId })
        this.machineList = res.data || []
      } catch { /* ignore */ }
    },
    async fetchList() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        this.queryParams.communityId = communityId
        const res = await getDispatchLog(this.queryParams)
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    handleMachineClick(item) {
      if (this.queryParams.machineId === item.machineId) {
        this.queryParams.machineId = ''
      } else {
        this.queryParams.machineId = item.machineId
      }
      this.queryParams.page = 1
      this.fetchList()
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = { page: 1, row: 10, personName: '', machineId: '', communityId: '' }
      this.fetchList()
    },
    viewImage(url) {
      this.imageUrl = url
      this.imageVisible = true
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
.content-row { margin-bottom: 0; }
.left-card { min-height: 300px; }
.machine-list { list-style: none; padding: 0; margin: 0; }
.machine-list li {
  padding: 10px 12px;
  cursor: pointer;
  border-bottom: 1px solid #ebeef5;
  font-size: 14px;
  color: #606266;
  transition: all 0.2s;
}
.machine-list li:hover { background-color: #f5f7fa; }
.machine-list li.active { background-color: #ecf5ff; color: #409eff; font-weight: 600; border-left: 3px solid #409eff; }
.machine-list li.empty-tip { cursor: default; color: #c0c4cc; text-align: center; padding: 20px; }
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
