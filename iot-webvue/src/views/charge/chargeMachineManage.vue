<template>
  <div class="charge-machine-manage">
    <el-row :gutter="16">
      <el-col :span="4">
        <div class="left-card">
          <ul class="charge-type-list">
            <li
              v-for="item in chargeTypeList"
              :key="item.statusCd"
              :class="{ active: queryParams.chargeType === item.statusCd }"
              @click="handleChargeTypeClick(item.statusCd)"
            >
              {{ item.name }}
            </li>
          </ul>
        </div>
      </el-col>
      <el-col :span="20">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.machineName" placeholder="请输入名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.machineCode" placeholder="请输入设备编号" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>充电桩列表</span>
            <el-button type="primary" size="small" @click="handleAdd">添加充电桩</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="machineName" label="名称" min-width="120" />
            <el-table-column prop="machineCode" label="设备编号" min-width="120" />
            <el-table-column prop="factoryName" label="厂家" min-width="100" />
            <el-table-column label="插槽/桩个数" width="130" align="center">
              <template slot-scope="{ row }">
                <el-button size="small" type="text" @click="handleViewPort(row)">{{ row.portCount }}</el-button>
              </template>
            </el-table-column>
            <el-table-column prop="ruleName" label="充电规则" min-width="100" />
            <el-table-column prop="chargeTypeName" label="充电类型" min-width="100" />
            <el-table-column prop="stateName" label="状态" width="90" align="center" />
            <el-table-column prop="heartbeatTime" label="心跳时间" min-width="160" />
            <el-table-column label="监控" min-width="100">
              <template slot-scope="{ row }">
                <el-button
                  v-if="row.monitorId"
                  size="small"
                  type="text"
                  @click="handleWatchMonitor(row)"
                >
                  观看
                </el-button>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="240" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button size="small" type="text" @click="handleRestart(row)">重启</el-button>
                <el-button size="small" type="text" @click="handleEdit(row)">修改</el-button>
                <el-button size="small" type="text" style="color: #f56c6c" @click="handleDelete(row)">删除</el-button>
                <el-button size="small" type="text" @click="handleQrCode(row)">二维码</el-button>
                <el-button size="small" type="text" @click="handleDetail(row)">详情</el-button>
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

    <AddChargeMachine :visible.sync="addVisible" @success="handleSuccess" />
    <EditChargeMachine :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteChargeMachine :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
    <RestartChargeMachine :visible.sync="restartVisible" :restart-data="currentRow" @success="handleSuccess" />
    <ChargeMachineQrCode :visible.sync="qrVisible" :qr-data="currentRow" />
  </div>
</template>

<script>
import { listChargeMachine } from '@/api/charge/chargeApi'
import { listDicts } from '@/api/system/systemApi'
import AddChargeMachine from '@/components/charge/AddChargeMachine.vue'
import EditChargeMachine from '@/components/charge/EditChargeMachine.vue'
import DeleteChargeMachine from '@/components/charge/DeleteChargeMachine.vue'
import RestartChargeMachine from '@/components/charge/RestartChargeMachine.vue'
import ChargeMachineQrCode from '@/components/charge/ChargeMachineQrCode.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ChargeMachineManage',
  components: { AddChargeMachine, EditChargeMachine, DeleteChargeMachine, RestartChargeMachine, ChargeMachineQrCode },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      chargeTypeList: [{ name: '全部', statusCd: '' }],
      addVisible: false,
      editVisible: false,
      delVisible: false,
      restartVisible: false,
      qrVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        machineName: '',
        machineCode: '',
        chargeType: ''
      }
    }
  },
  mounted() {
    this.loadChargeTypes()
    this.fetchList()
  },
  methods: {
    async loadChargeTypes() {
      try {
        const res = await listDicts({ tableName: 'charge_machine', tableColumns: 'charge_type' })
        if (res && res.length) {
          this.chargeTypeList = [{ name: '全部', statusCd: '' }, ...res]
        }
      } catch (e) {
        console.error('加载充电类型失败', e)
      }
    },
    async fetchList() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listChargeMachine({ ...this.queryParams, communityId })
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleChargeTypeClick(statusCd) {
      this.queryParams.chargeType = statusCd
      this.queryParams.page = 1
      this.fetchList()
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams.machineName = ''
      this.queryParams.machineCode = ''
      this.queryParams.page = 1
      this.fetchList()
    },
    handleAdd() {
      this.addVisible = true
    },
    handleEdit(row) {
      this.currentRow = { ...row }
      this.editVisible = true
    },
    handleDelete(row) {
      this.currentRow = { ...row }
      this.delVisible = true
    },
    handleRestart(row) {
      this.currentRow = { ...row }
      this.restartVisible = true
    },
    handleQrCode(row) {
      this.currentRow = { ...row }
      this.qrVisible = true
    },
    handleDetail(row) {
      this.$router.push('/pages/charge/chargeMachineDetail?machineId=' + row.machineId)
    },
    handleViewPort(row) {
      this.$router.push('/pages/charge/chargeMachinePortManage?machineId=' + row.machineId)
    },
    handleWatchMonitor(row) {
      this.$router.push('/pages/charge/chargeMachineDetail?machineId=' + row.machineId)
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
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
.left-card {
  border-radius: 4px;
  background: #fff;
  padding: 8px 0;
}
.charge-type-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.charge-type-list li {
  padding: 10px 16px;
  cursor: pointer;
  border-radius: 4px;
  margin: 0 8px 5px;
  text-align: center;
  font-size: 14px;
  color: #303133;
  transition: all 0.3s;
}
.charge-type-list li:hover { background-color: #f5f7fa; }
.charge-type-list li.active { background-color: #409eff; color: white; font-weight: bold; }
</style>
