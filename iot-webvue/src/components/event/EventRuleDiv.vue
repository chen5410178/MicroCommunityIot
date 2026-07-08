<template>
  <div class="event-rule-div">
    <div class="rule-btn-group">
      <el-button size="small" @click="handleAdd">添加</el-button>
      <el-button size="small" @click="handleEdit">修改</el-button>
      <el-button size="small" @click="handleDelete">删除</el-button>
    </div>
    <ul class="rule-list">
      <li
        v-for="rule in ruleList"
        :key="rule.ruleId"
        class="rule-item"
        :class="{ 'rule-item-active': currentRule && currentRule.ruleId === rule.ruleId }"
        @click="switchRule(rule)"
      >
        {{ rule.ruleName }}
      </li>
    </ul>

    <AddEventRule :visible.sync="addVisible" :template-list="templateList" @success="handleSuccess" />
    <EditEventRule :visible.sync="editVisible" :edit-data="currentRule" :template-list="templateList" @success="handleSuccess" />
    <DeleteEventRule :visible.sync="delVisible" :del-data="currentRule" @success="handleSuccess" />
  </div>
</template>

<script>
import { listEventRules, listEventTemplates } from '@/api/event/eventApi'
import { getCurrentCommunity } from '@/utils/auth'
import AddEventRule from '@/components/event/AddEventRule.vue'
import EditEventRule from '@/components/event/EditEventRule.vue'
import DeleteEventRule from '@/components/event/DeleteEventRule.vue'

export default {
  name: 'EventRuleDiv',
  components: { AddEventRule, EditEventRule, DeleteEventRule },
  data() {
    return {
      ruleList: [],
      templateList: [],
      currentRule: null,
      addVisible: false,
      editVisible: false,
      delVisible: false
    }
  },
  mounted() {
    this.fetchRules()
    this.fetchTemplates()
  },
  methods: {
    async fetchRules() {
      try {
        const community = getCurrentCommunity()
        const res = await listEventRules({ page: 1, row: 100, communityId: community.communityId })
        this.ruleList = res.data || []
        if (this.ruleList.length > 0) {
          this.switchRule(this.ruleList[0])
        }
      } catch (e) {
        console.error('加载事件规则失败', e)
      }
    },
    async fetchTemplates() {
      try {
        const community = getCurrentCommunity()
        const res = await listEventTemplates({ page: -1, row: 100, communityId: community.communityId })
        this.templateList = res.data || []
      } catch (e) {
        console.error('加载事件模板失败', e)
      }
    },
    switchRule(rule) {
      this.currentRule = rule
      this.$emit('switch-rule', rule)
    },
    handleAdd() {
      this.addVisible = true
    },
    handleEdit() {
      if (!this.currentRule) {
        this.$message.warning('请先选择事件规则')
        return
      }
      this.editVisible = true
    },
    handleDelete() {
      if (!this.currentRule) {
        this.$message.warning('请先选择事件规则')
        return
      }
      this.delVisible = true
    },
    handleSuccess() {
      this.fetchRules()
    }
  }
}
</script>

<style scoped>
.event-rule-div {
  background: #fff;
  border: 1px solid #EBEEF5;
  border-radius: 4px;
  min-height: 400px;
}
.rule-btn-group {
  padding: 12px 16px;
  border-bottom: 1px solid #EBEEF5;
  display: flex;
  gap: 8px;
}
.rule-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.rule-item {
  padding: 10px 16px;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 4px;
  font-size: 14px;
  color: #303133;
  transition: all 0.3s;
}
.rule-item:hover { background-color: #f5f7fa; }
.rule-item-active {
  background-color: #409eff;
  color: white;
  font-weight: 600;
}
</style>
