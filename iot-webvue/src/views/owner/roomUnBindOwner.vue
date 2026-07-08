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
        <el-table-column prop="name" label="人员名称" min-width="100" align="center" />
        <el-table-column label="人员性别" width="80" align="center">
          <template slot-scope="{ row }">{{ row.sex === 0 ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column prop="ownerTypeName" label="人员类型" min-width="100" align="center" />
        <el-table-column prop="link" label="联系电话" min-width="120" align="center" />
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
import { queryOwners } from '@/api/owner/ownerApi'
import { exitRoom } from '@/api/property/roomApi'

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
        const res = await queryOwners({ roomId: this.roomId, page: 1, row: 50 })
        this.ownerList = res.data || []
      } catch (e) { this.$message.error(e.message || '查询失败') }
      finally { this.loading = false }
    },
    async handleUnbind(row) {
      try {
        await this.$confirm(`确定要解除 <strong>${row.name}</strong> 与当前房屋的绑定关系吗？`, '解绑确认', {
          confirmButtonText: '确定解绑',
          cancelButtonText: '取消',
          type: 'warning',
          dangerouslyUseHTMLString: true
        })
      } catch {
        return
      }
      try {
        await exitRoom({
          ownerId: row.ownerId,
          roomId: this.roomId
        })
        this.$message.success('解绑成功')
        this.loadOwners()
      } catch (e) { this.$message.error(e.message || '解绑失败') }
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
