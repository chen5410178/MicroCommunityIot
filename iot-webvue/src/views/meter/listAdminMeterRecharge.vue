<template>
  <div class="list-admin-meter-recharge">
    <el-row :gutter="16">
      <el-col :span="4">
        <CommunitySelector @change="handleCommunityChange" />
      </el-col>
      <el-col :span="20">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.machineNameLike" placeholder="请输入名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.addressLike" placeholder="请输入表号" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-select v-model="queryParams.state" placeholder="请选择充值状态" clearable style="width: 150px">
                <el-option label="等待" value="W" />
                <el-option label="失败" value="F" />
                <el-option label="完成" value="C" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.roomNameLike" placeholder="楼栋-单元-房屋" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-date-picker
                v-model="queryParams.queryStartTime"
                type="datetime"
                placeholder="选择开始时间"
                value-format="yyyy-MM-dd HH:mm:ss"
                style="width: 190px"
              />
            </el-form-item>
            <el-form-item>
              <el-date-picker
                v-model="queryParams.queryEndTime"
                type="datetime"
                placeholder="选择结束时间"
                value-format="yyyy-MM-dd HH:mm:ss"
                style="width: 190px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>充值明细</span>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="communityName" label="小区名称" min-width="120" />
            <el-table-column prop="roomName" label="房屋" min-width="120" />
            <el-table-column prop="typeName" label="表类型" width="100" />
            <el-table-column prop="machineName" label="表名称" min-width="120" />
            <el-table-column prop="address" label="表号" min-width="120" />
            <el-table-column prop="chargeMoney" label="充值金额" width="100" align="center" />
            <el-table-column prop="chargeDegrees" label="充值度数" width="100" align="center" />
            <el-table-column prop="createTime" label="充值时间" min-width="160" />
            <el-table-column label="充值状态" width="100" align="center">
              <template slot-scope="{ row }">
                <el-tag :type="stateColorMap[row.state] || 'info'" size="small">
                  {{ stateMap[row.state] || '-' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="说明" min-width="120">
              <template slot-scope="{ row }">
                {{ row.remark || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" min-width="160" />
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
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { listAdminMeterCharge } from '@/api/meter/meterApi'
import CommunitySelector from '@/components/common/CommunitySelector.vue'

export default {
  name: 'ListAdminMeterRecharge',
  components: { CommunitySelector },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      stateMap: { 'W': '等待', 'F': '失败', 'C': '完成' },
      stateColorMap: { 'W': 'warning', 'F': 'danger', 'C': 'success' },
      queryParams: {
        page: 1,
        row: 10,
        communityId: '',
        machineNameLike: '',
        addressLike: '',
        state: '',
        roomNameLike: '',
        queryStartTime: '',
        queryEndTime: '',
        detailType: '1001'
      }
    }
  },
  mounted() {
    // CommunitySelector 会自动加载并 emit change 事件
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listAdminMeterCharge(this.queryParams)
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    handleCommunityChange(community) {
      this.queryParams.communityId = community.communityId || ''
      this.queryParams.page = 1
      this.fetchList()
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams.communityId = ''
      this.queryParams.machineNameLike = ''
      this.queryParams.addressLike = ''
      this.queryParams.state = ''
      this.queryParams.roomNameLike = ''
      this.queryParams.queryStartTime = ''
      this.queryParams.queryEndTime = ''
      this.queryParams.page = 1
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
.pagination-container { margin-top: 16px; text-align: right; }
</style>
