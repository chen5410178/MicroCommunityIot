<template>
  <div class="owner-detail-room">
    <el-table :data="rooms" v-loading="loading" border stripe>
      <el-table-column label="房屋" min-width="150">
        <template slot-scope="{ row }">{{ row.floorNum }}-{{ row.unitNum }}-{{ row.roomNum }}</template>
      </el-table-column>
      <el-table-column prop="layer" label="楼层" width="80">
        <template slot-scope="{ row }">{{ row.layer }}层</template>
      </el-table-column>
      <el-table-column prop="roomSubTypeName" label="类型" min-width="100" />
      <el-table-column label="建筑/室内面积" min-width="120">
        <template slot-scope="{ row }">{{ row.builtUpArea }}/{{ row.roomArea }}平方米</template>
      </el-table-column>
      <el-table-column prop="stateName" label="房屋状态" width="100" />
      <el-table-column label="入住时间" width="120">
        <template slot-scope="{ row }">{{ row.startTime || '-' }}</template>
      </el-table-column>
      <el-table-column v-for="col in dynamicColumns" :key="col.specCd" :label="col.specName" min-width="120">
        <template slot-scope="{ row }">{{ getDynamicValue(row, col.specCd) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="80" fixed="right" align="center">
        <template slot-scope="{ row }">
          <el-button size="small" type="text" @click="toRoomDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      :current-page="page"
      :page-size="row"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handlePageChange"
      class="pagination-container"
    />
  </div>
</template>

<script>
import { queryRoomsByOwner } from '@/api/owner/ownerApi'
import { listAttrSpecs } from '@/api/dev/devApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'OwnerDetailRoom',
  props: {
    ownerId: { type: String, default: '' },
    memberId: { type: String, default: '' },
    ownerName: { type: String, default: '' },
    link: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      rooms: [],
      total: 0,
      page: 1,
      row: 10,
      dynamicColumns: []
    }
  },
  mounted() {
    this.loadDynamicColumns().then(() => this.fetchList())
  },
  methods: {
    async loadDynamicColumns() {
      try {
        const res = await listAttrSpecs({ specCd: 'building_room_attr', page: 1, row: 100 })
        const specs = res.data || []
        this.dynamicColumns = specs.filter(item => item.listShow === 'Y').map(item => ({
          specCd: item.specCd,
          specName: item.specName
        }))
      } catch (e) { /* ignore */ }
    },
    async fetchList() {
      this.loading = true
      try {
        const community = getCurrentCommunity()
        const res = await queryRoomsByOwner({
          ownerId: this.ownerId,
          communityId: community ? community.communityId : '',
          page: this.page,
          row: this.row
        })
        this.rooms = res.data || []
        this.total = res.records || 0
      } finally { this.loading = false }
    },
    getDynamicValue(row, specCd) {
      if (!row.roomAttrDtos || row.roomAttrDtos.length === 0) return '-'
      const attr = row.roomAttrDtos.find(item => item.specCd === specCd)
      return attr ? (attr.valueName || attr.value || '-') : '-'
    },
    toRoomDetail(room) {
      this.$router.push({ path: '/pages/property/roomDetail', query: { roomId: room.roomId } })
    },
    handleSizeChange(val) { this.row = val; this.page = 1; this.fetchList() },
    handlePageChange(val) { this.page = val; this.fetchList() }
  }
}
</script>

<style scoped>
.pagination-container { margin-top: 16px; text-align: right; }
</style>
