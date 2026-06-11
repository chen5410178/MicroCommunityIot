<template>
  <el-dialog :visible.sync="dialogVisible" title="添加楼栋" width="500px" @close="handleClose">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="楼栋名称" prop="floorName">
        <el-input v-model="form.floorName" placeholder="请填写楼栋名称" />
      </el-form-item>
      <el-form-item label="楼栋编号" prop="floorNum">
        <el-input v-model="form.floorNum" placeholder="请填写楼栋编号" />
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
import { saveFloor } from '@/api/property/propertyApi'

export default {
  name: 'AddFloor',
  props: { visible: { type: Boolean, default: false } },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: { floorName: '', floorNum: '', remark: '' },
      rules: {
        floorName: [{ required: true, message: '楼栋名称不能为空', trigger: 'blur' }],
        floorNum: [{ required: true, message: '楼栋编号不能为空', trigger: 'blur' }]
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
        const communityInfo = this.$store.state.app.currentCommunity || {}
        await saveFloor({ ...this.form, communityId: communityInfo.communityId })
        this.$message.success('添加成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) { this.$message.error(e.msg || '添加失败') }
      finally { this.loading = false }
    }
  }
}
</script>
