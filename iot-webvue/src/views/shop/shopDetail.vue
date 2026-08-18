<template>
  <div class="shop-detail">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>商铺详情</span>
        <el-button size="small" @click="$router.back()">返回</el-button>
      </div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="商铺编号">{{ shop.shopNum || '-' }}</el-descriptions-item>
        <el-descriptions-item label="所在位置">{{ shop.locationName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="建筑面积">{{ shop.builtUpArea || '-' }}㎡</el-descriptions-item>
        <el-descriptions-item label="使用面积">{{ shop.shopArea || '-' }}㎡</el-descriptions-item>
        <el-descriptions-item label="租金">{{ shop.shopRent || '0.00' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="stateMap[shop.state] && stateMap[shop.state].type" size="small">
            {{ (stateMap[shop.state] && stateMap[shop.state].name) || shop.state }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="业主">
          <el-link v-if="shop.ownerId" type="primary" :underline="false" @click="goToOwnerDetail">{{ shop.ownerName }}</el-link>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ shop.link || '-' }}</el-descriptions-item>
        <el-descriptions-item label="租赁起止时间">
          <span v-if="shop.startTime">{{ shop.startTime }} ~ {{ shop.endTime || '' }}</span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="建档时间">{{ shop.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ shop.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card shadow="never" class="tab-card">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="业主" name="shopDetailOwner"></el-tab-pane>
        <el-tab-pane label="房屋" name="shopDetailRoom"></el-tab-pane>
        <el-tab-pane label="成员" name="shopDetailMember"></el-tab-pane>
        <el-tab-pane label="车辆" name="shopDetailCar"></el-tab-pane>
        <el-tab-pane label="商铺" name="shopDetailShop"></el-tab-pane>
      </el-tabs>

      <ShopDetailOwner v-show="activeTab === 'shopDetailOwner'" ref="shopDetailOwner" />
      <ShopDetailRoom v-show="activeTab === 'shopDetailRoom'" ref="shopDetailRoom" />
      <ShopDetailMember v-show="activeTab === 'shopDetailMember'" ref="shopDetailMember" />
      <ShopDetailCar v-show="activeTab === 'shopDetailCar'" ref="shopDetailCar" />
      <ShopDetailShop v-show="activeTab === 'shopDetailShop'" ref="shopDetailShop" />
    </el-card>
  </div>
</template>

<script>
import { listBuildingShops } from '@/api/shop/shopApi'
import { getCurrentCommunity } from '@/utils/auth'
import ShopDetailOwner from '@/components/shop/ShopDetailOwner.vue'
import ShopDetailRoom from '@/components/shop/ShopDetailRoom.vue'
import ShopDetailMember from '@/components/shop/ShopDetailMember.vue'
import ShopDetailCar from '@/components/shop/ShopDetailCar.vue'
import ShopDetailShop from '@/components/shop/ShopDetailShop.vue'

export default {
  name: 'ShopDetail',
  components: { ShopDetailOwner, ShopDetailRoom, ShopDetailMember, ShopDetailCar, ShopDetailShop },
  data() {
    return {
      activeTab: 'shopDetailOwner',
      shop: {},
      stateMap: {
        '2006': { name: '已出租', type: 'warning' },
        '2007': { name: '已出售', type: 'info' },
        '2008': { name: '空置', type: 'success' }
      }
    }
  },
  mounted() {
    this.fetchShop()
  },
  methods: {
    async fetchShop() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listBuildingShops({
          shopId: this.$route.query.shopId,
          communityId
        })
        this.shop = (res.data || [])[0] || {}
        this.changeTab(this.activeTab)
      } catch (e) { this.$message.error(e.message || '查询失败') }
    },
    handleTabClick(tab) {
      this.changeTab(tab.name)
    },
    changeTab(tabName) {
      const ownerId = this.shop.ownerId || ''
      if (tabName === 'shopDetailOwner' && this.$refs.shopDetailOwner) {
        this.$refs.shopDetailOwner.open(ownerId)
      } else if (tabName === 'shopDetailRoom' && this.$refs.shopDetailRoom) {
        this.$refs.shopDetailRoom.open(ownerId)
      } else if (tabName === 'shopDetailMember' && this.$refs.shopDetailMember) {
        this.$refs.shopDetailMember.open(ownerId)
      } else if (tabName === 'shopDetailCar' && this.$refs.shopDetailCar) {
        this.$refs.shopDetailCar.open(ownerId)
      } else if (tabName === 'shopDetailShop' && this.$refs.shopDetailShop) {
        this.$refs.shopDetailShop.open(ownerId, this.shop.shopId || '')
      }
    },
    goToOwnerDetail() {
      this.$router.push({
        path: '/pages/owner/ownerDetail',
        query: { ownerId: this.shop.ownerId }
      })
    }
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.tab-card {
  margin-top: 16px;
}
</style>
