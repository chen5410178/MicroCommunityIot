<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加停车场"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="停车场编号" prop="num">
        <el-input v-model="form.num" placeholder="必填，请填写停车场编号" maxlength="12" />
      </el-form-item>
      <el-form-item label="类型" prop="typeCd">
        <el-select v-model="form.typeCd" placeholder="必填，请选择类型" style="width: 100%">
          <el-option label="地上停车场" value="1001" />
          <el-option label="地下停车场" value="2002" />
        </el-select>
      </el-form-item>
      <el-form-item label="三方ID" prop="extPaId">
        <el-input v-model="form.extPaId" placeholder="选填，请填写三方停车场ID" maxlength="64" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" placeholder="选填，请填写备注" maxlength="300" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveParkingArea } from '@/api/car/carApi'

export default {
  name: 'AddParkingArea',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        num: '',
        typeCd: '',
        extPaId: '',
        remark: ''
      },
      rules: {
        num: [
          { required: true, message: '停车场编号不能为空', trigger: 'blur' },
          { max: 12, message: '停车场编号不能超过12', trigger: 'blur' }
        ],
        typeCd: [
          { required: true, message: '类型不能为空', trigger: 'change' }
        ],
        extPaId: [
          { max: 64, message: '三方ID不能超过64', trigger: 'blur' }
        ],
        remark: [
          { max: 300, message: '备注不能超过300', trigger: 'blur' }
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
        this.form.communityId = this.$store.state.app.currentCommunity.communityId || ''
        await saveParkingArea(this.form)
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
