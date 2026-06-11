<template>
  <div class="white-bg border-radius padding-sm">
    <el-row type="flex" justify="space-between">
      <div class="text-title">人员信息</div>
      <div>
        <el-button type="default" size="small" icon="el-icon-back" @click="$router.back()">返回</el-button>
      </div>
    </el-row>

    <div class="margin-top">
      <el-row style="min-height: 160px;">
        <el-col :span="3">
          <div class="text-center" style="width: 150px;" v-if="info.faceUrl">
            <img width="120px" height="140px" class="border-radius" :src="info.faceUrl" @error="errorLoadImg" />
          </div>
          <div class="text-center" style="width: 150px;" v-else>
            <img width="120px" height="140px" class="border-radius" src="/img/noPhoto.jpg" />
          </div>
        </el-col>
        <el-col :span="21" class="text-left">
          <el-form label-width="110px">
            <el-row>
              <el-col :span="6">
                <el-form-item label="人员ID：">
                  <div>{{ info.memberId }}</div>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="名称：">
                  <div>{{ info.name }}</div>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="电话：">
                  <div>{{ info.link }}</div>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="性别：">
                  <div>{{ info.sex == '0' ? '男' : '女' }}</div>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="6">
                <el-form-item label="身份证：">
                  <div>{{ info.idCard || '-' }}</div>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="家庭住址：">
                  <div>{{ info.address || '-' }}</div>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="人员类型：">
                  <div>{{ info.ownerTypeName || '-' }}</div>
                </el-form-item>
              </el-col>
            </el-row>
            <div v-for="(item, index) in info.ownerAttrDtos" :key="item.attrId">
              <el-row v-if="index % 4 == 0">
                <el-col :span="6">
                  <el-form-item :label="item.specName + '：'">
                    <div>{{ item.valueName || '-' }}</div>
                  </el-form-item>
                </el-col>
                <el-col :span="6" v-if="index < info.ownerAttrDtos.length - 1">
                  <el-form-item :label="info.ownerAttrDtos[index + 1].specName + '：'">
                    <div>{{ info.ownerAttrDtos[index + 1].valueName || '-' }}</div>
                  </el-form-item>
                </el-col>
                <el-col :span="6" v-if="index < info.ownerAttrDtos.length - 2">
                  <el-form-item :label="info.ownerAttrDtos[index + 2].specName + '：'">
                    <div>{{ info.ownerAttrDtos[index + 2].valueName || '-' }}</div>
                  </el-form-item>
                </el-col>
                <el-col :span="6" v-if="index < info.ownerAttrDtos.length - 3">
                  <el-form-item :label="info.ownerAttrDtos[index + 3].specName + '：'">
                    <div>{{ info.ownerAttrDtos[index + 3].valueName || '-' }}</div>
                  </el-form-item>
                </el-col>
              </el-row>
            </div>
          </el-form>
        </el-col>
      </el-row>
    </div>

    <div class="divider-line"></div>

    <div class="margin-top-sm">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="房屋" name="OwnerDetailRoom" />
        <el-tab-pane label="家庭人员" name="OwnerDetailMember" />
        <el-tab-pane label="车辆" name="OwnerDetailCar" />
        <el-tab-pane label="授权门禁" name="OwnerDetailAccessControlFace" />
        <el-tab-pane label="人脸下发" name="OwnerDetailAccessControlLog" />
        <el-tab-pane label="门禁开门" name="OwnerDetailAccessControlInout" />
        <el-tab-pane label="门锁授权" name="OwnerDetailLockPerson" />
        <el-tab-pane label="门锁下发" name="OwnerDetailLockLog" />
        <el-tab-pane label="门锁进出" name="OwnerDetailLockInout" />
        <el-tab-pane label="充电订单" name="OwnerDetailChargeOrder" />
      </el-tabs>
    </div>

    <component
      :is="activeTab"
      :owner-id="info.ownerId"
      :member-id="info.memberId"
      :owner-name="info.name"
      :link="info.link"
    />
  </div>
</template>

<script>
import { queryOwners } from '@/api/property/ownerApi'
import { getCurrentCommunity } from '@/utils/auth'
import OwnerDetailRoom from '@/views/owner/tabs/OwnerDetailRoom.vue'
import OwnerDetailMember from '@/views/owner/tabs/OwnerDetailMember.vue'
import OwnerDetailCar from '@/views/owner/tabs/OwnerDetailCar.vue'
import OwnerDetailAccessControlFace from '@/views/owner/tabs/OwnerDetailAccessControlFace.vue'
import OwnerDetailAccessControlLog from '@/views/owner/tabs/OwnerDetailAccessControlLog.vue'
import OwnerDetailAccessControlInout from '@/views/owner/tabs/OwnerDetailAccessControlInout.vue'
import OwnerDetailLockPerson from '@/views/owner/tabs/OwnerDetailLockPerson.vue'
import OwnerDetailLockLog from '@/views/owner/tabs/OwnerDetailLockLog.vue'
import OwnerDetailLockInout from '@/views/owner/tabs/OwnerDetailLockInout.vue'
import OwnerDetailChargeOrder from '@/views/owner/tabs/OwnerDetailChargeOrder.vue'

export default {
  name: 'OwnerDetail',
  components: {
    OwnerDetailRoom,
    OwnerDetailMember,
    OwnerDetailCar,
    OwnerDetailAccessControlFace,
    OwnerDetailAccessControlLog,
    OwnerDetailAccessControlInout,
    OwnerDetailLockPerson,
    OwnerDetailLockLog,
    OwnerDetailLockInout,
    OwnerDetailChargeOrder
  },
  data() {
    return {
      info: {
        memberId: '',
        ownerId: '',
        name: '',
        link: '',
        sex: '',
        idCard: '',
        address: '',
        ownerTypeName: '',
        faceUrl: '',
        ownerAttrDtos: []
      },
      activeTab: 'OwnerDetailRoom'
    }
  },
  mounted() {
    this.info.memberId = this.$route.query.memberId || ''
    this.info.ownerId = this.$route.query.ownerId || ''
    if (this.info.memberId) {
      this.loadOwnerInfo()
    }
  },
  methods: {
    async loadOwnerInfo() {
      try {
        const community = getCurrentCommunity()
        const res = await queryOwners({ memberId: this.info.memberId, page: 1, row: 1, communityId: community ? community.communityId : '' })
        const list = res.data || []
        if (list.length > 0) {
          const data = list[0]
          Object.assign(this.info, data)
          this.info.faceUrl = data.faceUrl || data.url || ''
          this.info.ownerAttrDtos = data.ownerAttrDtos || []
        }
      } catch (e) { /* ignore */ }
    },
    handleTabClick() {},
    errorLoadImg() {
      this.info.faceUrl = ''
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
.text-center { text-align: center; }
.text-left { text-align: left; }

.divider-line {
  margin: 20px 0;
  height: 2px;
  width: 100%;
  background: linear-gradient(90deg, rgba(64, 158, 255, 0) 0%, rgba(64, 158, 255, 0.3) 20%, rgba(64, 158, 255, 1) 50%, rgba(64, 158, 255, 0.3) 80%, rgba(64, 158, 255, 0) 100%);
}
</style>
