<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="购买月卡"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="车牌号" prop="carNum">
        <el-input v-model="form.carNum" placeholder="必填，请填写车牌号" maxlength="12" @blur="handleCarNumBlur" />
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
      <el-form-item label="应收金额" prop="receivableAmount">
        <el-input v-model="form.receivableAmount" disabled placeholder="选择月卡后自动计算" />
      </el-form-item>
      <el-form-item label="实收金额" prop="receivedAmount">
        <el-input v-model="form.receivedAmount" type="number" placeholder="必填，请填写实收金额" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="选填，请填写备注" maxlength="300" rows="3" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { buyCarMonthOrder, listCarMonthCards, queryOwnerCars } from '@/api/car/carApi'
import { listDicts } from '@/api/system/systemApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'BuyCarMonthCard',
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
        carNum: '',
        carId: '',
        paId: '',
        primeRate: '',
        receivableAmount: '',
        receivedAmount: '',
        remark: ''
      },
      rules: {
        cardId: [
          { required: true, message: '月卡不能为空', trigger: 'change' }
        ],
        carNum: [
          { required: true, message: '车牌号不能为空', trigger: 'blur' },
          { max: 12, message: '车牌号不能超过12', trigger: 'blur' }
        ],
        primeRate: [
          { required: true, message: '支付方式不能为空', trigger: 'change' }
        ],
        receivedAmount: [
          { required: true, message: '实收金额不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.loadDicts()
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
        carNum: '',
        carId: '',
        paId: '',
        primeRate: '',
        receivableAmount: '',
        receivedAmount: '',
        remark: ''
      }
      this.cards = []
    },
    async loadDicts() {
      try {
        const res = await listDicts({ tableName: 'pay_fee_detail', tableColumns: 'prime_rate' })
        this.primeRates = res || []
      } catch (e) { /* ignore */ }
    },
    async handleCarNumBlur() {
      if (!this.form.carNum) return
      const communityId = (getCurrentCommunity() || {}).communityId || ''
      try {
        const res = await queryOwnerCars({ page: 1, row: 1, communityId, carNum: this.form.carNum, carTypeCd: '1001' })
        if (!res.data || res.data.length === 0) {
          this.$message.warning('月租车不存在,请先添加为月租车')
          return
        }
        const car = res.data[0]
        if (!car.paId) {
          this.$message.warning('月租车没有绑定车位')
          return
        }
        this.form.carId = car.carId
        this.form.paId = car.paId
        this.loadCards(car.paId)
      } catch (e) {
        this.$message.error(e.msg || '查询车辆失败')
      }
    },
    async loadCards(paId) {
      const communityId = (getCurrentCommunity() || {}).communityId || ''
      try {
        const res = await listCarMonthCards({ page: 1, row: 100, communityId, paId })
        this.cards = res.data || []
      } catch (e) { /* ignore */ }
    },
    handleCardChange() {
      const card = this.cards.find(item => item.cardId === this.form.cardId)
      if (card) {
        this.form.receivableAmount = card.cardPrice
        this.form.receivedAmount = card.cardPrice
      }
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        this.form.communityId = (getCurrentCommunity() || {}).communityId || ''
        await buyCarMonthOrder(this.form)
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
