<template>
  <div class="owner-detail">
    <div class="header-bar">
      <h3>人员信息</h3>
      <el-button type="default" size="small" icon="el-icon-back" @click="$router.back()">返回</el-button>
    </div>
    <el-card shadow="never" class="info-card">
      <el-row>
        <el-col :span="4" style="text-align: center;">
          <img v-if="info.url" :src="info.url" style="width: 120px; height: 140px; border-radius: 4px;" @error="info.url = ''" />
          <img v-else src="/img/noPhoto.jpg" style="width: 120px; height: 140px; border-radius: 4px;" />
        </el-col>
        <el-col :span="20">
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="info-item"><label>人员ID：</label><span>{{ info.memberId }}</span></div>
            </el-col>
            <el-col :span="6">
              <div class="info-item"><label>名称：</label><span>{{ info.name }}</span></div>
            </el-col>
            <el-col :span="6">
              <div class="info-item"><label>电话：</label><span>{{ info.link }}</span></div>
            </el-col>
            <el-col :span="6">
              <div class="info-item"><label>性别：</label><span>{{ info.sex == '0' ? '男' : '女' }}</span></div>
            </el-col>
          </el-row>
          <el-row :gutter="20" style="margin-top: 12px;">
            <el-col :span="6">
              <div class="info-item"><label>身份证：</label><span>{{ info.idCard || '-' }}</span></div>
            </el-col>
            <el-col :span="6">
              <div class="info-item"><label>家庭住址：</label><span>{{ info.address || '-' }}</span></div>
            </el-col>
            <el-col :span="6">
              <div class="info-item"><label>人员类型：</label><span>{{ info.ownerTypeName || '-' }}</span></div>
            </el-col>
          </el-row>
          <el-row :gutter="20" style="margin-top: 12px;" v-if="info.ownerAttrDtos && info.ownerAttrDtos.length > 0">
            <el-col :span="6" v-for="attr in info.ownerAttrDtos" :key="attr.attrId">
              <div class="info-item"><label>{{ attr.specName }}：</label><span>{{ attr.valueName || '-' }}</span></div>
            </el-col>
          </el-row>
        </el-col>
      </el-row>
    </el-card>
    <el-tabs v-model="activeTab" class="tab-card" @tab-click="handleTabClick">
      <el-tab-pane label="房屋" name="ownerDetailRoom" />
      <el-tab-pane label="家庭人员" name="ownerDetailMember" />
      <el-tab-pane label="车辆" name="ownerDetailCar" />
      <el-tab-pane label="授权门禁" name="ownerDetailAccessControlFace" />
      <el-tab-pane label="人脸下发" name="ownerDetailAccessControlLog" />
      <el-tab-pane label="门禁开门" name="ownerDetailAccessControlInout" />
      <el-tab-pane label="门锁授权" name="ownerDetailLockPerson" />
      <el-tab-pane label="门锁下发" name="ownerDetailLockLog" />
      <el-tab-pane label="门锁进出" name="ownerDetailLockInout" />
      <el-tab-pane label="充电订单" name="ownerDetailChargeOrder" />
    </el-tabs>
    <div v-if="activeTab === 'ownerDetailRoom'" class="tab-content">
      <OwnerDetailRoom :owner-id="info.ownerId" :member-id="info.memberId" :owner-name="info.name" :link="info.link" />
    </div>
    <div v-else class="tab-content">
      <el-empty description="该功能暂未实现，敬请期待" />
    </div>
  </div>
</template>

<script>
import { queryOwners } from '@/api/property/ownerApi'
import { getCurrentCommunity } from '@/utils/auth'
import OwnerDetailRoom from '@/views/owner/tabs/OwnerDetailRoom.vue'

export default {
  name: 'OwnerDetail',
  components: { OwnerDetailRoom },
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
        url: '',
        ownerAttrDtos: []
      },
      activeTab: 'ownerDetailRoom'
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
          Object.assign(this.info, list[0])
        }
      } catch (e) { /* ignore */ }
    },
    handleTabClick() {}
  }
}
</script>

<style scoped>
.header-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.header-bar h3 { margin: 0; font-size: 16px; }
.info-card { margin-bottom: 16px; }
.info-item { line-height: 32px; }
.info-item label { color: #909399; }
.tab-card { margin-bottom: 16px; }
.tab-content { min-height: 200px; }
</style>
