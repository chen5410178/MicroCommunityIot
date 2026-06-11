<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加客服"
    width="550px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="操作人" prop="staffId">
        <el-select v-model="form.staffId" placeholder="必填，请选择操作人" filterable clearable style="width: 100%">
          <el-option
            v-for="item in staffList"
            :key="item.userId"
            :label="item.userName"
            :value="item.userId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="默认客服" prop="masterFlag">
        <el-select v-model="form.masterFlag" placeholder="必填，请选择是否是默认客服" style="width: 100%">
          <el-option label="是" value="Y" />
          <el-option label="否" value="N" />
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
import { saveTelMachineUser, listStaffs } from '@/api/lamp/lampApi'

export default {
  name: 'AddTelMachineUser',
  props: {
    visible: { type: Boolean, default: false },
    machineId: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      staffList: [],
      form: {
        staffId: '',
        masterFlag: ''
      },
      rules: {
        staffId: [
          { required: true, message: '操作人不能为空', trigger: 'change' }
        ],
        masterFlag: [
          { required: true, message: '默认客服不能为空', trigger: 'change' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.fetchStaffs()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async fetchStaffs() {
      try {
        const res = await listStaffs({ page: 1, row: 300 })
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
        await saveTelMachineUser({ ...this.form, machineId: this.machineId, communityId })
        this.$message.success('添加成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '添加失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
