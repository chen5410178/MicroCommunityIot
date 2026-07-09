<template>
  <el-dialog :visible.sync="dialogVisible" title="修改房屋" width="700px" @close="handleClose">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
      <el-form-item label="房屋ID">
        <el-input v-model="form.roomId" disabled />
      </el-form-item>
      <el-form-item label="房屋编号" prop="roomNum">
        <el-input v-model="form.roomNum" placeholder="必填，请填写房屋编号" maxlength="12" />
      </el-form-item>
      <el-form-item label="房屋楼层" prop="layer">
        <el-input v-model="form.layer" placeholder="必填，请填写房屋楼层" />
      </el-form-item>
      <el-form-item label="房屋单元" prop="unitId">
        <el-select v-model="form.unitId" placeholder="请选择单元" style="width: 100%">
          <el-option v-for="unit in units" :key="unit.unitId" :label="unit.unitNum + '单元'" :value="unit.unitId" />
        </el-select>
      </el-form-item>
      <el-form-item label="房屋户型" prop="apartment1">
        <el-col :span="12">
          <el-select v-model="form.apartment1" placeholder="请选择" style="width: 100%">
            <el-option label="一室" value="10" />
            <el-option label="两室" value="20" />
            <el-option label="三室" value="30" />
            <el-option label="四室" value="40" />
            <el-option label="五室" value="50" />
            <el-option label="六室" value="60" />
            <el-option label="七室" value="70" />
            <el-option label="八室" value="80" />
          </el-select>
        </el-col>
        <el-col :span="12">
          <el-select v-model="form.apartment2" placeholder="请选择" style="width: 100%">
            <el-option label="一厅" value="101" />
            <el-option label="两厅" value="102" />
            <el-option label="三厅" value="103" />
            <el-option label="四厅" value="104" />
            <el-option label="五厅" value="105" />
            <el-option label="六厅" value="106" />
            <el-option label="七厅" value="107" />
            <el-option label="八厅" value="108" />
          </el-select>
        </el-col>
      </el-form-item>
      <el-form-item label="房屋类型" prop="roomSubType">
        <el-select v-model="form.roomSubType" placeholder="请选择房屋类型" style="width: 100%">
          <el-option v-for="item in roomSubTypes" :key="item.statusCd" :label="item.name" :value="item.statusCd" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="form.roomSubType !== '110'" label="租金">
        <el-input v-model="form.roomRent" placeholder="请填写租金" />
      </el-form-item>
      <el-form-item label="建筑面积" prop="builtUpArea">
        <el-input v-model="form.builtUpArea" type="number" placeholder="必填，请填写房屋建筑面积" />
      </el-form-item>
      <el-form-item label="室内面积(平方)" prop="roomArea">
        <el-input v-model="form.roomArea" placeholder="必填，请填写室内面积" />
      </el-form-item>
      <el-form-item label="算费系数" prop="feeCoefficient">
        <el-input v-model="form.feeCoefficient" placeholder="请填写算费系数" />
      </el-form-item>
      <el-form-item v-if="form.state !== '2002'" label="房屋状态" prop="state">
        <el-select v-model="form.state" placeholder="请选择房屋状态" style="width: 100%">
          <el-option label="已入住" value="2001" />
          <el-option label="已交房" value="2003" />
          <el-option label="已装修" value="2005" />
          <el-option label="未入住" value="2004" />
          <el-option label="空闲" value="2008" />
          <el-option label="装修中" value="2009" />
        </el-select>
      </el-form-item>
      <el-form-item v-for="attr in form.attrs" :key="attr.specCd" :label="attr.specName">
        <el-input v-if="attr.specType === '2233'" v-model="attr.value" :placeholder="attr.specHoldplace" />
        <el-select v-else-if="attr.specType === '3344'" v-model="attr.value" :placeholder="attr.specHoldplace" style="width: 100%">
          <el-option v-for="v in attr.values" :key="v.value" :label="v.valueName" :value="v.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" placeholder="请填写备注信息" maxlength="200" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">保存</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateRoom } from '@/api/property/roomApi'
