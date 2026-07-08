<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改设备位置"
    width="500px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="位置名称" prop="locationName">
        <el-input v-model="form.locationName" placeholder="请填写位置名称" maxlength="100" />
      </el-form-item>
      <el-form-item label="位置类型">
        <el-input :value="locationTypeMap[form.locationType] || ''" disabled />
      </el-form-item>
      <el-form-item label="位置对象">
        <el-input :value="form.locationObjName" disabled />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateMachineLocation } from '@/api/machine/machineApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditMachineLocation',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      locationTypeMap: { '1000': '小区', '6000': '楼栋', '2000': '单元' },
      form: {
        locationId: '',
        locationName: '',
        locationType: '',
        locationObjName: ''
      },
      rules: {
        locationName: [
          { required: true, message: '位置名称不能为空', trigger: 'blur' },
          { max: 100, message: '位置名称不能超过100字符', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.form = {
          locationId: this.editData.locationId || '',
          locationName: this.editData.locationName || '',
          locationType: this.editData.locationType || '',
          locationObjName: this.editData.locationObjName || ''
        }
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
      try { await this.$refs.formRef.validate() } catch { return }
      this.loading = true
      try {
        const communityInfo = getCurrentCommunity() || {}
        await updateMachineLocation({
          locationId: this.form.locationId,
          locationName: this.form.locationName,
          communityId: communityInfo.communityId
        })
        this.$message.success('修改成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) { this.$message.error(e.msg || '修改失败') }
      finally { this.loading = false }
    }
  }
}
</script>
