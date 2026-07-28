<template>
  <div class="charge-machine-port-manage">
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>端口管理</span>
        <div>
          <el-button type="primary" size="small" @click="handleAdd">添加端口</el-button>
          <el-button size="small" @click="handleBack">返回</el-button>
        </div>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="portName" label="插座名称" min-width="150" />
        <el-table-column prop="portCode" label="插座编号" min-width="150" />
        <el-table-column prop="stateName" label="插座状态" min-width="120" />
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button
              v-if="row.state === '1'"
              size="small"
              type="text"
              style="color: #e6a23c"
              @click="handleStop(row)"
            >
              停止充电
            </el-button>
            <el-button size="small" type="text" @click="handleQrCode(row)">二维码</el-button>
            <el-button size="small" type="text" style="color: #f56c6c" @click="handleDelete(row)">删除</el-button>
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

    <AddChargeMachinePort :visible.sync="addVisible" :machine-id="machineId" @success="handleSuccess" />
    <DeleteChargeMachinePort :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
    <ChargeMachineQrCode :visible.sync="qrVisible" :qr-data="currentRow" />
    <StopChargeMachine ref="stopRef" @success="handleSuccess" />
  </div>
</template>

<script>
import { listChargeMachinePort } from '@/api/charge/chargeApi'
import AddChargeMachinePort from '@/components/charge/AddChargeMachinePort.vue'
import DeleteChargeMachinePort from '@/components/charge/DeleteChargeMachinePort.vue'
import ChargeMachineQrCode from '@/components/charge/ChargeMachineQrCode.vue'
import StopChargeMachine from '@/components/charge/StopChargeMachine.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ChargeMachinePortManage',
  components: { AddChargeMachinePort, DeleteChargeMachinePort, ChargeMachineQrCode, StopChargeMachine },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      machineId: '',
      addVisible: false,
      delVisible: false,
      qrVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10
      }
    }
  },
  mounted() {
    this.machineId = this.$route.query.machineId || ''
    if (this.machineId) {
      this.fetchList()
    }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listChargeMachinePort({
          machineId: this.machineId,
          communityId,
          page: this.queryParams.page,
          row: this.queryParams.row
        })
        this.list = res.data || []
        this.total = res.total || 0
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
    handleQrCode(row) {
      this.currentRow = { ...row }
      this.qrVisible = true
    },
    handleStop(row) {
      this.$refs.stopRef.open(row)
    },
    handleSuccess() {
      this.fetchList()
    },
    handleBack() {
      this.$router.go(-1)
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
