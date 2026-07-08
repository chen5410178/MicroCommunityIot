<template>
  <div class="tel-machine-user">
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
            <span>话机客服</span>
            <el-button
              type="primary"
              size="small"
              :disabled="!activeMachineId"
              @click="handleAdd"
            >
              添加客服
            </el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="tmuId" label="编号" width="120" align="center" />
            <el-table-column prop="machineName" label="设备ID" min-width="150" align="center" />
            <el-table-column prop="staffName" label="操作人" min-width="120" align="center" />
            <el-table-column label="默认客服" width="100" align="center">
              <template slot-scope="{ row }">
                {{ row.masterFlag === 'Y' ? '是' : '否' }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button size="small" type="text" style="color: #f56c6c" @click="handleDelete(row)">
                  删除
                </el-button>
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
    <AddTelMachineUser
      :visible.sync="addVisible"
      :machineId="activeMachineId"
      @success="handleSuccess"
    />
    <DeleteTelMachineUser
      :visible.sync="delVisible"
      :delData="currentRow"
      @success="handleSuccess"
    />
  </div>
</template>

<script>
import { listTelMachine, listTelMachineUser } from '@/api/lamp/lampApi'
import AddTelMachineUser from '@/components/lamp/AddTelMachineUser.vue'
import DeleteTelMachineUser from '@/components/lamp/DeleteTelMachineUser.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'TelMachineUser',
  components: { AddTelMachineUser, DeleteTelMachineUser },
  data() {
    return {
      machines: [],
      activeMachineId: '',
      deviceLoading: false,
      loading: false,
      list: [],
      total: 0,
      addVisible: false,
      delVisible: false,
      currentRow: null,
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
        const res = await listTelMachineUser({
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
    handleAdd() {
      this.addVisible = true
    },
    handleDelete(row) {
      this.currentRow = { ...row }
      this.delVisible = true
    },
    handleSuccess() {
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
