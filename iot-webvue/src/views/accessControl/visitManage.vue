<template>
  <div class="list-visit-manage">
    <el-row :gutter="16">
      <el-col :span="3">
        <el-card shadow="never" class="left-type-card">
          <div slot="header" class="card-header">
            <span>访客类型</span>
          </div>
          <div class="type-list">
            <div
              v-for="item in visitTypeList"
              :key="item.typeId || '__all__'"
              class="type-item"
              :class="{ 'type-active': queryParams.typeId === (item.typeId || '') }"
              @click="switchVisitType(item)"
            >
              {{ item.name || '全部' }}
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="21">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-select v-model="queryParams.state" placeholder="请选择审核状态" clearable style="width: 160px">
                <el-option v-for="item in stateList" :key="item.statusCd" :label="item.statusName" :value="item.statusCd" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.nameLike" placeholder="请输入访客姓名" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-select v-model="queryParams.visitGender" placeholder="请选择性别" clearable style="width: 120px">
                <el-option label="男" value="0" />
                <el-option label="女" value="1" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.phoneNumberLike" placeholder="请输入电话号码" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.roomNameLike" placeholder="楼栋-单元-房屋" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.ownerNameLike" placeholder="请输入拜访业主" clearable style="width: 160px" />
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
            <span>访客人员</span>
            <div>
              <el-button size="small" @click="showQrCode">访客二维码</el-button>
              <el-button type="primary" size="small" @click="handleAdd">登记</el-button>
            </div>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="visitId" label="拜访ID" min-width="120" />
            <el-table-column prop="name" label="姓名" min-width="100" />
            <el-table-column label="性别" width="70" align="center">
              <template slot-scope="{ row }">{{ row.visitGender == 0 ? '男' : '女' }}</template>
            </el-table-column>
            <el-table-column prop="phoneNumber" label="电话号码" min-width="120" />
            <el-table-column prop="roomName" label="拜访房屋" min-width="150" />
            <el-table-column label="拜访业主" min-width="100">
              <template slot-scope="{ row }">{{ row.ownerName || '-' }}</template>
            </el-table-column>
            <el-table-column label="访客车辆" min-width="120">
              <template slot-scope="{ row }">{{ row.carNum || '-' }}</template>
            </el-table-column>
            <el-table-column prop="visitTypeName" label="访客类型" min-width="100" />
            <el-table-column label="拜访事由" min-width="150" show-overflow-tooltip>
              <template slot-scope="{ row }">{{ row.visitCase || '-' }}</template>
            </el-table-column>
            <el-table-column label="拜访时间" min-width="160">
              <template slot-scope="{ row }">{{ row.visitTime || '-' }}</template>
            </el-table-column>
            <el-table-column label="离开时间" min-width="160">
              <template slot-scope="{ row }">{{ row.departureTime || '-' }}</template>
            </el-table-column>
            <el-table-column prop="stateName" label="审核状态" width="100" align="center" />
            <el-table-column label="审核备注" min-width="150" show-overflow-tooltip>
              <template slot-scope="{ row }">{{ row.msg || '-' }}</template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" min-width="160" />
            <el-table-column label="操作" width="200" fixed="right" align="center">
              <template slot-scope="{ row }">
                <template v-if="row.state == 0">
                  <el-button size="small" type="text" @click="handleAuth(row)">审核</el-button>
                  <el-button size="small" type="text" @click="handleEdit(row)">修改</el-button>
                </template>
                <el-button size="small" type="text" style="color: #f56c6c" @click="handleDelete(row)">删除</el-button>
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
      </el-col>
    </el-row>
    <DeleteVisit :visible.sync="delVisible" :del-data="currentRow" @success="fetchList" />
    <VisitAuth :visible.sync="authVisible" :auth-data="currentRow" @success="fetchList" />
    <VisitQrCode :visible.sync="qrVisible" />
  </div>
</template>

<script>
import { listVisits, listVisitTypes } from '@/api/accessControl/accessControlApi'
import { listDicts } from '@/api/dev/devApi'
import DeleteVisit from '@/components/accessControl/DeleteVisit.vue'
import VisitAuth from '@/components/accessControl/VisitAuth.vue'
import VisitQrCode from '@/components/accessControl/VisitQrCode.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListVisitManage',
  components: { DeleteVisit, VisitAuth, VisitQrCode },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      visitTypeList: [],
      stateList: [],
      delVisible: false,
      authVisible: false,
      qrVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        state: '',
        nameLike: '',
        visitGender: '',
        phoneNumberLike: '',
        roomNameLike: '',
        ownerNameLike: '',
        typeId: '',
        queryStartTime: '',
        queryEndTime: ''
      }
    }
  },
  mounted() {
    this.loadDicts()
    this.fetchList()
  },
  methods: {
    async loadDicts() {
      try {
        const [dictRes, typeRes] = await Promise.all([
          listDicts({ tableName: 'visit', columnName: 'state' }),
          listVisitTypes({ page: -1, row: 100, communityId: (getCurrentCommunity() || {}).communityId })
        ])
        this.stateList = dictRes.data || []
        const types = typeRes.data || []
        this.visitTypeList = [{ name: '全部', typeId: '' }, ...types]
      } catch (e) {
        console.error('加载字典失败', e)
      }
    },
    async fetchList() {
      this.loading = true
      try {
        const params = {
          ...this.queryParams,
          communityId: (getCurrentCommunity() || {}).communityId
        }
        const res = await listVisits(params)
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    switchVisitType(item) {
      this.queryParams.typeId = item.typeId || ''
      this.queryParams.page = 1
      this.fetchList()
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = {
        page: 1,
        row: 10,
        state: '',
        nameLike: '',
        visitGender: '',
        phoneNumberLike: '',
        roomNameLike: '',
        ownerNameLike: '',
        typeId: '',
        queryStartTime: '',
        queryEndTime: ''
      }
      this.fetchList()
    },
    handleAdd() {
      this.$router.push('/pages/accessControl/addVisit')
    },
    handleEdit(row) {
      this.$router.push('/pages/accessControl/editVisit?visitId=' + row.visitId)
    },
    handleDelete(row) {
      this.currentRow = { ...row }
      this.delVisible = true
    },
    handleAuth(row) {
      this.currentRow = { ...row }
      this.authVisible = true
    },
    handleDetail(row) {
      this.$router.push('/pages/accessControl/visitDetail?visitId=' + row.visitId + '&phoneNumber=' + row.phoneNumber)
    },
    showQrCode() {
      this.qrVisible = true
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
.left-type-card { margin-bottom: 16px; }
.type-list { }
.type-item {
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 4px;
  font-size: 13px;
  color: #606266;
  transition: all 0.2s;
}
.type-item:hover { background: #ecf5ff; color: #409eff; }
.type-active { background: #409eff; color: #fff; }
.type-active:hover { background: #409eff; color: #fff; }
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
