<template>
  <el-dialog :visible.sync="dialogVisible" title="重置密码" width="400px">
    <div style="text-align: center; padding: 20px 0;">
      <i class="el-icon-warning" style="font-size: 48px; color: #E6A23C" />
      <p style="margin-top: 20px; font-size: 16px;">
        确认要重置员工 <strong>{{ staffInfo.username }}</strong> 的密码吗？
      </p>
    </div>
    <span slot="footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确认重置</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { resetStaffPwd, listStoreStaff } from '@/api/admin/propertyCompanyApi'

export default {
  name: 'ResetStaffPwd',
  data() {
    return {
      dialogVisible: false,
      loading: false,
      staffInfo: {}
    }
  },
  methods: {
    open(staffInfo) {
      this.dialogVisible = true
      if (staffInfo.userId) {
        this.staffInfo = {
          userId: staffInfo.userId,
          username: staffInfo.username || staffInfo.name || ''
        }
      } else if (staffInfo.storeId) {
        this.loadStoreStaff(staffInfo.storeId)
      }
    },
    async loadStoreStaff(storeId) {
      try {
        const res = await listStoreStaff({ page: 1, row: 1, adminFlag: 'Y', storeId })
        if (res.data && res.data.length > 0) {
          this.staffInfo = {
            userId: res.data[0].staffId,
            username: res.data[0].staffName || ''
          }
        }
      } catch { /* ignore */ }
    },
    async handleSubmit() {
      this.loading = true
      try {
        const communityStr = sessionStorage.getItem('iot_webvue_current_community')
        const communityInfo = communityStr ? JSON.parse(communityStr) : {}
        const res = await resetStaffPwd({
          communityId: communityInfo.communityId,
          staffId: this.staffInfo.userId
        })
        if (res.pwd) {
          this.$message.success('重置成功，新密码为' + res.pwd + '，请及时修改', 10000)
        } else {
          this.$message.success('重置密码成功')
        }
        this.dialogVisible = false
        this.$emit('success')
      } catch (e) {
        this.$message.error(e.msg || '重置失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
