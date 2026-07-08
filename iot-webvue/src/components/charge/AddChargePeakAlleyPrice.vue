<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="设置尖峰谷平价格"
    width="800px"
    @open="handleOpen"
  >
    <el-table :data="prices" border stripe>
      <el-table-column prop="typeName" label="费率类型" width="100" align="center" />
      <el-table-column label="电费费率" align="center">
        <template slot-scope="{ row }">
          <el-input v-model="row.price" type="number" placeholder="请填写电费费率" size="small" />
        </template>
      </el-table-column>
      <el-table-column label="服务费费率" align="center">
        <template slot-scope="{ row }">
          <el-input v-model="row.servicePrice" type="number" placeholder="请填写服务费费率" size="small" />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" min-width="180">
        <template slot-scope="{ row }">
          <el-input v-model="row.remark" placeholder="请填写备注" size="small" />
        </template>
      </el-table-column>
    </el-table>
    <span slot="footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">保存</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { listChargePeakAlleyPrice, saveChargePeakAlleyPrice } from '@/api/charge/chargeApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddChargePeakAlleyPrice',
  props: {
    visible: { type: Boolean, default: false },
    ruleId: { type: String, default: '' },
    chargeType: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      prices: []
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async handleOpen() {
      await this.fetchPrices()
    },
    async fetchPrices() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listChargePeakAlleyPrice({ page: 1, row: 4, communityId })
        this.prices = (res.data || []).map(item => ({ ...item }))
      } catch (e) {
        console.log('获取尖峰谷平价格失败')
      }
    },
    async handleSubmit() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await saveChargePeakAlleyPrice({
          prices: this.prices,
          ruleId: this.ruleId,
          communityId
        })
        this.$message.success('保存成功')
        this.dialogVisible = false
      } catch (e) {
        this.$message.error(e.msg || '保存失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
