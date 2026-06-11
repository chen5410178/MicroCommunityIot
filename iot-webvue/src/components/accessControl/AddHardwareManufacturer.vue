<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加门禁厂家"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="厂商名称" prop="hmName">
        <el-input v-model="form.hmName" placeholder="必填，请填写厂商名称" maxlength="200" />
      </el-form-item>
      <el-form-item label="协议版本号" prop="version">
        <el-input v-model="form.version" placeholder="必填，请填写协议版本号" maxlength="12" />
      </el-form-item>
      <el-form-item label="协议实现" prop="protocolImpl">
        <el-input v-model="form.protocolImpl" placeholder="必填，请填写协议实现" maxlength="200" />
      </el-form-item>
      <el-form-item label="开发者" prop="author">
        <el-input v-model="form.author" placeholder="必填，请填写开发者" maxlength="64" />
      </el-form-item>
      <el-form-item label="联系方式" prop="link">
        <el-input v-model="form.link" placeholder="必填，请填写手机号" maxlength="11" />
      </el-form-item>
      <el-form-item label="协议许可" prop="license">
        <el-input v-model="form.license" type="textarea" :rows="3" placeholder="必填，请填写协议许可" />
      </el-form-item>
      <el-form-item label="产品官网" prop="prodUrl">
        <el-input v-model="form.prodUrl" placeholder="必填，请填写产品官网" maxlength="500" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveHardwareManufacturer } from '@/api/accessControl/hardwareManufacturerApi'

export default {
  name: 'AddHardwareManufacturer',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        hmName: '',
        version: '',
        protocolImpl: '',
        author: '',
        link: '',
        license: '',
        prodUrl: '',
        hmType: '1001'
      },
      rules: {
        hmName: [
          { required: true, message: '厂商名称不能为空', trigger: 'blur' },
          { max: 200, message: '厂商名称不能超过200', trigger: 'blur' }
        ],
        version: [
          { required: true, message: '协议版本号不能为空', trigger: 'blur' },
          { max: 12, message: '协议版本号不能超过12', trigger: 'blur' }
        ],
        protocolImpl: [
          { required: true, message: '协议实现不能为空', trigger: 'blur' },
          { max: 200, message: '协议实现不能超过200', trigger: 'blur' }
        ],
        author: [
          { required: true, message: '开发者不能为空', trigger: 'blur' },
          { max: 64, message: '开发者不能超过64', trigger: 'blur' }
        ],
        link: [
          { required: true, message: '联系方式不能为空', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        license: [
          { required: true, message: '协议许可不能为空', trigger: 'blur' }
        ],
        prodUrl: [
          { required: true, message: '产品官网不能为空', trigger: 'blur' },
          { max: 500, message: '产品官网不能超过500', trigger: 'blur' }
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
        await saveHardwareManufacturer(this.form)
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
