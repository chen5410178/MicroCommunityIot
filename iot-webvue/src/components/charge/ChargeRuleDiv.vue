<template>
  <div>
    <ul class="rule-list" v-if="rules && rules.length > 0">
      <li
        v-for="(rule, index) in rules"
        :key="index"
        :class="{ active: curRule && curRule.ruleId === rule.ruleId }"
        @click="switchRule(rule)"
      >
        {{ rule.ruleName }}
      </li>
    </ul>
    <div v-else class="empty-tip">请先添加规则</div>
    <AddChargeRule :visible.sync="addVisible" @success="handleSuccess" />
    <EditChargeRule :visible.sync="editVisible" :edit-data="curRule" @success="handleSuccess" />
    <DeleteChargeRule :visible.sync="delVisible" :del-data="curRule" @success="handleDelSuccess" />
  </div>
</template>

<script>
import { listChargeRule } from '@/api/charge/chargeApi'
import AddChargeRule from '@/components/charge/AddChargeRule.vue'
import EditChargeRule from '@/components/charge/EditChargeRule.vue'
import DeleteChargeRule from '@/components/charge/DeleteChargeRule.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ChargeRuleDiv',
  components: { AddChargeRule, EditChargeRule, DeleteChargeRule },
  data() {
    return {
      rules: [],
      curRule: null,
      addVisible: false,
      editVisible: false,
      delVisible: false
    }
  },
  mounted() {
    this.fetchRules()
  },
  methods: {
    async fetchRules() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listChargeRule({ page: 1, row: 100, communityId })
        this.rules = res.data || []
        if (this.rules.length > 0 && !this.curRule) {
          this.switchRule(this.rules[0])
        }
      } catch (e) {
        console.log('获取规则列表失败')
      }
    },
    switchRule(rule) {
      this.curRule = rule
      this.$emit('select', rule)
    },
    handleAdd() {
      this.addVisible = true
    },
    handleEdit() {
      if (!this.curRule) {
        this.$message.warning('未选择规则')
        return
      }
      this.editVisible = true
    },
    handleDelete() {
      if (!this.curRule) {
        this.$message.warning('未选择规则')
        return
      }
      this.delVisible = true
    },
    handleSuccess() {
      this.fetchRules()
    },
    handleDelSuccess() {
      this.curRule = null
      this.fetchRules()
    }
  }
}
</script>

<style scoped>
.rule-list {
  list-style: none;
  padding: 0;
  margin: 0;
  max-height: calc(100vh - 200px);
  overflow-y: auto;
}
.rule-list li {
  padding: 10px 12px;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 5px;
  text-align: center;
  font-size: 14px;
  color: #303133;
  transition: all 0.3s;
}
.rule-list li:hover {
  background-color: #f5f7fa;
}
.rule-list li.active {
  background-color: #409eff;
  color: white;
  font-weight: bold;
}
.empty-tip {
  padding: 20px;
  text-align: center;
  color: #909399;
  font-size: 14px;
}
</style>
