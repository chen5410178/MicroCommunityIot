<template>
  <el-dialog :visible.sync="dialogVisible" title="修改物业公司" width="600px" @close="handleClose">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="form.name" placeholder="必填，请填写名称" />
      </el-form-item>
      <el-form-item label="地址" prop="address">
        <el-input v-model="form.address" placeholder="必填，请填写地址" />
      </el-form-item>
      <el-form-item label="电话" prop="tel">
        <el-input v-model="form.tel" placeholder="必填，请填写电话" />
      </el-form-item>
      <el-form-item label="公司法人" prop="corporation">
        <el-input v-model="form.corporation" placeholder="必填，请填写公司法人" />
      </el-form-item>
      <el-form-item label="成立日期" prop="foundingTime">
        <el-date-picker v-model="form.foundingTime" type="date" placeholder="必填，请选择成立日期" value-format="yyyy-MM-dd" style="width: 100%" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateProperty } from '@/api/admin/propertyCompanyApi'

export default {
  name: 'EditPropertyCompany',
  props: {
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        storeId: '',
        name: '',
        address: '',
        tel: '',
        corporation: '',
        foundingTime: ''
      },
      rules: {
        name: [
          { required: true, message: '名称不能为空', trigger: 'blur' },
          { max: 100, message: '名称不能超过100字符', trigger: 'blur' }
        ],
        address: [
          { required: true, message: '地址不能为空', trigger: 'blur' },
          { max: 200, message: '地址不能超过200字符', trigger: 'blur' }
        ],
        tel: [
          { required: true, message: '电话不能为空', trigger: 'blur' },
          { max: 11, message: '电话不能超过11字符', trigger: 'blur' }
        ],
        corporation: [
          { required: true, message: '公司法人不能为空', trigger: 'blur' },
          { max: 64, message: '公司法人不能超过64字符', trigger: 'blur' }
        ],
        foundingTime: [
          { required: true, message: '成立日期不能为空', trigger: 'change' }
        ]
      }
    }
  },
  watch: {
    dialogVisible(val) {
      if (val && this.editData) {
        this.form = { ...this.editData }
      }
    }
  },
  methods: {
    open() {
      this.dialogVisible = true
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
        await updateProperty(this.form)
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
