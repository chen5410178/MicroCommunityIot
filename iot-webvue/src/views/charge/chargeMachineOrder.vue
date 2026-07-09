<template>
  <div class="charge-machine-order">
    <el-row :gutter="16">
      <el-col :span="4">
        <el-card shadow="never" class="left-card">
          <div class="type-list">
            <div
              v-for="item in chargeTypes"
              :key="item.statusCd"
              :class="['type-item', { active: queryParams.chargeType === item.statusCd }]"
              @click="handleTypeChange(item)"
            >
              {{ item.name }}
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="20">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.orderId" placeholder="请输入订单编号" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.personName" placeholder="请输入名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.personTel" placeholder="请输入手机号" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.machineName" placeholder="请输入充电桩名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.portName" placeholder="请输入插槽名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-select v-model="queryParams.state" placeholder="请选择插座状态" clearable style="width: 180px">
                <el-option label="充电中" value="1001" />
                <el-option label="充电完成" value="2002" />
                <el-option label="充电失败" value="3003" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-date-picker
                v-model="queryParams.queryStartTime"
                type="datetime"
                placeholder="请选择开始时间"
                value-format="yyyy-MM-dd HH:mm:ss"
                style="width: 190px"
              />
            </el-form-item>
            <el-form-item>
              <el-date-picker
                v-model="queryParams.queryEndTime"
                type="datetime"
                placeholder="请选择结束时间"
                value-format="yyyy-MM-dd HH:mm:ss"
                style="width: 190px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>充电订单</span>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="orderId" label="编号" min-width="100" />
            <el-table-column prop="personName" label="名称" min-width="100" />
            <el-table-column prop="personTel" label="手机号" min-width="120" />
            <el-table-column prop="machineName" label="充电桩" min-width="120" />
            <el-table-column prop="portName" label="插槽" min-width="100" />
            <el-table-column label="充电小时" width="110" align="center">
              <template slot-scope="{ row }">
                {{ row.chargeHours == 999 ? '按量充电' : row.chargeHours + '小时' }}
              </template>
            </el-table-column>
            <el-table-column prop="energy" label="充电量" width="90" align="center" />
            <el-table-column prop="startTime" label="开始时间" min-width="160" />
            <el-table-column prop="endTime" label="结束时间" min-width="160" />
            <el-table-column label="扣款账户" width="130" align="center">
              <template slot-scope="{ row }">
                {{ row.acctDetailId }}
                <el-button type="text" size="mini" @click="handleViewAccount(row)">查看</el-button>
              </template>
            </el-table-column>
            <el-table-column prop="durationPrice" label="电费" width="90" align="center" />
            <el-table-column prop="servicePrice" label="服务费" width="90" align="center" />
            <el-table-column label="扣款金额" width="110" align="center">
              <template slot-scope="{ row }">
                {{ row.amount }}
                <el-button type="text" size="mini" @click="handleViewAccts(row)">明细</el-button>
              </template>
            </el-table-column>
            <el-table-column label="插座状态" width="150" align="center">
              <template slot-scope="{ row }">
                {{ row.stateName }}
                <span v-if="row.state == '1001' || row.state == '1000'">
                  <el-button type="text" size="mini" @click="handleStopCharge(row)">停止充电</el-button>
                  <el-button type="text" size="mini" @click="handleThrowStopCharge(row)">异常停止</el-button>
                </span>
              </template>
            </el-table-column>
            <el-table-column label="说明" min-width="150">
              <template slot-scope="{ row }">
                <div style="max-width: 150px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                  {{ row.remark || '-' }}
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" min-width="160" />
            <el-table-column label="操作" width="80" align="center" fixed="right">
              <template slot-scope="{ row }">
                <el-button type="text" size="mini" @click="handleDetail(row)">详情</el-button>
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

    <el-dialog title="账户详情" :visible.sync="accountVisible" width="500px">
      <el-form label-width="100px">
        <el-form-item label="账户">{{ accountInfo.acctName }}</el-form-item>
        <el-form-item label="账户类型">{{ accountInfo.acctTypeName }}</el-form-item>
        <el-form-item label="账户余额">{{ accountInfo.amount }}</el-form-item>
        <el-form-item label="账户编号">{{ accountInfo.acctId }}</el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="accountVisible = false">关闭</el-button>
      </div>
    </el-dialog>

    <StopChargeMachine ref="stopChargeMachineRef" @success="fetchList" />
    <ThrowStopChargeMachine ref="throwStopChargeMachineRef" @success="fetchList" />
  </div>
