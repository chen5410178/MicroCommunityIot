<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="房屋认证审核"
    width="400px"
    @close="handleClose"
  >
    <div style="text-align: center; padding: 20px 0;">
      <p style="font-size: 16px;">
        确定对 <strong>{{ appUserData && appUserData.ownerName }}</strong> 进行审核吗？
      </p>
    </div>
    <span slot="footer">
      <el-button type="primary" @click="handlePass" :loading="loading">通过</el-button>
      <el-button type="danger" @click="handleReject" :loading="loading">不通过</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateAppUser } from '@/api/owner/ownerApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditAppUser',
  props: {
    visible: { type: Boolean, default: false },
    appUserData: { type: Object, default: null }
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
    handleClose() { this.dialogVisible = false },
    async handlePass() {
      await this.doAudit('12000', '')
    },
    async handleReject() {
      await this.doAudit('13000', '失败')
    },
    async doAudit(state, stateMsg) {
      this.loading = true
      try {
        const community = getCurrentCommunity()
        await updateAppUser({
          auId: this.appUserData.auId,
          state: state,
          stateMsg: stateMsg,
          communityId: community ? community.communityId : ''
        })
        this.$message.success(state === '12000' ? '审核通过' : '审核不通过')
        this.$emit('success')
        this.dialogVisible = false
      } catch (e) {
        this.$message.error(e.msg || '操作失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
