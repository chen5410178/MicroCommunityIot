<template>
  <div class="parking-region-manage">
    <div class="page-container">
      <div class="left-panel">
        <el-card shadow="never" class="area-card">
          <div class="area-list">
            <div
              v-for="item in parkingAreas"
              :key="item.paId"
              class="area-item"
              :class="{ active: activePaId === item.paId }"
              @click="handleSelectArea(item.paId)"
            >
              {{ item.num }}
            </div>
          </div>
          <div v-if="parkingAreas.length === 0" class="empty-tip">暂无停车场数据</div>
        </el-card>
      </div>
      <div class="right-panel">
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>停车区域</span>
            <el-button v-if="activePaId" type="primary" size="small" @click="handleAdd">添加</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="prId" label="编号" min-width="100" />
            <el-table-column prop="regionCode" label="区域编号" min-width="120" />
            <el-table-column prop="paNum" label="停车场" min-width="120" />
            <el-table-column prop="createTime" label="创建时间" min-width="160" />
            <el-table-column label="备注" width="100" align="center">
              <template slot-scope="{ row }">
                <el-popover v-if="row.remark" placement="top" trigger="hover" :content="row.remark">
                  <el-button slot="reference" size="small" type="text">查看</el-button>
                </el-popover>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button size="small" type="text" @click="handleEdit(row)">修改</el-button>
                <el-button size="small" type="text" style="color: #f56c6c" @click="handleDelete(row)">删除</el-button>
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
        </el-card>
      </div>
    </div>
    <AddParkingRegion :visible.sync="addVisible" :pa-id="activePaId" @success="handleSuccess" />
    <EditParkingRegion :visible.sync="editVisible" :edit-data="currentRow" @success="handleSuccess" />
    <DeleteParkingRegion :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listParkingRegions, listParkingAreas } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'
import AddParkingRegion from '@/components/car/AddParkingRegion.vue'
import EditParkingRegion from '@/components/car/EditParkingRegion.vue'
import DeleteParkingRegion from '@/components/car/DeleteParkingRegion.vue'

export default {
  name: 'ParkingRegion',
  components: { AddParkingRegion, EditParkingRegion, DeleteParkingRegion },
  computed: {
    currentCommunity() { return getCurrentCommunity() || {} }
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      parkingAreas: [],
      activePaId: '',
      addVisible: false,
      editVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        paId: '',
        communityId: ''
      }
    }
  },
  mounted() {
    this.fetchParkingAreas()
  },
  methods: {
    async fetchParkingAreas() {
      try {
        const communityId = this.currentCommunity.communityId || ''
        const res = await listParkingAreas({ communityId, page: -1, row: 100 })
        this.parkingAreas = res.data || []
        if (this.parkingAreas.length > 0) {
          this.activePaId = this.parkingAreas[0].paId
          this.fetchList()
        }
      } catch (e) {
        console.error('获取停车场列表失败', e)
      }
    },
    async fetchList() {
      this.loading = true
      this.queryParams.paId = this.activePaId
      this.queryParams.communityId = this.currentCommunity.communityId || ''
      try {
        const res = await listParkingRegions(this.queryParams)
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    handleSelectArea(paId) {
      this.activePaId = paId
      this.queryParams.page = 1
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
.page-container { display: flex; gap: 16px; }
.left-panel { width: 200px; flex-shrink: 0; }
.right-panel { flex: 1; min-width: 0; }
.area-card { height: 100%; }
.area-list { max-height: calc(100vh - 200px); overflow-y: auto; }
.area-item { padding: 10px 12px; cursor: pointer; border-radius: 4px; margin-bottom: 5px; text-align: center; font-size: 13px; transition: all 0.3s; }
.area-item:hover { background-color: #f5f7fa; }
.area-item.active { background-color: #409eff; color: white; font-weight: 600; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
.empty-tip { text-align: center; color: #909399; padding: 20px 0; }
</style>
