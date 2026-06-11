<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改地区"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="地区名称" prop="areaName">
        <el-input v-model="form.areaName" placeholder="必填，请填写地区名称" maxlength="64" />
      </el-form-item>
      <el-form-item label="地区编码">
        <el-input v-model="form.areaCode" disabled />
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
import { listAreas, updateCityArea } from '@/api/common/cityAreaApi'

export default {
  name: 'EditCityArea',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        id: '',
        areaCode: '',
        areaName: '',
        areaLevel: '',
        parentAreaCode: '',
        parentAreaName: ''
      },
      rules: {
        areaName: [
          { required: true, message: '地区名称不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.loadDetail()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadDetail() {
      try {
        const res = await listAreas({ id: this.editData.id, page: 1, row: 1 })
        const data = res.data && res.data.length > 0 ? res.data[0] : null
        if (data) {
          this.form = {
            id: data.id || this.editData.id,
            areaCode: data.areaCode || this.editData.areaCode,
            areaName: data.areaName || this.editData.areaName,
            areaLevel: data.areaLevel || this.editData.areaLevel,
            parentAreaCode: data.parentAreaCode || '',
            parentAreaName: data.parentAreaName || ''
          }
        } else {
          this.form = {
            id: this.editData.id,
            areaCode: this.editData.areaCode,
            areaName: this.editData.areaName,
            areaLevel: this.editData.areaLevel,
            parentAreaCode: this.editData.parentAreaCode || '',
            parentAreaName: this.editData.parentAreaName || ''
          }
        }
      } catch {
        this.form = {
          id: this.editData.id,
          areaCode: this.editData.areaCode,
          areaName: this.editData.areaName,
          areaLevel: this.editData.areaLevel,
          parentAreaCode: this.editData.parentAreaCode || '',
          parentAreaName: this.editData.parentAreaName || ''
        }
      }
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
        await updateCityArea(this.form)
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
