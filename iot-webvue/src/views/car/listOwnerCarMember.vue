<template>
  <div class="list-owner-car-member">
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>{{ carNum }} 成员车辆</span>
        <div>
          <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
          <el-button size="small" @click="$router.go(-1)">返回</el-button>
        </div>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="carNum" label="车牌号" min-width="120" />
        <el-table-column prop="roomName" label="房屋号" min-width="120" />
        <el-table-column prop="carBrand" label="车辆品牌" min-width="100" />
        <el-table-column prop="carTypeName" label="车辆类型" min-width="100" />
        <el-table-column prop="carColor" label="颜色" width="80" />
        <el-table-column label="业主" min-width="120">
          <template slot-scope="{ row }">{{ row.ownerName }}({{ row.link }})</template>
        </el-table-column>
        <el-table-column label="车位" min-width="130">
          <template slot-scope="{ row }">
            <template v-if="row.areaNum">{{ row.areaNum }}车场{{ row.num }}车位</template>
            <template v-else>无</template>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="起租时间" min-width="150" />
        <el-table-column prop="endTime" label="截止时间" min-width="150" />
        <el-table-column label="状态" min-width="100">
          <template slot-scope="{ row }">
            {{ row.stateName }}
            <span v-if="row.iotStateName">(<el-button type="text" @click="viewIotState(row)">{{ row.iotStateName }}</el-button>)</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="mini" type="text" @click="handleEdit(row)">修改</el-button>
            <el-button size="mini" type="text" style="color: #f56c6c" @click="handleDelete(row)">删除</el-button>
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

    <AddMemberCar ref="addMemberCarRef" @success="fetchList" />
    <EditMemberCar ref="editMemberCarRef" @success="fetchList" />
    <DeleteMemberCar ref="delMemberCarRef" @success="fetchList" />
  </div>
</template>

<script>
import { queryOwnerCars } from '@/api/car/carApi'
import AddMemberCar from '@/components/car/AddMemberCar.vue'
import EditMemberCar from '@/components/car/EditMemberCar.vue'
import DeleteMemberCar from '@/components/car/DeleteMemberCar.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListOwnerCarMember',
  components: { AddMemberCar, EditMemberCar, DeleteMemberCar },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      carId: '',
      carNum: '',
      queryParams: {
        page: 1,
        row: 10
      }
    }
  },
  mounted() {
    this.carId = this.$route.query.carId || ''
    this.carNum = this.$route.query.carNum || ''
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await queryOwnerCars({
          page: this.queryParams.page,
          row: this.queryParams.row,
          carId: this.carId,
          carTypeCd: '1002',
          communityId: (getCurrentCommunity() || {}).communityId || ''
        })
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    handleAdd() {
      this.$refs.addMemberCarRef.open({ carId: this.carId })
    },
    handleEdit(row) {
      this.$refs.editMemberCarRef.open(row)
    },
    handleDelete(row) {
      this.$refs.delMemberCarRef.open(row)
    },
    viewIotState(car) {
      this.$alert(car.iotRemark || '无', car.carNum + ' 同步物联网详情', { confirmButtonText: '确定' })
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
