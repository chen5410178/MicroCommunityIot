<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加3D模型"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="模型名称" prop="modalName">
        <el-input v-model="form.modalName" placeholder="必填，请填写模型名称" maxlength="200" />
      </el-form-item>
      <el-form-item label="模型编码" prop="modalCode">
        <el-input v-model="form.modalCode" placeholder="必填，请填写模型编码" maxlength="64" />
      </el-form-item>
      <el-form-item label="模型类型" prop="modalType">
        <el-select v-model="form.modalType" placeholder="必填，请选择模型类型" style="width: 100%">
          <el-option
            v-for="item in modalTypeOptions"
            :key="item.statusCd"
            :label="item.name"
            :value="item.statusCd"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="文件路径" prop="path">
        <el-input v-model="form.path" placeholder="必填，请填写模型文件路径" maxlength="512" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveDtModal } from '@/api/3d/dtModalApi'
import { listDicts } from '@/api/dev/devApi'

export default {
  name: 'AddDtModal',
  props: {
    visible: { type: Boolean, default: false },
    defaultModalType: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      modalTypeOptions: [],
      form: {
        modalName: '',
        modalCode: '',
        modalType: '',
        path: ''
      },
      rules: {
        modalName: [
          { required: true, message: '模型名称不能为空', trigger: 'blur' },
          { max: 200, message: '模型名称不能超过200', trigger: 'blur' }
        ],
        modalCode: [
          { required: true, message: '模型编码不能为空', trigger: 'blur' },
          { max: 64, message: '模型编码不能超过64', trigger: 'blur' }
        ],
        modalType: [
          { required: true, message: '模型类型不能为空', trigger: 'change' }
        ],
        path: [
          { required: true, message: '文件路径不能为空', trigger: 'blur' },
          { max: 512, message: '文件路径不能超过512', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.form.modalType = this.defaultModalType
        this.loadModalTypes()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadModalTypes() {
      try {
        const res = await listDicts({ tableName: 'dt_modal', columnName: 'modal_type' })
        this.modalTypeOptions = res.data || []
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
        await saveDtModal(this.form)
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
