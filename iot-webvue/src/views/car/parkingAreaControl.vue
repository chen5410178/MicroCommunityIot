<template>
  <div class="parking-area-control">
    <el-row :gutter="16">
      <el-col :span="18">
        <ParkingAreaControlVideo ref="videoRef" :boxId="boxId" :paId="paId" @ws-data="onVideoWsData" @show-custom-inout="showCustomCarInout" @show-qrcode="showBarrierGateQrCode" @unlicensed-car-in="handleUnlicensedCarIn" @changeMachine="onChangeMachine" />
        <div class="vc-line-primary"></div>
        <el-tabs v-model="activeTab" @tab-click="handleTabClick">
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
            <el-tab-pane label="临时车审核" name="parkingAreaControlTempCarAuths">
              <ParkingAreaControlTempCarAuths ref="parkingAreaControlTempCarAuths" :boxId="boxId" :paId="paId" @show-temp-car-auth="showTempCarAuth" />
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
      </el-col>
      <el-col :span="6">
        <ParkingAreaControlFee ref="feeRef" :boxId="boxId" :paId="paId" @show-qrcode="showBarrierGateQrCode" @show-custom-inout="showCustomCarInout" />
      </el-col>
    </el-row>
    <CustomCarInout ref="customCarInoutRef" @success="onCustomCarInoutSuccess" />
    <BarrierGateQrCode ref="barrierGateQrCodeRef" />
    <UnlicensedCarQrCode ref="unlicensedCarQrCodeRef" />
    <ParkingAreaControlTempCarAuthConfirm ref="tempCarAuthConfirmRef" @success="onTempCarAuthSuccess" />
  </div>
</template>

<script>
import ParkingAreaControlVideo from '@/components/car/ParkingAreaControlVideo.vue'
import ParkingAreaControlFee from '@/components/car/ParkingAreaControlFee.vue'
import ParkingAreaControlInCar from '@/components/car/ParkingAreaControlInCar.vue'
import ParkingAreaControlCarInouts from '@/components/car/ParkingAreaControlCarInouts.vue'
import ParkingAreaControlPayment from '@/components/car/ParkingAreaControlPayment.vue'
import ParkingAreaControlOwnerCar from '@/components/car/ParkingAreaControlOwnerCar.vue'
import ParkingAreaControlTempCar from '@/components/car/ParkingAreaControlTempCar.vue'
import ParkingAreaControlTempCarAuths from '@/components/car/ParkingAreaControlTempCarAuths.vue'
import ParkingAreaControlTempCarAuthConfirm from '@/components/car/ParkingAreaControlTempCarAuthConfirm.vue'
import ParkingAreaControlWhiteCar from '@/components/car/ParkingAreaControlWhiteCar.vue'
import ParkingAreaControlBlackCar from '@/components/car/ParkingAreaControlBlackCar.vue'
import ParkingAreaControlRemaining from '@/components/car/ParkingAreaControlRemaining.vue'
import ParkingAreaControlPaymentSummary from '@/components/car/ParkingAreaControlPaymentSummary.vue'
import ParkingAreaControlManualOpenDoorLog from '@/components/car/ParkingAreaControlManualOpenDoorLog.vue'
import CustomCarInout from '@/components/car/CustomCarInout.vue'
import BarrierGateQrCode from '@/components/car/BarrierGateQrCode.vue'
import UnlicensedCarQrCode from '@/components/car/UnlicensedCarQrCode.vue'
import ParkingWebSocket from '@/utils/parkingWebSocket'

export default {
  name: 'ParkingAreaControl',
  components: {
    ParkingAreaControlVideo,
    ParkingAreaControlFee,
    ParkingAreaControlInCar,
    ParkingAreaControlCarInouts,
    ParkingAreaControlPayment,
    ParkingAreaControlOwnerCar,
    ParkingAreaControlTempCar,
    ParkingAreaControlTempCarAuths,
    ParkingAreaControlTempCarAuthConfirm,
    ParkingAreaControlWhiteCar,
    ParkingAreaControlBlackCar,
    ParkingAreaControlRemaining,
    ParkingAreaControlPaymentSummary,
    ParkingAreaControlManualOpenDoorLog,
    CustomCarInout,
    BarrierGateQrCode,
    UnlicensedCarQrCode
  },
  data() {
    return {
      boxId: '',
      paId: '',
      activeTab: 'parkingAreaControlCarInouts',
      parkingWs: null
    }
  },
  mounted() {
    this.boxId = this.$route.query.boxId || ''
    this.paId = this.$route.query.paId || ''
    if (this.boxId) {
      this.parkingWs = new ParkingWebSocket(this.boxId, this.onWsData)
    }
  },
  beforeDestroy() {
    if (this.parkingWs) {
      this.parkingWs.release()
      this.parkingWs = null
    }
  },
  methods: {
    onWsData(data) {
      if (this.$refs.videoRef) {
        this.$refs.videoRef.onWsMessage(data)
      }
      if (this.$refs.feeRef) {
        this.$refs.feeRef.onWsNotify(data)
      }
      if (this.$refs.tempCarAuthConfirmRef) {
        this.$refs.tempCarAuthConfirmRef.onWsNotify(data)
      }
    },
    handleTabClick(tab) {
      this.activeTab = tab.name
      const ref = this.$refs[tab.name]
      if (ref && typeof ref.loadData === 'function') {
        ref.loadData()
      }
    },
    onVideoWsData(data) {
      if (this.$refs.feeRef) {
        this.$refs.feeRef.onWsNotify(data._data || data)
      }
    },
    onChangeMachine(data) {
      if (this.$refs.feeRef && data.outMachineId) {
        this.$refs.feeRef.changeMachine(data)
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
    showTempCarAuth(car) {
      if (this.$refs.tempCarAuthConfirmRef) {
        this.$refs.tempCarAuthConfirmRef.open(car)
      }
    },
    handleUnlicensedCarIn(data) {
      if (this.$refs.unlicensedCarQrCodeRef) {
        this.$refs.unlicensedCarQrCodeRef.open(data)
      }
    },
    onCustomCarInoutSuccess() {
      if (this.$refs.videoRef) {
        this.$refs.videoRef.refresh()
      }
    },
    onTempCarAuthSuccess() {
      if (this.$refs.parkingAreaControlTempCarAuths) {
        this.$refs.parkingAreaControlTempCarAuths.loadData()
      }
    }
  }
}
</script>

<style scoped>
.parking-area-control {
  min-height: calc(100vh - 100px);
  background: #fff;
  padding: 16px;
}
.vc-line-primary {
  height: 1px;
  background-color: #409EFF;
  margin: 12px 0;
}
</style>
