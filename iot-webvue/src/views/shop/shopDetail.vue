<template>
  <div class="white-bg border-radius padding-sm">
    <el-row type="flex" justify="space-between">
      <div class="text-title">商铺详情</div>
      <div>
        <el-button type="default" size="small" icon="el-icon-back" @click="$router.back()">返回</el-button>
      </div>
    </el-row>

    <div class="margin-top">
      <el-form label-width="110px">
        <el-row>
          <el-col :span="6">
            <el-form-item label="商铺编号：">
              <div>{{ shop.shopNum || '-' }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="所在位置：">
              <div>{{ shop.locationName || '-' }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="建筑面积：">
              <div>{{ shop.builtUpArea || '-' }}㎡</div>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="使用面积：">
              <div>{{ shop.shopArea || '-' }}㎡</div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-form-item label="租金：">
              <div>{{ shop.shopRent || '0.00' }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="状态：">
              <el-tag :type="stateMap[shop.state] && stateMap[shop.state].type" size="small">
                {{ (stateMap[shop.state] && stateMap[shop.state].name) || shop.state }}
              </el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="业主：">
              <el-link v-if="shop.ownerId" type="primary" :underline="false" @click="goToOwnerDetail">{{ shop.ownerName }}</el-link>
              <span v-else>-</span>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="联系电话：">
              <div>{{ shop.link || '-' }}</div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="6">
            <el-form-item label="租赁起止时间：">
              <div v-if="shop.startTime">{{ shop.startTime }} ~ {{ shop.endTime || '' }}</div>
              <div v-else>-</div>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="建档时间：">
              <div>{{ shop.createTime || '-' }}</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注：">
              <div>{{ shop.remark || '-' }}</div>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="divider-line"></div>

    <div class="margin-top-sm">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="业主" name="ShopDetailOwner" />
        <el-tab-pane label="房屋" name="ShopDetailRoom" />
        <el-tab-pane label="成员" name="ShopDetailMember" />
        <el-tab-pane label="车辆" name="ShopDetailCar" />
        <el-tab-pane label="商铺" name="ShopDetailShop" />
      </el-tabs>
    </div>

    <component
      :is="activeTab"
      :owner-id="shop.ownerId"
      :member-id="shop.memberId"
      :owner-name="shop.ownerName"
      :link="shop.link"
      :current-shop-id="shop.shopId"
    />
  </div>
</template>

<script>
import { listBuildingShops } from '@/api/shop/shopApi'
import { getCurrentCommunity } from '@/utils/auth'
import ShopDetailOwner from '@/views/shop/tabs/ShopDetailOwner.vue'
import ShopDetailRoom from '@/views/shop/tabs/ShopDetailRoom.vue'
import ShopDetailMember from '@/views/shop/tabs/ShopDetailMember.vue'
import ShopDetailCar from '@/views/shop/tabs/ShopDetailCar.vue'
import ShopDetailShop from '@/views/shop/tabs/ShopDetailShop.vue'

export default {
  name: 'ShopDetail',
  components: { ShopDetailOwner, ShopDetailRoom, ShopDetailMember, ShopDetailCar, ShopDetailShop },
  data() {
    return {
      activeTab: 'ShopDetailOwner',
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
          page:1,
          row:1,
          communityId
        })
        this.shop = (res.data || [])[0] || {}
      } catch (e) { this.$message.error(e.message || '查询失败') }
    },
    handleTabClick() {},
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
.white-bg { background-color: #fff; }
.padding-sm { padding: 16px; }
.border-radius { border-radius: 4px; }
.margin-top { margin-top: 16px; }
.margin-top-sm { margin-top: 10px; }
.text-title { font-size: 18px; font-weight: bold; }

.divider-line {
  margin: 20px 0;
  height: 2px;
  width: 100%;
  background: linear-gradient(90deg, rgba(64, 158, 255, 0) 0%, rgba(64, 158, 255, 0.3) 20%, rgba(64, 158, 255, 1) 50%, rgba(64, 158, 255, 0.3) 80%, rgba(64, 158, 255, 0) 100%);
}
</style>
