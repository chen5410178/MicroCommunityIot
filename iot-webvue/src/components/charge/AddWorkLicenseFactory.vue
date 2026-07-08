<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加工牌厂家"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
      <el-form-item label="厂家名称" prop="factoryName">
        <el-input v-model="form.factoryName" placeholder="必填，请填写厂家名称" maxlength="64" />
      </el-form-item>
      <el-form-item label="厂家处理类" prop="beanImpl">
        <el-input v-model="form.beanImpl" placeholder="必填，请填写厂家处理类" maxlength="512" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" placeholder="选填，请填写备注" maxlength="512" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveWorkLicenseFactory } from '@/api/charge/chargeApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddWorkLicenseFactory',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        factoryName: '',
        beanImpl: '',
        remark: ''
      },
      rules: {
        factoryName: [
          { required: true, message: '厂家名称不能为空', trigger: 'blur' },
          { max: 64, message: '厂家名称不能超过64', trigger: 'blur' }
        ],
        beanImpl: [
          { required: true, message: '厂家处理类不能为空', trigger: 'blur' },
          { max: 512, message: '厂家处理类不能超过512', trigger: 'blur' }
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
        await saveWorkLicenseFactory({ ...this.form, communityId })
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
