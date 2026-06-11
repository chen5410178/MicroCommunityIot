<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改道闸摄像头"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="设备名称" prop="machineName">
        <el-input v-model="form.machineName" placeholder="必填，请填写设备名称" maxlength="200" />
      </el-form-item>
      <el-form-item label="设备编码" prop="machineCode">
        <el-input v-model="form.machineCode" placeholder="必填，请填写设备编码" maxlength="64" />
      </el-form-item>
      <el-form-item label="设备IP" prop="machineIp">
        <el-input v-model="form.machineIp" placeholder="必填，请填写设备IP" maxlength="64" />
      </el-form-item>
      <el-form-item label="设备方向" prop="direction">
        <el-select v-model="form.direction" placeholder="必填，请选择设备方向" style="width: 100%">
          <el-option label="进" value="3306" />
          <el-option label="出" value="3307" />
        </el-select>
      </el-form-item>
      <el-form-item label="道闸厂家" prop="implBean">
        <el-select v-model="form.implBean" placeholder="必填，请选择道闸厂家" style="width: 100%">
          <el-option v-for="item in hms" :key="item.hmId" :label="item.hmName" :value="item.hmId" />
        </el-select>
      </el-form-item>
      <el-form-item label="岗亭" prop="boxId">
        <el-select v-model="form.boxId" placeholder="必填，请选择岗亭" style="width: 100%">
          <el-option v-for="item in parkingBoxs" :key="item.boxId" :label="item.boxName" :value="item.boxId" />
        </el-select>
      </el-form-item>
      <el-form-item label="监控视频" prop="monitorId">
        <el-select v-model="form.monitorId" placeholder="选填，请选择监控视频" style="width: 100%">
          <el-option label="没有监控" value="-1" />
          <el-option v-for="item in monitors" :key="item.machineId" :label="item.machineName" :value="item.machineId" />
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
import { updateBarrier, listParkingBoxs, listHms } from '@/api/car/carApi'
import { listMonitorMachines } from '@/api/monitor/monitorApi'

export default {
  name: 'EditBarrier',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      hms: [],
      parkingBoxs: [],
      monitors: [],
      form: {
        machineId: '',
        machineName: '',
        machineCode: '',
        machineIp: '',
        direction: '',
        implBean: '',
        boxId: '',
        monitorId: ''
      },
      rules: {
        machineName: [
          { required: true, message: '设备名称不能为空', trigger: 'blur' },
          { max: 200, message: '设备名称不能超过200', trigger: 'blur' }
        ],
        machineCode: [
          { required: true, message: '设备编码不能为空', trigger: 'blur' },
          { max: 64, message: '设备编码不能超过64', trigger: 'blur' }
        ],
        machineIp: [
          { required: true, message: '设备IP不能为空', trigger: 'blur' },
          { max: 64, message: '设备IP不能超过64', trigger: 'blur' }
        ],
        direction: [
          { required: true, message: '设备方向不能为空', trigger: 'change' }
        ],
        implBean: [
          { required: true, message: '道闸厂家不能为空', trigger: 'change' }
        ],
        boxId: [
          { required: true, message: '岗亭不能为空', trigger: 'change' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.loadOptions()
        if (this.editData) {
          this.$nextTick(() => { this.form = { ...this.editData } })
        }
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },
    async loadOptions() {
      const communityId = this.$store.state.app.currentCommunity.communityId || ''
      try {
        const [hmsRes, boxsRes, monitorsRes] = await Promise.all([
          listHms({ page: 1, row: 50, hmType: '2002' }),
          listParkingBoxs({ page: 1, row: 50, communityId }),
          listMonitorMachines({ page: 1, row: 50, communityId })
        ])
        this.hms = hmsRes.data || []
        this.parkingBoxs = boxsRes.data || []
        this.monitors = monitorsRes.data || []
      } catch (e) { /* ignore */ }
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        this.form.communityId = this.$store.state.app.currentCommunity.communityId || ''
        await updateBarrier(this.form)
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
