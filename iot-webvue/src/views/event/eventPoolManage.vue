<template>
  <div class="event-pool-manage">
    <div class="content-wrap">
      <div class="left-tree">
        <el-card shadow="never" class="tree-card">
          <div slot="header" class="card-header">
            <span>对象类型</span>
          </div>
          <div class="tree-body">
            <div
              class="tree-item"
              :class="{ 'tree-item-active': currentObjType === '' }"
              @click="switchObjType('')"
            >
              全部
            </div>
            <div
              v-for="item in objTypeList"
              :key="item.statusCd"
              class="tree-item"
              :class="{ 'tree-item-active': currentObjType === item.statusCd }"
              @click="switchObjType(item.statusCd)"
            >
              {{ item.statusName || item.statusCd }}
            </div>
            <div v-if="objTypeList.length === 0" class="tree-empty">暂无类型</div>
          </div>
        </el-card>
      </div>
      <div class="right-content">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item label="规则">
              <el-select v-model="queryParams.ruleId" placeholder="请选择规则" clearable style="width: 180px">
                <el-option
                  v-for="item in ruleList"
                  :key="item.ruleId"
                  :label="item.ruleName"
                  :value="item.ruleId"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="事件类型">
              <el-select v-model="queryParams.eventType" placeholder="请选择事件类型" clearable style="width: 180px">
                <el-option
                  v-for="item in eventTypeList"
                  :key="item.statusCd"
                  :label="item.statusName || item.statusCd"
                  :value="item.statusCd"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="事件状态">
              <el-select v-model="queryParams.state" placeholder="请选择状态" clearable style="width: 160px">
                <el-option label="待处理" value="W" />
                <el-option label="处理中" value="D" />
                <el-option label="正常处理完成" value="NC" />
                <el-option label="超时处理完成" value="TC" />
              </el-select>
            </el-form-item>
            <el-form-item label="对象名称">
              <el-input v-model="queryParams.objectName" placeholder="请输入对象名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item label="事件来源">
              <el-select v-model="queryParams.fromType" placeholder="请选择来源" clearable style="width: 160px">
                <el-option label="人员创建" value="PERSON" />
                <el-option label="平台创建" value="SYSTEM" />
              </el-select>
            </el-form-item>
            <el-form-item label="时间">
              <el-date-picker
                v-model="dateRange"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="yyyy-MM-dd HH:mm:ss"
                style="width: 360px"
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
            <span>事件池列表</span>
            <el-button type="primary" size="small" @click="handleAdd">添加</el-button>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="eventId" label="编号" min-width="100" />
            <el-table-column prop="ruleName" label="规则名称" min-width="120" />
            <el-table-column prop="eventTypeName" label="事件类型" min-width="100" />
            <el-table-column label="事件描述" width="100" align="center">
              <template slot-scope="{ row }">
                <el-button size="small" type="text" @click="handleViewEventMsg(row)">查看</el-button>
              </template>
            </el-table-column>
            <el-table-column prop="objTypeName" label="对象类型" width="100" />
            <el-table-column prop="objectName" label="对象名称" min-width="120" />
            <el-table-column label="来自" width="80" align="center">
              <template slot-scope="{ row }">
                {{ row.fromType === 'PERSON' ? '人员' : '平台' }}
              </template>
            </el-table-column>
            <el-table-column prop="fromName" label="人员/平台名称" min-width="120" />
            <el-table-column label="状态" width="110" align="center">
              <template slot-scope="{ row }">
                {{ stateMap[row.state] || row.state }}
              </template>
            </el-table-column>
            <el-table-column label="备注" width="80" align="center">
              <template slot-scope="{ row }">
                <el-button size="small" type="text" @click="handleViewRemark(row)">查看</el-button>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="添加时间" width="170" />
            <el-table-column label="操作" width="260" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button size="small" type="text" @click="handleDetail(row)">详情</el-button>
                <el-button
                  v-if="row.state === 'W'"
                  size="small"
                  type="text"
                  @click="handleAssign(row)"
                >
                  分配
                </el-button>
                <el-button
                  v-if="row.state !== 'W'"
                  size="small"
                  type="text"
                  @click="handleViewStaff(row)"
                >
                  查看分配
                </el-button>
                <el-button
                  v-if="row.state === 'D' && row.staffState === 'D'"
                  size="small"
                  type="text"
                  @click="handleComplete(row)"
                >
                  完成模拟
                </el-button>
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
    <ViewData :visible.sync="viewVisible" :title="viewTitle" :content="viewContent" />
    <DeleteEventPool :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
    <AddEventPoolStaff :visible.sync="assignVisible" :event-id="currentEventId" @success="handleSuccess" />
    <ViewEventPoolStaffInfo :visible.sync="staffInfoVisible" :staff-data="staffInfoData" @success="handleSuccess" />
  </div>
</template>

