<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加设备位置"
    width="550px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="位置名称" prop="locationName">
        <el-input v-model="form.locationName" placeholder="请填写位置名称" maxlength="100" />
      </el-form-item>
      <el-form-item label="位置类型" prop="locationType">
        <el-select v-model="form.locationType" placeholder="请选择位置类型" clearable style="width: 100%" @change="handleTypeChange">
          <el-option label="小区" value="1000" />
          <el-option label="楼栋" value="6000" />
          <el-option label="单元" value="2000" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="form.locationType === '6000' || form.locationType === '2000'" label="楼栋" prop="floorId">
        <el-select v-model="form.floorId" placeholder="请选择楼栋" clearable style="width: 100%" @change="handleFloorChange">
          <el-option v-for="item in floors" :key="item.floorId" :label="item.floorName || item.floorNum" :value="item.floorId" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="form.locationType === '2000'" label="单元" prop="unitId">
        <el-select v-model="form.unitId" placeholder="请选择单元" clearable style="width: 100%">
          <el-option v-for="item in units" :key="item.unitId" :label="item.unitNum" :value="item.unitId" />
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
import { saveMachineLocation, queryFloors, queryUnits } from '@/api/machine/machineApi'

export default {
  name: 'AddMachineLocation',
  props: { visible: { type: Boolean, default: false } },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      floors: [],
      units: [],
      form: {
        locationName: '',
        locationType: '',
        floorId: '',
        unitId: ''
      },
      rules: {
        locationName: [
          { required: true, message: '位置名称不能为空', trigger: 'blur' },
          { max: 100, message: '位置名称不能超过100字符', trigger: 'blur' }
        ],
        locationType: [
          { required: true, message: '位置类型不能为空', trigger: 'change' }
        ],
        floorId: [
          { required: true, message: '楼栋不能为空', trigger: 'change' }
        ],
        unitId: [
          { required: true, message: '单元不能为空', trigger: 'change' }
        ]
      }
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
      this.floors = []
      this.units = []
      this.form.unitId = ''
      this.form.floorId = ''
    },
    handleTypeChange(val) {
      this.form.floorId = ''
      this.form.unitId = ''
      this.units = []
      if (val === '6000' || val === '2000') {
        this.loadFloors()
      }
    },
    handleFloorChange() {
      this.form.unitId = ''
      this.units = []
      if (this.form.locationType === '2000' && this.form.floorId) {
        this.loadUnits()
      }
    },
    async loadFloors() {
      const communityInfo = this.$store.state.app.currentCommunity || {}
      try {
        const res = await queryFloors({ communityId: communityInfo.communityId, page: 1, row: 100 })
        this.floors = res.data || []
      } catch (e) { /* 加载失败静默处理 */ }
    },
    async loadUnits() {
      const communityInfo = this.$store.state.app.currentCommunity || {}
      try {
        const res = await queryUnits({ communityId: communityInfo.communityId, floorId: this.form.floorId, page: 1, row: 100 })
        this.units = Array.isArray(res) ? res : (res.data || [])
      } catch (e) { /* 加载失败静默处理 */ }
    },
    async handleSubmit() {
      try { await this.$refs.formRef.validate() } catch { return }
      this.loading = true
      try {
        const communityInfo = this.$store.state.app.currentCommunity || {}
        const data = {
          communityId: communityInfo.communityId,
          locationName: this.form.locationName,
          locationType: this.form.locationType
        }
        if (this.form.locationType === '1000') {
          data.locationObjId = communityInfo.communityId
          data.locationObjName = communityInfo.name || ''
        } else if (this.form.locationType === '6000') {
          data.locationObjId = this.form.floorId
          const floor = this.floors.find(f => f.floorId === this.form.floorId)
          data.locationObjName = floor ? (floor.floorName || floor.floorNum) : ''
        } else if (this.form.locationType === '2000') {
          data.locationObjId = this.form.unitId
          const unit = this.units.find(u => u.unitId === this.form.unitId)
          data.locationObjName = unit ? unit.unitNum : ''
        }
        await saveMachineLocation(data)
        this.$message.success('添加成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) { this.$message.error(e.msg || '添加失败') }
      finally { this.loading = false }
    }
  }
}
</script>
