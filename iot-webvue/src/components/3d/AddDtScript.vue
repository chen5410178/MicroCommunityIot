<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加3D脚本"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="脚本名称" prop="scriptName">
        <el-input v-model="form.scriptName" placeholder="必填，请填写脚本名称" maxlength="200" />
      </el-form-item>
      <el-form-item label="脚本类型" prop="scriptType">
        <el-select v-model="form.scriptType" placeholder="必填，请选择脚本类型" style="width: 100%">
          <el-option
            v-for="item in scriptTypeOptions"
            :key="item.statusCd"
            :label="item.name"
            :value="item.statusCd"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="脚本内容" prop="jsContent">
        <el-input v-model="form.jsContent" type="textarea" :rows="8" placeholder="必填，请填写脚本内容" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveDtScript } from '@/api/3d/dtScriptApi'
import { listDicts } from '@/api/system/systemApi'

export default {
  name: 'AddDtScript',
  props: {
    visible: { type: Boolean, default: false },
    defaultScriptType: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      scriptTypeOptions: [],
      form: {
        scriptName: '',
        scriptType: '',
        jsContent: ''
      },
      rules: {
        scriptName: [
          { required: true, message: '脚本名称不能为空', trigger: 'blur' },
          { max: 200, message: '脚本名称不能超过200', trigger: 'blur' }
        ],
        scriptType: [
          { required: true, message: '脚本类型不能为空', trigger: 'change' }
        ],
        jsContent: [
          { required: true, message: '脚本内容不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.form.scriptType = this.defaultScriptType
        this.loadScriptTypes()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadScriptTypes() {
      try {
        const res = await listDicts({ tableName: 'dt_modal', tableColumns: 'modal_type' })
        this.scriptTypeOptions = res || []
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
        await saveDtScript(this.form)
        this.$message.success('添加成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.message || '添加失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
