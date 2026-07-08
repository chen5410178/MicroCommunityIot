<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改话机"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
      <el-form-item label="设备编码" prop="machineCode">
        <el-input v-model="form.machineCode" placeholder="必填，请填写设备编码" maxlength="30" />
      </el-form-item>
      <el-form-item label="设备名称" prop="machineName">
        <el-input v-model="form.machineName" placeholder="必填，请填写设备名称" maxlength="200" />
      </el-form-item>
      <el-form-item label="设备IP" prop="machineIp">
        <el-input v-model="form.machineIp" placeholder="必填，请填写设备IP" maxlength="64" />
      </el-form-item>
      <el-form-item label="设备端口" prop="machinePort">
        <el-input v-model="form.machinePort" placeholder="必填，请填写设备端口" maxlength="64" />
      </el-form-item>
      <el-form-item label="厂家" prop="implBean">
        <el-select v-model="form.implBean" placeholder="请选择厂家" style="width: 100%">
          <el-option value="10010" label="松饼厂家" />
        </el-select>
      </el-form-item>
      <el-form-item label="接听提示语" prop="successPrompt">
        <el-input v-model="form.successPrompt" type="textarea" :rows="3" placeholder="必填，请填写接听提示语" maxlength="512" />
      </el-form-item>
      <el-form-item label="未接听提示语" prop="failPrompt">
        <el-input v-model="form.failPrompt" type="textarea" :rows="3" placeholder="必填，请填写未接听提示语" maxlength="512" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateTelMachine } from '@/api/lamp/lampApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditTelMachine',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        machineId: '',
        machineCode: '',
        machineName: '',
        machineIp: '',
        machinePort: '',
        implBean: '',
        successPrompt: '',
        failPrompt: ''
      },
      rules: {
        machineCode: [
          { required: true, message: '设备编码不能为空', trigger: 'blur' },
          { max: 30, message: '设备编码不能超过30', trigger: 'blur' }
        ],
        machineName: [
          { required: true, message: '设备名称不能为空', trigger: 'blur' },
          { max: 200, message: '设备名称不能超过200', trigger: 'blur' }
        ],
        machineIp: [
          { required: true, message: '设备IP不能为空', trigger: 'blur' },
          { max: 64, message: '设备IP不能超过64', trigger: 'blur' }
        ],
        machinePort: [
          { required: true, message: '设备端口不能为空', trigger: 'blur' },
          { max: 64, message: '设备端口不能超过64', trigger: 'blur' }
        ],
        implBean: [
          { required: true, message: '门禁厂家不能为空', trigger: 'change' }
        ],
        successPrompt: [
          { required: true, message: '接听提示语不能为空', trigger: 'blur' },
          { max: 512, message: '接听提示语不能超过512', trigger: 'blur' }
        ],
        failPrompt: [
          { required: true, message: '未接听提示语不能为空', trigger: 'blur' },
          { max: 512, message: '未接听提示语不能超过512', trigger: 'blur' }
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
        await updateTelMachine({ ...this.form, communityId })
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
