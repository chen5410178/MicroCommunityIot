<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加服务提供"
    width="650px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="服务名称" prop="name">
        <el-input v-model="form.name" placeholder="必填，请填写服务名称" maxlength="50" />
      </el-form-item>
      <el-form-item label="服务编码" prop="serviceCode">
        <el-input v-model="form.serviceCode" placeholder="必填，请填写服务编码" maxlength="50" />
      </el-form-item>
      <el-form-item label="参数" prop="params">
        <el-input v-model="form.params" placeholder="必填，请填写参数" maxlength="500" />
      </el-form-item>
      <el-form-item label="实现方式" prop="queryModel">
        <el-select v-model="form.queryModel" placeholder="必填，请选择实现方式" style="width: 100%" @change="handleQueryModelChange">
          <el-option label="sql方式" value="1" />
          <el-option label="存储过程方式" value="2" />
          <el-option label="java方式" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="form.queryModel == 1" label="sql" prop="sql">
        <el-input v-model="form.sql" type="textarea" :rows="4" placeholder="选填，请填写sql" maxlength="2000" />
      </el-form-item>
      <el-form-item v-if="form.queryModel == 1" label="输出模板" prop="template">
        <el-input v-model="form.template" type="textarea" :rows="4" placeholder="选填，请填写输出模板" maxlength="2000" />
      </el-form-item>
      <el-form-item v-if="form.queryModel == 2" label="存储过程" prop="proc">
        <el-input v-model="form.proc" type="textarea" :rows="4" placeholder="选填，请填写存储过程" maxlength="200" />
      </el-form-item>
      <el-form-item v-if="form.queryModel == 3" label="java" prop="javaScript">
        <el-input v-model="form.javaScript" type="textarea" :rows="4" placeholder="选填，请填写java" maxlength="2000" />
      </el-form-item>
      <el-form-item label="描述" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="选填，请填写描述" maxlength="200" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveServiceProvide } from '@/api/dev/devApi'

export default {
  name: 'AddServiceProvide',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        name: '',
        serviceCode: '',
        params: '',
        queryModel: '',
        sql: '',
        template: '',
        proc: '',
        javaScript: '',
        remark: ''
      },
      rules: {
        name: [
          { required: true, message: '服务名称不能为空', trigger: 'blur' },
          { min: 2, max: 50, message: '服务名称必须在2至50字符之间', trigger: 'blur' }
        ],
        serviceCode: [
          { required: true, message: '服务编码不能为空', trigger: 'blur' },
          { min: 2, max: 50, message: '服务编码必须在2至50字符之间', trigger: 'blur' }
        ],
        params: [
          { required: true, message: '参数不能为空', trigger: 'blur' },
          { max: 500, message: '参数内容不能超过500', trigger: 'blur' }
        ],
        queryModel: [
          { required: true, message: '实现方式不能为空', trigger: 'change' }
        ]
      }
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    handleQueryModelChange() {
      this.form.sql = ''
      this.form.template = ''
      this.form.proc = ''
      this.form.javaScript = ''
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
        await saveServiceProvide(this.form)
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
