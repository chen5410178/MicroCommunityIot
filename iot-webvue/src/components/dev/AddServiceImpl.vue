<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加服务实现"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="业务类型" prop="businessTypeCd">
        <el-input v-model="form.businessTypeCd" placeholder="必填，2至50字符" />
      </el-form-item>
      <el-form-item label="业务名称" prop="name">
        <el-input v-model="form.name" placeholder="必填，2至50字符" />
      </el-form-item>
      <el-form-item label="调用方式" prop="invokeType">
        <el-select v-model="form.invokeType" placeholder="请选择调用方式" style="width: 100%">
          <el-option label="微服务POST方式" value="1" />
          <el-option label="webservice方式" value="2" />
          <el-option label="http post方式" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="调用地址" prop="url">
        <el-input v-model="form.url" placeholder="必填，不超过200字符" />
      </el-form-item>
      <el-form-item label="kafka主题" prop="messageTopic">
        <el-input v-model="form.messageTopic" placeholder="选填，不超过50字符" />
      </el-form-item>
      <el-form-item label="超时时间" prop="timeout">
        <el-input v-model="form.timeout" placeholder="必填，请输入超时时间" />
      </el-form-item>
      <el-form-item label="重试次数" prop="retryCount">
        <el-input v-model="form.retryCount" placeholder="必填，请输入重试次数" />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input type="textarea" v-model="form.description" placeholder="选填，不超过200字符" :rows="3" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveServiceImpl } from '@/api/dev/devApi'

export default {
  name: 'AddServiceImpl',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        businessTypeCd: '',
        name: '',
        invokeType: '',
        url: '',
        messageTopic: '',
        timeout: '60',
        retryCount: '3',
        description: ''
      },
      rules: {
        businessTypeCd: [
          { required: true, message: '业务类型不能为空', trigger: 'blur' },
          { min: 2, max: 50, message: '业务类型必须在2至50字符之间', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '业务名称不能为空', trigger: 'blur' },
          { min: 2, max: 50, message: '业务名称必须在2至50字符之间', trigger: 'blur' }
        ],
        invokeType: [
          { required: true, message: '请选择调用方式', trigger: 'change' }
        ],
        url: [
          { required: true, message: '调用地址不能为空', trigger: 'blur' },
          { max: 200, message: '调用地址不能超过200字符', trigger: 'blur' }
        ],
        messageTopic: [
          { max: 50, message: 'kafka主题不能超过50字符', trigger: 'blur' }
        ],
        timeout: [
          { required: true, message: '超时时间不能为空', trigger: 'blur' },
          { pattern: /^\d+$/, message: '超时时间必须为数字', trigger: 'blur' }
        ],
        retryCount: [
          { required: true, message: '重试次数不能为空', trigger: 'blur' },
          { pattern: /^\d+$/, message: '重试次数必须为数字', trigger: 'blur' }
        ],
        description: [
          { max: 200, message: '描述不能超过200字符', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
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
        await saveServiceImpl(this.form)
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
