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
import ParkingWebSocket from '@/utils/parkingWebSocket'

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
      parkingWs: null
    }
  },
  created() {
    this.paId = this.$route.query.paId || ''
  },
  mounted() {
    if (this.paId) {
      this.parkingWs = new ParkingWebSocket('parkingArea', this.paId, (data) => {
        if (this.$refs.videoRef) {
          this.$refs.videoRef.onWsMessage(data)
        }
        if (this.$refs.feeRef) {
          this.$refs.feeRef.onWsNotify(data)
        }
      })
    }
  },
  beforeDestroy() {
    if (this.parkingWs) {
      this.parkingWs.release()
      this.parkingWs = null
    }
  },
  methods: {
    handleTabClick(tab) {
      this.activeTab = tab.name
    },
    onWsData(data) {
      if (this.$refs.feeRef) {
        this.$refs.feeRef.onWsNotify(data)
      }
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
