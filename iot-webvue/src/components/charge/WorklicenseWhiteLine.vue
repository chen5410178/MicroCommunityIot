<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="设置通话白名单"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
      <el-form-item label="号码1" prop="tel1">
        <el-input v-model="form.tel1" placeholder="必填，请填写手机号" maxlength="13" />
      </el-form-item>
      <el-form-item label="号码2">
        <el-input v-model="form.tel2" placeholder="请填写手机号" maxlength="13" />
      </el-form-item>
      <el-form-item label="号码3">
        <el-input v-model="form.tel3" placeholder="请填写手机号" maxlength="13" />
      </el-form-item>
    </el-form>
    <div style="color: #909399; font-size: 12px; margin-top: -10px; margin-bottom: 10px;">
      要更换为自己的SIM卡（支持通话及开通VOLTE通话功能）。默认的为流量卡，不具备通话能力。手机号请输入完整手机号189XXXXXXXX，固话请输入010XXXXXXXX，不要括号。
    </div>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveWhiteList, listWhiteList } from '@/api/charge/chargeApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'WorklicenseWhiteLine',
  props: {
    visible: { type: Boolean, default: false },
    machineData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        machineId: '',
        machineCode: '',
        tel1: '',
        tel2: '',
        tel3: ''
      },
      rules: {
        tel1: [
          { required: true, message: '号码1不能为空', trigger: 'blur' },
          { max: 13, message: '长度不能超过13', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.machineData) {
        this.form.machineId = this.machineData.machineId || ''
        this.form.machineCode = this.machineData.machineCode || ''
        this.form.tel1 = ''
        this.form.tel2 = ''
        this.form.tel3 = ''
        this.loadWhiteList()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadWhiteList() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listWhiteList({ page: 1, row: 1, machineId: this.form.machineId, communityId })
        if (res.data && res.data.length > 0) {
          const item = res.data[0]
          this.form.tel1 = item.tel1 || ''
          this.form.tel2 = item.tel2 || ''
          this.form.tel3 = item.tel3 || ''
        }
      } catch { /* ignore */ }
    },
    handleClose() {
      this.dialogVisible = false
      this.form = { machineId: '', machineCode: '', tel1: '', tel2: '', tel3: '' }
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await saveWhiteList({ ...this.form, communityId })
        this.$message.success('保存成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '保存失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
