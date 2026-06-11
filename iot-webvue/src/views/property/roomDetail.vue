<template>
  <div class="white-bg border-radius padding-sm">
    <el-row type="flex" justify="space-between">
      <div class="text-title">房屋详情</div>
      <div>
        <el-button type="default" size="small" icon="el-icon-back" @click="$router.back()">返回</el-button>
      </div>
    </el-row>

    <div class="margin-top">
      <el-row style="min-height: 100px;">
        <el-col :span="24" class="text-left">
          <el-form label-width="110px">
            <el-row>
              <el-col :span="6">
                <el-form-item label="房屋：">
                  <div>{{ roomInfo.floorNum }}-{{ roomInfo.unitNum }}-{{ roomInfo.roomNum }}</div>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="楼层：">
                  <div>{{ roomInfo.layer }}层</div>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="类型：">
                  <div>{{ roomInfo.roomSubTypeName }}</div>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="状态：">
                  <div>{{ roomInfo.stateName }}</div>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="6">
                <el-form-item label="建筑面积：">
                  <div>{{ roomInfo.builtUpArea }}㎡</div>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="室内面积：">
                  <div>{{ roomInfo.roomArea }}㎡</div>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="入住时间：">
                  <div>{{ roomInfo.startTime || '-' }}</div>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="业主：">
                  <div>{{ roomInfo.ownerName || '-' }}</div>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="6">
                <el-form-item label="联系方式：">
                  <div>{{ roomInfo.link || '-' }}</div>
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-col>
      </el-row>
    </div>

    <div class="divider-line"></div>

    <div class="margin-top-sm">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="人员" name="roomDetailOwner" />
        <el-tab-pane label="车辆" name="roomDetailCar" />
        <el-tab-pane label="授权门禁" name="roomDetailAccessControl" />
        <el-tab-pane label="人脸下发" name="roomDetailAccessControlLog" />
        <el-tab-pane label="门禁开门" name="roomDetailAccessControlInout" />
        <el-tab-pane label="水电表" name="roomDetailMeter" />
        <el-tab-pane label="水电充值" name="roomDetailMeterRecharge" />
        <el-tab-pane label="门锁" name="roomDetailLock" />
        <el-tab-pane label="门锁授权" name="roomDetailLockPerson" />
        <el-tab-pane label="门锁下发" name="roomDetailLockLog" />
        <el-tab-pane label="门锁进出" name="roomDetailLockInout" />
      </el-tabs>
    </div>

    <div class="tab-content">
      <RoomDetailOwner v-if="activeTab === 'roomDetailOwner'" :room-id="roomId" />
      <RoomDetailCar v-else-if="activeTab === 'roomDetailCar'" :room-id="roomId" />
      <RoomDetailAccessControl v-else-if="activeTab === 'roomDetailAccessControl'" :room-id="roomId" />
      <RoomDetailAccessControlLog v-else-if="activeTab === 'roomDetailAccessControlLog'" :room-id="roomId" />
      <RoomDetailAccessControlInout v-else-if="activeTab === 'roomDetailAccessControlInout'" :room-id="roomId" />
      <RoomDetailMeter v-else-if="activeTab === 'roomDetailMeter'" :room-id="roomId" />
      <RoomDetailMeterRecharge v-else-if="activeTab === 'roomDetailMeterRecharge'" :room-id="roomId" />
      <RoomDetailLock v-else-if="activeTab === 'roomDetailLock'" :room-id="roomId" />
      <RoomDetailLockPerson v-else-if="activeTab === 'roomDetailLockPerson'" :room-id="roomId" />
      <RoomDetailLockLog v-else-if="activeTab === 'roomDetailLockLog'" :room-id="roomId" />
      <RoomDetailLockInout v-else-if="activeTab === 'roomDetailLockInout'" :room-id="roomId" />
    </div>
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
.white-bg { background-color: #fff; }
.padding-sm { padding: 16px; }
.border-radius { border-radius: 4px; }
.margin-top { margin-top: 16px; }
.margin-top-sm { margin-top: 10px; }
.text-title { font-size: 18px; font-weight: bold; }
.text-left { text-align: left; }

.divider-line {
  margin: 20px 0;
  height: 2px;
  width: 100%;
  background: linear-gradient(90deg, rgba(64, 158, 255, 0) 0%, rgba(64, 158, 255, 0.3) 20%, rgba(64, 158, 255, 1) 50%, rgba(64, 158, 255, 0.3) 80%, rgba(64, 158, 255, 0) 100%);
}

.tab-content {
  min-height: 200px;
}
</style>
