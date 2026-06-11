<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="清零确认"
    width="480px"
  >
    <div style="text-align: center; padding: 20px 0;">
      <i class="el-icon-warning" style="font-size: 48px; color: #e6a23c" />
      <p style="margin-top: 20px; font-size: 16px;">
        确定要清零智能水电表 <strong>{{ cleanData && cleanData.machineName }}</strong> 吗？
      </p>
      <p style="color: #909399; font-size: 14px; margin-top: 8px;">清零后当前度数将归零，此操作不可恢复</p>
    </div>
    <span slot="footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="warning" @click="handleSubmit" :loading="loading">确定清零</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { cleanMeterMachine } from '@/api/meter/meterApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'CleanMeterMachine',
  props: {
    visible: { type: Boolean, default: false },
    cleanData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async handleSubmit() {
      this.loading = true
      try {
        const community = getCurrentCommunity()
        const data = {
          address: this.cleanData.address,
          communityId: community.communityId,
          roomId: this.cleanData.roomId,
          machineId: this.cleanData.machineId,
          controlType: this.cleanData.controlType,
          meterType: this.cleanData.meterType,
          implBean: this.cleanData.implBean,
          implBeanName: this.cleanData.implBeanName,
          curDegrees: this.cleanData.curDegrees,
          prestoreDegrees: this.cleanData.prestoreDegrees
        }
        await cleanMeterMachine(data)
        this.$message.success('清零成功')
        this.$emit('success')
        this.dialogVisible = false
      } catch (e) {
        this.$message.error(e.msg || '清零失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
