<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="分配员工"
    width="500px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="员工" prop="staffId">
        <el-select v-model="form.staffId" placeholder="请选择员工" style="width: 100%" filterable>
          <el-option
            v-for="item in staffList"
            :key="item.staffId"
            :label="item.staffName"
            :value="item.staffId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="事件ID">
        <el-input :value="eventId" disabled />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveEventPoolStaff, listCommunityStaffs } from '@/api/event/eventApi'

export default {
  name: 'AddEventPoolStaff',
  props: {
    visible: { type: Boolean, default: false },
    eventId: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      staffList: [],
      form: {
        staffId: ''
      },
      rules: {
        staffId: [{ required: true, message: '请选择员工', trigger: 'change' }]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.loadStaffs()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadStaffs() {
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listCommunityStaffs({ communityId, page: 1, row: 200 })
        this.staffList = res.data || []
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
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        await saveEventPoolStaff({
          eventId: this.eventId,
          staffId: this.form.staffId,
          communityId,
          state: 'W'
        })
        this.$message.success('分配成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '分配失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
