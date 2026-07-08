<template>
  <div class="list-admin-meter-manage">
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
          <el-input v-model="queryParams.machineNameLike" placeholder="请输入名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.address" placeholder="请输入表号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.implBean" placeholder="请选择厂家" clearable style="width: 180px">
            <el-option
              v-for="item in factoryList"
              :key="item.factoryId"
              :label="item.factoryName"
              :value="item.factoryId"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.machineModel" placeholder="请选择模式" clearable style="width: 150px">
            <el-option label="充值模式" value="1001" />
            <el-option label="抄表模式" value="2002" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.roomNameLike" placeholder="楼栋-单元-房屋" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>智能水电表</span>
      </div>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="machineId" label="编号" width="100" align="center" />
        <el-table-column prop="communityName" label="小区名称" min-width="120" />
        <el-table-column prop="machineName" label="名称" min-width="120" />
        <el-table-column prop="address" label="表号" min-width="120" />
        <el-table-column label="表类型" width="90" align="center">
          <template slot-scope="{ row }">
            {{ row.meterTypeName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="模式" width="90" align="center">
          <template slot-scope="{ row }">
            {{ row.machineModel == '1001' ? '充值模式' : '抄表模式' }}
          </template>
        </el-table-column>
        <el-table-column prop="roomName" label="房屋" min-width="120" />
        <el-table-column prop="implBeanName" label="设备厂家" min-width="120" />
        <el-table-column label="当前度数" width="100" align="center">
          <template slot-scope="{ row }">
            {{ row.machineModel == '1001' ? (row.degree || row.prestoreDegrees || '-') : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="剩余金额" width="100" align="center">
          <template slot-scope="{ row }">
            {{ row.machineModel == '1001' ? (row.curDegrees || '-') : (row.curDegrees || '-') }}
          </template>
        </el-table-column>
        <el-table-column prop="curReadingTime" label="读表时间" min-width="150" />
        <el-table-column prop="stateName" label="状态" width="80" align="center" />
        <el-table-column prop="heartbeatTime" label="心跳时间" min-width="150" />
        <el-table-column label="操作" width="80" fixed="right" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="handleDetail(row)">详情</el-button>
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
</template>

<script>
import { listAdminMeterMachine, listMeterMachineFactory } from '@/api/meter/meterApi'
import { getCommunityList } from '@/utils/auth'

export default {
  name: 'ListAdminMeter',
  computed: {
    communityList() { return getCommunityList() || [] }
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      factoryList: [],
      queryParams: {
        page: 1,
        row: 10,
        communityId: '',
        machineNameLike: '',
        address: '',
        implBean: '',
        machineModel: '',
        roomNameLike: ''
      }
    }
  },
  mounted() {
    this.fetchList()
    this.loadFactoryList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listAdminMeterMachine(this.queryParams)
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    async loadFactoryList() {
      try {
        const res = await listMeterMachineFactory({ page: 1, row: 500 })
        this.factoryList = res.data || []
      } catch (e) {
        console.error('加载厂家列表失败', e)
      }
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = {
        page: 1,
        row: 10,
        communityId: '',
        machineNameLike: '',
        address: '',
        implBean: '',
        machineModel: '',
        roomNameLike: ''
      }
      this.fetchList()
    },
    handleDetail(row) {
      this.$router.push(`/pages/meter/adminMeterDetail?machineId=${row.machineId}&roomId=${row.roomId}`)
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
.pagination-container { margin-top: 16px; text-align: right; }
</style>
