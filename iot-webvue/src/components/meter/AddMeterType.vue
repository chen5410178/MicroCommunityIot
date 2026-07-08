<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加抄表类型"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="名称" prop="typeName">
        <el-input v-model="form.typeName" placeholder="必填，请填写名称" maxlength="12" />
      </el-form-item>
      <el-form-item label="单价" prop="price">
        <el-input v-model="form.price" placeholder="必填，请填写单价" maxlength="30" />
      </el-form-item>
      <el-form-item label="表类型" prop="typeCd">
        <el-select v-model="form.typeCd" placeholder="请选择表类型" style="width: 100%">
          <el-option label="电表" value="888800010016" />
          <el-option label="水表" value="888800010015" />
          <el-option label="煤气表" value="888800010009" />
        </el-select>
      </el-form-item>
      <el-form-item label="说明" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="必填，请填写说明" maxlength="200" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveMeterType } from '@/api/meter/meterApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddMeterType',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        typeName: '',
        price: '',
        typeCd: '',
        remark: ''
      },
      rules: {
        typeName: [
          { required: true, message: '名称不能为空', trigger: 'blur' },
          { max: 12, message: '名称不能超过12位', trigger: 'blur' }
        ],
        price: [
          { required: true, message: '单价不能为空', trigger: 'blur' },
          { max: 30, message: '单价不能超过30位', trigger: 'blur' }
        ],
        typeCd: [
          { required: true, message: '请选择表类型', trigger: 'change' }
        ],
        remark: [
          { required: true, message: '说明不能为空', trigger: 'blur' },
          { max: 200, message: '说明不能超过200字', trigger: 'blur' }
        ]
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
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await saveMeterType({ ...this.form, communityId })
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
