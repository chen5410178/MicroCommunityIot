<template>
  <div class="list-person-face">
    <div class="page-container">
      <div class="left-panel">
        <el-card shadow="never" class="state-card">
          <div slot="header" class="card-header">
            <span>审核状态</span>
          </div>
          <div class="state-list">
            <div
              v-for="item in stateList"
              :key="item.statusCd"
              class="state-item"
              :class="{ active: currentState === item.statusCd }"
              @click="switchState(item)"
            >
              {{ item.name }}
            </div>
          </div>
        </el-card>
      </div>
      <div class="right-panel">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.roomName" placeholder="请输入房屋名称" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.personName" placeholder="请输入人员名称" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.personTel" placeholder="请输入人员电话" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-select v-model="queryParams.ownerTypeCd" placeholder="请选择人员类型" clearable style="width: 140px">
                <el-option label="业主本人" value="1001" />
                <el-option label="家庭成员" value="1002" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-date-picker v-model="queryParams.queryStartTime" type="date" placeholder="请选择开始时间" value-format="yyyy-MM-dd" style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-date-picker v-model="queryParams.queryEndTime" type="date" placeholder="请选择结束时间" value-format="yyyy-MM-dd" style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>人脸审核</span>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column label="人脸" width="90" align="center">
              <template slot-scope="{ row }">
                <el-image v-if="row.faceUrl" :src="row.faceUrl" style="width: 60px; height: 60px; border-radius: 4px" fit="cover" />
                <img v-else src="/img/noPhoto.jpg" style="width: 60px; height: 60px; border-radius: 4px" />
              </template>
            </el-table-column>
            <el-table-column prop="roomName" label="房屋名称" min-width="120" />
            <el-table-column prop="qrcodeName" label="二维码" min-width="100" />
            <el-table-column prop="personName" label="人员名称" min-width="100" />
            <el-table-column prop="personTel" label="人员电话" min-width="120" />
            <el-table-column label="人员类型" width="100" align="center">
              <template slot-scope="{ row }">
                {{ row.ownerTypeCd == '1001' ? '业主本人' : '家庭成员' }}
              </template>
            </el-table-column>
            <el-table-column prop="openId" label="微信openId" min-width="160" show-overflow-tooltip />
            <el-table-column prop="stateName" label="状态" width="80" align="center" />
            <el-table-column label="审核说明" min-width="120" show-overflow-tooltip>
              <template slot-scope="{ row }">{{ row.remark || '-' }}</template>
            </el-table-column>
            <el-table-column prop="createTime" label="申请时间" min-width="150" />
            <el-table-column prop="pfId" label="编号" min-width="120" />
            <el-table-column label="操作" width="120" fixed="right" align="center">
              <template slot-scope="{ row }">
                <template v-if="row.state == 'W'">
                  <el-button size="small" type="text" @click="handleAudit(row)">审核</el-button>
                  <el-button size="small" type="text" style="color: #f56c6c" @click="handleDelete(row)">删除</el-button>
                </template>
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
    <AuditPersonFace :visible.sync="auditVisible" :audit-data="currentRow" @success="handleSuccess" />
    <DeletePersonFace :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listPersonFaces } from '@/api/accessControl/personFaceApi'
import AuditPersonFace from '@/components/accessControl/AuditPersonFace.vue'
import DeletePersonFace from '@/components/accessControl/DeletePersonFace.vue'

export default {
  name: 'ListPersonFace',
  components: { AuditPersonFace, DeletePersonFace },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      stateList: [
        { statusCd: '', name: '全部' },
        { statusCd: 'W', name: '待审核' },
        { statusCd: 'C', name: '审核通过' },
        { statusCd: 'F', name: '审核拒绝' }
      ],
      currentState: '',
      currentRow: null,
      auditVisible: false,
      delVisible: false,
      queryParams: {
        page: 1,
        row: 10,
        roomName: '',
        personName: '',
        personTel: '',
        ownerTypeCd: '',
        state: '',
        queryStartTime: '',
        queryEndTime: ''
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
        const params = {
          ...this.queryParams,
          communityId: this.$store.state.app.currentCommunity.communityId
        }
        const res = await listPersonFaces(params)
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    switchState(item) {
      this.currentState = item.statusCd
      this.queryParams.state = item.statusCd
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
        roomName: '',
        personName: '',
        personTel: '',
        ownerTypeCd: '',
        state: this.currentState,
        queryStartTime: '',
        queryEndTime: ''
      }
      this.fetchList()
    },
    handleAudit(row) {
      this.currentRow = { ...row }
      this.auditVisible = true
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
.left-panel { width: 160px; flex-shrink: 0; }
.right-panel { flex: 1; min-width: 0; }
.state-card { height: 100%; }
.state-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.state-list { max-height: calc(100vh - 200px); overflow-y: auto; }
.state-item { padding: 10px 12px; cursor: pointer; border-bottom: 1px solid #ebeef5; font-size: 13px; transition: all 0.2s; }
.state-item:hover { background: #ecf5ff; color: #409eff; }
.state-item.active { background: #ecf5ff; color: #409eff; font-weight: 600; }
.search-card { margin-bottom: 16px; }
.search-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.table-card { margin-bottom: 0; }
.table-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
