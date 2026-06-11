<template>
  <div class="room-unbind-owner">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>解绑人员</span>
        <el-button size="small" @click="$router.go(-1)">返回</el-button>
      </div>
      <el-alert
        :title="'当前房屋：' + (roomName || '')"
        type="info"
        :closable="false"
        style="margin-bottom: 16px"
      />
      <el-table :data="ownerList" v-loading="loading" border stripe>
        <el-table-column prop="ownerId" label="业主ID" width="120" />
        <el-table-column prop="name" label="名称" width="100" />
        <el-table-column prop="link" label="联系方式" width="120" />
        <el-table-column prop="idCard" label="身份证" width="180" />
        <el-table-column prop="ownerTypeCdName" label="类型" width="80" />
        <el-table-column label="操作" width="80" align="center">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" style="color: #f56c6c" @click="handleUnbind(row)">解绑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { queryOwners, deleteOwnerRoomRel } from '@/api/property/propertyApi'

export default {
  name: 'RoomUnBindOwner',
  data() {
    return {
      loading: false,
      ownerList: [],
      roomId: '',
      roomName: ''
    }
  },
  mounted() {
    this.roomId = this.$route.query.roomId || ''
    this.roomName = this.$route.query.roomName || ''
    if (this.roomId) this.loadOwners()
  },
  methods: {
    async loadOwners() {
      this.loading = true
      try {
        const communityInfo = this.$store.state.app.currentCommunity || {}
        const res = await queryOwners({ communityId: communityInfo.communityId, roomId: this.roomId, page: 1, row: 50 })
        this.ownerList = Array.isArray(res) ? res : (res.data || [])
      } catch (e) { this.$message.error(e.msg || '查询失败') }
      finally { this.loading = false }
    },
    async handleUnbind(row) {
      try {
        const communityInfo = this.$store.state.app.currentCommunity || {}
        await deleteOwnerRoomRel({
          ownerId: row.ownerId,
          roomId: this.roomId,
          communityId: communityInfo.communityId
        })
        this.$message.success('解绑成功')
        this.loadOwners()
      } catch (e) { this.$message.error(e.msg || '解绑失败') }
    }
  }
}
</script>

<style scoped>
.room-unbind-owner {
  padding: 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