</template>

<script>
import { listChargeMachineOrder, queryOwnerAccount } from '@/api/charge/chargeApi'
import { listDicts } from '@/api/system/systemApi'
import { getCurrentCommunity } from '@/utils/auth'
import StopChargeMachine from '@/components/charge/StopChargeMachine.vue'
import ThrowStopChargeMachine from '@/components/charge/ThrowStopChargeMachine.vue'

export default {
  name: 'ChargeMachineOrder',
  components: { StopChargeMachine, ThrowStopChargeMachine },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      chargeTypes: [{ name: '全部', statusCd: '' }],
      queryParams: {
        page: 1,
        row: 10,
        communityId: '',
        chargeType: '',
        orderId: '',
        personName: '',
        personTel: '',
        machineName: '',
        portName: '',
        state: '',
        queryStartTime: '',
        queryEndTime: ''
      },
      accountVisible: false,
      accountInfo: {}
    }
  },
  mounted() {
    const community = getCurrentCommunity()
    if (community) {
      this.queryParams.communityId = community.communityId
    }
    this.loadChargeTypes()
    this.fetchList()
  },
  methods: {
    async loadChargeTypes() {
      try {
        const res = await listDicts({ tableName: 'charge_machine', tableColumns: 'charge_type' })
        if (res && res.length) {
          res.forEach(item => {
            this.chargeTypes.push(item)
          })
        }
      } catch (e) {
        console.error('加载充电类型失败', e)
      }
    },
    handleTypeChange(item) {
      this.queryParams.chargeType = item.statusCd
      this.queryParams.page = 1
      this.fetchList()
    },
    async fetchList() {
      this.loading = true
      try {
        const res = await listChargeMachineOrder(this.queryParams)
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams.orderId = ''
      this.queryParams.personName = ''
      this.queryParams.personTel = ''
      this.queryParams.machineName = ''
      this.queryParams.portName = ''
      this.queryParams.state = ''
      this.queryParams.queryStartTime = ''
      this.queryParams.queryEndTime = ''
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
    },
    handleStopCharge(row) {
      this.$refs.stopChargeMachineRef.open(row)
    },
    handleThrowStopCharge(row) {
      this.$refs.throwStopChargeMachineRef.open(row)
    },
    handleViewAccts(row) {
      this.$router.push('/pages/charge/chargeMachineOrderAccts?orderId=' + row.orderId)
    },
    handleDetail(row) {
      this.$router.push('/pages/charge/chargeMachineDetail?machineId=' + row.machineId + '&orderId=' + row.orderId + '&portId=' + row.portId + '&acctId=' + row.acctDetailId)
    },
    async handleViewAccount(row) {
      try {
        const res = await queryOwnerAccount({
          acctId: row.acctDetailId,
          page: 1,
          row: 1,
          communityId: this.queryParams.communityId
        })
        if (res.data && res.data.length > 0) {
          this.accountInfo = res.data[0]
          this.accountVisible = true
        } else {
          this.$message.warning('未找到账户信息')
        }
      } catch (e) {
        this.$message.error('查询账户失败')
      }
    }
  }
}
</script>

<style scoped>
.left-card { margin-bottom: 16px; }
.type-list { padding: 0; }
.type-item { padding: 10px 15px; cursor: pointer; border-radius: 4px; margin-bottom: 5px; text-align: center; font-size: 14px; transition: all 0.3s; color: #606266; }
.type-item:hover { background-color: #f5f7fa; }
.type-item.active { background-color: #409eff; color: white; font-weight: bold; }
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
