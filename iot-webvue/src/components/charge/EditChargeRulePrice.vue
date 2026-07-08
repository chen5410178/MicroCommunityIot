<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改价格明细"
    width="600px"
    @close="handleClose"
    @open="handleOpen"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="开始时间" prop="startTime">
        <el-time-picker
          v-model="form.startTime"
          placeholder="请选择时段开始时间"
          format="HH:mm"
          value-format="HH:mm"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-time-picker
          v-model="form.endTime"
          placeholder="请选择时段结束时间"
          format="HH:mm"
          value-format="HH:mm"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="价格策略" prop="cpaId">
        <el-select v-model="form.cpaId" placeholder="请选择策略" style="width: 100%">
          <el-option
            v-for="(item, index) in peakAlleyPrices"
            :key="index"
            :value="item.cpaId"
            :label="item.typeName + '--电费率：' + item.price + ' --服务费率：' + item.servicePrice"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="选填，请填写备注" maxlength="512" :rows="3" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateChargeRulePrice, listChargePeakAlleyPrice } from '@/api/charge/chargeApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditChargeRulePrice',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      peakAlleyPrices: [],
      form: {
        crpId: '',
        startTime: '',
        endTime: '',
        cpaId: '',
        remark: ''
      },
      rules: {
        startTime: [
          { required: true, message: '时段开始时间不能为空', trigger: 'change' }
        ],
        endTime: [
          { required: true, message: '时段结束时间不能为空', trigger: 'change' }
        ],
        cpaId: [
          { required: true, message: '请选择价格策略', trigger: 'change' }
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
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    handleOpen() {
      this.fetchPeakAlleyPrices()
      if (this.editData) {
        this.form = { ...this.editData }
      }
    },
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },
    async fetchPeakAlleyPrices() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listChargePeakAlleyPrice({ page: 1, row: 4, communityId })
        this.peakAlleyPrices = res.data || []
      } catch (e) {
        console.log('获取尖峰谷平价格失败')
      }
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        let price = ''
        let servicePrice = ''
        this.peakAlleyPrices.forEach((item) => {
          if (item.cpaId === this.form.cpaId) {
            price = item.price
            servicePrice = item.servicePrice
          }
        })
        await updateChargeRulePrice({
          ...this.form,
          price,
          servicePrice,
          communityId
        })
        this.$message.success('修改成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '修改失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
