<template>
  <div class="instrument-log-manage">
    <el-row :gutter="16">
      <el-col :span="4">
        <CommunitySelector @change="handleCommunityChange" />
      </el-col>
      <el-col :span="20">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-select v-model="queryParams.action" placeholder="请选择" clearable style="width: 160px">
                <el-option label="实时数据" value="1001" />
                <el-option label="事件数据" value="2002" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.machineCode" placeholder="请输入设备编码" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.machineName" placeholder="请输入设备名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-date-picker v-model="queryParams.queryStartTime" type="datetime" placeholder="选择开始时间" value-format="yyyy-MM-dd HH:mm:ss" style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-date-picker v-model="queryParams.queryEndTime" type="datetime" placeholder="选择结束时间" value-format="yyyy-MM-dd HH:mm:ss" style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>仪表上报数据</span>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="logId" label="日志ID" width="100" align="center" />
            <el-table-column label="上报级别" width="100" align="center">
              <template slot-scope="{ row }">
                {{ row.action == '1001' ? '实时数据' : row.action == '2002' ? '事件数据' : row.action }}
              </template>
            </el-table-column>
            <el-table-column prop="sensorValue" label="上报数据" min-width="120" show-overflow-tooltip />
            <el-table-column prop="machineId" label="仪表ID" width="120" align="center" />
            <el-table-column prop="machineCode" label="仪表编码" width="120" />
            <el-table-column prop="machineName" label="仪表名称" width="120" />
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column label="操作" width="80" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button size="small" type="text" @click="handleShowSensor(row)">查看报文</el-button>
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
      </el-col>
    </el-row>

    <ShowInstrumentLogSensor :visible.sync="showSensorVisible" ref="showSensorRef" />
  </div>
</template>

<script>
import { listInstrumentLogs } from '@/api/meter/meterApi'
import CommunitySelector from '@/components/common/CommunitySelector.vue'
import ShowInstrumentLogSensor from '@/components/meter/ShowInstrumentLogSensor.vue'

export default {
  name: 'InstrumentLogManage',
  components: { CommunitySelector, ShowInstrumentLogSensor },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      showSensorVisible: false,
      queryParams: {
        page: 1,
        row: 10,
        communityId: '',
        action: '',
        machineCode: '',
        machineName: '',
        queryStartTime: '',
        queryEndTime: ''
      }
    }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listInstrumentLogs(this.queryParams)
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleCommunityChange(community) {
      this.queryParams.communityId = community.communityId || ''
      this.queryParams.page = 1
      this.fetchList()
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams.action = ''
      this.queryParams.machineCode = ''
      this.queryParams.machineName = ''
      this.queryParams.queryStartTime = ''
      this.queryParams.queryEndTime = ''
      this.queryParams.page = 1
      this.fetchList()
    },
    handleShowSensor(row) {
      this.$refs.showSensorRef.open(row)
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
