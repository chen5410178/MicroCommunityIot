<template>
  <div class="tab-component">
    <el-card shadow="never">
      <el-table :data="infoList" border stripe size="small" :show-header="true">
        <el-table-column prop="total" label="总车位数" align="center">
          <template>{{ info.total }}位</template>
        </el-table-column>
        <el-table-column prop="freeCount" label="剩余车位数" align="center">
          <template>{{ info.freeCount }}位</template>
        </el-table-column>
        <el-table-column prop="createTime" label="采集时间" align="center" />
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { getFreeParkingSpace } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ParkingAreaControlRemaining',
  props: { paId: { type: String, default: '' } },
  data() {
    return {
      info: { total: 0, freeCount: 0, createTime: '' },
      infoList: [{}]
    }
  },
  mounted() { this.loadData() },
  methods: {
    async loadData() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await getFreeParkingSpace({ paId: this.paId, communityId })
        const data = res.data || {}
        this.info.total = data.total || 0
        this.info.freeCount = data.freeCount || 0
        this.info.createTime = new Date().toLocaleString()
        this.infoList = [this.info]
      } catch (e) { console.error('获取剩余车位失败', e) }
    }
  }
}
</script>

<style scoped>
.tab-component { padding: 0; }
</style>
