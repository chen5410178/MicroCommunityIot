<template>
  <el-dialog :visible.sync="dialogVisible" title="业主成员" width="700px" @close="handleClose">
    <el-table :data="memberList" v-loading="loading" border stripe>
      <el-table-column prop="memberId" label="成员ID" min-width="120" />
      <el-table-column prop="name" label="名称" width="100" />
      <el-table-column prop="ownerTypeCdName" label="类型" width="80" />
      <el-table-column prop="link" label="联系方式" width="120" />
      <el-table-column prop="idCard" label="身份证" width="180" />
    </el-table>
    <span slot="footer">
      <el-button @click="handleClose">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { listOwnerMembers } from '@/api/property/propertyApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'OwnerMembers',
  props: { visible: { type: Boolean, default: false }, ownerData: { type: Object, default: () => ({}) } },
  data() {
    return { dialogVisible: false, loading: false, memberList: [] }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) this.loadMembers()
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    handleClose() { this.dialogVisible = false },
    async loadMembers() {
      if (!this.ownerData.ownerId && !this.ownerData.roomId) { this.memberList = []; return }
      this.loading = true
      try {
        const communityInfo = getCurrentCommunity() || {}
        const params = { communityId: communityInfo.communityId, page: 1, row: 50 }
        if (this.ownerData.ownerId) params.memberId = this.ownerData.ownerId
        const res = await listOwnerMembers(params)
        this.memberList = Array.isArray(res) ? res : (res.data || [])
      } catch (e) { this.memberList = [] }
      finally { this.loading = false }
    }
  }
}
</script>
