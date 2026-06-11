<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="充值"
    width="500px"
    @close="handleClose"
  >
    <el-form :model="form" ref="formRef" label-width="100px">
      <el-form-item label="房屋">
        <el-input v-model="form.roomName" disabled />
      </el-form-item>
      <el-form-item label="表号">
        <el-input v-model="form.address" disabled />
      </el-form-item>
      <el-form-item label="充值金额" prop="chargeMoney" :rules="[{ required: true, message: '请输入充值金额', trigger: 'blur' }]">
        <el-input v-model="form.chargeMoney" placeholder="必填，请输入充值金额" />
      </el-form-item>
      <el-form-item label="说明" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="输入说明" :rows="3" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { chargeMeterMoney } from '@/api/meter/meterApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ChargeMeter',
  props: {
    visible: { type: Boolean, default: false },
    chargeData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        machineId: '',
        machineName: '',
        address: '',
        roomName: '',
        chargeMoney: '',
        remark: ''
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.chargeData) {
        this.form = {
          machineId: this.chargeData.machineId || '',
          machineName: this.chargeData.machineName || '',
          address: this.chargeData.address || '',
          roomName: this.chargeData.roomName || '',
          chargeMoney: '',
          remark: ''
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
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const community = getCurrentCommunity()
        await chargeMeterMoney({
          ...this.form,
          communityId: community.communityId
        })
        this.$message.success('充值成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '充值失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
