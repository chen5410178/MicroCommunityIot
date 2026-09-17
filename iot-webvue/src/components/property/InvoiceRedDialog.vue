<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="发起红冲"
    width="500px"
    append-to-body
    @close="handleClose"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="冲红原因" prop="reason">
        <el-select v-model="form.reason" placeholder="请选择冲红原因" style="width: 100%">
          <el-option
            v-for="item in reasonOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="form.remark"
          type="textarea"
          :rows="3"
          maxlength="200"
          show-word-limit
          placeholder="请输入备注"
        />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确定红冲</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveInvoiceRed } from '@/api/property/invoiceApi'

export default {
  name: 'InvoiceRedDialog',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      invoiceOrderId: '',
      form: {
        reason: '',
        remark: ''
      },
      // 冲红原因需与商城 invoice_red.reason 保持一致，选项值固定中文
      reasonOptions: [
        { value: '开票有误', label: '开票有误' },
        { value: '销售退回', label: '销售退回' },
        { value: '服务终止', label: '服务终止' },
        { value: '其他', label: '其他' }
      ],
      rules: {
        reason: [{ required: true, message: '请选择冲红原因', trigger: 'change' }]
      }
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    open(row) {
      this.invoiceOrderId = row.invoiceOrderId
      this.form = { reason: '', remark: '' }
      this.dialogVisible = true
    },
    handleClose() {
      this.invoiceOrderId = ''
      this.form = { reason: '', remark: '' }
      if (this.$refs.formRef) {
        this.$refs.formRef.resetFields()
      }
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch (e) {
        return
      }
      this.loading = true
      try {
        const res = await saveInvoiceRed({
          invoiceOrderId: this.invoiceOrderId,
          reason: this.form.reason,
          remark: this.form.remark
        })
        const data = res.data || {}
        if (data.state === 'DONE') {
          this.$message.success('红冲成功')
        } else {
          // 冲红单已落商城但红字开票未完成，等待商城重试
          this.$message.warning(data.failReason || '红冲已提交，红字发票开具中')
        }
        this.$emit('success')
        this.dialogVisible = false
      } catch (e) {
        this.$message.error(e.message || '发起红冲失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
