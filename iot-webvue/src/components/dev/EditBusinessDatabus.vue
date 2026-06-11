<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改 Databus"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="名称" prop="databusName">
        <el-input v-model="form.databusName" placeholder="必填，请填写名称" />
      </el-form-item>
      <el-form-item label="业务类型" prop="businessTypeCd">
        <el-input v-model="form.businessTypeCd" placeholder="必填，请填写业务类型" />
      </el-form-item>
      <el-form-item label="适配器" prop="beanName">
        <el-input v-model="form.beanName" placeholder="必填，请填写适配器" />
      </el-form-item>
      <el-form-item label="顺序" prop="seq">
        <el-input v-model.number="form.seq" placeholder="必填，请填写顺序" />
      </el-form-item>
      <el-form-item label="状态" prop="state">
        <el-select v-model="form.state" placeholder="必填，请选择状态" style="width: 100%">
          <el-option label="在用" value="1001" />
          <el-option label="停用" value="2002" />
        </el-select>
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateBusinessDatabus } from '@/api/dev/devApi'

export default {
  name: 'EditBusinessDatabus',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        databusId: '',
        databusName: '',
        businessTypeCd: '',
        beanName: '',
        seq: '',
        state: ''
      },
      rules: {
        databusName: [
          { required: true, message: '名称不能为空', trigger: 'blur' },
          { max: 128, message: '名称不能超过128位', trigger: 'blur' }
        ],
        businessTypeCd: [
          { required: true, message: '业务类型不能为空', trigger: 'blur' }
        ],
        beanName: [
          { required: true, message: '适配器不能为空', trigger: 'blur' },
          { max: 256, message: '适配器不能超过256位', trigger: 'blur' }
        ],
        seq: [
          { required: true, message: '顺序不能为空', trigger: 'blur' },
          { type: 'number', message: '顺序必须为数字', trigger: 'blur' }
        ],
        state: [
          { required: true, message: '状态不能为空', trigger: 'change' }
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
        await updateBusinessDatabus(this.form)
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
