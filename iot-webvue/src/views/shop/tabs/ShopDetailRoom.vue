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
      <el-pagination
        v-if="total > 0"
        :current-page="query.page"
        :page-size="query.row"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="handlePageChange"
        class="pagination-container"
      />
    </div>
  </div>
</template>

<script>
import { queryRoomsByOwner } from '@/api/owner/ownerApi'

export default {
  name: 'ShopDetailRoom',
  props: {
    ownerId: { type: String, default: '' },
    memberId: { type: String, default: '' },
    ownerName: { type: String, default: '' },
    link: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      roomList: [],
      total: 0,
      query: { page: 1, row: 10 }
    }
  },
  mounted() {
    this.loadRooms()
  },
  watch: {
    ownerId: {
      handler() {
        this.roomList = []
        this.total = 0
        this.query.page = 1
        this.loadRooms()
      }
    }
  },
  methods: {
    async loadRooms() {
      if (!this.ownerId) return
      this.loading = true
      try {
        const res = await queryRoomsByOwner({
          ownerId: this.ownerId,
          page: this.query.page,
          row: this.query.row
        })
        this.roomList = res.data || []
        this.total = res.total || 0
      } catch (e) {
        this.roomList = []
      } finally {
        this.loading = false
      }
    },
    handlePageChange(val) {
      this.query.page = val
      this.loadRooms()
    }
  }
}
</script>

<style scoped>
.pagination-container {
  margin-top: 16px;
  text-align: right;
}
</style>
