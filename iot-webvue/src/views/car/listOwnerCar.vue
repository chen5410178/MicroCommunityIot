<template>
  <div class="list-owner-car" style="display: flex; gap: 16px;">
    <div class="left-sidebar" style="width: 180px; flex-shrink: 0;">
      <el-card shadow="never">
        <ul class="lease-type-list">
          <li
            v-for="item in leaseTypes"
            :key="item.statusCd"
            :class="{ active: queryParams.leaseType === item.statusCd }"
            @click="switchLeaseType(item)"
          >{{ item.name }}</li>
        </ul>
      </el-card>
    </div>
    <div class="right-content" style="flex: 1; min-width: 0;">
      <el-card shadow="never" class="search-card">
        <div slot="header" class="card-header">
          <span>查询条件</span>
        </div>
        <el-form :inline="true" :model="queryParams">
          <el-form-item>
            <el-input v-model="queryParams.carNumLike" placeholder="请输入车牌号" clearable style="width: 160px" />
          </el-form-item>
          <el-form-item>
            <el-select v-model="queryParams.valid" placeholder="请选择车位状态" clearable style="width: 160px">
              <el-option label="正常" value="1" />
              <el-option label="过期" value="3" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-input v-model="queryParams.ownerName" placeholder="请填写业主名称" clearable style="width: 160px" />
          </el-form-item>
          <el-form-item>
            <el-input v-model="queryParams.link" placeholder="请输入联系方式" clearable style="width: 160px" />
          </el-form-item>
          <el-form-item>
            <el-input v-model="queryParams.memberCarNumLike" placeholder="请输入成员车牌号" clearable style="width: 160px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <el-card shadow="never" class="table-card">
        <div slot="header" class="card-header">
          <span>车辆信息</span>
          <div>
            <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
            <el-button size="small" @click="handleImport">车辆导入</el-button>
            <el-button size="small" @click="handleExport">车辆导出</el-button>
          </div>
        </div>
        <el-table :data="list" v-loading="loading" border stripe>
          <el-table-column label="车牌号" min-width="120">
            <template slot-scope="{ row }">
              <el-button type="text">{{ row.carNum }}</el-button>
            </template>
          </el-table-column>
          <el-table-column label="成员车辆" width="80" align="center">
            <template slot-scope="{ row }">
              <el-button type="text" @click="goMemberCar(row)">{{ row.memberCarCount }}</el-button>
            </template>
          </el-table-column>
          <el-table-column prop="roomName" label="房屋号" min-width="120" />
          <el-table-column label="车牌类型" min-width="100">
            <template slot-scope="{ row }">
              <template v-if="row.leaseType === 'T'">临时车</template>
              <template v-else>{{ row.leaseTypeName }}</template>
            </template>
          </el-table-column>
          <el-table-column prop="carTypeName" label="车辆类型" min-width="100" />
          <el-table-column label="业主" min-width="120">
            <template slot-scope="{ row }">
              {{ row.ownerName }}({{ row.link }})
            </template>
          </el-table-column>
          <el-table-column label="联系人" min-width="100">
            <template slot-scope="{ row }">
              {{ row.contact || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="联系电话" min-width="120">
            <template slot-scope="{ row }">
              {{ row.tel || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="停车场" min-width="150">
            <template slot-scope="{ row }">
              {{ row.paNum || '-' }}<template v-if="row.num">（{{ row.num }}）</template>
            </template>
          </el-table-column>
          <el-table-column label="有效期" min-width="180">
            <template slot-scope="{ row }">
              <template v-if="row.leaseType === 'H'">{{ row.startTime }}~{{ row.endTime }}</template>
              <template v-else>-</template>
            </template>
          </el-table-column>
          <el-table-column label="备注" min-width="120">
            <template slot-scope="{ row }">
              {{ row.remark || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="状态" min-width="120">
            <template slot-scope="{ row }">
              <span :class="{ 'text-danger': getCarState(row) === '到期' }">{{ getCarState(row) }}</span>
              <span v-if="row.iotStateName">(<el-button type="text" @click="viewIotState(row)">{{ row.iotStateName }}</el-button>)</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right" align="center">
            <template slot-scope="{ row }">
              <template v-if="row.carTypeCd !== '1003'">
                <el-button size="mini" type="text" @click="handleEdit(row)">修改</el-button>
              </template>
              <el-button size="mini" type="text" @click="handleDetail(row)">详情</el-button>
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
    </div>

    <EditCar ref="editCarRef" @success="fetchList" />
    <DeleteOwnerCar ref="delCarRef" @success="fetchList" />
    <ImportOwnerCar ref="importCarRef" @success="fetchList" />
  </div>
</template>

<script>
import { queryOwnerCars, exportData } from '@/api/car/carApi'
import { listDicts } from '@/api/system/systemApi'
import EditCar from '@/components/car/EditCar.vue'
import DeleteOwnerCar from '@/components/car/DeleteOwnerCar.vue'
import ImportOwnerCar from '@/components/car/ImportOwnerCar.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListOwnerCar',
  components: { EditCar, DeleteOwnerCar, ImportOwnerCar },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10,
        carNumLike: '',
        valid: '',
        leaseType: '',
        ownerName: '',
        link: '',
        memberCarNumLike: ''
      },
      leaseTypes: [
        { statusCd: '', name: '全部车辆' }
      ]
    }
  },
  mounted() {
    this.loadLeaseTypes()
    this.fetchList()
  },
  methods: {
    async loadLeaseTypes() {
      try {
        const res = await listDicts({ tableName: 'owner_car', tableColumns: 'lease_type' })
        const data = res || []
        const keepTypes = ['H', 'I', 'NM']
        this.leaseTypes = [{ statusCd: '', name: '全部车辆' }]
        data.forEach(item => {
          if (keepTypes.indexOf(item.statusCd) > -1) {
            this.leaseTypes.push({ statusCd: item.statusCd, name: item.name })
          }
        })
        this.leaseTypes.push({ statusCd: 'expireCar', name: '到期车辆' })
      } catch { /* ignore */ }
    },
    switchLeaseType(item) {
      this.queryParams.leaseType = item.statusCd
      this.queryParams.page = 1
      this.fetchList()
    },
    async fetchList() {
      this.loading = true
      try {
        const params = { ...this.queryParams }
        params.communityId = (getCurrentCommunity() || {}).communityId || ''
        params.carTypeCd = '1001'
        if (params.leaseType === 'expireCar') {
          params.leaseType = ''
          params.valid = '3'
        }
        const res = await queryOwnerCars(params)
        this.list = res.data || []
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
      this.queryParams = {
        page: 1, row: 10,
        carNumLike: '', valid: '',
        leaseType: '', ownerName: '', link: '', memberCarNumLike: ''
      }
      this.fetchList()
    },
    handleAdd() {
      this.$router.push('/pages/car/hireParkingSpace')
    },
    handleImport() {
      this.$refs.importCarRef.open()
    },
    handleExport() {
      const params = {
        ...this.queryParams,
        communityId: (getCurrentCommunity() || {}).communityId || '',
        pagePath: 'exportOwnerCar'
      }
      exportData(params).then(res => {
        if (res.code === 0) {
          this.$message.success(res.msg || '导出成功')
          this.$router.push('/pages/property/downloadTempFile?tab=下载中心')
        } else {
          this.$message.error(res.msg || '导出失败')
        }
      }).catch(() => {
        this.$message.error('导出失败')
      })
    },
    handleEdit(row) {
      this.$refs.editCarRef.open(row)
    },
    handleDelete(row) {
      this.$refs.delCarRef.open(row)
    },
    handleDetail(row) {
      this.goDetail(row)
    },
    goDetail(row) {
      this.$router.push({ path: '/pages/car/carDetail', query: { memberId: row.memberId } })
    },
    goMemberCar(row) {
      this.$router.push({ path: '/pages/car/listOwnerCarMember', query: { carId: row.carId, carNum: row.carNum } })
    },
    getCarState(car) {
      if (car.state === '3003') return '到期'
      if (!car.endTime) return '正常'
      if (new Date(car.endTime).getTime() > Date.now()) return '正常'
      return '到期'
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
.lease-type-list {
  list-style: none;
  margin: 0;
  padding: 0;
}
.lease-type-list li {
  padding: 10px 12px;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 5px;
  text-align: center;
  font-size: 14px;
  color: #606266;
  transition: all 0.3s;
}
.lease-type-list li:hover {
  background-color: #f5f7fa;
}
.lease-type-list li.active {
  color: white;
  background-color: #409eff;
  font-weight: bold;
}
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
.text-danger { color: #f56c6c; font-weight: bold; }
</style>
