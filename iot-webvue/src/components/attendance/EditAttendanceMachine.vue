<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改考勤设备"
    width="600px"
    append-to-body
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="设备编码" prop="machineCode">
        <el-input v-model="form.machineCode" placeholder="必填，请填写设备编码" maxlength="30" />
      </el-form-item>
      <el-form-item label="设备名称" prop="machineName">
        <el-input v-model="form.machineName" placeholder="必填，请填写设备名称" maxlength="200" />
      </el-form-item>
      <el-form-item label="设备IP" prop="machineIp">
        <el-input v-model="form.machineIp" placeholder="请填写设备IP" maxlength="64" />
      </el-form-item>
      <el-form-item label="门禁厂家" prop="implBean">
        <el-select v-model="form.implBean" placeholder="请选择厂家" style="width: 100%">
          <el-option v-for="item in hmList" :key="item.hmId" :label="item.hmName" :value="item.hmId" />
        </el-select>
      </el-form-item>
      <el-form-item label="班组ID" prop="classesId">
        <el-input v-model="form.classesId" placeholder="必填，请填写班组ID" maxlength="64" />
      </el-form-item>
      <el-form-item label="班组名称" prop="classesName">
        <el-input v-model="form.classesName" placeholder="必填，请填写班组名称" maxlength="200" />
      </el-form-item>
      <el-form-item label="监控视频" prop="monitorId">
        <el-select v-model="form.monitorId" placeholder="请选择监控视频" style="width: 100%">
          <el-option label="没有监控" :value="-1" />
          <el-option v-for="item in monitorList" :key="item.monitorId" :label="item.monitorName" :value="item.monitorId" />
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
import { updateAttendanceMachine, listHardwareManufacturers, listMonitorMachines } from '@/api/attendance/attendanceApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditAttendanceMachine',
  data() {
    return {
      dialogVisible: false,
      loading: false,
      hmList: [],
      monitorList: [],
      form: {
        machineId: '',
        machineCode: '',
        machineName: '',
        machineIp: '',
        implBean: '',
        classesId: '',
        classesName: '',
        monitorId: -1,
        communityId: ''
      },
      rules: {
        machineCode: [
          { required: true, message: '设备编码不能为空', trigger: 'blur' },
          { max: 30, message: '设备编码不能超过30', trigger: 'blur' }
        ],
        machineName: [
          { required: true, message: '设备名称不能为空', trigger: 'blur' },
          { max: 200, message: '设备名称不能超过200', trigger: 'blur' }
        ],
        implBean: [{ required: true, message: '请选择门禁厂家', trigger: 'change' }],
        classesId: [{ required: true, message: '班组ID不能为空', trigger: 'blur' }],
        classesName: [{ required: true, message: '班组名称不能为空', trigger: 'blur' }]
      }
    }
  },
  methods: {
    open(row) {
      this.form = {
        machineId: row.machineId || '',
        machineCode: row.machineCode || '',
        machineName: row.machineName || '',
        machineIp: row.machineIp || '',
        implBean: row.implBean || '',
        classesId: row.classesId || '',
        classesName: row.classesName || '',
        monitorId: row.monitorId != null ? row.monitorId : -1,
        communityId: row.communityId || (getCurrentCommunity() || {}).communityId || ''
      }
      this.dialogVisible = true
      this.loadOptions()
    },
    async loadOptions() {
      const communityId = (getCurrentCommunity() || {}).communityId || ''
      try {
        const [hmRes, monitorRes] = await Promise.all([
          listHardwareManufacturers({ hmType: '4004', page: 1, row: 100 }),
          listMonitorMachines({ page: 1, row: 100, communityId })
        ])
        this.hmList = hmRes.data || []
        this.monitorList = monitorRes.data || []
      } catch (e) {
        console.error('加载下拉选项失败', e)
      }
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
        await updateAttendanceMachine(this.form)
        this.$message.success('修改成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '修改失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
