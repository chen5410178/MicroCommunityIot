<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="续期"
    width="500px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="续期开始时间" prop="startTime">
        <el-date-picker
          v-model="form.startTime"
          type="datetime"
          placeholder="请选择续期开始时间"
          value-format="yyyy-MM-dd HH:mm:ss"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="续期结束时间" prop="endTime">
        <el-date-picker
          v-model="form.endTime"
          type="datetime"
          placeholder="请选择续期结束时间"
          value-format="yyyy-MM-dd HH:mm:ss"
          style="width: 100%"
        />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">保存</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { renewalLockPerson } from '@/api/lock/lockApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'RenewalLockPerson',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        startTime: '',
        endTime: ''
      },
      rules: {
        startTime: [{ required: true, message: '续期开始时间不能为空', trigger: 'change' }],
        endTime: [{ required: true, message: '续期结束时间不能为空', trigger: 'change' }]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.$nextTick(() => {
          this.form.startTime = ''
          this.form.endTime = ''
          this.$refs.formRef && this.$refs.formRef.clearValidate()
        })
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await renewalLockPerson({ ...this.editData, startTime: this.form.startTime, endTime: this.form.endTime, communityId })
        this.$message.success('续期成功')
        this.$emit('success')
        this.dialogVisible = false
      } catch (e) {
        this.$message.error(e.msg || '续期失败')
      } finally {
        this.loading = false
      }
    },
    handleClose() {
      this.form = { startTime: '', endTime: '' }
    }
  }
}
</script>
