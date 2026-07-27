<template>
  <div class="list-car-inout-his">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-input v-model="queryParams.carNumLike" placeholder="请输入车牌号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-date-picker v-model="queryParams.startTime" type="date" value-format="yyyy-MM-dd" placeholder="开始时间" style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-date-picker v-model="queryParams.endTime" type="date" value-format="yyyy-MM-dd" placeholder="结束时间" style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>历史记录</span>
        <el-button type="danger" size="small" @click="handleDeleteData">删除数据</el-button>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column label="进场图" width="80" align="center">
          <template slot-scope="{ row }">
            <el-image v-if="row.photoJpg" style="width: 50px; height: 50px" :src="row.photoJpg" :preview-src-list="[row.photoJpg]" fit="cover" />
            <img v-else src="/img/noPhoto.jpg" style="width: 50px; height: 50px" />
          </template>
        </el-table-column>
        <el-table-column prop="communityName" label="小区名称" min-width="120" />
        <el-table-column label="车辆状态" min-width="150">
          <template slot-scope="{ row }">
            <div>{{ row.carInout == '3306' ? '进场' : '出场' }}({{ row.stateName }})</div>
            <div>{{ row.machineName }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="carNum" label="车牌号" min-width="120" />
        <el-table-column prop="paNum" label="停车场" min-width="120" />
        <el-table-column prop="feeName" label="计费规则" min-width="120" />
        <el-table-column prop="carTypeName" label="车牌类型" min-width="100" />
        <el-table-column prop="inTime" label="进场时间" min-width="150" />
        <el-table-column label="出场时间" min-width="150">
          <template slot-scope="{ row }">
            {{ row.carInout != '3307' ? '-' : row.openTime }}
          </template>
        </el-table-column>
        <el-table-column label="停车时间" min-width="120">
          <template slot-scope="{ row }">
            {{ row.hours }}小时{{ row.min }}分
          </template>
        </el-table-column>
        <el-table-column label="收费金额" min-width="150">
          <template slot-scope="{ row }">
            <template v-if="row.carType == '1001'">-</template>
            <template v-else>{{ row.payCharge }}</template>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="说明" min-width="150" />
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

    <el-dialog :visible.sync="deleteVisible" title="删除历史数据" width="500px" append-to-body @close="handleDeleteClose">
      <el-form :model="deleteForm" ref="deleteFormRef" label-width="100px">
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker v-model="deleteForm.startTime" type="date" value-format="yyyy-MM-dd" placeholder="请选择开始时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="deleteForm.endTime" type="date" value-format="yyyy-MM-dd" placeholder="请选择结束时间" style="width: 100%" />
        </el-form-item>
      </el-form>
      <div style="text-align: center; padding: 10px 0; color: #f56c6c;">
        <i class="el-icon-warning" style="font-size: 20px;" />
        <span style="margin-left: 8px;">此操作将物理删除指定时间段内的历史记录，不可恢复</span>
      </div>
      <span slot="footer">
        <el-button @click="deleteVisible = false">取消</el-button>
        <el-button type="danger" @click="handleDeleteConfirm" :loading="deleteLoading">确定删除</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listCarInoutHisDetails, deleteCarInoutHis } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListCarInoutHis',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10,
        carNumLike: '',
        startTime: '',
        endTime: ''
      },
      deleteVisible: false,
      deleteLoading: false,
      deleteForm: {
        startTime: '',
        endTime: ''
      }
    }
  },
  mounted() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const params = { ...this.queryParams }
        params.communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listCarInoutHisDetails(params)
        this.list = res.data || []
        this.total = res.records || 0
      } catch (e) {
        this.$message.error(e.message || '查询失败')
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = {
        page: 1, row: 10,
        carNumLike: '',
        startTime: '', endTime: ''
      }
      this.fetchList()
    },
    handleSizeChange(val) {
      this.queryParams.row = val
      this.fetchList()
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.fetchList()
    },
    handleDeleteData() {
      this.deleteForm = {
        startTime: '',
        endTime: ''
      }
      this.deleteVisible = true
    },
    handleDeleteClose() {
      this.deleteForm = { startTime: '', endTime: '' }
    },
    async handleDeleteConfirm() {
      if (!this.deleteForm.startTime || !this.deleteForm.endTime) {
        this.$message.warning('请选择时间段')
        return
      }
      try {
        await this.$confirm('确定要删除该时间段内的历史记录吗？此操作不可恢复。', '二次确认', {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch {
        return
      }
      this.deleteLoading = true
      try {
        const params = { ...this.deleteForm }
        params.communityId = (getCurrentCommunity() || {}).communityId || ''
        await deleteCarInoutHis(params)
        this.$message.success('删除成功')
        this.deleteVisible = false
        this.fetchList()
      } catch (e) {
        this.$message.error(e.message || '删除失败')
      } finally {
        this.deleteLoading = false
      }
    }
  }
}
</script>

<style scoped>
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
