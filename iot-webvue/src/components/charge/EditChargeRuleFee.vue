<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改费用明细"
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
import { updateChargeRuleFee } from '@/api/charge/chargeApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditChargeRuleFee',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        crfId: '',
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
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.form = { ...this.editData }
      }
    },
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
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await updateChargeRuleFee({ ...this.form, communityId })
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
