<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改位置"
    width="500px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="位置名称" prop="locationName">
        <el-input v-model="form.locationName" placeholder="请填写位置名称" maxlength="100" />
      </el-form-item>
      <el-form-item label="排序" prop="seq">
        <el-input v-model="form.seq" placeholder="请填写排序（数字，越小越靠前）" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" placeholder="请填写备注" maxlength="200" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateShopLocation } from '@/api/shop/shopApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditLocation',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: () => null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: { locationId: '', locationName: '', seq: 1, remark: '' },
      rules: {
        locationName: [{ required: true, message: '位置名称不能为空', trigger: 'blur' }],
        seq: [{ required: true, message: '排序不能为空', trigger: 'blur' }]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.form = {
          locationId: this.editData.locationId,
          locationName: this.editData.locationName,
          seq: this.editData.seq,
          remark: this.editData.remark || ''
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
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await updateShopLocation({ ...this.form, communityId })
        this.$message.success('修改成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) { this.$message.error(e.message || '修改失败') }
      finally { this.loading = false }
    }
  }
}
</script>
