<template>
  <div class="parking-space-manage">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.num" placeholder="请输入车位编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.paId" placeholder="请选择停车场" clearable style="width: 180px">
            <el-option v-for="item in parkingAreas" :key="item.paId" :label="item.num" :value="item.paId" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.state" placeholder="请选择车位状态" clearable style="width: 180px">
            <el-option label="空闲" value="F" />
            <el-option label="出售" value="S" />
            <el-option label="出租" value="H" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.parkingType" placeholder="请选择车位类型" clearable style="width: 180px">
            <el-option label="普通车位" value="1" />
            <el-option label="子母车位" value="2" />
            <el-option label="豪华车位" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>停车位</span>
        <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
      </div>
      <div class="table-wrapper">
      <el-table :data="list" v-loading="loading" border stripe style="width: 100%">
        <el-table-column prop="paNum" label="停车场" min-width="120" />
        <el-table-column label="停车区域" min-width="100" align="center">
          <template slot-scope="{ row }">{{ row.regionCode || '-' }}</template>
        </el-table-column>
        <el-table-column prop="num" label="车位编号" min-width="100" />
        <el-table-column label="车位状态" min-width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag v-if="row.state === 'F'" size="small">空闲</el-tag>
            <el-tag v-else-if="row.state === 'S'" type="warning" size="small">出售</el-tag>
            <el-tag v-else-if="row.state === 'H'" type="info" size="small">出租</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="车位类型" min-width="80" align="center">
          <template slot-scope="{ row }">
            {{ row.parkingType === '1' ? '普通车位' : row.parkingType === '2' ? '子母车位' : row.parkingType === '3' ? '豪华车位' : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="psStateName" label="车位占用" min-width="100" align="center">
          <template slot-scope="{ row }">{{ row.psStateName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="parkCarNum" label="停放车牌号" min-width="120" align="center">
          <template slot-scope="{ row }">{{ row.parkCarNum || '未知' }}</template>
        </el-table-column>
        <el-table-column prop="lockBattery" label="地锁电量" min-width="80" align="center">
          <template slot-scope="{ row }">{{ row.lockBattery || '未知' }}</template>
        </el-table-column>
        <el-table-column prop="lockStateName" label="地锁状态" min-width="80" align="center">
          <template slot-scope="{ row }">{{ row.lockStateName || '-' }}</template>
        </el-table-column>
        <el-table-column label="车位图片" width="80" align="center">
          <template slot-scope="{ row }">
            <el-image
              v-if="row.imagePhoto"
              style="width: 50px; height: 50px"
              :src="row.imagePhoto"
              :preview-src-list="[row.imagePhoto]"
              fit="cover"
            />
            <img v-else src="/img/noCar.png" style="width: 50px; height: 50px" />
          </template>
        </el-table-column>
        <el-table-column label="地磁" min-width="140" align="center">
          <template slot-scope="{ row }">
            <template v-if="row.machineName">{{ row.machineName }}({{ row.machineStateName }})</template>
            <span v-else>未知</span>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="上报时间" min-width="160" align="center">
          <template slot-scope="{ row }">{{ row.updateTime || '-' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" align="center" />
        <el-table-column label="备注" min-width="100" align="center">
          <template slot-scope="{ row }">
            <el-popover v-if="row.remark" placement="top" trigger="hover" :content="row.remark">
              <el-button slot="reference" size="small" type="text">查看</el-button>
            </el-popover>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="监控视频" min-width="120" align="center">
          <template slot-scope="{ row }">
            <template v-if="row.monitorName">
              {{ row.monitorName }}
              <el-button type="text" size="small" @click="handleViewVideo(row)">(观看)</el-button>
            </template>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleEdit(row)">修改</el-button>
            <el-button size="small" type="text" style="color: #f56c6c" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      </div>
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
    </el-card>
    <AddParkingSpace :visible.sync="addVisible" @success="handleSuccess" />
    <EditParkingSpace :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteParkingSpace :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
    <PlayMonitorVideo :visible.sync="videoVisible" :video-info="videoInfo" />
  </div>
</template>

<script>
import { listParkingSpaces, listParkingAreas } from '@/api/car/carApi'
import AddParkingSpace from '@/components/car/AddParkingSpace.vue'
import EditParkingSpace from '@/components/car/EditParkingSpace.vue'
import DeleteParkingSpace from '@/components/car/DeleteParkingSpace.vue'
import PlayMonitorVideo from '@/components/monitor/PlayMonitorVideo.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ParkingSpace',
  components: { AddParkingSpace, EditParkingSpace, DeleteParkingSpace, PlayMonitorVideo },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      parkingAreas: [],
      addVisible: false,
      editVisible: false,
      delVisible: false,
      videoVisible: false,
      currentRow: null,
      videoInfo: {},
      queryParams: {
        page: 1,
        row: 10,
        num: '',
        paId: '',
        state: '',
        parkingType: ''
      }
    }
  },
  mounted() {
    this.fetchParkingAreas()
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listParkingSpaces({ ...this.queryParams, communityId })
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    async fetchParkingAreas() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listParkingAreas({ page: 1, row: 100, communityId })
        this.parkingAreas = res.data || []
      } catch (e) { /* ignore */ }
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = { page: 1, row: 10, num: '', paId: '', state: '', parkingType: '' }
      this.fetchList()
    },
    handleAdd() {
      this.addVisible = true
    },
    handleEdit(row) {
      this.currentRow = { ...row }
      this.editVisible = true
    },
    handleDelete(row) {
      this.currentRow = { ...row }
      this.delVisible = true
    },
    handleViewVideo(row) {
      this.videoInfo = {
        machineId: row.monitorId,
        machineName: row.monitorName,
        communityId: row.communityId
      }
      this.videoVisible = true
    },
    handleSuccess() {
      this.fetchList()
    },
    handleSizeChange(val) {
      this.queryParams.row = val
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
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.table-wrapper { overflow-x: auto; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
