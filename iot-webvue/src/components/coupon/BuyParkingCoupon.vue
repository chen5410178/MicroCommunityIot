<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="购买停车券"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="停车券" prop="couponId">
        <el-select v-model="form.couponId" placeholder="请选择停车券" style="width: 100%" @change="handleCouponChange">
          <el-option v-for="item in coupons" :key="item.couponId" :label="item.name + ' -- 售价:' + item.valuePrice" :value="item.couponId" />
        </el-select>
      </el-form-item>
      <el-form-item label="购买数量" prop="quantity">
        <el-input v-model="form.quantity" type="number" placeholder="必填，请填写购买数量" @blur="computeMoney" />
      </el-form-item>
      <el-form-item label="支付方式" prop="payType">
        <el-select v-model="form.payType" placeholder="必填，请选择支付方式" style="width: 100%">
          <el-option v-for="item in payTypes" :key="item.statusCd" :label="item.name" :value="item.statusCd" />
        </el-select>
      </el-form-item>
      <el-form-item label="应收">
        <el-input v-model="form.receivableAmount" placeholder="自动计算" disabled />
      </el-form-item>
      <el-form-item label="实收金额" prop="receivedAmount">
        <el-input v-model="form.receivedAmount" placeholder="必填，请填写实收金额" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" placeholder="请填写备注" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { buyParkingCoupon, listParkingCoupons } from '@/api/coupon/couponApi'
import { listDicts } from '@/api/dev/devApi'

export default {
  name: 'BuyParkingCoupon',
  props: {
    visible: { type: Boolean, default: false },
    shopData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      coupons: [],
      payTypes: [],
      valuePrice: 0,
      form: {
        couponId: '',
        quantity: '',
        payType: '',
        receivableAmount: '',
        receivedAmount: '',
        remark: ''
      },
      rules: {
        couponId: [
          { required: true, message: '请选择停车券', trigger: 'change' }
        ],
        quantity: [
          { required: true, message: '购买数量不能为空', trigger: 'blur' }
        ],
        payType: [
          { required: true, message: '请选择支付方式', trigger: 'change' }
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
        this.loadPayTypes()
        this.loadCoupons()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadPayTypes() {
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listDicts({ tableName: 'parking_coupon_order', columnName: 'pay_type', page: 1, row: 100, communityId })
        this.payTypes = res.data || []
      } catch { /* ignore */ }
    },
    async loadCoupons() {
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listParkingCoupons({ page: 1, row: 100, communityId })
        this.coupons = res.data || []
      } catch { /* ignore */ }
    },
    handleCouponChange() {
      const selected = this.coupons.find(item => item.couponId === this.form.couponId)
      if (selected) {
        this.valuePrice = parseFloat(selected.valuePrice) || 0
      }
      this.computeMoney()
    },
    computeMoney() {
      if (!this.valuePrice) return
      const quantity = parseFloat(this.form.quantity) || 0
      this.form.receivableAmount = (this.valuePrice * quantity).toFixed(2)
    },
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
      this.form.receivableAmount = ''
      this.valuePrice = 0
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        await buyParkingCoupon({
          ...this.form,
          shopId: this.shopData.shopId,
          communityId
        })
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
