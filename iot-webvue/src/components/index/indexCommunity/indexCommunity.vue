<template>
  <div class="index-community-card">
    <div class="index-title">小区信息</div>
    <div class="index-top">
      <div class="index-top-item">
        <div class="index-icon-box"><i class="el-icon-office-building"></i></div>
        <div>
          <div class="index-number">{{ data.floorCount || 0 }}</div>
          <div class="index-label">楼宇</div>
        </div>
      </div>
      <div class="index-top-item">
        <div class="index-icon-box"><i class="el-icon-s-home"></i></div>
        <div>
          <div class="index-number">{{ data.roomCount || 0 }}</div>
          <div class="index-label">房屋</div>
        </div>
      </div>
      <div class="index-top-item">
        <div class="index-icon-box"><i class="el-icon-shopping-bag-2"></i></div>
        <div>
          <div class="index-number">{{ data.shopCount || 0 }}</div>
          <div class="index-label">商铺</div>
        </div>
      </div>
      <div class="index-top-item">
        <div class="index-icon-box"><i class="el-icon-truck"></i></div>
        <div>
          <div class="index-number">{{ data.spaceCount || 0 }}</div>
          <div class="index-label">车位</div>
        </div>
      </div>
    </div>
    <div class="index-bottom">
      <div class="index-bottom-item">
        <div class="index-bottom-left">
          <div class="index-name">住户登记</div>
          <div class="index-name-icon"><i class="el-icon-user"></i></div>
        </div>
        <div class="index-bottom-right">
          <div class="index-number">{{ data.ownerCount || 0 }}<span class="index-unit">个</span></div>
          <div class="index-link" @click="toOwner">登记住户&gt;</div>
        </div>
      </div>
      <div class="index-bottom-item">
        <div class="index-bottom-left">
          <div class="index-name">车辆</div>
          <div class="index-name-icon"><i class="el-icon-truck"></i></div>
        </div>
        <div class="index-bottom-right">
          <div class="index-number">{{ data.carCount || 0 }}<span class="index-unit">个</span></div>
          <div class="index-link" @click="toCar">查看车辆&gt;</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { queryPropertyAssetsIndex } from '@/api/index/indexApi'

export default {
  name: 'IndexCommunity',
  props: {
    communityId: { type: String, default: '' }
  },
  data() {
    return {
      data: {}
    }
  },
  watch: {
    communityId: {
      immediate: true,
      handler(val) {
        if (val) this.loadData()
      }
    }
  },
  methods: {
    loadData() {
      queryPropertyAssetsIndex({
        page: 1, row: 10,
        communityId: this.communityId
      }).then(res => {
        if (res && res.data) {
          this.data = res.data
        } else {
          this.data = res || {}
        }
      }).catch(() => {})
    },
    toOwner() {
      this.$router.push('/pages/property/listOwner?tab=业主信息')
    },
    toCar() {
      this.$router.push('/pages/car/listOwnerCar?tab=业主车辆')
    }
  }
}
</script>

<style scoped>
.index-community-card {
  background-color: #FFFFFF;
  border-radius: 10px;
  height: 350px;
  padding: 15px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}
.index-title {
  color: #404357;
  font-size: 20px;
  font-weight: bold;
}
.index-top {
  display: flex;
  justify-content: space-around;
  padding: 50px 0;
  border-bottom: 1px solid #f0f0f0;
}
.index-top-item {
  display: flex;
  justify-content: flex-start;
  align-items: center;
}
.index-icon-box {
  background-color: #F6F6F6;
  width: 50px;
  height: 50px;
  border-radius: 5px;
  text-align: center;
  line-height: 60px;
  margin-right: 10px;
}
.index-icon-box i {
  color: #00BF6F;
  font-weight: bold;
  font-size: 24px;
}
.index-number {
  color: #404357;
  font-size: 26px;
  font-weight: bold;
}
.index-label {
  color: #9D9D9F;
}
.index-bottom {
  width: 90%;
  margin: 40px auto;
  display: flex;
  justify-content: space-between;
}
.index-bottom-item {
  width: 45%;
  display: flex;
  justify-content: space-between;
}
.index-bottom-left {
  text-align: center;
}
.index-bottom-right {
  text-align: center;
}
.index-name {
  color: #404357;
  font-size: 20px;
  font-weight: bold;
}
.index-name-icon {
  font-size: 30px;
  color: #00BF6F;
  font-weight: bold;
  margin-top: 5px;
}
.index-unit {
  color: #9D9D9F;
  font-size: 14px;
}
.index-link {
  color: #9D9D9F;
  margin-top: 5px;
  cursor: pointer;
}
</style>
