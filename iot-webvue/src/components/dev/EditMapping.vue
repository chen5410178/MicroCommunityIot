<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改编码映射"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="配置项" prop="domain">
        <el-select v-model="form.domain" placeholder="必填，请选择配置项" style="width: 100%">
          <el-option
            v-for="item in domains"
            :key="item.domain"
            :label="item.domainName"
            :value="item.domain"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="form.name" placeholder="必填，请填写名称" maxlength="50" />
      </el-form-item>
      <el-form-item label="键" prop="mappingKey">
        <el-input v-model="form.mappingKey" placeholder="必填，请填写键" maxlength="100" />
      </el-form-item>
      <el-form-item label="值" prop="value">
        <el-input v-model="form.value" placeholder="必填，请填写值" maxlength="100" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="选填，请填写备注" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateMapping, listMappingDomains } from '@/api/dev/devApi'

export default {
  name: 'EditMapping',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      domains: [],
      form: {
        id: '',
        domain: '',
        name: '',
        mappingKey: '',
        value: '',
        remark: ''
      },
      rules: {
        domain: [
          { required: true, message: '域不能为空', trigger: 'change' },
          { max: 50, message: '域长度不能超过50', trigger: 'change' }
        ],
        name: [
          { required: true, message: '名称不能为空', trigger: 'blur' },
          { min: 2, max: 50, message: '名称必须在2至50字符之间', trigger: 'blur' }
        ],
        mappingKey: [
          { required: true, message: '键不能为空', trigger: 'blur' },
          { min: 1, max: 100, message: '键必须在1至100字符之间', trigger: 'blur' }
        ],
        value: [
          { required: true, message: '值不能为空', trigger: 'blur' },
          { min: 1, max: 100, message: '值必须在1至100字符之间', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.form = { ...this.editData }
        this.fetchDomains()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async fetchDomains() {
      try {
        const res = await listMappingDomains({ page: 1, row: 300 })
        this.domains = res.data || []
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
        await updateMapping(this.form)
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
