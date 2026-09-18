<template>
  <div class="lock-detail-room">
    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column label="房屋" min-width="180">
        <template slot-scope="{ row }">
          {{ row.floorNum }}-{{ row.unitNum }}-{{ row.roomNum }}
        </template>
      </el-table-column>
      <el-table-column prop="layer" label="楼层" width="80" align="center" />
      <el-table-column prop="roomSubTypeName" label="房屋类型" min-width="100" />
      <el-table-column prop="ownerName" label="业主" min-width="100" />
      <el-table-column label="建筑/室内面积" min-width="140" align="center">
        <template slot-scope="{ row }">
          {{ row.builtUpArea }}/{{ row.roomArea }} ㎡
        </template>
      </el-table-column>
      <el-table-column prop="stateName" label="房屋状态" width="100" align="center" />
      <el-table-column prop="startTime" label="交房时间" min-width="160" />
    </el-table>
    <el-pagination
      :current-page="queryParams.page"
      :page-size="queryParams.row"
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
import { queryRooms } from '@/api/property/roomApi'

export default {
  name: 'LockDetailRoom',
  props: {
    roomId: { type: String, default: '' },
    machineId: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: { page: 1, row: 10 }
    }
  },
  watch: {
    roomId: {
      immediate: true,
      handler() {
        if (!this.roomId) {
          this.list = []
          this.total = 0
          return
        }
        this.queryParams.page = 1
        this.fetchList()
      }
    }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await queryRooms({
          roomId: this.roomId,
          page: this.queryParams.page,
          row: this.queryParams.row
        })
        this.list = res.data || []
        this.total = res.total || 0
      } catch (e) {
        this.list = []
        this.total = 0
        this.$message.error(e.message || '获取房屋信息失败')
      } finally {
        this.loading = false
      }
    },
    handleSizeChange(val) {
      this.queryParams.row = val
      this.queryParams.page = 1
      this.fetchList()
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.fetchList()
    }
  }
}
</script>

<style scoped>
.pagination-container { margin-top: 16px; text-align: right; }
</style>
