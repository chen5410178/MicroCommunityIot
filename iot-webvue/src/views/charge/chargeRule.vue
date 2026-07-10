<template>
  <div class="charge-rule-page">
    <div class="page-container">
      <div class="left-panel">
        <el-card shadow="never" class="rule-card">
          <div slot="header" class="card-header">
            <el-button type="primary" size="small" @click="$refs.chargeRuleDivRef.handleAdd()">添加</el-button>
            <el-button size="small" @click="$refs.chargeRuleDivRef.handleEdit()">修改</el-button>
            <el-button size="small" type="danger" @click="$refs.chargeRuleDivRef.handleDelete()">删除</el-button>
          </div>
          <ChargeRuleDiv ref="chargeRuleDivRef" @select="handleRuleSelect" />
        </el-card>
      </div>
      <div class="right-panel">
        <el-card shadow="never" class="content-card">
          <div v-if="curChargeRule.ruleId">
            <h4 class="rule-title">{{ curChargeRule.ruleName }} -- {{ curChargeRule.chargeType === '1001' ? '电动自行车（两轮、三轮）' : '电动汽车' }}</h4>
            <p class="rule-remark">{{ curChargeRule.remark }}</p>
            <el-divider />
            <ChargeRuleFee
              v-if="curChargeRule.chargeType === '1001'"
              :key="curChargeRule.ruleId"
              :rule-id="curChargeRule.ruleId"
              :charge-type="curChargeRule.chargeType"
            />
            <ChargeRulePrice
              v-if="curChargeRule.chargeType === '1002'"
              :key="curChargeRule.ruleId"
              :rule-id="curChargeRule.ruleId"
              :charge-type="curChargeRule.chargeType"
            />
          </div>
          <div v-else class="empty-tip">请从左侧选择充电规则</div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
import ChargeRuleDiv from '@/components/charge/ChargeRuleDiv.vue'
import ChargeRuleFee from '@/components/charge/ChargeRuleFee.vue'
import ChargeRulePrice from '@/components/charge/ChargeRulePrice.vue'

export default {
  name: 'ChargeRule',
  components: { ChargeRuleDiv, ChargeRuleFee, ChargeRulePrice },
  data() {
    return {
      curChargeRule: {}
    }
  },
  methods: {
    handleRuleSelect(rule) {
      this.curChargeRule = rule
    }
  }
}
</script>

<style scoped>
.page-container { display: flex; gap: 16px; }
.left-panel { width: 240px; flex-shrink: 0; }
.right-panel { flex: 1; min-width: 0; }
.rule-card { height: 100%; }
.rule-card .card-header { display: flex; justify-content: space-between; align-items: center; }
.content-card { margin-bottom: 0; }
.rule-title { margin: 0 0 8px 0; font-size: 16px; color: #303133; }
.rule-remark { margin: 0; font-size: 13px; color: #909399; }
.empty-tip { padding: 60px 0; text-align: center; color: #909399; font-size: 14px; }
</style>
