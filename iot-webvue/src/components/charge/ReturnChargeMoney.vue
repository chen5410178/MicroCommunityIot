<template>
  <el-dialog title="订单退款" :visible.sync="visible" width="550px" :close-on-click-modal="false">
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="订单编号">
        <span>{{ form.orderId }}</span>
      </el-form-item>
      <el-form-item label="总扣款">
        <span>{{ totalMoney }} 元</span>
      </el-form-item>
      <el-form-item label="充电时间">
        <template v-if="accts.length > 0">
          <span>{{ accts[0].startTime }}~{{ accts[accts.length - 1].endTime }}</span>
        </template>
      </el-form-item>
      <el-form-item label="本次退款" prop="returnMoney">
        <el-input v-model="form.returnMoney" type="number" placeholder="必填，请输入退款金额" style="width: 220px" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="必填，请填写退款原因" />
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleConfirm">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { orderRefundMoney } from '@/api/charge/chargeApi'

export default {
  name: 'ReturnChargeMoney',
  data() {
    return {
      visible: false,
      submitting: false,
      form: {
        orderId: '',
        returnMoney: '',
        remark: '',
        communityId: ''
      },
      totalMoney: 0,
      accts: [],
      rules: {
        returnMoney: [
          { required: true, message: '退款金额不能为空', trigger: 'blur' }
        ],
        remark: [
          { required: true, message: '备注不能为空', trigger: 'blur' },
          { max: 512, message: '备注不能超过512', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    open(orderData) {
      this.form.orderId = orderData.orderId || orderData.conditions?.orderId || ''
      this.form.communityId = orderData.communityId || orderData.conditions?.communityId || ''
      this.form.returnMoney = ''
      this.form.remark = ''
      this.accts = orderData.chargeMachineOrderAcctss || orderData.chargeMachineOrderAccts || []
      let sum = 0
      this.accts.forEach(item => {
        sum += Math.round(parseFloat(item.amount || 0) * 100)
      })
      this.totalMoney = (sum / 100).toFixed(2)
      this.visible = true
      this.$nextTick(() => {
        this.$refs.form && this.$refs.form.clearValidate()
      })
    },
    validateMoney() {
      const total = parseFloat(this.totalMoney)
      const ret = parseFloat(this.form.returnMoney)
      if (ret <= 0) {
        this.$message.warning('退款必须是正数')
        return false
      }
      if (ret > total) {
        this.$message.warning('本次退款金额需要小于总扣款金额')
        return false
      }
      return true
    },
    async handleConfirm() {
      try {
        await this.$refs.form.validate()
      } catch {
        return
      }
      if (!this.validateMoney()) {
        return
      }
      this.submitting = true
      try {
        const res = await orderRefundMoney({
          orderId: this.form.orderId,
          returnMoney: this.form.returnMoney,
          remark: this.form.remark,
          communityId: this.form.communityId
        })
        if (res.code === 0) {
          this.visible = false
          this.$message.success('退款成功')
          this.$emit('success')
        } else {
          this.$message.error(res.msg || '操作失败')
        }
      } catch (e) {
        this.$message.error('操作失败')
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>
