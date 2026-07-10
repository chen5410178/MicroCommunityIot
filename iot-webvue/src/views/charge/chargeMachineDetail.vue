<template>
  <div class="charge-machine-detail">
    <el-card shadow="never" class="info-card">
      <div slot="header" class="card-header">
        <span>充电桩信息</span>
        <el-button size="small" @click="handleBack">返回</el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="info-item"><span class="info-label">充电桩ID：</span>{{ info.machineId }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">充电桩名称：</span>{{ info.machineName }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">充电桩编号：</span>{{ info.machineCode }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">厂家名称：</span>{{ info.factoryName }}</div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="info-row">
        <el-col :span="6">
          <div class="info-item"><span class="info-label">充电类型：</span>{{ info.chargeTypeName }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">插槽个数：</span>{{ info.portCount }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">充电规则：</span>{{ info.ruleName }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">状态：</span>{{ info.stateName }}</div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="info-row">
        <el-col :span="6">
          <div class="info-item"><span class="info-label">心跳时间：</span>{{ info.heartbeatTime }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">添加时间：</span>{{ info.createTime }}</div>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="never" class="tab-card">
      <el-tabs v-model="currentTab" @tab-click="handleTabClick">
        <el-tab-pane label="插槽" name="chargeDetailPort">
          <ChargeDetailPort :machine-id="machineId" :port-id="portId" />
        </el-tab-pane>
        <el-tab-pane v-if="info.chargeType === '1001'" label="充电规则" name="chargeDetailRule">
          <ChargeDetailRule :rule-id="info.ruleId" />
        </el-tab-pane>
        <el-tab-pane v-if="info.chargeType === '1002'" label="充电规则" name="chargeDetailPrice">
          <ChargeDetailPrice :rule-id="info.ruleId" />
        </el-tab-pane>
        <el-tab-pane label="充电订单" name="chargeDetailOrder">
          <ChargeDetailOrder :machine-id="machineId" :order-id="orderId" />
        </el-tab-pane>
        <el-tab-pane label="停车券" name="chargeDetailParking">
          <ChargeDetailParking :machine-id="machineId" :order-id="orderId" />
        </el-tab-pane>
        <el-tab-pane v-if="acctId" label="账户详情" name="chargeDetailAcct">
          <ChargeDetailAcct :acct-id="acctId" />
        </el-tab-pane>
        <el-tab-pane v-if="orderId" label="扣款明细" name="chargeDetailOrderAccts">
          <ChargeDetailOrderAccts :order-id="orderId" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { listChargeMachine } from '@/api/charge/chargeApi'
import ChargeDetailPort from '@/components/charge/ChargeDetailPort.vue'
import ChargeDetailRule from '@/components/charge/ChargeDetailRule.vue'
import ChargeDetailPrice from '@/components/charge/ChargeDetailPrice.vue'
import ChargeDetailOrder from '@/components/charge/ChargeDetailOrder.vue'
import ChargeDetailParking from '@/components/charge/ChargeDetailParking.vue'
import ChargeDetailAcct from '@/components/charge/ChargeDetailAcct.vue'
import ChargeDetailOrderAccts from '@/components/charge/ChargeDetailOrderAccts.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ChargeMachineDetail',
  components: {
    ChargeDetailPort,
    ChargeDetailRule,
    ChargeDetailPrice,
    ChargeDetailOrder,
    ChargeDetailParking,
    ChargeDetailAcct,
    ChargeDetailOrderAccts
  },
  data() {
    return {
      machineId: '',
      orderId: '',
      portId: '',
      acctId: '',
      currentTab: 'chargeDetailPort',
      info: {}
    }
  },
  mounted() {
    this.machineId = this.$route.query.machineId || ''
    this.orderId = this.$route.query.orderId || ''
    this.portId = this.$route.query.portId || ''
    this.acctId = this.$route.query.acctId || ''
    if (this.machineId) {
      this.loadMachineInfo()
    }
  },
  methods: {
    async loadMachineInfo() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listChargeMachine({
          machineId: this.machineId,
          communityId,
          page: 1,
          row: 1
        })
        if (res.data && res.data.length > 0) {
          this.info = res.data[0]
          this.orderId = this.orderId || this.info.orderId || ''
          this.acctId = this.acctId || this.info.acctId || ''
        }
      } catch (e) {
        console.error('获取充电桩信息失败', e)
      }
    },
    handleTabClick() {},
    handleBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.info-card { margin-bottom: 16px; }
.tab-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.info-item { line-height: 32px; font-size: 14px; }
.info-label { color: #909399; }
.info-row { margin-top: 8px; }
</style>
