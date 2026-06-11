<template>
  <div class="event-rule-page">
    <el-row :gutter="16">
      <el-col :span="5">
        <EventRuleDiv ref="eventRuleDiv" @switch-rule="handleSwitchRule" />
      </el-col>
      <el-col :span="19">
        <el-card shadow="never" class="detail-card" v-if="currentRule && currentRule.ruleId">
          <h5 class="rule-title">{{ currentRule.ruleName }}</h5>
          <div class="rule-context">超时时间：{{ currentRule.limitTime }} 分钟</div>
          <div class="rule-context" v-if="currentRule.remark">{{ currentRule.remark }}</div>
          <div class="divider" />

          <el-tabs v-model="activeTab" @tab-click="handleTabClick">
            <el-tab-pane label="事件对象" name="eventObject">
              <EventObject ref="eventObject" :rule-id="currentRule.ruleId" />
            </el-tab-pane>
            <el-tab-pane label="事件通知员工" name="eventStaff">
              <EventStaff ref="eventStaff" :rule-id="currentRule.ruleId" />
            </el-tab-pane>
          </el-tabs>
        </el-card>
        <el-card shadow="never" class="detail-card" v-else>
          <div style="text-align: center; padding: 40px 0; color: #909399;">
            <i class="el-icon-info" style="font-size: 48px;" />
            <p style="margin-top: 16px;">请选择左侧事件规则查看详情</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import EventRuleDiv from '@/components/event/EventRuleDiv.vue'
import EventObject from '@/components/event/EventObject.vue'
import EventStaff from '@/components/event/EventStaff.vue'

export default {
  name: 'EventRule',
  components: { EventRuleDiv, EventObject, EventStaff },
  data() {
    return {
      currentRule: null,
      activeTab: 'eventObject'
    }
  },
  methods: {
    handleSwitchRule(rule) {
      this.currentRule = rule
      this.activeTab = 'eventObject'
    },
    handleTabClick(tab) {
      if (tab.name === 'eventObject') {
        this.$refs.eventObject && this.$refs.eventObject.handleSearch()
      } else if (tab.name === 'eventStaff') {
        this.$refs.eventStaff && this.$refs.eventStaff.handleSearch()
      }
    }
  }
}
</script>

<style scoped>
.detail-card { min-height: 400px; }
.rule-title { margin: 0 0 8px 0; font-size: 18px; color: #303133; }
.rule-context { font-size: 14px; color: #606266; margin-bottom: 4px; }
.divider { height: 1px; background: #EBEEF5; margin: 16px 0; }
</style>
