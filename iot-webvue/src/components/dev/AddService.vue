<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加服务"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="服务名称" prop="name">
        <el-input v-model="form.name" placeholder="必填，请填写服务名称，不超过200字符" />
      </el-form-item>
      <el-form-item label="服务编码" prop="serviceCode">
        <el-input v-model="form.serviceCode" placeholder="必填，请填写服务编码，2至100字符" />
      </el-form-item>
      <el-form-item label="调用地址" prop="url">
        <el-select v-model="form.url" placeholder="请选择调用地址" clearable filterable allow-create style="width: 100%">
          <el-option v-for="item in urlOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="调用方式" prop="method">
        <el-select v-model="form.method" placeholder="请选择调用方式" style="width: 100%">
          <el-option label="GET" value="GET" />
          <el-option label="POST" value="POST" />
        </el-select>
      </el-form-item>
      <el-form-item label="超时时间" prop="timeout">
        <el-input v-model="form.timeout" placeholder="必填，请填写超时时间" />
      </el-form-item>
      <el-form-item label="重试次数" prop="retryCount">
        <el-input v-model="form.retryCount" placeholder="必填，请填写重试次数" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveService, listCServiceDict } from '@/api/dev/devApi'

export default {
  name: 'AddService',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      urlOptions: [],
      form: {
        name: '',
        serviceCode: '',
        url: '',
        method: '',
        timeout: 60,
        retryCount: 3,
        businessTypeCd: 'API',
        seq: '1',
        isInstance: 'CMD',
        provideAppId: '8000418002',
        messageQueueName: ''
      },
      rules: {
        name: [
          { required: true, message: '服务名称不能为空', trigger: 'blur' },
          { max: 200, message: '服务名称不能超过200字符', trigger: 'blur' }
        ],
        serviceCode: [
          { required: true, message: '服务编码不能为空', trigger: 'blur' },
          { min: 2, max: 100, message: '服务编码必须在2至100字符之间', trigger: 'blur' }
        ],
        method: [
          { required: true, message: '请选择调用方式', trigger: 'change' }
        ],
        timeout: [
          { required: true, message: '超时时间不能为空', trigger: 'blur' }
        ],
        retryCount: [
          { required: true, message: '重试次数不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.fetchUrlOptions()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async fetchUrlOptions() {
      try {
        const res = await listCServiceDict()
        this.urlOptions = (res.data || []).map(item => ({
          label: item.name || item.value,
          value: item.name || item.value
        }))
      } catch { /* ignore */ }
    },
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
        await saveService(this.form)
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
