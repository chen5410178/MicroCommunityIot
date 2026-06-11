<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加月卡"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
      <el-form-item label="月卡名称" prop="cardName">
        <el-input v-model="form.cardName" placeholder="必填，请填写月卡名称" maxlength="200" />
      </el-form-item>
      <el-form-item label="月数" prop="cardMonth">
        <el-input v-model="form.cardMonth" placeholder="必填，请填写月数" />
      </el-form-item>
      <el-form-item label="月价" prop="cardPrice">
        <el-input v-model="form.cardPrice" placeholder="必填，请填写月价" />
      </el-form-item>
      <el-form-item label="每日充电小时" prop="dayHours">
        <el-input v-model="form.dayHours" placeholder="必填，请填写每日充电小时" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" placeholder="选填，请填写备注" maxlength="512" type="textarea" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveChargeMonthCard } from '@/api/charge/chargeApi'

export default {
  name: 'AddChargeMonthCard',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        cardName: '',
        cardMonth: '',
        cardPrice: '',
        dayHours: '',
        remark: ''
      },
      rules: {
        cardName: [
          { required: true, message: '月卡名称不能为空', trigger: 'blur' },
          { max: 200, message: '月卡名称不能超过200', trigger: 'blur' }
        ],
        cardMonth: [
          { required: true, message: '月数不能为空', trigger: 'blur' }
        ],
        cardPrice: [
          { required: true, message: '月价不能为空', trigger: 'blur' }
        ],
        dayHours: [
          { required: true, message: '每日充电小时不能为空', trigger: 'blur' }
        ],
        remark: [
          { max: 512, message: '备注不能超过512', trigger: 'blur' }
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
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        await saveChargeMonthCard({ ...this.form, communityId })
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
