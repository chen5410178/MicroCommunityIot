<template>
  <div class="room-detail-car">
    <el-table :data="carList" v-loading="loading" border stripe>
      <el-table-column prop="carNum" label="车牌号" width="120" />
      <el-table-column prop="carBrand" label="品牌" width="100" />
      <el-table-column prop="carType" label="类型" width="80" />
      <el-table-column prop="carColor" label="颜色" width="80" />
      <el-table-column prop="createTime" label="登记时间" width="160" />
    </el-table>
  </div>
</template>

<script>
import { listOwnerCars } from '@/api/property/propertyApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'RoomDetailCar',
  props: { roomId: { type: String, default: '' } },
  data() {
    return { loading: false, carList: [] }
  },
  watch: {
    roomId: { immediate: true, handler() { this.loadCars() } }
  },
  methods: {
    async loadCars() {
      if (!this.roomId) { this.carList = []; return }
      this.loading = true
      try {
        const communityInfo = getCurrentCommunity() || {}
        const res = await listOwnerCars({ communityId: communityInfo.communityId, roomId: this.roomId, page: 1, row: 50 })
        this.carList = res.data || []
      } catch (e) { this.carList = [] }
      finally { this.loading = false }
    }
  }
}
</script>
