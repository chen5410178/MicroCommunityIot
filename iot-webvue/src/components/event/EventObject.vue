<template>
  <div class="event-object">
    <el-form :inline="true" :model="queryParams" class="search-form">
      <el-form-item>
        <el-select v-model="queryParams.objType" placeholder="请选择对象类型" clearable style="width: 180px">
          <el-option v-for="item in objTypeList" :key="item.statusCd" :label="item.name" :value="item.statusCd" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-input v-model="queryParams.objectName" placeholder="请输入对象名称" clearable style="width: 180px" />
      </el-form-item>
      <el-form-item>
        <el-select v-model="queryParams.state" placeholder="请选择状态" clearable style="width: 120px">
          <el-option label="生效" value="ON" />
          <el-option label="失效" value="OFF" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-button type="primary" @click="handleAdd">添加</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" v-loading="loading" border stripe>
      <el-table-column prop="eoId" label="编号" width="100" align="center" />
      <el-table-column prop="objTypeName" label="对象类型" min-width="120" />
      <el-table-column prop="objectName" label="对象名称" min-width="150" />
      <el-table-column label="状态" width="120" align="center">
        <template slot-scope="{ row }">
          <template v-if="row.state === 'ON'">
            生效中
            <el-link type="primary" :underline="false" @click="changeState(row)">失效</el-link>
          </template>
          <template v-if="row.state === 'OFF'">
            已失效
            <el-link type="primary" :underline="false" @click="changeState(row)">生效</el-link>
          </template>
        </template>
      </el-table-column>
      <el-table-column label="备注" width="100" align="center">
        <template slot-scope="{ row }">
          <el-popover v-if="row.remark" placement="top" trigger="hover" :content="row.remark">
            <el-button slot="reference" size="small" type="text">查看</el-button>
          </el-popover>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="80" fixed="right" align="center">
        <template slot-scope="{ row }">
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

    <DeleteEventObject :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listEventObjects, updateEventObject } from '@/api/event/eventApi'
import { listDicts } from '@/api/system/systemApi'
import { getCurrentCommunity } from '@/utils/auth'
import DeleteEventObject from '@/components/event/DeleteEventObject.vue'

export default {
  name: 'EventObject',
  components: { DeleteEventObject },
  props: {
    ruleId: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      objTypeList: [],
      delVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        objType: '',
        objectName: '',
        state: ''
      }
    }
  },
  watch: {
    ruleId: {
      immediate: true,
      handler(val) {
        if (val) {
          this.loadObjTypeDict()
          this.handleSearch()
        }
      }
    }
  },
  methods: {
    async loadObjTypeDict() {
      try {
        const res = await listDicts({ tableName: 'event_object', tableColumns: 'obj_type' })
        this.objTypeList = res || []
      } catch { /* ignore */ }
    },
    async fetchList() {
      if (!this.ruleId) return
      this.loading = true
      try {
        const community = getCurrentCommunity()
        const res = await listEventObjects({
          ...this.queryParams,
          ruleId: this.ruleId,
          communityId: community.communityId
        })
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
      this.queryParams = { page: 1, row: 10, objType: '', objectName: '', state: '' }
      this.fetchList()
    },
    handleAdd() {
      this.$router.push({ path: '/pages/event/addEventObject', query: { ruleId: this.ruleId } })
    },
    handleDelete(row) {
      this.currentRow = { ...row, communityId: getCurrentCommunity().communityId }
      this.delVisible = true
    },
    async changeState(row) {
      const newState = row.state === 'ON' ? 'OFF' : 'ON'
      const text = row.state === 'ON' ? '失效' : '生效'
      try {
        await this.$confirm(`确定要${text}该事件对象吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch { return }
      try {
        const community = getCurrentCommunity()
        await updateEventObject({ ...row, state: newState, communityId: community.communityId })
        this.$message.success(`${text}成功`)
        this.fetchList()
      } catch (e) {
        this.$message.error(e.msg || '操作失败')
      }
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
.search-form { padding: 0; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
