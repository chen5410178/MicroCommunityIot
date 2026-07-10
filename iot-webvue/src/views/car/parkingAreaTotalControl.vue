<template>
  <div class="parking-area-total-control">
    <el-row :gutter="16">
      <el-col :span="18">
        <el-card shadow="never">
          <el-tabs v-model="activeTab" @tab-click="handleTabClick">
            <el-tab-pane label="总控台" name="parkingAreaTotalControlVideo">
              <ParkingAreaTotalControlVideo ref="videoRef" :paId="paId" @ws-data="onWsData" @show-custom-inout="showCustomCarInout" />
            </el-tab-pane>
            <el-tab-pane label="在场车辆" name="parkingAreaControlInCar">
              <ParkingAreaControlInCar ref="parkingAreaControlInCar" :paId="paId" @show-custom-inout="showCustomCarInout" />
            </el-tab-pane>
            <el-tab-pane label="出入场明细" name="parkingAreaControlCarInouts">
              <ParkingAreaControlCarInouts ref="parkingAreaControlCarInouts" :paId="paId" @show-custom-inout="showCustomCarInout" />
            </el-tab-pane>
            <el-tab-pane label="收费明细" name="parkingAreaControlPayment">
              <ParkingAreaControlPayment ref="parkingAreaControlPayment" :paId="paId" />
            </el-tab-pane>
            <el-tab-pane label="月租车" name="parkingAreaControlOwnerCar">
              <ParkingAreaControlOwnerCar ref="parkingAreaControlOwnerCar" :paId="paId" />
            </el-tab-pane>
            <el-tab-pane label="临时车" name="parkingAreaControlTempCar">
              <ParkingAreaControlTempCar ref="parkingAreaControlTempCar" :paId="paId" />
            </el-tab-pane>
            <el-tab-pane label="白名单" name="parkingAreaControlWhiteCar">
              <ParkingAreaControlWhiteCar ref="parkingAreaControlWhiteCar" :paId="paId" />
            </el-tab-pane>
            <el-tab-pane label="黑名单" name="parkingAreaControlBlackCar">
              <ParkingAreaControlBlackCar ref="parkingAreaControlBlackCar" :paId="paId" />
            </el-tab-pane>
            <el-tab-pane label="剩余车位" name="parkingAreaControlRemaining">
              <ParkingAreaControlRemaining ref="parkingAreaControlRemaining" :paId="paId" />
            </el-tab-pane>
            <el-tab-pane label="收费统计" name="parkingAreaControlPaymentSummary">
              <ParkingAreaControlPaymentSummary ref="parkingAreaControlPaymentSummary" :paId="paId" />
            </el-tab-pane>
            <el-tab-pane label="开闸日志" name="parkingAreaControlManualOpenDoorLog">
              <ParkingAreaControlManualOpenDoorLog ref="parkingAreaControlManualOpenDoorLog" :paId="paId" />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
      <el-col :span="6">
        <ParkingAreaTotalControlFee ref="feeRef" :paId="paId" @show-qrcode="showBarrierGateQrCode" @show-custom-inout="showCustomCarInout" />
      </el-col>
    </el-row>
    <CustomCarInout ref="customCarInoutRef" @success="onCustomCarInoutSuccess" />
    <BarrierGateQrCode ref="barrierGateQrCodeRef" />
  </div>
</template>

<script>
import ParkingAreaTotalControlVideo from '@/components/car/ParkingAreaTotalControlVideo.vue'
import ParkingAreaTotalControlFee from '@/components/car/ParkingAreaTotalControlFee.vue'
import ParkingAreaControlInCar from '@/components/car/ParkingAreaControlInCar.vue'
import ParkingAreaControlCarInouts from '@/components/car/ParkingAreaControlCarInouts.vue'
import ParkingAreaControlPayment from '@/components/car/ParkingAreaControlPayment.vue'
import ParkingAreaControlOwnerCar from '@/components/car/ParkingAreaControlOwnerCar.vue'
import ParkingAreaControlTempCar from '@/components/car/ParkingAreaControlTempCar.vue'
import ParkingAreaControlWhiteCar from '@/components/car/ParkingAreaControlWhiteCar.vue'
import ParkingAreaControlBlackCar from '@/components/car/ParkingAreaControlBlackCar.vue'
import ParkingAreaControlRemaining from '@/components/car/ParkingAreaControlRemaining.vue'
import ParkingAreaControlPaymentSummary from '@/components/car/ParkingAreaControlPaymentSummary.vue'
import ParkingAreaControlManualOpenDoorLog from '@/components/car/ParkingAreaControlManualOpenDoorLog.vue'
import CustomCarInout from '@/components/car/CustomCarInout.vue'
import BarrierGateQrCode from '@/components/car/BarrierGateQrCode.vue'

