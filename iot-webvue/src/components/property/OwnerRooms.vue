<template>
  <el-dialog :visible.sync="dialogVisible" title="业主房屋" width="700px" @close="handleClose">
    <el-table :data="roomList" v-loading="loading" border stripe>
      <el-table-column label="房屋">
        <template slot-scope="{ row }">{{ row.floorNum }}-{{ row.unitNum }}-{{ row.roomNum }}</template>
      </el-table-column>
      <el-table-column prop="stateName" label="状态" width="80" />
      <el-table-column prop="roomSubTypeName" label="类型" width="80" />
      <el-table-column label="面积" width="120">
        <template slot-scope="{ row }">{{ row.builtUpArea }}/{{ row.roomArea }}㎡</template>
      </el-table-column>
    </el-table>
    <span slot="footer">
      <el-button @click="handleClose">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { queryRooms } from '@/api/property/propertyApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'OwnerRooms',
  props: { visible: { type: Boolean, default: false }, roomData: { type: Object, default: () => ({}) } },
  data() {
    return { dialogVisible: false, loading: false, roomList: [] }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) this.loadRooms()
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    handleClose() { this.dialogVisible = false },
    async loadRooms() {
      if (!this.roomData.ownerId) { this.roomList = []; return }
      this.loading = true
      try {
        const communityInfo = getCurrentCommunity() || {}
        const res = await queryRooms({ communityId: communityInfo.communityId, ownerId: this.roomData.ownerId, page: 1, row: 50 })
        this.roomList = res.data || []
      } catch (e) { this.roomList = [] }
      finally { this.loading = false }
    }
  }
}
</script>