<script>
import {
  listEventPools, updateEventPool, listEventRules
} from '@/api/event/eventApi'
import { listDicts } from '@/api/dev/devApi'
import ViewData from '@/components/event/ViewData.vue'
import DeleteEventPool from '@/components/event/DeleteEventPool.vue'
import AddEventPoolStaff from '@/components/event/AddEventPoolStaff.vue'
import ViewEventPoolStaffInfo from '@/components/event/ViewEventPoolStaffInfo.vue'

export default {
  name: 'EventPoolManage',
  components: { ViewData, DeleteEventPool, AddEventPoolStaff, ViewEventPoolStaffInfo },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      objTypeList: [],
      currentObjType: '',
      ruleList: [],
      eventTypeList: [],
      stateMap: { W: '待处理', D: '处理中', NC: '正常处理完成', TC: '超时处理完成' },
      dateRange: [],
      viewVisible: false,
      viewTitle: '',
      viewContent: '',
      delVisible: false,
      assignVisible: false,
      staffInfoVisible: false,
      currentRow: null,
      currentEventId: '',
      staffInfoData: {},
      queryParams: {
        page: 1,
        row: 10,
        ruleId: '',
        eventType: '',
        state: '',
        objType: '',
        objectName: '',
        fromType: '',
        startTime: '',
        endTime: ''
      }
    }
  },
  mounted() {
    this.loadObjTypes()
    this.loadEventTypes()
    this.loadRules()
    this.fetchList()
  },
  methods: {
    async loadObjTypes() {
      try {
        const res = await listDicts({ tableName: 'event_object', columnName: 'obj_type' })
        this.objTypeList = res.data || []
      } catch { /* ignore */ }
    },
    async loadEventTypes() {
      try {
        const res = await listDicts({ tableName: 'event_pool', columnName: 'event_type' })
        this.eventTypeList = res.data || []
      } catch { /* ignore */ }
    },
    async loadRules() {
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listEventRules({ communityId, page: 1, row: 200 })
        this.ruleList = res.data || []
      } catch { /* ignore */ }
    },
    async fetchList() {
      this.loading = true
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const params = { ...this.queryParams, communityId, objType: this.currentObjType }
        if (this.dateRange && this.dateRange.length === 2) {
          params.startTime = this.dateRange[0]
          params.endTime = this.dateRange[1]
        } else {
          params.startTime = ''
          params.endTime = ''
        }
        const res = await listEventPools(params)
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    switchObjType(type) {
      this.currentObjType = type
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
        ruleId: '',
        eventType: '',
        state: '',
        objType: '',
        objectName: '',
        fromType: '',
        startTime: '',
        endTime: ''
      }
      this.dateRange = []
      this.currentObjType = ''
      this.fetchList()
    },
    handleAdd() {
      this.$router.push('/pages/event/addEventPoolManage')
    },
    handleDetail(row) {
      this.$router.push({
        path: '/pages/event/eventDetail',
        query: { eventId: row.eventId, ruleId: row.ruleId }
      })
    },
    handleViewEventMsg(row) {
      this.viewTitle = '事件描述'
      this.viewContent = row.eventMsg || ''
      this.viewVisible = true
    },
    handleViewRemark(row) {
      this.viewTitle = '备注'
      this.viewContent = row.remark || ''
      this.viewVisible = true
    },
    handleAssign(row) {
      this.currentEventId = row.eventId
      this.assignVisible = true
    },
    handleViewStaff(row) {
      this.staffInfoData = {
        epsId: row.epsId,
        staffName: row.staffName,
        createTime: row.staffCreateTime,
        state: row.staffState,
        stateName: this.stateMap[row.staffState] || row.staffState,
        eventReason: row.eventReason,
        eventResult: row.eventResult,
        eventHours: row.eventHours
      }
      this.staffInfoVisible = true
    },
    async handleComplete(row) {
      try {
        await this.$confirm('确定要模拟完成该事件吗？', '确认操作', { type: 'info' })
      } catch { return }
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        await updateEventPool({ eventId: row.eventId, communityId, state: 'NC' })
        this.$message.success('操作成功')
        this.fetchList()
      } catch (e) {
        this.$message.error(e.msg || '操作失败')
      }
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
.content-wrap {
  display: flex;
  gap: 16px;
}
.left-tree {
  width: 200px;
  flex-shrink: 0;
}
.right-content {
  flex: 1;
  min-width: 0;
}
.tree-card {
  height: 100%;
}
.tree-body {
  max-height: calc(100vh - 260px);
  overflow-y: auto;
}
.tree-item {
  padding: 10px 12px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.2s;
}
.tree-item:hover {
  background-color: #f5f7fa;
}
.tree-item-active {
  background-color: #ecf5ff;
  color: #409eff;
}
.tree-empty {
  padding: 20px;
  text-align: center;
  color: #909399;
}
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
