<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改仪表组"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
      <el-form-item label="仪表类型ID" prop="typeId">
        <el-input v-model="form.typeId" disabled />
      </el-form-item>
      <el-form-item label="类型名称" prop="name">
        <el-input v-model="form.name" placeholder="必填，请填写类型名称" maxlength="30" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="选填，请填写备注" maxlength="256" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateInstrumentType } from '@/api/meter/meterApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditInstrumentType',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        typeId: '',
        name: '',
        remark: ''
      },
      rules: {
        name: [
          { required: true, message: '类型名称不能为空', trigger: 'blur' },
          { max: 30, message: '类型名称不能超过30位', trigger: 'blur' }
        ],
        remark: [
          { max: 256, message: '备注不能超过256字', trigger: 'blur' }
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
        await updateInstrumentType({ ...this.form, communityId })
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
