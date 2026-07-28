<template>
  <div class="list-admin-inout">
    <el-card shadow="never" class="search-card">
      <div slot="header" class="card-header">
        <span>查询条件</span>
      </div>
      <el-form :inline="true" :model="queryParams">
        <el-form-item>
          <el-select v-model="queryParams.machineId" placeholder="请选择门禁设备" clearable style="width: 200px">
            <el-option v-for="item in accessControls" :key="item.machineId" :label="item.machineName" :value="item.machineId" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.name" placeholder="请输入用户名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.tel" placeholder="请输入手机号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.state" placeholder="请选择开门状态" clearable style="width: 160px">
            <el-option label="全部" value="" />
            <el-option label="开门成功" value="C" />
            <el-option label="开门失败" value="F" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="queryParams.queryStartTime"
            type="datetime"
            placeholder="选择开始时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-date-picker
            v-model="queryParams.queryEndTime"
            type="datetime"
            placeholder="选择结束时间"
            value-format="yyyy-MM-dd HH:mm:ss"
            clearable
            style="width: 200px"
          />
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
        <el-table-column label="人脸" width="90" align="center">
          <template slot-scope="{ row }">
            <el-image v-if="row.facePath" :src="row.facePath" style="width: 60px; height: 60px; border-radius: 4px" fit="cover" :preview-src-list="[row.facePath]" />
            <img v-else src="/img/noPhoto.jpg" style="width: 60px; height: 60px; border-radius: 4px" />
          </template>
        </el-table-column>
        <el-table-column prop="communityName" label="小区名称" min-width="120" />
        <el-table-column prop="inoutId" label="编号" min-width="100" />
        <el-table-column prop="machineName" label="设备名称" min-width="120" />
        <el-table-column prop="machineCode" label="设备编码" min-width="120" />
        <el-table-column prop="name" label="用户名称" min-width="100" />
        <el-table-column label="开门方式" width="90" align="center">
          <template slot-scope="{ row }">
            {{
              row.openTypeCd == '1000' ? '人脸' :
              row.openTypeCd == '2000' ? '卡片' :
              row.openTypeCd == '3000' ? '二维码' :
              row.openTypeCd == '4000' ? '密码' :
              row.openTypeCd == '5000' ? '远程' : '其他'
            }}
          </template>
        </el-table-column>
        <el-table-column prop="tel" label="手机号" width="130">
          <template slot-scope="{ row }">
            {{ row.tel || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="idCard" label="用户身份证" min-width="160">
          <template slot-scope="{ row }">
            {{ row.idCard || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="similar" label="相似度" width="80" align="center" />
        <el-table-column label="开门状态" width="90" align="center">
          <template slot-scope="{ row }">
            {{ row.state == 'C' ? '开门成功' : '开门失败' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="开门时间" min-width="150" />
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
import { listAdminAccessControlInout, listAdminAccessControl } from '@/api/accessControl/accessControlApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListAdminInout',
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      accessControls: [],
      queryParams: {
        page: 1,
        row: 10,
        communityId: '',
        machineId: '',
        name: '',
        tel: '',
        state: '',
        queryStartTime: '',
        queryEndTime: ''
      }
    }
  },
  computed: {
    currentCommunityId() {
      return (getCurrentCommunity() || {}).communityId || ''
    }
  },
  watch: {
    currentCommunityId: {
      immediate: true,
      handler(val) {
        this.queryParams.communityId = val
        if (val) {
          this.loadAccessControls()
          this.fetchList()
        }
      }
    }
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await listAdminAccessControlInout(this.queryParams)
        this.list = res.data || []
        this.total = res.total || 0
      } finally {
        this.loading = false
      }
    },
    async loadAccessControls() {
      try {
        const res = await listAdminAccessControl({ page: 1, row: 100, communityId: this.queryParams.communityId })
        this.accessControls = res.data || []
      } catch (e) {
        this.accessControls = []
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
        communityId: this.queryParams.communityId,
        machineId: '',
        name: '',
        tel: '',
        state: '',
        queryStartTime: '',
        queryEndTime: ''
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
