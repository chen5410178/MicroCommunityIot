<template>
  <div class="parking-area-control">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>岗亭控制台</span>
        <div>
          <el-tag type="info" style="margin-right: 8px">岗亭：{{ boxId }}</el-tag>
          <el-tag type="info">停车场：{{ paId }}</el-tag>
        </div>
      </div>
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="出入场" name="parkingAreaControlCarInout">
          <div class="placeholder-tab">功能开发中</div>
        </el-tab-pane>
        <el-tab-pane label="在场车辆" name="parkingAreaControlInCar">
          <div class="placeholder-tab">功能开发中</div>
        </el-tab-pane>
        <el-tab-pane label="出入场明细" name="parkingAreaControlCarInouts">
          <div class="placeholder-tab">功能开发中</div>
        </el-tab-pane>
        <el-tab-pane label="收费明细" name="parkingAreaControlPayment">
          <div class="placeholder-tab">功能开发中</div>
        </el-tab-pane>
        <el-tab-pane label="月租车" name="parkingAreaControlOwnerCar">
          <div class="placeholder-tab">功能开发中</div>
        </el-tab-pane>
        <el-tab-pane label="临时车" name="parkingAreaControlTempCar">
          <div class="placeholder-tab">功能开发中</div>
        </el-tab-pane>
        <el-tab-pane label="临时车审核" name="parkingAreaControlTempCarAuths">
          <div class="placeholder-tab">功能开发中</div>
        </el-tab-pane>
        <el-tab-pane label="白名单" name="parkingAreaControlWhiteCar">
          <div class="placeholder-tab">功能开发中</div>
        </el-tab-pane>
        <el-tab-pane label="黑名单" name="parkingAreaControlBlackCar">
          <div class="placeholder-tab">功能开发中</div>
        </el-tab-pane>
        <el-tab-pane label="剩余车位" name="parkingAreaControlRemaining">
          <div class="placeholder-tab">功能开发中</div>
        </el-tab-pane>
        <el-tab-pane label="收费统计" name="parkingAreaControlPaymentSummary">
          <div class="placeholder-tab">功能开发中</div>
        </el-tab-pane>
        <el-tab-pane label="开闸日志" name="parkingAreaControlManualOpenDoorLog">
          <div class="placeholder-tab">功能开发中</div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'ParkingAreaControl',
  data() {
    return {
      boxId: '',
      paId: '',
      activeTab: 'parkingAreaControlCarInout'
    }
  },
  mounted() {
    this.boxId = this.$route.query.boxId || ''
    this.paId = this.$route.query.paId || ''
  },
  methods: {
    handleTabClick(tab) {
      this.activeTab = tab.name
    }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.placeholder-tab { text-align: center; padding: 60px 0; color: #909399; font-size: 16px; }
</style>
