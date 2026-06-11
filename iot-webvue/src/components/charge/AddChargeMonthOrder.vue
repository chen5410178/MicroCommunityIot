<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="购买月卡"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="用户电话" prop="personTel">
        <el-input v-model="form.personTel" placeholder="必填，请填写充电电话" maxlength="11" />
      </el-form-item>
      <el-form-item label="月卡" prop="cardId">
        <el-select v-model="form.cardId" placeholder="必填，请选择月卡" style="width: 100%" @change="handleCardChange">
          <el-option v-for="item in cards" :key="item.cardId" :label="item.cardName + '(' + item.cardMonth + '月)'" :value="item.cardId" />
        </el-select>
      </el-form-item>
      <el-form-item label="支付方式" prop="primeRate">
        <el-select v-model="form.primeRate" placeholder="必填，请选择支付方式" style="width: 100%">
          <el-option
            v-for="item in primeRates"
            :key="item.statusCd"
            v-if="item.statusCd !== '5' && item.statusCd !== '6'"
            :label="item.name"
            :value="item.statusCd"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="实收金额" prop="receivedAmount">
        <el-input v-model="form.receivedAmount" type="number" placeholder="必填，请填写实收金额" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="请填写备注" maxlength="512" rows="3" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveChargeMonthOrder, listChargeMonthCards } from '@/api/charge/chargeApi'
import { listDicts } from '@/api/property/propertyApi'

export default {
  name: 'AddChargeMonthOrder',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      cards: [],
      primeRates: [],
      form: {
        cardId: '',
        personTel: '',
        primeRate: '',
        receivedAmount: '',
        remark: ''
      },
      rules: {
        cardId: [
          { required: true, message: '月卡不能为空', trigger: 'change' }
        ],
        personTel: [
          { required: true, message: '充电电话不能为空', trigger: 'blur' },
          { max: 11, message: '充电电话不能超过11位', trigger: 'blur' }
        ],
        primeRate: [
          { required: true, message: '支付方式不能为空', trigger: 'change' }
        ],
        receivedAmount: [
          { required: true, message: '实收金额不能为空', trigger: 'blur' }
        ],
        remark: [
          { max: 512, message: '备注不能超过512', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.loadDicts()
        this.loadCards()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
      this.resetForm()
    },
    resetForm() {
      this.form = {
        cardId: '',
        personTel: '',
        primeRate: '',
        receivedAmount: '',
        remark: ''
      }
      this.cards = []
    },
    async loadDicts() {
      try {
        const res = await listDicts({ page: 1, row: 100, tableName: 'pay_fee_detail', columnName: 'prime_rate' })
        this.primeRates = res.data || []
      } catch (e) { /* ignore */ }
    },
    async loadCards() {
      const communityId = this.$store.state.app.currentCommunity.communityId || ''
      try {
        const res = await listChargeMonthCards({ page: 1, row: 100, communityId })
        this.cards = res.data || []
      } catch (e) { /* ignore */ }
    },
    handleCardChange() {
      const card = this.cards.find(item => item.cardId === this.form.cardId)
      if (card) {
        this.form.receivedAmount = card.cardPrice
      }
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        this.form.communityId = this.$store.state.app.currentCommunity.communityId || ''
        await saveChargeMonthOrder(this.form)
        this.$message.success('购买成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '购买失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
