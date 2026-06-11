<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加费用明细"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="最大功率" prop="maxEnergyPrice">
        <el-input v-model="form.maxEnergyPrice" type="number" placeholder="必填，请填写最大功率（W）">
          <template slot="append">W</template>
        </el-input>
      </el-form-item>
      <el-form-item label="电费单价" prop="durationPrice">
        <el-input v-model="form.durationPrice" type="number" placeholder="必填，请填写电费单价" />
      </el-form-item>
      <el-form-item label="服务费单价" prop="servicePrice">
        <el-input v-model="form.servicePrice" type="number" placeholder="必填，请填写服务费单价" />
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
import { saveChargeRuleFee } from '@/api/charge/chargeApi'

export default {
  name: 'AddChargeRuleFee',
  props: {
    visible: { type: Boolean, default: false },
    ruleId: { type: String, default: '' },
    chargeType: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        maxEnergyPrice: '',
        durationPrice: '',
        servicePrice: '',
        remark: '',
        minEnergyPrice: '0'
      },
      rules: {
        maxEnergyPrice: [
          { required: true, message: '最大功率不能为空', trigger: 'blur' }
        ],
        durationPrice: [
          { required: true, message: '电费单价不能为空', trigger: 'blur' }
        ],
        servicePrice: [
          { required: true, message: '服务费单价不能为空', trigger: 'blur' }
        ],
        remark: [
          { max: 512, message: '备注不能超过512', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        await saveChargeRuleFee({ ...this.form, ruleId: this.ruleId, chargeType: this.chargeType, communityId })
        this.$message.success('添加成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '添加失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