export default {
  name: 'ParkingAreaTotalControl',
  components: {
    ParkingAreaTotalControlVideo,
    ParkingAreaTotalControlFee,
    ParkingAreaControlInCar,
    ParkingAreaControlCarInouts,
    ParkingAreaControlPayment,
    ParkingAreaControlOwnerCar,
    ParkingAreaControlTempCar,
    ParkingAreaControlWhiteCar,
    ParkingAreaControlBlackCar,
    ParkingAreaControlRemaining,
    ParkingAreaControlPaymentSummary,
    ParkingAreaControlManualOpenDoorLog,
    CustomCarInout,
    BarrierGateQrCode
  },
  data() {
    return {
      paId: '',
      activeTab: 'parkingAreaTotalControlVideo',
      websocket: null,
      wsPingTimer: null,
      wsPingInterval: null,
      clientId: ''
    }
  },
  mounted() {
    this.paId = this.$route.query.paId || ''
    this.clientId = this.generateUUID()
    if (this.paId) {
      this.initWebSocket()
    }
  },
  beforeDestroy() {
    this.releaseWebSocket()
  },
  methods: {
    generateUUID() {
      return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        const r = Math.random() * 16 | 0
        const v = c === 'x' ? r : (r & 0x3 | 0x8)
        return v.toString(16)
      })
    },
    initWebSocket() {
      const protocol = window.location.protocol
      const host = window.location.host
      const wsProtocol = protocol.startsWith('https') ? 'wss' : 'ws'
      const url = `${wsProtocol}://${host}/ws/parkingArea/${this.paId}/${this.clientId}`
      try {
        this.websocket = new WebSocket(url)
        this.websocket.onopen = () => {
          this.startHeartbeat()
        }
        this.websocket.onmessage = (event) => {
          this.resetHeartbeat()
          try {
            const data = JSON.parse(event.data)
            if (this.$refs.videoRef) {
              this.$refs.videoRef.onWsMessage(data)
            }
          } catch (e) {
            // ignore non-JSON
          }
        }
        this.websocket.onclose = () => {
          this.stopHeartbeat()
          setTimeout(() => {
            if (!this._destroyed) {
              this.initWebSocket()
            }
          }, 3000)
        }
        this.websocket.onerror = () => {
          this.websocket && this.websocket.close()
        }
      } catch (e) {
        setTimeout(() => {
          if (!this._destroyed) {
            this.initWebSocket()
          }
        }, 3000)
      }
    },
    startHeartbeat() {
      const that = this
      this.wsPingTimer = new Date().getTime()
      this.wsPingInterval = setInterval(function () {
        if (that.websocket && that.websocket.readyState === 1) {
          const now = new Date().getTime()
          if (now - that.wsPingTimer > 15000) {
            that.websocket.send("{'cmd':'ping'}")
            that.wsPingTimer = now
          }
        } else {
          that.releaseWebSocket()
          if (!that._destroyed) {
            that.initWebSocket()
          }
        }
      }, 30000)
    },
    resetHeartbeat() {
      this.wsPingTimer = new Date().getTime()
    },
    stopHeartbeat() {
      if (this.wsPingInterval) {
        clearInterval(this.wsPingInterval)
        this.wsPingInterval = null
      }
    },
    releaseWebSocket() {
      this._destroyed = true
      this.stopHeartbeat()
      if (this.websocket) {
        this.websocket.close()
        this.websocket = null
      }
    },
    onWsData(data) {
      if (this.$refs.feeRef) {
        this.$refs.feeRef.onWsNotify(data)
      }
    },
    handleTabClick(tab) {
      this.activeTab = tab.name
    },
    showBarrierGateQrCode(data) {
      if (this.$refs.barrierGateQrCodeRef) {
        this.$refs.barrierGateQrCodeRef.open(data)
      }
    },
    showCustomCarInout(data) {
      if (this.$refs.customCarInoutRef) {
        this.$refs.customCarInoutRef.open(data)
      }
    },
    onCustomCarInoutSuccess() {
      if (this.$refs.videoRef) {
        this.$refs.videoRef.refresh()
      }
    }
  }
}
</script>

<style scoped>
.parking-area-total-control {
  min-height: calc(100vh - 100px);
}

</style>
