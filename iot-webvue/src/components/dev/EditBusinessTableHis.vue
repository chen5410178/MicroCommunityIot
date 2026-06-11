<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改业务轨迹"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="业务类型" prop="businessTypeCd">
        <el-input v-model="form.businessTypeCd" placeholder="必填，请填写业务类型" maxlength="30" />
      </el-form-item>
      <el-form-item label="动作" prop="action">
        <el-select v-model="form.action" placeholder="必填，请选择动作" style="width: 100%">
          <el-option label="ADD" value="ADD" />
          <el-option label="MOD" value="MOD" />
          <el-option label="DEL" value="DEL" />
        </el-select>
      </el-form-item>
      <el-form-item label="表名" prop="actionObj">
        <el-input v-model="form.actionObj" placeholder="必填，请填写表名" maxlength="64" />
      </el-form-item>
      <el-form-item label="轨迹表名" prop="actionObjHis">
        <el-input v-model="form.actionObjHis" placeholder="必填，请填写轨迹表名" maxlength="64" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" placeholder="选填，请填写备注" maxlength="200" type="textarea" :rows="3" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateBusinessTableHis } from '@/api/dev/devApi'

export default {
  name: 'EditBusinessTableHis',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        hisId: '',
        businessTypeCd: '',
        action: '',
        actionObj: '',
        actionObjHis: '',
        remark: ''
      },
      rules: {
        businessTypeCd: [
          { required: true, message: '业务类型不能为空', trigger: 'blur' },
          { max: 30, message: '业务类型不能超过30', trigger: 'blur' }
        ],
        action: [
          { required: true, message: '动作不能为空', trigger: 'change' }
        ],
        actionObj: [
          { required: true, message: '表名不能为空', trigger: 'blur' },
          { max: 64, message: '表名不能超过64', trigger: 'blur' }
        ],
        actionObjHis: [
          { required: true, message: '轨迹表名不能为空', trigger: 'blur' },
          { max: 64, message: '轨迹表名不能超过64', trigger: 'blur' }
        ],
        remark: [
          { max: 200, message: '备注不能超过200', trigger: 'blur' }
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
        await updateBusinessTableHis(this.form)
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
