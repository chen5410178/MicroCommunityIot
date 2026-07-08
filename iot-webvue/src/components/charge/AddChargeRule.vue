<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加充电规则"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
      <el-form-item label="规则名称" prop="ruleName">
        <el-input v-model="form.ruleName" placeholder="必填，请填写规则名称" maxlength="64" />
      </el-form-item>
      <el-form-item label="规则使用对象" prop="chargeType">
        <el-select v-model="form.chargeType" placeholder="请选择规则使用对象" style="width: 100%">
          <el-option label="电动自行车（两轮、三轮）" value="1001" />
          <el-option label="电动汽车" value="1002" />
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
import { saveChargeRule } from '@/api/charge/chargeApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddChargeRule',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        ruleName: '',
        chargeType: '',
        remark: ''
      },
      rules: {
        ruleName: [
          { required: true, message: '规则名称不能为空', trigger: 'blur' },
          { max: 64, message: '规则名称不能超过64', trigger: 'blur' }
        ],
        chargeType: [
          { required: true, message: '规则对象不能为空', trigger: 'change' }
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
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await saveChargeRule({ ...this.form, communityId })
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
