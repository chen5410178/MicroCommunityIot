<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加电梯厂家规格"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="规格名称" prop="specName">
        <el-input v-model="form.specName" placeholder="必填，请填写规格名称" maxlength="64" />
      </el-form-item>
      <el-form-item label="描述" prop="remark">
        <el-input v-model="form.remark" placeholder="选填，请填写描述" maxlength="256" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveLiftMachineFactorySpec } from '@/api/lift/liftApi'

export default {
  name: 'AddLiftMachineFactorySpec',
  props: {
    visible: { type: Boolean, default: false },
    factoryId: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        specName: '',
        remark: ''
      },
      rules: {
        specName: [
          { required: true, message: '规格名称不能为空', trigger: 'blur' },
          { max: 64, message: '规格名称不能超过64', trigger: 'blur' }
        ],
        remark: [
          { max: 256, message: '描述不能超过256', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.form = { specName: '', remark: '' }
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
        await saveLiftMachineFactorySpec({ ...this.form, factoryId: this.factoryId })
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
