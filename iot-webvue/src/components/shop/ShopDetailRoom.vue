<template>
  <div class="shop-detail-room">
    <div v-loading="loading">
      <el-table :data="roomList" border stripe>
        <el-table-column label="房屋" min-width="140">
          <template slot-scope="{ row }">{{ row.floorNum }}-{{ row.unitNum }}-{{ row.roomNum }}</template>
        </el-table-column>
        <el-table-column prop="stateName" label="状态" min-width="100" />
        <el-table-column prop="roomSubTypeName" label="类型" min-width="100" />
        <el-table-column label="面积" min-width="120">
          <template slot-scope="{ row }">{{ row.builtUpArea }}/{{ row.roomArea }}㎡</template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && roomList.length === 0" description="暂无数据" />
    </div>
  </div>
</template>

<script>
import { queryRoomsByOwner } from '@/api/owner/ownerApi'

export default {
  name: 'ShopDetailRoom',
  data() {
    return {
      loading: false,
      roomList: [],
      ownerId: ''
    }
  },
  methods: {
    async open(ownerId) {
      this.ownerId = ownerId || ''
      this.roomList = []
      if (!this.ownerId) return
      await this.loadRooms()
    },
    async loadRooms() {
      this.loading = true
      try {
        const res = await queryRoomsByOwner({ ownerId: this.ownerId })
        this.roomList = res.data || []
      } catch (e) {
        this.roomList = []
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
