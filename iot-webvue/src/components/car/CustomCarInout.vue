<template>
  <el-dialog
    :visible.sync="dialogVisible"
    :title="form.type === '1101' ? '车辆进场' : '车辆出场'"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="车牌号" prop="carNum">
        <el-input v-model="form.carNum" placeholder="必填，请填写车牌号" maxlength="64" @blur="onCarNumBlur" />
      </el-form-item>
      <el-form-item label="出场道闸" prop="machineId" v-if="form.type !== '1101' && machines.length > 0">
        <el-select v-model="form.machineId" placeholder="请选择出场道闸" style="width: 100%">
          <el-option v-for="item in machines" :key="item.machineId" :label="item.machineName" :value="item.machineId" />
        </el-select>
      </el-form-item>
      <el-form-item label="应收金额" prop="payCharge" v-if="form.type !== '1101'">
        <el-input v-model="form.payCharge" disabled placeholder="应收金额" />
      </el-form-item>
      <el-form-item label="实收金额" prop="amount" v-if="form.type !== '1101'">
        <el-input v-model="form.amount" placeholder="必填，请填写实收金额" maxlength="20" />
      </el-form-item>
      <el-form-item label="停车时间" prop="costMin" v-if="form.type !== '1101'">
        <el-input v-model="form.costMin" disabled placeholder="停车时间" />
      </el-form-item>
      <el-form-item label="支付方式" prop="payType" v-if="form.type !== '1101'">
        <el-select v-model="form.payType" placeholder="请选择支付方式" style="width: 100%">
          <el-option v-for="item in payTypes" :key="item.statusCd" :label="item.name" :value="item.statusCd" />
        </el-select>
      </el-form-item>
      <el-form-item label="优惠券" v-if="form.type !== '1101' && coupons.length > 0">
        <el-checkbox-group v-model="form.pccIds" @change="onCouponChange">
          <el-checkbox v-for="coupon in coupons" :key="coupon.pccId" :label="coupon.pccId">
            {{ coupon.couponName }}-
            <template v-if="coupon.typeCd === '1001'">{{ coupon.value }}分钟</template>
            <template v-else-if="coupon.typeCd === '2002'">{{ coupon.value }}元</template>
            <template v-else-if="coupon.typeCd === '3003'">{{ coupon.value }}折</template>
            <template v-else-if="coupon.typeCd === '4004'">全免</template>
          </el-checkbox>
        </el-checkbox-group>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请填写备注信息" maxlength="300" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { listCarInParkingArea, listBarriers, listParkingCouponCars, getTempCarFeeOrder, customCarInOut } from '@/api/car/carApi'
import { listDicts } from '@/api/system/systemApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'CustomCarInout',
  data() {
    return {
      dialogVisible: false,
      loading: false,
      payTypes: [],
      machines: [],
      coupons: [],
      form: {
        type: '1102',
        carNum: '',
        payCharge: '',
        amount: '',
        costMin: '0',
        machineId: '',
        boxId: '',
        paId: '',
        payType: '',
        pccIds: [],
        inoutId: '',
        remark: ''
      },
      rules: {
        carNum: [{ required: true, message: '车牌号不能为空', trigger: 'blur' }],
        amount: [{ required: true, message: '实收金额不能为空', trigger: 'blur' }],
        machineId: [{ required: true, message: '出场道闸不能为空', trigger: 'change' }],
        payType: [{ required: true, message: '支付方式不能为空', trigger: 'change' }]
      }
    }
  },
  created() {
    this.loadPayTypes()
  },
  methods: {
    async loadPayTypes() {
      try {
        const res = await listDicts({ tableName: 'car_inout_payment', tableColumns: 'pay_type' })
        this.payTypes = res || []
      } catch { /* ignore */ }
    },
    open(data) {
      this.resetForm()
      this.dialogVisible = true
      Object.assign(this.form, {
        type: '1102',
        carNum: data.carNum || '',
        payCharge: data.payCharge || '',
        amount: data.payCharge || '',
        machineId: data.machineId || '',
        boxId: data.boxId || '',
        paId: data.paId || ''
      })
      if (this.form.type !== '1101') {
        this.queryCarMoney()
      }
    },
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },
    resetForm() {
      this.machines = []
      this.coupons = []
      this.form = {
        type: '1102',
        carNum: '',
        payCharge: '',
        amount: '',
        costMin: '0',
        machineId: '',
        boxId: '',
        paId: '',
        payType: '',
        pccIds: [],
        inoutId: '',
        remark: ''
      }
      this.$nextTick(() => {
        this.$refs.formRef && this.$refs.formRef.clearValidate()
      })
    },
    onCarNumBlur() {
      if (this.form.type === '1101') return
      this.queryCarMoney()
    },
    async queryCarMoney() {
      if (!this.form.carNum) return
      const communityId = (getCurrentCommunity() || {}).communityId || ''
      try {
        const res = await listCarInParkingArea({
          page: 1, row: 1,
          communityId,
          boxId: this.form.boxId,
          paId: this.form.paId,
          carNum: this.form.carNum
        })
        const data = res.data
        if (!data || data.length < 1) {
          this.$message.warning('未查询到在场车辆')
          return
        }
        this.form.payCharge = data[0].payCharge
        this.form.amount = data[0].payCharge
        this.form.inoutId = data[0].inoutId
        this.form.costMin = (data[0].hours || '0') + '时' + (data[0].min || '0') + '分'
      } catch { /* ignore */ }
      if (this.form.paId && !this.form.machineId) {
        this.loadOutMachines()
      }
      this.loadOutCoupons()
    },
    async loadOutMachines() {
      const communityId = (getCurrentCommunity() || {}).communityId || ''
      try {
        const res = await listBarriers({
          paId: this.form.paId,
          page: 1, row: 100,
          direction: '3307',
          communityId
        })
        this.machines = res.data || []
      } catch { /* ignore */ }
    },
    async loadOutCoupons() {
      const communityId = (getCurrentCommunity() || {}).communityId || ''
      try {
        const res = await listParkingCouponCars({
          paId: this.form.paId,
          page: 1, row: 30,
          state: '1001',
          carNum: this.form.carNum,
          communityId
        })
        this.coupons = res.data || []
      } catch { /* ignore */ }
    },
    async onCouponChange() {
      if (!this.form.pccIds || this.form.pccIds.length < 1) {
        this.queryCarMoney()
        return
      }
      try {
        const res = await getTempCarFeeOrder({
          paId: this.form.paId,
          boxId: this.form.boxId,
          pccIds: this.form.pccIds.join(','),
          carNum: this.form.carNum
        })
        const data = res.data
        if (data) {
          this.form.payCharge = data.amount
          this.form.amount = data.amount
        }
      } catch { /* ignore */ }
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        this.form.communityId = (getCurrentCommunity() || {}).communityId || ''
        await customCarInOut(this.form)
        this.$message.success('操作成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '操作失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
