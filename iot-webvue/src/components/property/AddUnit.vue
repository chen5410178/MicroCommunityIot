<template>
  <el-dialog :visible.sync="dialogVisible" title="添加单元" width="500px" @close="handleClose">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="单元编号" prop="unitNum">
        <el-input v-model="form.unitNum" placeholder="请填写单元编号" />
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
import { saveUnit } from '@/api/property/propertyApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddUnit',
  props: { visible: { type: Boolean, default: false }, floorId: { type: String, default: '' } },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: { unitNum: '', remark: '' },
      rules: {
        unitNum: [{ required: true, message: '单元编号不能为空', trigger: 'blur' }]
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
      try { await this.$refs.formRef.validate() } catch { return }
      this.loading = true
      try {
        const communityInfo = getCurrentCommunity() || {}
        await saveUnit({ ...this.form, floorId: this.floorId, communityId: communityInfo.communityId })
        this.$message.success('添加成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) { this.$message.error(e.msg || '添加失败') }
      finally { this.loading = false }
    }
  }
}
</script>
