<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改属性值"
    width="500px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="值" prop="value">
        <el-input v-model="form.value" placeholder="请填写值" maxlength="200" />
      </el-form-item>
      <el-form-item label="值名称" prop="valueName">
        <el-input v-model="form.valueName" placeholder="请填写值名称" maxlength="200" />
      </el-form-item>
      <el-form-item label="显示" prop="valueShow">
        <el-select v-model="form.valueShow" placeholder="请选择显示" clearable style="width: 100%">
          <el-option label="是" value="Y" />
          <el-option label="否" value="N" />
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
import { updateAttrValue } from '@/api/dev/devApi'

export default {
  name: 'EditAttrValue',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        valueId: '',
        specId: '',
        value: '',
        valueName: '',
        valueShow: ''
      },
      rules: {
        value: [{ required: true, message: '值不能为空', trigger: 'blur' }, { max: 200, message: '值不能超过200字符', trigger: 'blur' }],
        valueName: [{ required: true, message: '值名称不能为空', trigger: 'blur' }, { max: 200, message: '值名称不能超过200字符', trigger: 'blur' }],
        valueShow: [{ required: true, message: '显示不能为空', trigger: 'blur' }]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) { this.form = { ...this.editData } }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },
    async handleSubmit() {
      try { await this.$refs.formRef.validate() } catch { return }
      this.loading = true
      try {
        await updateAttrValue(this.form)
        this.$message.success('修改成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) { this.$message.error(e.msg || '修改失败') }
      finally { this.loading = false }
    }
  }
}
</script>
