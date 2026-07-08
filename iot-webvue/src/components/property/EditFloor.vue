<template>
  <el-dialog :visible.sync="dialogVisible" title="修改楼栋" width="500px" @close="handleClose">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="楼栋名称" prop="floorName">
        <el-input v-model="form.floorName" placeholder="请填写楼栋名称" />
      </el-form-item>
      <el-form-item label="楼栋编号" prop="floorNum">
        <el-input v-model="form.floorNum" placeholder="请填写楼栋编号" />
      </el-form-item>
      <el-form-item label="建筑面积" prop="floorArea">
        <el-input v-model="form.floorArea" placeholder="请填写建筑面积" />
      </el-form-item>
      <el-form-item label="排序" prop="seq">
        <el-input v-model="form.seq" placeholder="请填写排序" />
      </el-form-item>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="纬度">
            <el-input v-model="form.lat" placeholder="纬度" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="经度">
            <el-input v-model="form.lng" placeholder="经度" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" placeholder="请填写备注" maxlength="200" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { editFloor } from '@/api/property/floorApi'

export default {
  name: 'EditFloor',
  props: { visible: { type: Boolean, default: false }, editData: { type: Object, default: () => ({}) } },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: { floorId: '', floorName: '', floorNum: '', floorArea: '', seq: '', lat: '', lng: '', remark: '' },
      rules: {
        floorName: [{ required: true, message: '楼栋名称不能为空', trigger: 'blur' }],
        floorNum: [{ required: true, message: '楼栋编号不能为空', trigger: 'blur' }],
        floorArea: [{ required: true, message: '建筑面积不能为空', trigger: 'blur' }],
        seq: [{ required: true, message: '排序不能为空', trigger: 'blur' }]
      }
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) },
    editData: {
      immediate: true,
      handler(val) {
        if (val && val.floorId) {
          this.form = {
            floorId: val.floorId, floorName: val.floorName || val.floorNum || '',
            floorNum: val.floorNum || '', floorArea: val.floorArea || '',
            seq: val.seq || '', lat: val.lat || '', lng: val.lng || '',
            remark: val.remark || ''
          }
        }
      }
    }
  },
  methods: {
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },
    async handleSubmit() {
      try { await this.$refs.formRef.validate() } catch { return }
      this.loading = true
      try {
        const submitData = { ...this.form, name: this.form.floorNum + '号楼' }
        await editFloor(submitData)
        this.$message.success('修改成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) { this.$message.error(e.message || '修改失败') }
      finally { this.loading = false }
    }
  }
}
</script>
