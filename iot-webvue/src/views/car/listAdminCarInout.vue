<template>
  <div class="list-admin-car-inout">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-select v-model="queryParams.communityId" placeholder="请选择小区" clearable style="width: 180px" @change="handleSearch">
            <el-option
              v-for="item in communityList"
              :key="item.communityId"
              :label="item.name"
              :value="item.communityId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.carNumLike" placeholder="请输入车牌号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.state" placeholder="请选择车辆状态" clearable style="width: 180px">
            <el-option label="进场状态" value="100300" />
            <el-option label="支付完成" value="100400" />
            <el-option label="离场状态" value="100500" />
            <el-option label="支付超时重新支付" value="100600" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.carType" placeholder="请选择车牌类型" clearable style="width: 180px">
            <el-option label="临时车" value="T" />
            <el-option label="出售车辆" value="S" />
            <el-option label="月租车" value="H" />
            <el-option label="白名单" value="W" />
            <el-option label="黑名单" value="B" />
            <el-option label="内部车" value="I" />
            <el-option label="免费车" value="NM" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-date-picker v-model="queryParams.startTime" type="date" value-format="yyyy-MM-dd" placeholder="请选择开始时间" style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-date-picker v-model="queryParams.endTime" type="date" value-format="yyyy-MM-dd" placeholder="请选择结束时间" style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>进出记录</span>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column label="进场图" width="80" align="center">
          <template slot-scope="{ row }">
            <el-image v-if="row.photoJpg" style="width: 50px; height: 50px" :src="row.photoJpg" :preview-src-list="[row.photoJpg]" fit="cover" />
            <img v-else src="/img/noPhoto.jpg" style="width: 50px; height: 50px" />
          </template>
        </el-table-column>
        <el-table-column prop="communityName" label="小区名称" min-width="120" />
        <el-table-column prop="inoutId" label="进出场编号" min-width="120" />
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
            <template v-else>
              {{ row.payCharge }}
              <el-button v-if="row.inState == '100300' || row.inState == '100400' || row.inState == '100600'" type="text" size="small" @click="handleCustomCarOut(row)">出场</el-button>
            </template>
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

    <CustomCarInout ref="customCarInoutRef" @success="fetchList" />
  </div>
</template>

<script>
import { listAdminCarInoutDetails } from '@/api/car/carApi'
import { mapState } from 'vuex'
import CustomCarInout from '@/components/car/CustomCarInout.vue'

export default {
  name: 'ListAdminCarInout',
  components: { CustomCarInout },
  computed: {
    ...mapState('app', ['communityList'])
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1,
        row: 10,
        communityId: '',
        carNumLike: '',
        state: '',
        carType: '',
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
        const res = await listAdminCarInoutDetails(this.queryParams)
        this.list = res.data || []
        this.total = res.records || 0
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
        communityId: '', carNumLike: '',
        state: '', carType: '',
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
    handleCustomCarOut(row) {
      this.$refs.customCarInoutRef.open({
        carNum: row.carNum,
        payCharge: row.payCharge,
        machineId: row.machineId,
        boxId: row.boxId,
        paId: row.paId
      })
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
