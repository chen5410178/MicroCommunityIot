<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加停车位"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="车位编号" prop="num">
        <el-input v-model="form.num" placeholder="必填，请填写车位编号" maxlength="12" />
      </el-form-item>
      <el-form-item label="停车场" prop="paId">
        <el-select v-model="form.paId" placeholder="必填，请选择停车场" style="width: 100%" @change="handlePaChange">
          <el-option v-for="item in parkingAreas" :key="item.paId" :label="item.num" :value="item.paId" />
        </el-select>
      </el-form-item>
      <el-form-item label="停车区域" prop="prId">
        <el-select v-model="form.prId" placeholder="选填，请选择停车区域" style="width: 100%">
          <el-option v-for="item in parkingRegions" :key="item.prId" :label="item.regionCode" :value="item.prId" />
        </el-select>
      </el-form-item>
      <el-form-item label="车位类型" prop="parkingType">
        <el-select v-model="form.parkingType" placeholder="必填，请选择车位类型" style="width: 100%">
          <el-option label="普通车位" value="1" />
          <el-option label="子母车位" value="2" />
          <el-option label="豪华车位" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="选填，请填写备注" maxlength="300" rows="3" />
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
import { saveParkingSpace, listParkingAreas, listParkingRegions } from '@/api/car/carApi'
import { listMonitorMachines } from '@/api/monitor/monitorApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddParkingSpace',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      parkingAreas: [],
      parkingRegions: [],
      monitors: [],
      form: {
        num: '',
        paId: '',
        prId: '',
        parkingType: '',
        remark: '',
        monitorId: ''
      },
      rules: {
        num: [
          { required: true, message: '车位编号不能为空', trigger: 'blur' },
          { max: 12, message: '车位编号不能超过12', trigger: 'blur' }
        ],
        paId: [
          { required: true, message: '停车场不能为空', trigger: 'change' }
        ],
        parkingType: [
          { required: true, message: '车位类型不能为空', trigger: 'change' }
        ],
        remark: [
          { max: 300, message: '备注不能超过300', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) { this.loadOptions() }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },
    async loadOptions() {
      const communityId = (getCurrentCommunity() || {}).communityId || ''
      try {
        const [areasRes, monitorsRes] = await Promise.all([
          listParkingAreas({ page: 1, row: 100, communityId }),
          listMonitorMachines({ page: 1, row: 50, communityId })
        ])
        this.parkingAreas = areasRes.data || []
        this.monitors = monitorsRes.data || []
      } catch (e) { /* ignore */ }
    },
    async handlePaChange() {
      if (!this.form.paId) {
        this.parkingRegions = []
        this.form.prId = ''
        return
      }
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listParkingRegions({ page: 1, row: 100, communityId, paId: this.form.paId })
        this.parkingRegions = res.data || []
      } catch (e) { /* ignore */ }
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        this.form.communityId = (getCurrentCommunity() || {}).communityId || ''
        this.form.state = 'F'
        await saveParkingSpace(this.form)
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
