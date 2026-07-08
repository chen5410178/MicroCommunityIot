<template>
  <div class="room-detail-owner">
    <el-button type="primary" size="small" @click="handleAdd">添加成员</el-button>
    <el-table :data="ownerList" v-loading="loading" border stripe style="margin-top: 12px">
      <el-table-column prop="memberId" label="成员ID" width="120" />
      <el-table-column prop="name" label="名称" width="100" />
      <el-table-column prop="ownerTypeCdName" label="类型" width="80" />
      <el-table-column prop="link" label="联系方式" width="120" />
      <el-table-column prop="idCard" label="身份证" width="180" />
      <el-table-column prop="createTime" label="创建时间" width="160" />
    </el-table>
  </div>
</template>

<script>
import { listOwnerMembers } from '@/api/property/propertyApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'RoomDetailOwner',
  props: { roomId: { type: String, default: '' } },
  data() {
    return { loading: false, ownerList: [] }
  },
  watch: {
    roomId: { immediate: true, handler() { this.loadOwners() } }
  },
  methods: {
    async loadOwners() {
      if (!this.roomId) { this.ownerList = []; return }
      this.loading = true
      try {
        const communityInfo = getCurrentCommunity() || {}
        const res = await listOwnerMembers({ communityId: communityInfo.communityId, roomId: this.roomId, page: 1, row: 50 })
        this.ownerList = Array.isArray(res) ? res : (res.data || [])
      } catch (e) { this.ownerList = [] }
      finally { this.loading = false }
    },
    handleAdd() {
      /* TODO: add member dialog */
    }
  }
}
</script>
