<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="播放语音"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
      <el-form-item label="播放内容" prop="ppsText">
        <el-input v-model="form.ppsText" type="textarea" :rows="4" placeholder="请填写需要播放的内容" maxlength="200" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { playPps } from '@/api/charge/chargeApi'

export default {
  name: 'WorklicensePps',
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
        ppsText: ''
      },
      rules: {
        ppsText: [
          { required: true, message: '语音不能为空', trigger: 'blur' },
          { max: 200, message: '语音不能超过200', trigger: 'blur' }
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
        this.form.ppsText = ''
        this.$nextTick(() => {
          this.$refs.formRef && this.$refs.formRef.clearValidate()
        })
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    handleClose() {
      this.dialogVisible = false
      this.form = { machineId: '', machineCode: '', ppsText: '' }
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        await playPps({ ...this.form, communityId })
        this.$message.success('播放成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '播放失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
