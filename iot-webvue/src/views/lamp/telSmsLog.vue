<template>
  <div class="list-tel-sms-log">
    <el-row :gutter="16">
      <el-col :span="5">
        <el-card shadow="never" class="device-card">
          <div class="device-list" v-loading="deviceLoading">
            <div
              v-for="item in machines"
              :key="item.machineId"
              class="device-item"
              :class="{ active: activeMachineId === item.machineId }"
              @click="handleSwitchMachine(item)"
            >
              <i class="el-icon-phone-outline" style="margin-right: 8px" />
              <span>{{ item.machineName }}</span>
            </div>
            <div v-if="!deviceLoading && machines.length === 0" class="empty-tip">
              暂无设备
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="19">
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>短信记录</span>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column label="呼叫类型" width="100" align="center">
              <template slot-scope="{ row }">
                {{ row.callType === 'CALL_IN' ? '接受' : '发出' }}
              </template>
            </el-table-column>
            <el-table-column prop="state" label="状态" width="100" align="center" />
            <el-table-column prop="tel" label="电话号" width="140" />
            <el-table-column prop="smsContent" label="短信内容" min-width="200" />
            <el-table-column prop="personName" label="操作人" width="120" align="center" />
            <el-table-column prop="createTime" label="创建时间" width="180" />
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
  </div>
</template>

<script>
import { listTelMachine, listTelSmsLog } from '@/api/lamp/lampApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListTelSmsLog',
  data() {
    return {
      machines: [],
      activeMachineId: '',
      deviceLoading: false,
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
    this.fetchMachines()
  },
  methods: {
    async fetchMachines() {
      this.deviceLoading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listTelMachine({ page: 1, row: 100, communityId })
        this.machines = res.data || []
        if (this.machines.length > 0) {
          this.handleSwitchMachine(this.machines[0])
        }
      } finally {
        this.deviceLoading = false
      }
    },
    handleSwitchMachine(item) {
      this.activeMachineId = item.machineId
      this.queryParams.page = 1
      this.fetchList()
    },
    async fetchList() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listTelSmsLog({
          ...this.queryParams,
          machineId: this.activeMachineId,
          communityId
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
.device-card {
  height: 100%;
}
.device-card >>> .el-card__body {
  padding: 0;
}
.device-list {
  max-height: calc(100vh - 200px);
  overflow-y: auto;
}
.device-item {
  padding: 12px 16px;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 5px;
  font-size: 14px;
  display: flex;
  align-items: center;
  transition: all 0.3s;
}
.device-item:hover {
  background-color: #f5f7fa;
}
.device-item.active {
  background-color: #409eff;
  color: white;
  font-weight: bold;
}
.empty-tip {
  padding: 40px 0;
  text-align: center;
  color: #909399;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.table-card {
  margin-bottom: 0;
}
.pagination-container {
  margin-top: 16px;
  text-align: right;
}
</style>
