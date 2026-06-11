<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="临时车入场审核"
    width="500px"
    @close="handleClose"
  >
    <div class="auth-content">
      <div class="form-group">
        <label>车牌号</label>
        <el-input v-model="authInfo.carNum" disabled size="small" />
      </div>
      <div class="form-group">
        <label>停车场</label>
        <el-input v-model="authInfo.paNum" disabled size="small" />
      </div>
      <div class="form-group">
        <label>入口</label>
        <el-input v-model="authInfo.machineName" disabled size="small" />
      </div>
      <div class="form-group">
        <label>开门信息</label>
        <el-input v-model="authInfo.remark" type="textarea" :rows="2" disabled size="small" />
      </div>
      <div class="form-group">
        <label>说明</label>
        <el-input v-model="authInfo.msg" type="textarea" :rows="2" placeholder="请填写审核说明" size="small" />
      </div>
    </div>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="success" @click="doConfirm('C')" :loading="loading">允许通行</el-button>
      <el-button type="danger" @click="doConfirm('F')" :loading="loading">禁止通行</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { tempCarAuth } from '@/api/car/carApi'

export default {
  name: 'ParkingAreaControlTempCarAuthConfirm',
  data() {
    return {
      dialogVisible: false,
      loading: false,
      authInfo: {
        carNum: '',
        authId: '',
        machineName: '',
        paNum: '',
        remark: '',
        msg: '',
        state: ''
      }
    }
  },
  methods: {
    open(data) {
      this.dialogVisible = true
      this.authInfo = {
        carNum: data.carNum || '',
        authId: data.authId || data.taId || '',
        machineName: data.machineName || '',
        paNum: data.paNum || '',
        remark: data.remark || '',
        msg: '',
        state: ''
      }
    },
    onWsNotify(data) {
      if (!data || data.action !== 'TEMP_CAR_AUTH') return
      this.dialogVisible = true
      this.authInfo = {
        carNum: data.body ? data.body.carNum : '',
        authId: data.body ? data.body.authId || data.body.taId : '',
        machineName: data.body ? data.body.machineName : '',
        paNum: data.body ? data.body.paNum : '',
        remark: data.body ? data.body.remark : '',
        msg: '',
        state: ''
      }
    },
    handleClose() {
      this.dialogVisible = false
      this.authInfo = {
        carNum: '', authId: '', machineName: '', paNum: '', remark: '', msg: '', state: ''
      }
    },
    async doConfirm(state) {
      if (state === 'C') {
        this.authInfo.msg = this.authInfo.msg || '允许通行'
      } else {
        this.authInfo.msg = this.authInfo.msg || '禁止通行'
      }
      this.authInfo.state = state
      const communityId = this.$store.state.app.currentCommunity.communityId || ''
      this.loading = true
      try {
        const res = await tempCarAuth({ ...this.authInfo, communityId })
        if (res.code === 0) {
          this.$message.success(res.msg || '操作成功')
          this.handleClose()
          this.$emit('success')
        } else {
          this.$message.error(res.msg || '操作失败')
        }
      } catch (e) {
        this.$message.error('操作失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.auth-content { }
.form-group { margin-bottom: 12px; }
.form-group label { display: block; font-size: 12px; color: #909399; margin-bottom: 4px; }
</style>
