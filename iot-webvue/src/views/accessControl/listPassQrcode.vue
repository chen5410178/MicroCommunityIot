<template>
  <div class="list-pass-qrcode">
    <div class="layout-container">
      <div class="left-panel">
        <ul class="state-list">
          <li
            v-for="item in stateList"
            :key="item.statusCd"
            :class="{ 'state-active': queryParams.state === item.statusCd }"
            @click="handleSwitchState(item)"
          >
            {{ item.name || item.statusName }}
          </li>
        </ul>
      </div>
      <div class="right-panel">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.personName" placeholder="请输入名称" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.personTel" placeholder="请输入手机号" clearable style="width: 160px" />
            </el-form-item>
            <el-form-item>
              <el-date-picker
                v-model="queryParams.queryStartTime"
                type="datetime"
                placeholder="选择开始时间"
                value-format="yyyy-MM-dd HH:mm:ss"
                style="width: 180px"
              />
            </el-form-item>
            <el-form-item>
              <el-date-picker
                v-model="queryParams.queryEndTime"
                type="datetime"
                placeholder="选择结束时间"
                value-format="yyyy-MM-dd HH:mm:ss"
                style="width: 180px"
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
            <span>通行码</span>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="pqId" label="编号" min-width="100" />
            <el-table-column prop="personName" label="名称" min-width="100" />
            <el-table-column prop="personTel" label="手机号" min-width="120" />
            <el-table-column prop="openId" label="openId" min-width="140" show-overflow-tooltip />
            <el-table-column prop="qrcodeTime" label="刷新时间" min-width="160" />
            <el-table-column prop="stateName" label="状态" min-width="80" align="center" />
            <el-table-column prop="remark" label="审核说明" min-width="120" show-overflow-tooltip>
              <template slot-scope="{ row }">
                {{ row.remark || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="申请时间" min-width="160" />
            <el-table-column label="操作" width="210" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button size="small" type="text" @click="handleViewQrcode(row)">通行码</el-button>
                <el-button v-if="row.state === 'W'" size="small" type="text" @click="handleAudit(row)">审核</el-button>
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
      </div>
    </div>
    <AuditPassQrcode :visible.sync="auditVisible" :audit-data="currentRow" @success="handleSuccess" />
    <DeletePassQrcode :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
    <ViewQrCode :visible.sync="qrcodeVisible" :url="qrcodeUrl" title="通行码" :remark="qrcodeRemark" />
  </div>
</template>

<script>
import { listPassQrcodes } from '@/api/accessControl/accessControlApi'
import { listDicts } from '@/api/dev/devApi'
import AuditPassQrcode from '@/components/accessControl/AuditPassQrcode.vue'
import DeletePassQrcode from '@/components/accessControl/DeletePassQrcode.vue'
import ViewQrCode from '@/components/common/ViewQrCode.vue'

export default {
  name: 'ListPassQrcode',
  components: { AuditPassQrcode, DeletePassQrcode, ViewQrCode },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      auditVisible: false,
      delVisible: false,
      qrcodeVisible: false,
      currentRow: null,
      qrcodeUrl: '',
      qrcodeRemark: '',
      stateList: [{ statusCd: '', name: '全部' }],
      queryParams: {
        page: 1,
        row: 10,
        personName: '',
        personTel: '',
        state: '',
        queryStartTime: '',
        queryEndTime: ''
      }
    }
  },
  mounted() {
    this.loadStates()
    this.fetchList()
  },
  methods: {
    async loadStates() {
      try {
        const res = await listDicts({ tableName: 'person_face', columnName: 'state' })
        const dicts = res.data || []
        dicts.forEach(item => {
          this.stateList.push(item)
        })
      } catch (e) {
        console.error('加载状态字典失败', e)
      }
    },
    async fetchList() {
      this.loading = true
      try {
        const params = {
          ...this.queryParams,
          communityId: this.$store.state.app.currentCommunity.communityId
        }
        const res = await listPassQrcodes(params)
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
        page: 1,
        row: 10,
        personName: '',
        personTel: '',
        state: '',
        queryStartTime: '',
        queryEndTime: ''
      }
      this.fetchList()
    },
    handleSwitchState(item) {
      this.queryParams.state = item.statusCd
      this.queryParams.page = 1
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
    handleViewQrcode(row) {
      this.qrcodeUrl = row.qrcode || ''
      this.qrcodeRemark = row.personName || ''
      this.qrcodeVisible = true
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
.layout-container { display: flex; gap: 16px; }
.left-panel { width: 160px; flex-shrink: 0; }
.right-panel { flex: 1; min-width: 0; }
.state-list { list-style: none; padding: 0; margin: 0; background: #fff; border-radius: 4px; overflow: hidden; }
.state-list li { padding: 10px 16px; cursor: pointer; border-bottom: 1px solid #ebeef5; color: #606266; font-size: 14px; }
.state-list li:hover { color: #409eff; background: #ecf5ff; }
.state-list li.state-active { color: #409eff; background: #ecf5ff; font-weight: bold; }
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
