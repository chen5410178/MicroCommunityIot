<template>
  <div class="room-bind-owner">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>绑定人员</span>
        <el-button size="small" @click="$router.go(-1)">返回</el-button>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="bind-form">
        <el-form-item label="房屋" prop="roomId">
          <el-input v-model="roomDisplayName" disabled placeholder="必填，请填写房屋名称" />
        </el-form-item>
        <el-form-item label="人员" prop="ownerId">
          <div class="owner-select-row">
            <el-input v-model="form.ownerName" disabled placeholder="必填，请选择人员" class="owner-input" />
            <el-button type="primary" size="small" @click="openSearchOwner">选择人员</el-button>
          </div>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="form.startTime" type="date" value-format="yyyy-MM-dd" placeholder="必填，请填写开始时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="form.endTime" type="date" value-format="yyyy-MM-dd" placeholder="必填，请填写结束时间" style="width: 100%" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-dialog title="选择业主" :visible.sync="searchVisible" width="900px" append-to-body @close="handleSearchClose">
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.roomName" placeholder="输入房屋编号楼栋-单元-房屋" clearable style="width: 220px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.name" placeholder="输入业主名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="ownerList" v-loading="loading" border stripe>
        <el-table-column prop="name" label="名称" width="100" align="center" />
        <el-table-column prop="sex" label="性别" width="60" align="center">
          <template slot-scope="{ row }">{{ row.sex === 0 ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column prop="idCard" label="身份证" width="180" align="center" />
        <el-table-column prop="link" label="联系方式" width="130" align="center" />
        <el-table-column prop="userName" label="创建员工" min-width="120" align="center" />
        <el-table-column label="操作" width="80" align="center">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="chooseOwner(row)">选择</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-if="total > 0"
        :current-page="queryParams.page"
        :page-size="queryParams.row"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        class="pagination-container"
      />
    </el-dialog>
  </div>
</template>

<script>
import { queryOwners } from '@/api/owner/ownerApi'
import { queryRooms, sellRoom } from '@/api/property/roomApi'

export default {
  name: 'RoomBindOwner',
  data() {
    const today = new Date()
    const y = today.getFullYear()
    const m = String(today.getMonth() + 1).padStart(2, '0')
    const d = String(today.getDate()).padStart(2, '0')
    return {
      loading: false,
      submitLoading: false,
      searchVisible: false,
      roomDisplayName: '',
      ownerList: [],
      total: 0,
      queryParams: { roomName: '', name: '', page: 1, row: 10 },
      form: {
        roomId: '',
        ownerId: '',
        ownerName: '',
        startTime: `${y}-${m}-${d}`,
        endTime: '2050-01-01'
      },
      rules: {
        roomId: [{ required: true, message: '房屋不能为空', trigger: 'change' }],
        ownerId: [{ required: true, message: '请选择人员', trigger: 'change' }],
        startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
        endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
      }
    }
  },
  mounted() {
    const roomId = this.$route.query.roomId
    if (roomId) {
      this.form.roomId = roomId
      this.loadRoomInfo(roomId)
    }
  },
  methods: {
    async loadRoomInfo(roomId) {
      try {
        const res = await queryRooms({ roomId, page: 1, row: 1 })
        const rooms = res.data || []
        if (rooms.length > 0) {
          const room = rooms[0]
          this.roomDisplayName = `${room.floorNum || ''}-${room.unitNum || ''}-${room.roomNum || ''}`
        }
      } catch (e) {
        this.$message.error(e.message || '查询房屋信息失败')
      }
    },
    openSearchOwner() {
      this.searchVisible = true
      this.resetSearch()
      this.handleSearch()
    },
    resetSearch() {
      this.queryParams = { roomName: '', name: '', page: 1, row: 10 }
    },
    handleSearchClose() {
      this.ownerList = []
      this.total = 0
    },
    async handleSearch() {
      this.loading = true
      this.queryParams.page = 1
      try {
        const res = await queryOwners({ ...this.queryParams, ownerTypeCd: '1001' })
        this.ownerList = res.data || []
        this.total = res.records || 0
      } catch (e) {
        this.$message.error(e.message || '查询失败')
      } finally {
        this.loading = false
      }
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.handleSearch()
    },
    handleSizeChange(val) {
      this.queryParams.row = val
      this.handleSearch()
    },
    chooseOwner(row) {
      this.form.ownerId = row.ownerId
      this.form.ownerName = row.name
      this.searchVisible = false
      this.$refs.formRef && this.$refs.formRef.validateField('ownerId')
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch {
        return
      }
      this.submitLoading = true
      try {
        await sellRoom({
          ownerId: this.form.ownerId,
          roomId: this.form.roomId,
          startTime: this.form.startTime,
          endTime: this.form.endTime,
          state: '2001'
        })
        this.$message.success('提交成功')
        this.$router.go(-1)
      } catch (e) {
        this.$message.error(e.message || '绑定失败')
      } finally {
        this.submitLoading = false
      }
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
.bind-form {
  max-width: 600px;
}
.owner-select-row {
  display: flex;
  gap: 10px;
}
.owner-input {
  flex: 1;
}
.pagination-container {
  margin-top: 16px;
  text-align: right;
}
</style>