import { queryUnits } from '@/api/property/unitApi'
import { listDicts } from '@/api/system/systemApi'
import { listAttrSpecs, listAttrValues } from '@/api/property/propertyApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditRoom',
  props: { visible: { type: Boolean, default: false }, editData: { type: Object, default: () => ({}) } },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      units: [],
      roomSubTypes: [],
      form: {
        roomId: '', unitId: '', roomNum: '', layer: '', section: '0',
        apartment: '', apartment1: '', apartment2: '', builtUpArea: '',
        feeCoefficient: '1.00', state: '', remark: '', roomSubType: '110',
        roomArea: '', roomRent: '0', communityId: '', attrs: [], floorId: ''
      },
      rules: {
        roomNum: [{ required: true, message: '房屋编号不能为空', trigger: 'blur' }],
        layer: [{ required: true, message: '房屋楼层不能为空', trigger: 'blur' }],
        unitId: [{ required: true, message: '单元不能为空', trigger: 'change' }],
        builtUpArea: [{ required: true, message: '建筑面积不能为空', trigger: 'blur' }],
        roomArea: [{ required: true, message: '室内面积不能为空', trigger: 'blur' }],
        feeCoefficient: [{ required: true, message: '算费系数不能为空', trigger: 'blur' }],
        roomSubType: [{ required: true, message: '房屋类型不能为空', trigger: 'change' }]
      }
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) },
    editData: {
      immediate: true,
      handler(val) {
        if (val && val.roomId) {
          this.initForm(val)
        }
      }
    }
  },
  methods: {
    async initForm(data) {
      const communityInfo = getCurrentCommunity() || {}
      this.form = {
        roomId: data.roomId || '', unitId: data.unitId || '', roomNum: data.roomNum || '',
        layer: data.layer || '', section: data.section || '0',
        apartment: data.apartment || '', apartment1: '', apartment2: '',
        builtUpArea: data.builtUpArea || '', feeCoefficient: data.feeCoefficient || '1.00',
        state: data.state || '', remark: data.remark || '', roomSubType: data.roomSubType || '110',
        roomArea: data.roomArea || '', roomRent: data.roomRent || '0',
        communityId: communityInfo.communityId, attrs: [], floorId: data.floorId || ''
      }
      if (data.apartment) {
        this.form.apartment1 = data.apartment.substr(0, 2)
        this.form.apartment2 = data.apartment.substr(2)
      }
      try {
        const [dictRes, specRes] = await Promise.all([
          listDicts({ communityId: communityInfo.communityId, tableName: 'building_room', tableColumns: 'room_sub_type' }),
        ])
        this.roomSubTypes = dictRes || []
        const specs = Array.isArray(specRes) ? specRes : (specRes.data || [])
        this.form.attrs = specs.filter(s => s.specShow === 'Y').map(s => ({ ...s, value: '', values: [], attrId: '' }))
        for (const attr of this.form.attrs) {
          try {
            const valRes = await listAttrValues({ specCd: attr.specCd })
            attr.values = Array.isArray(valRes) ? valRes.filter(v => v.valueShow === 'Y') : ((valRes.data || []).filter(v => v.valueShow === 'Y'))
          } catch (e) { attr.values = [] }
        }
        if (data.roomAttrDto && data.roomAttrDto.length) {
          data.roomAttrDto.forEach(ra => {
            const match = this.form.attrs.find(a => a.specCd === ra.specCd)
            if (match) { match.attrId = ra.attrId; match.value = ra.value }
          })
        }
      } catch (e) { /* ignore */ }
      if (data.floorId) this.loadUnits(data.floorId)
    },
    async loadUnits(floorId) {
      try {
        const communityInfo = getCurrentCommunity() || {}
        const res = await queryUnits({ communityId: communityInfo.communityId, floorId, page: 1, row: 100 })
        this.units = Array.isArray(res) ? res : (res.data || [])
      } catch (e) { this.units = [] }
    },
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },
    async handleSubmit() {
      try { await this.$refs.formRef.validate() } catch { return }
      this.loading = true
      try {
        this.form.apartment = (this.form.apartment1 || '') + (this.form.apartment2 || '')
        await updateRoom(this.form)
        this.$message.success('修改成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) { this.$message.error(e.message || '修改失败') }
      finally { this.loading = false }
    }
  }
}
</script>
