<template>
  <div class="room-detail">
    <el-card shadow="never" class="header-card">
      <div slot="header" class="card-header">
        <span>房屋详情</span>
        <el-button size="small" @click="$router.go(-1)">返回</el-button>
      </div>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="房屋">{{ roomInfo.floorNum }}-{{ roomInfo.unitNum }}-{{ roomInfo.roomNum }}</el-descriptions-item>
        <el-descriptions-item label="楼层">{{ roomInfo.layer }}层</el-descriptions-item>
        <el-descriptions-item label="类型">{{ roomInfo.roomSubTypeName }}</el-descriptions-item>
        <el-descriptions-item label="建筑面积">{{ roomInfo.builtUpArea }}㎡</el-descriptions-item>
        <el-descriptions-item label="室内面积">{{ roomInfo.roomArea }}㎡</el-descriptions-item>
        <el-descriptions-item label="状态">{{ roomInfo.stateName }}</el-descriptions-item>
        <el-descriptions-item label="入住时间">{{ roomInfo.startTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="业主">{{ roomInfo.ownerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ roomInfo.link || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
    <el-card shadow="never" class="tab-card">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="人员" name="roomDetailOwner">
          <RoomDetailOwner :room-id="roomId" />
        </el-tab-pane>
        <el-tab-pane label="车辆" name="roomDetailCar">
          <RoomDetailCar :room-id="roomId" />
        </el-tab-pane>
        <el-tab-pane label="授权门禁" name="roomDetailAccessControl">
          <RoomDetailAccessControl :room-id="roomId" />
        </el-tab-pane>
        <el-tab-pane label="人脸下发" name="roomDetailAccessControlLog">
          <RoomDetailAccessControlLog :room-id="roomId" />
        </el-tab-pane>
        <el-tab-pane label="门禁开门" name="roomDetailAccessControlInout">
          <RoomDetailAccessControlInout :room-id="roomId" />
        </el-tab-pane>
        <el-tab-pane label="水电表" name="roomDetailMeter">
          <RoomDetailMeter :room-id="roomId" />
        </el-tab-pane>
        <el-tab-pane label="水电充值" name="roomDetailMeterRecharge">
          <RoomDetailMeterRecharge :room-id="roomId" />
        </el-tab-pane>
        <el-tab-pane label="门锁" name="roomDetailLock">
          <RoomDetailLock :room-id="roomId" />
        </el-tab-pane>
        <el-tab-pane label="门锁授权" name="roomDetailLockPerson">
          <RoomDetailLockPerson :room-id="roomId" />
        </el-tab-pane>
        <el-tab-pane label="门锁下发" name="roomDetailLockLog">
          <RoomDetailLockLog :room-id="roomId" />
        </el-tab-pane>
        <el-tab-pane label="门锁进出" name="roomDetailLockInout">
          <RoomDetailLockInout :room-id="roomId" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { queryRooms } from '@/api/property/propertyApi'
import RoomDetailOwner from '@/components/property/RoomDetailOwner.vue'
import RoomDetailCar from '@/components/property/RoomDetailCar.vue'
import RoomDetailAccessControl from '@/components/property/RoomDetailAccessControl.vue'
import RoomDetailAccessControlLog from '@/components/property/RoomDetailAccessControlLog.vue'
import RoomDetailAccessControlInout from '@/components/property/RoomDetailAccessControlInout.vue'
import RoomDetailMeter from '@/components/property/RoomDetailMeter.vue'
import RoomDetailMeterRecharge from '@/components/property/RoomDetailMeterRecharge.vue'
import RoomDetailLock from '@/components/property/RoomDetailLock.vue'
import RoomDetailLockPerson from '@/components/property/RoomDetailLockPerson.vue'
import RoomDetailLockLog from '@/components/property/RoomDetailLockLog.vue'
import RoomDetailLockInout from '@/components/property/RoomDetailLockInout.vue'

export default {
  name: 'RoomDetail',
  components: {
    RoomDetailOwner, RoomDetailCar,
    RoomDetailAccessControl, RoomDetailAccessControlLog, RoomDetailAccessControlInout,
    RoomDetailMeter, RoomDetailMeterRecharge,
    RoomDetailLock, RoomDetailLockPerson, RoomDetailLockLog, RoomDetailLockInout
  },
  data() {
    return {
      roomId: '',
      activeTab: 'roomDetailOwner',
      roomInfo: {}
    }
  },
  mounted() {
    this.roomId = this.$route.query.roomId || ''
    if (this.roomId) this.loadRoomInfo()
  },
  methods: {
    async loadRoomInfo() {
      try {
        const communityInfo = this.$store.state.app.currentCommunity || {}
        const res = await queryRooms({ communityId: communityInfo.communityId, roomId: this.roomId, page: 1, row: 1 })
        if (res.data && res.data.length) {
          this.roomInfo = res.data[0]
        }
      } catch (e) { /* ignore */ }
    }
  }
}
</script>

<style scoped>
.room-detail {
  padding: 16px;
}
.header-card {
  margin-bottom: 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.tab-card {
  margin-bottom: 0;
}
</style>
