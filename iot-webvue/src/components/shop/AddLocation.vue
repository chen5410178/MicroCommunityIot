<template>
  <el-dialog
    :visible.sync="dialogVisible"
    :title="title"
    width="500px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="父级位置">
        <el-input :value="parentName || '顶级位置'" disabled />
      </el-form-item>
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
import { saveShopLocation } from '@/api/shop/shopApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddLocation',
  props: {
    visible: { type: Boolean, default: false },
    parentId: { type: String, default: '-1' },
    parentName: { type: String, default: '' },
    level: { type: Number, default: 0 }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: { locationName: '', seq: 1, remark: '' },
      rules: {
        locationName: [{ required: true, message: '位置名称不能为空', trigger: 'blur' }],
        seq: [{ required: true, message: '排序不能为空', trigger: 'blur' }]
      }
    }
  },
  computed: {
    title() {
      return this.parentId === '-1' ? '新增顶级位置' : '新增子位置'
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
      this.form = { locationName: '', seq: 1, remark: '' }
    },
    async handleSubmit() {
      try { await this.$refs.formRef.validate() } catch { return }
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const submitData = {
          ...this.form,
          parentId: this.parentId,
          level: this.level > 0 ? this.level + 1 : 1,
          communityId
        }
        await saveShopLocation(submitData)
        this.$message.success('添加成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) { this.$message.error(e.message || '添加失败') }
      finally { this.loading = false }
    }
  }
}
</script>
