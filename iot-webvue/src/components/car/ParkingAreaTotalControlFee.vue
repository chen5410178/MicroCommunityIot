<template>
  <div class="total-control-fee">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span class="fee-title">收费信息</span>
      </div>
      <div class="fee-content">
        <div class="fee-section">
          <div class="form-group">
            <label>收费车辆</label>
            <el-input v-model="feeInfo.feeCarNum" disabled size="small" placeholder="车牌号" />
          </div>
          <div class="form-group">
            <label>停车时间</label>
            <el-input v-model="feeInfo.costMin" disabled size="small" placeholder="停车时间" />
          </div>
          <div class="form-group">
            <label>应收金额</label>
            <el-input v-model="feeInfo.payCharge" disabled size="small" placeholder="应收" />
          </div>
        </div>

        <div class="fee-buttons">
          <el-button @click="showQrCode">场内二维码</el-button>
          <el-button type="primary" :disabled="!feeInfo.pay || feeInfo.pay <= 0" @click="tempCarFee">临时车收费</el-button>
        </div>

        <div class="door-info-section">
          <h3 class="section-title">开门信息</h3>
          <div v-if="feeInfo.carInoutInfos.length > 0" class="door-info-list">
            <div v-for="(item, index) in feeInfo.carInoutInfos" :key="index" class="door-info-item">
              <div class="door-info-row">
                <span>{{ item.inOutTime }}</span>
                <span>{{ item.carNum }}</span>
                <el-tag :type="item.inoutType === '1001' ? 'success' : 'warning'" size="mini">
                  {{ item.inoutType === '1001' ? '进场' : '出场' }}
                </el-tag>
              </div>
              <div class="door-info-row">
                <span>{{ item.open }}</span>
                <span>{{ item.openMsg }}</span>
                <el-button
                  v-if="item.payCharge > 0 && item.inoutType === '2002'"
                  size="mini"
                  type="text"
                  @click="arrayCarOut(item)"
                >出场</el-button>
              </div>
            </div>
          </div>
          <div v-else class="empty-tip">没有进出场车辆</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'ParkingAreaTotalControlFee',
  props: {
    paId: { type: String, default: '' }
  },
  data() {
    return {
      feeInfo: {
        feeCarNum: '',
        costMin: '',
        payCharge: '0.0',
        pay: 0,
        machineId: '',
        boxId: '',
        carInoutInfos: []
      }
    }
  },
  methods: {
    onWsNotify(data) {
      if (!data || data.action !== 'FEE_INFO') return

      const _machine = data._machine
      const _data = data._data || data
      const oldPayCharge = this.feeInfo.payCharge

      let inoutType = '2002'
      if (_machine && _machine.direction !== '3307') {
        this.feeInfo.payCharge = oldPayCharge
        inoutType = '1001'
      } else {
        this.feeInfo.feeCarNum = _data.carNum || ''
        const hours = _data.hours || '0'
        const min = _data.min || '0'
        this.feeInfo.costMin = hours + '小时' + min + '分钟'
        this.feeInfo.pay = _data.payCharge || 0
        this.feeInfo.payCharge = _data.payCharge || '0'
        this.feeInfo.machineId = _data.extMachineId || ''
        this.feeInfo.remark = ''
      }

      const infos = [...this.feeInfo.carInoutInfos].reverse()
      infos.push({
        carNum: _data.carNum,
        inOutTime: _data.inOutTime,
        open: _data.open,
        openMsg: _data.remark,
        inoutType: inoutType,
        payCharge: _data.payCharge
      })
      this.feeInfo.carInoutInfos = infos.reverse().slice(0, 10)
    },
    showQrCode() {
      this.$emit('show-qrcode', { paId: this.paId, boxId: this.feeInfo.boxId })
    },
    tempCarFee() {
      this.$emit('show-custom-inout', {
        type: '1102',
        carNum: this.feeInfo.feeCarNum,
        amount: this.feeInfo.payCharge,
        payCharge: this.feeInfo.payCharge,
        machineId: this.feeInfo.machineId,
        paId: this.paId
      })
    },
    arrayCarOut(item) {
      this.$emit('show-custom-inout', {
        type: '1102',
        carNum: item.carNum,
        machineId: this.feeInfo.machineId,
        boxId: this.feeInfo.boxId,
        paId: this.paId
      })
    },
    clearFeeInfo() {
      const infos = [...this.feeInfo.carInoutInfos]
      this.feeInfo = {
        feeCarNum: '',
        costMin: '',
        payCharge: '0.0',
        pay: 0,
        machineId: '',
        boxId: '',
        carInoutInfos: infos
      }
    }
  }
}
</script>

<style scoped>
.total-control-fee {
  height: 100%;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.fee-title {
  font-weight: 600;
}
.fee-content {
  padding: 0;
}
.fee-section {
  margin-bottom: 16px;
}
.form-group {
  margin-bottom: 12px;
}
.form-group label {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}
.fee-buttons {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
  justify-content: center;
}
.door-info-section {
  border-top: 1px solid #ebeef5;
  padding-top: 16px;
}
.section-title {
  font-size: 14px;
  font-weight: 600;
  margin: 0 0 12px 0;
}
.door-info-list {
  max-height: 400px;
  overflow-y: auto;
}
.door-info-item {
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
  font-size: 12px;
}
.door-info-row {
  display: flex;
  align-items: center;
  gap: 6px;
  line-height: 20px;
}
.empty-tip {
  text-align: center;
  color: #909399;
  padding: 20px 0;
  font-size: 13px;
}
</style>
