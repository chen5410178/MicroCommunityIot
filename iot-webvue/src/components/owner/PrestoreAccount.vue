<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="预存/维护账户"
    width="550px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="账户类型" prop="acctType">
        <el-select v-model="form.acctType" placeholder="必填，请选择账户类型" style="width: 100%">
          <el-option
            v-for="item in acctTypeList"
            :key="item.statusCd"
            :label="item.name"
            :value="item.statusCd"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="业主手机" prop="tel">
        <el-input v-model="form.tel" placeholder="必填，请输入业主手机号" />
      </el-form-item>
      <el-form-item label="预存金额" prop="amount">
        <el-input v-model="form.amount" placeholder="必填，请输入预存金额" />
      </el-form-item>
      <el-form-item label="刷卡卡号">
        <el-input v-model="form.swipeCardNo" placeholder="请输入刷卡卡号" />
      </el-form-item>
      <el-form-item label="支付方式" prop="primeRate">
        <el-select v-model="form.primeRate" placeholder="必填，请选择支付方式" style="width: 100%">
          <el-option
            v-for="item in payTypeList"
            :key="item.statusCd"
            :label="item.name"
            :value="item.statusCd"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { ownerPrestoreAccount } from '@/api/owner/ownerApi'
import { listDicts } from '@/api/system/systemApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'PrestoreAccount',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      acctTypeList: [],
      payTypeList: [],
      form: {
        acctType: '',
        tel: '',
        amount: '',
        swipeCardNo: '',
        primeRate: '',
        remark: ''
      },
      rules: {
        acctType: [{ required: true, message: '请选择账户类型', trigger: 'change' }],
        tel: [{ required: true, message: '请输入业主手机号', trigger: 'blur' }],
        amount: [{ required: true, message: '请输入预存金额', trigger: 'blur' }],
        primeRate: [{ required: true, message: '请选择支付方式', trigger: 'change' }]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.loadDicts()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadDicts() {
      try {
        const [acctRes, payRes] = await Promise.all([
          listDicts({ tableName: 'account', tableColumns: 'acct_type' }),
          listDicts({ tableName: 'pay_fee_detail', tableColumns: 'prime_rate' })
        ])
        this.acctTypeList = acctRes || []
        this.payTypeList = payRes || []
      } catch { /* ignore */ }
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
        const community = getCurrentCommunity()
        await ownerPrestoreAccount({
          ...this.form,
          communityId: community ? community.communityId : ''
        })
        this.$message.success('操作成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '操作失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
