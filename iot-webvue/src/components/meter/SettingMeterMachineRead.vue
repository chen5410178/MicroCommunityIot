<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="批量设置抄表时间"
    width="480px"
    @close="handleClose"
  >
    <el-form :model="form" ref="formRef" label-width="100px">
      <el-form-item label="抄表日" prop="readDay" :rules="[{ required: true, message: '请选择抄表日', trigger: 'change' }]">
        <el-select v-model="form.readDay" placeholder="请选择抄表日" style="width: 100%">
          <el-option :value="day" :label="day + '日'" v-for="day in 31" :key="day" />
        </el-select>
      </el-form-item>
      <el-form-item label="抄表时" prop="readHours" :rules="[{ required: true, message: '请选择抄表时', trigger: 'change' }]">
        <el-select v-model="form.readHours" placeholder="请选择抄表时" style="width: 100%">
          <el-option :value="h" :label="h + '时'" v-for="h in 24" :key="h" />
        </el-select>
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { settingMeterMachineRead } from '@/api/meter/meterApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'SettingMeterMachineRead',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        readDay: '',
        readHours: ''
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
        const community = getCurrentCommunity()
        await settingMeterMachineRead({
          readDay: this.form.readDay,
          readHours: this.form.readHours,
          communityId: community.communityId
        })
        this.$message.success('设置成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '设置失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
