<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改呼叫平台"
    width="500px"
    append-to-body
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="平台名称" prop="platformName">
        <el-input v-model="form.platformName" placeholder="必填，请填写平台名称" maxlength="30" />
      </el-form-item>
      <el-form-item label="平台IP地址" prop="ipAddr">
        <el-input v-model="form.ipAddr" placeholder="必填，请填写平台IP地址" maxlength="32" />
      </el-form-item>
      <el-form-item label="端口" prop="port">
        <el-input v-model="form.port" placeholder="必填，请填写端口" maxlength="10" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="必填，请填写备注" maxlength="200" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateCallingPlatform } from '@/api/accessControl/accessControlApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditCallingPlatform',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        platformId: '',
        platformName: '',
        ipAddr: '',
        port: '',
        remark: ''
      },
      rules: {
        platformName: [
          { required: true, message: '平台名称不能为空', trigger: 'blur' },
          { max: 30, message: '平台名称不能超过30', trigger: 'blur' }
        ],
        ipAddr: [
          { required: true, message: '平台IP地址不能为空', trigger: 'blur' },
          { max: 32, message: '平台IP地址不能超过32', trigger: 'blur' }
        ],
        port: [
          { required: true, message: '端口不能为空', trigger: 'blur' }
        ],
        remark: [
          { required: true, message: '备注不能为空', trigger: 'blur' },
          { max: 200, message: '备注不能超过200', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.form = {
          platformId: this.editData.platformId || '',
          platformName: this.editData.platformName || '',
          ipAddr: this.editData.ipAddr || '',
          port: this.editData.port || '',
          remark: this.editData.remark || ''
        }
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
        const data = {
          ...this.form,
          communityId: (getCurrentCommunity() || {}).communityId
        }
        await updateCallingPlatform(data)
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
