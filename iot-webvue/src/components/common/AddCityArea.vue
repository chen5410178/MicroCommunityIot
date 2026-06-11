<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加地区"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="地区名称" prop="areaName">
        <el-input v-model="form.areaName" placeholder="必填，请填写地区名称" maxlength="64" />
      </el-form-item>
      <el-form-item label="地区编码" prop="areaCode">
        <el-input v-model="form.areaCode" placeholder="必填，请填写地区编码" maxlength="64" />
      </el-form-item>
      <el-form-item label="上级">
        <el-input :value="form.parentAreaName" disabled />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveCityArea } from '@/api/common/cityAreaApi'

export default {
  name: 'AddCityArea',
  props: {
    visible: { type: Boolean, default: false },
    parentData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        areaCode: '',
        areaName: '',
        areaLevel: '',
        parentAreaCode: '',
        parentAreaName: ''
      },
      rules: {
        areaName: [
          { required: true, message: '地区名称不能为空', trigger: 'blur' }
        ],
        areaCode: [
          { required: true, message: '地区编码不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.parentData) {
        this.form.areaCode = ''
        this.form.areaName = ''
        this.form.areaLevel = this.parentData.areaLevel || ''
        this.form.parentAreaCode = this.parentData.parentAreaCode || ''
        this.form.parentAreaName = this.parentData.parentAreaName || ''
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
        await saveCityArea(this.form)
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
