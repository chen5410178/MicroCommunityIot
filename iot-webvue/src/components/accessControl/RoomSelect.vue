<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="选择房屋"
    width="800px"
    append-to-body
    @close="handleClose"
  >
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-select v-model="queryParams.floorId" placeholder="请选择楼栋" clearable @change="handleFloorChange" style="width: 160px">
            <el-option v-for="item in floorList" :key="item.floorId" :label="item.floorNum + '栋'" :value="item.floorId" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.unitId" placeholder="请选择单元" clearable style="width: 160px">
            <el-option v-for="item in unitList" :key="item.unitId" :label="item.unitNum + '单元'" :value="item.unitId" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.roomNumLike" placeholder="房屋编号" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.ownerNameLike" placeholder="业主姓名" clearable style="width: 140px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-table :data="roomList" v-loading="loading" border stripe max-height="350">
      <el-table-column prop="floorNum" label="楼栋" width="80" align="center" />
      <el-table-column prop="unitNum" label="单元" width="80" align="center" />
      <el-table-column prop="roomNum" label="房屋编号" min-width="100" />
      <el-table-column prop="roomArea" label="面积(㎡)" width="90" align="center" />
      <el-table-column prop="ownerName" label="业主" min-width="100" />
      <el-table-column prop="stateName" label="状态" width="80" align="center" />
      <el-table-column label="操作" width="70" align="center">
        <template slot-scope="{ row }">
          <el-button size="small" type="text" @click="handleSelect(row)">选择</el-button>
        </template>
      </el-table-column>
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
  </el-dialog>
</template>

<script>
import { queryRooms } from '@/api/property/roomApi'
import { queryFloors } from '@/api/property/floorApi'
import { queryUnits } from '@/api/property/unitApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'RoomSelect',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      roomList: [],
      total: 0,
      floorList: [],
      unitList: [],
      queryParams: {
        page: 1,
        row: 10,
        floorId: '',
        unitId: '',
        roomNumLike: '',
        ownerNameLike: ''
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.loadFloors()
        this.fetchList()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadFloors() {
      try {
        const res = await queryFloors({ page: 1, row: 1000 })
        this.floorList = res.data || []
      } catch (e) {
        console.error('加载楼栋失败', e)
      }
    },
    async handleFloorChange(floorId) {
      this.queryParams.unitId = ''
      this.unitList = []
      if (!floorId) return
      try {
        const res = await queryUnits({ floorId, page: 1, row: 1000 })
        this.unitList = res.data || []
      } catch (e) {
        console.error('加载单元失败', e)
      }
    },
    async fetchList() {
      this.loading = true
      try {
        const params = { ...this.queryParams }
        const res = await queryRooms(params)
        this.roomList = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = { page: 1, row: 10, floorId: '', unitId: '', roomNumLike: '', ownerNameLike: '' }
      this.unitList = []
      this.fetchList()
    },
    handleSelect(row) {
      this.$emit('confirm', {
        roomId: row.roomId,
        roomName: row.floorNum + '-' + row.unitNum + '-' + row.roomNum
      })
      this.dialogVisible = false
    },
    handleSizeChange(val) {
      this.queryParams.row = val
      this.fetchList()
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.fetchList()
    },
    handleClose() {
      this.dialogVisible = false
    }
  }
}
</script>

<style scoped>
.search-card { margin-bottom: 16px; }
.search-card >>> .el-card__body { padding: 12px 16px 0; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
