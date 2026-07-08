<template>
  <div class="room-bind-owner">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>绑定人员</span>
        <el-button size="small" @click="$router.go(-1)">返回</el-button>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.name" placeholder="请输入名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.link" placeholder="请输入联系方式" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="ownerList" v-loading="loading" border stripe>
        <el-table-column prop="ownerId" label="业主ID" width="120" />
        <el-table-column prop="name" label="名称" width="100" />
        <el-table-column prop="link" label="联系方式" width="120" />
        <el-table-column prop="idCard" label="身份证" width="180" />
        <el-table-column prop="address" label="地址" min-width="150" />
        <el-table-column label="操作" width="80" align="center">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="handleBind(row)">绑定</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { queryOwners, saveOwnerRoomRel } from '@/api/property/propertyApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'RoomBindOwner',
  data() {
    return {
      loading: false,
      ownerList: [],
      queryParams: { name: '', link: '', page: 1, row: 10 }
    }
  },
  mounted() {
    this.handleSearch()
  },
  methods: {
    async handleSearch() {
      this.loading = true
      try {
        const communityInfo = getCurrentCommunity() || {}
        const res = await queryOwners({ ...this.queryParams, communityId: communityInfo.communityId, ownerTypeCd: '1001' })
        this.ownerList = Array.isArray(res) ? res : (res.data || [])
      } catch (e) { this.$message.error(e.msg || '查询失败') }
      finally { this.loading = false }
    },
    async handleBind(row) {
      try {
        const roomId = this.$route.query.roomId
        const communityInfo = getCurrentCommunity() || {}
        await saveOwnerRoomRel({
          ownerId: row.ownerId,
          roomId: roomId,
          communityId: communityInfo.communityId
        })
        this.$message.success('绑定成功')
        this.$router.go(-1)
      } catch (e) { this.$message.error(e.msg || '绑定失败') }
    }
  }
}
</script>

<style scoped>
.room-bind-owner {
  padding: 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
