<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="分配详情"
    width="550px"
  >
    <el-form :model="form" label-width="100px" disabled>
      <el-form-item label="员工姓名">
        <el-input :value="form.staffName" />
      </el-form-item>
      <el-form-item label="分配时间">
        <el-input :value="form.createTime" />
      </el-form-item>
      <el-form-item label="状态">
        <el-input :value="form.stateName" />
      </el-form-item>
      <el-form-item label="事故原因">
        <el-input type="textarea" :value="form.eventReason" :rows="3" />
      </el-form-item>
      <el-form-item label="处理方案">
        <el-input type="textarea" :value="form.eventResult" :rows="3" />
      </el-form-item>
      <el-form-item label="处理时长">
        <el-input :value="form.eventHours" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button v-if="form.state === 'W'" type="danger" @click="handleCancel" :loading="cancelLoading">取消分配</el-button>
      <el-button @click="dialogVisible = false">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateEventPoolStaff } from '@/api/event/eventApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ViewEventPoolStaffInfo',
  props: {
    visible: { type: Boolean, default: false },
    staffData: { type: Object, default: () => ({}) }
  },
  data() {
    return {
      dialogVisible: false,
      cancelLoading: false,
      form: {}
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.staffData) {
        this.form = { ...this.staffData }
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async handleCancel() {
      try {
        await this.$confirm('确定要取消此分配吗？', '确认取消', { type: 'warning' })
      } catch { return }
      this.cancelLoading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await updateEventPoolStaff({
          epsId: this.form.epsId,
          state: 'NC',
          communityId
        })
        this.$message.success('取消分配成功')
        this.$emit('success')
        this.dialogVisible = false
      } catch (e) {
        this.$message.error(e.msg || '操作失败')
      } finally {
        this.cancelLoading = false
      }
    }
  }
}
</script>
