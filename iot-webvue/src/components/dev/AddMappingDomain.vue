<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加配置项域"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="名称" prop="domainName">
        <el-input v-model="form.domainName" placeholder="必填，请填写名称" maxlength="50" />
      </el-form-item>
      <el-form-item label="配置项" prop="domain">
        <el-input v-model="form.domain" placeholder="必填，请填写配置项" maxlength="50" />
      </el-form-item>
      <el-form-item label="排序" prop="seq">
        <el-input v-model="form.seq" type="number" placeholder="必填，请填写排序" maxlength="12" />
      </el-form-item>
      <el-form-item label="描述" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="必填，请填写描述" maxlength="256" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveMappingDomain } from '@/api/dev/devApi'

export default {
  name: 'AddMappingDomain',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        domainName: '',
        domain: '',
        seq: '',
        remark: ''
      },
      rules: {
        domain: [
          { required: true, message: '配置项不能为空', trigger: 'blur' },
          { max: 50, message: '配置项不能超过50', trigger: 'blur' }
        ],
        domainName: [
          { required: true, message: '名称不能为空', trigger: 'blur' },
          { max: 50, message: '名称不能超过50', trigger: 'blur' }
        ],
        seq: [
          { required: true, message: '排序不能为空', trigger: 'blur' },
          { max: 12, message: '排序不能超过12', trigger: 'blur' }
        ],
        remark: [
          { required: true, message: '描述不能为空', trigger: 'blur' },
          { max: 256, message: '描述不能超过256', trigger: 'blur' }
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
        await saveMappingDomain(this.form)
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
