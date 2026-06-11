<template>
  <el-dialog :visible.sync="dialogVisible" title="请确认您的操作" width="400px">
    <div style="text-align: center; padding: 20px 0;">
      <i class="el-icon-warning" style="font-size: 48px; color: #E6A23C" />
      <p style="margin-top: 20px; font-size: 16px;">确认是否重置密码!</p>
    </div>
    <span slot="footer">
      <el-button @click="dialogVisible = false">点错了</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确认重置</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { listStoreStaff, resetStaffPwd } from '@/api/admin/propertyCompanyApi'

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
    async open(row) {
      this.dialogVisible = true
      try {
        const res = await listStoreStaff({ page: 1, row: 1, adminFlag: 'Y', storeId: row.storeId })
        if (res.data && res.data.length > 0) {
          this.staffInfo = {
            username: res.data[0].staffName,
            userId: res.data[0].staffId,
            curUserName: (() => { const u = sessionStorage.getItem('iot_webvue_user'); return u ? JSON.parse(u).name || '' : '' })()
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
          this.$message.success('修改密码成功，密码为' + res.pwd + '请及时修改密码', 10000)
        } else {
          this.$message.success('重置密码成功')
        }
        this.dialogVisible = false
      } catch (e) {
        this.$message.error(e.msg || '重置失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
