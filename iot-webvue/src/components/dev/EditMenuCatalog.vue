<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改菜单目录"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="form.name" placeholder="必填，请填写名称" maxlength="10" />
      </el-form-item>
      <el-form-item label="图标" prop="icon">
        <el-input v-model="form.icon" placeholder="必填，请填写图标" maxlength="20" />
      </el-form-item>
      <el-form-item label="顺序" prop="seq">
        <el-input v-model="form.seq" placeholder="必填，请填写顺序" maxlength="11" />
      </el-form-item>
      <el-form-item label="商户类型" prop="storeType">
        <el-select v-model="form.storeType" placeholder="必填，请选择商户类型" style="width: 100%">
          <el-option label="运营团队" value="800900000001" />
          <el-option label="物业" value="800900000003" />
          <el-option label="商家" value="800900000005" />
          <el-option label="开发团队" value="800900000000" />
        </el-select>
      </el-form-item>
      <el-form-item label="页面" prop="url">
        <el-input v-model="form.url" placeholder="必填，请填写页面" maxlength="256" />
      </el-form-item>
      <el-form-item label="是否显示" prop="isShow">
        <el-select v-model="form.isShow" placeholder="必填，请选择是否显示" style="width: 100%">
          <el-option label="是" value="Y" />
          <el-option label="否" value="N" />
        </el-select>
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateMenuCatalog } from '@/api/dev/devApi'

export default {
  name: 'EditMenuCatalog',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        caId: '',
        name: '',
        icon: '',
        seq: '',
        storeType: '',
        url: '',
        isShow: ''
      },
      rules: {
        name: [
          { required: true, message: '名称不能为空', trigger: 'blur' },
          { max: 10, message: '名称不能超过10', trigger: 'blur' }
        ],
        icon: [
          { required: true, message: '图标不能为空', trigger: 'blur' },
          { max: 20, message: '图标不能超过20', trigger: 'blur' }
        ],
        seq: [
          { required: true, message: '顺序不能为空', trigger: 'blur' },
          { max: 11, message: '顺序不能超过11', trigger: 'blur' }
        ],
        storeType: [
          { required: true, message: '商户类型不能为空', trigger: 'change' }
        ],
        url: [
          { required: true, message: '页面不能为空', trigger: 'blur' },
          { max: 256, message: '页面不能超过256', trigger: 'blur' }
        ],
        isShow: [
          { required: true, message: '是否显示不能为空', trigger: 'change' }
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
        await updateMenuCatalog(this.form)
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
