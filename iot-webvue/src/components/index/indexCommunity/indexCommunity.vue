<template>
  <el-card shadow="never" class="index-card">
    <template #header>
      <span>小区信息</span>
    </template>
    <div class="index-grid">
      <div class="index-item">
        <div class="index-icon"><i class="el-icon-office-building"></i></div>
        <div class="index-number">{{ data.floorCount || 0 }}</div>
        <div class="index-label">楼宇</div>
      </div>
      <div class="index-item">
        <div class="index-icon"><i class="el-icon-house"></i></div>
        <div class="index-number">{{ data.roomCount || 0 }}</div>
        <div class="index-label">房屋</div>
      </div>
      <div class="index-item">
        <div class="index-icon"><i class="el-icon-shopping-bag-2"></i></div>
        <div class="index-number">{{ data.shopCount || 0 }}</div>
        <div class="index-label">商铺</div>
      </div>
      <div class="index-item">
        <div class="index-icon"><i class="el-icon-truck"></i></div>
        <div class="index-number">{{ data.spaceCount || 0 }}</div>
        <div class="index-label">车位</div>
      </div>
    </div>
    <div class="index-bottom">
      <div class="index-bottom-item">
        <div class="index-sub-icon"><i class="el-icon-user"></i></div>
        <div>
          <div class="index-number">{{ data.ownerCount || 0 }} <span class="index-unit">个</span></div>
          <div class="index-link">登记住户</div>
        </div>
      </div>
      <div class="index-bottom-item">
        <div class="index-sub-icon"><i class="el-icon-finished"></i></div>
        <div>
          <div class="index-number">{{ data.carCount || 0 }} <span class="index-unit">个</span></div>
          <div class="index-link">车辆</div>
        </div>
      </div>
    </div>
  </el-card>
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
    }
  }
}
</script>

<style scoped>
.index-card {
  height: 100%;
}
.index-grid {
  display: flex;
  justify-content: space-around;
  padding: 8px 0 16px;
  border-bottom: 1px solid #f0f0f0;
}
.index-item {
  text-align: center;
  flex: 1;
}
.index-icon {
  font-size: 32px;
  color: #1890ff;
  margin-bottom: 8px;
}
.index-number {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}
.index-label {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}
.index-bottom {
  display: flex;
  padding-top: 16px;
  gap: 16px;
}
.index-bottom-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #fafafa;
  border-radius: 4px;
}
.index-sub-icon {
  font-size: 28px;
  color: #1890ff;
}
.index-unit {
  font-size: 14px;
  font-weight: normal;
  color: #999;
}
.index-link {
  font-size: 13px;
  color: #1890ff;
  cursor: pointer;
  margin-top: 4px;
}
</style>
