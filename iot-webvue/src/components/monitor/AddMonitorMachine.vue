<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加监控设备"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="监控名称" prop="machineName">
        <el-input v-model="form.machineName" placeholder="必填，请填写监控名称" />
      </el-form-item>
      <el-form-item label="监控编号" prop="machineCode">
        <el-input v-model="form.machineCode" placeholder="必填，请填写监控编号" />
      </el-form-item>
      <el-form-item label="监控位置" prop="locationName">
        <el-input v-model="form.locationName" placeholder="必填，请填写监控位置" />
      </el-form-item>
      <el-form-item label="监控区域" prop="maId">
        <el-select v-model="form.maId" placeholder="必填，请选择监控区域" style="width: 100%">
          <el-option
            v-for="item in areaList"
            :key="item.maId"
            :label="item.maName"
            :value="item.maId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="监控平台" prop="mmId">
        <el-select v-model="form.mmId" placeholder="必填，请选择监控平台" style="width: 100%">
          <el-option
            v-for="item in manufactorList"
            :key="item.mmId"
            :label="item.mmName"
            :value="item.mmId"
          />
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
import { saveMonitorMachine, listMonitorAreas, listMonitorManufactors } from '@/api/monitor/monitorApi'

export default {
  name: 'AddMonitorMachine',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      areaList: [],
      manufactorList: [],
      form: {
        machineName: '',
        machineCode: '',
        locationName: '',
        maId: '',
        mmId: ''
      },
      rules: {
        machineName: [
          { required: true, message: '监控名称不能为空', trigger: 'blur' },
          { max: 200, message: '监控名称不能超过200', trigger: 'blur' }
        ],
        machineCode: [
          { required: true, message: '监控编号不能为空', trigger: 'blur' },
          { max: 30, message: '监控编号不能超过30', trigger: 'blur' }
        ],
        locationName: [
          { required: true, message: '监控位置不能为空', trigger: 'blur' },
          { max: 64, message: '监控位置不能超过64', trigger: 'blur' }
        ],
        maId: [
          { required: true, message: '请选择监控区域', trigger: 'change' }
        ],
        mmId: [
          { required: true, message: '请选择监控平台', trigger: 'change' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.fetchAreaList()
        this.fetchManufactorList()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async fetchAreaList() {
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listMonitorAreas({ page: 1, row: 100, communityId })
        this.areaList = res.data || []
      } catch { /* ignore */ }
    },
    async fetchManufactorList() {
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listMonitorManufactors({ page: 1, row: 100, communityId })
        this.manufactorList = res.data || []
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
        await saveMonitorMachine({ ...this.form, communityId })
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
