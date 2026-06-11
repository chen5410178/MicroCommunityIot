<template>
  <div class="charge-rule-page">
    <el-card shadow="never" class="table-card">
      <div slot="header" class="card-header">
        <span>充电规则</span>
      </div>
      <el-row :gutter="16">
        <el-col :span="4">
          <ChargeRuleDiv @select="handleRuleSelect" />
        </el-col>
        <el-col :span="20">
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
        </el-col>
      </el-row>
    </el-card>
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
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.rule-title { margin: 0 0 8px 0; font-size: 16px; color: #303133; }
.rule-remark { margin: 0; font-size: 13px; color: #909399; }
.empty-tip { padding: 60px 0; text-align: center; color: #909399; font-size: 14px; }
</style>
