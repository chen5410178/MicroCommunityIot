<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加端口"
    width="500px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="插座名称" prop="portName">
        <el-input v-model="form.portName" placeholder="请输入插座名称" maxlength="64" />
      </el-form-item>
      <el-form-item label="插座编号" prop="portCode">
        <el-input v-model="form.portCode" placeholder="请输入插座编号" maxlength="64" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveChargeMachinePort } from '@/api/charge/chargeApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddChargeMachinePort',
  props: {
    visible: { type: Boolean, default: false },
    machineId: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        portName: '',
        portCode: ''
      },
      rules: {
        portName: [
          { required: true, message: '插座名称不能为空', trigger: 'blur' },
          { max: 64, message: '插座名称不能超过64', trigger: 'blur' }
        ],
        portCode: [
          { required: true, message: '插座编号不能为空', trigger: 'blur' },
          { max: 64, message: '插座编号不能超过64', trigger: 'blur' }
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
        await saveChargeMachinePort({ ...this.form, machineId: this.machineId, communityId })
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
