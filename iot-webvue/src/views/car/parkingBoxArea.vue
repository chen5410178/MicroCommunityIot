<template>
  <div class="parking-box-area-manage">
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>{{ boxName }} 停车场</span>
        <div>
          <el-button size="small" @click="handleBack">返回</el-button>
          <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
        </div>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="baId" label="关系ID" min-width="120" />
        <el-table-column prop="paId" label="停车场编号" min-width="120" />
        <el-table-column prop="paNum" label="停车场" min-width="120" />
        <el-table-column label="默认停车场" width="100" align="center">
          <template slot-scope="{ row }">
            {{ row.defaultArea === 'T' ? '是' : '否' }}
          </template>
        </el-table-column>
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
            <el-button
              v-if="row.defaultArea !== 'T'"
              size="small"
              type="text"
              @click="handleSetDefault(row)"
            >
              设为默认停车场
            </el-button>
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
    <AddParkingBoxArea :visible.sync="addVisible" :box-id="boxId" @success="handleSuccess" />
    <DeleteParkingBoxArea :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listParkingBoxAreas, updateParkingBoxArea } from '@/api/car/carApi'
import AddParkingBoxArea from '@/components/car/AddParkingBoxArea.vue'
import DeleteParkingBoxArea from '@/components/car/DeleteParkingBoxArea.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ParkingBoxArea',
  components: { AddParkingBoxArea, DeleteParkingBoxArea },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      boxId: '',
      boxName: '',
      addVisible: false,
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10
      }
    }
  },
  mounted() {
    this.boxId = this.$route.query.boxId || ''
    this.boxName = this.$route.query.boxName || ''
    if (!this.boxId) {
      this.$message.warning('缺少岗亭信息')
      this.$router.back()
      return
    }
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listParkingBoxAreas({ ...this.queryParams, boxId: this.boxId, communityId })
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    handleBack() {
      this.$router.back()
    },
    handleAdd() {
      this.addVisible = true
    },
    handleDelete(row) {
      this.currentRow = { ...row }
      this.delVisible = true
    },
    async handleSetDefault(row) {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await updateParkingBoxArea({ baId: row.baId, boxId: this.boxId, paId: row.paId, defaultArea: 'T', communityId })
        this.$message.success('设置成功')
        this.fetchList()
      } catch (e) {
        this.$message.error(e.msg || '设置失败')
      }
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
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
