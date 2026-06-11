<template>
  <div class="event-detail">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>事件详情</span>
        <el-button size="small" @click="handleBack">返回</el-button>
      </div>
      <div v-loading="loading">
        <el-form :model="detail" label-width="110px" disabled class="detail-form">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="事件ID">
                <el-input :value="detail.eventId" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="事件类型">
                <el-input :value="detail.eventTypeName" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="事件描述">
            <el-input :value="detail.eventMsg" type="textarea" :rows="3" />
          </el-form-item>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="规则名称">
                <el-input :value="detail.ruleName" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="对象类型">
                <el-input :value="detail.objTypeName" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="来源">
                <el-input :value="detail.fromType === 'PERSON' ? '人员' : '平台'" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="人员/平台名称">
                <el-input :value="detail.fromName" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="状态">
                <el-input :value="stateMap[detail.state] || detail.state" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="添加时间">
                <el-input :value="detail.createTime" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>

        <el-tabs type="border-card" class="detail-tabs">
          <el-tab-pane label="事件规则">
            <el-table :data="ruleList" border stripe size="small">
              <el-table-column prop="ruleId" label="规则ID" min-width="100" />
              <el-table-column prop="ruleName" label="规则名称" min-width="120" />
              <el-table-column prop="limitTime" label="限制时间" width="100" />
              <el-table-column prop="createTime" label="创建时间" width="170" />
              <el-table-column prop="remark" label="备注" min-width="150" />
            </el-table>
            <div v-if="ruleList.length === 0" class="tab-empty">暂无规则信息</div>
          </el-tab-pane>
          <el-tab-pane label="事件模板">
            <div class="tab-empty">暂无模板信息</div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-card>
  </div>
</template>

<script>
import { listEventPools } from '@/api/event/eventApi'
import { listEventRules } from '@/api/event/eventApi'

export default {
  name: 'EventDetail',
  data() {
    return {
      loading: false,
      eventId: '',
      ruleId: '',
      detail: {},
      ruleList: [],
      stateMap: { W: '待处理', D: '处理中', NC: '正常处理完成', TC: '超时处理完成' }
    }
  },
  mounted() {
    this.eventId = this.$route.query.eventId || ''
    this.ruleId = this.$route.query.ruleId || ''
    if (this.eventId) {
      this.loadDetail()
    } else {
      this.$router.go(-1)
    }
  },
  methods: {
    async loadDetail() {
      this.loading = true
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listEventPools({ communityId, eventId: this.eventId, page: 1, row: 1 })
        if (res.data && res.data.length > 0) {
          this.detail = res.data[0]
        }
        if (this.ruleId) {
          const ruleRes = await listEventRules({ communityId, ruleId: this.ruleId, page: 1, row: 10 })
          this.ruleList = ruleRes.data || []
        }
      } catch { /* ignore */ } finally {
        this.loading = false
      }
    },
    handleBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.detail-form { max-width: 900px; }
.detail-tabs { margin-top: 20px; }
.tab-empty { padding: 40px; text-align: center; color: #909399; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
