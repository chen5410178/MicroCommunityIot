<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加共享岗亭"
    width="500px"
    append-to-body
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="停车场" prop="paId">
        <el-select v-model="form.paId" placeholder="必填，请选择停车场" style="width: 100%">
          <el-option v-for="item in parkingAreas" :key="item.paId" :label="item.num" :value="item.paId" />
        </el-select>
      </el-form-item>
      <el-form-item label="默认停车场" prop="defaultArea">
        <el-select v-model="form.defaultArea" placeholder="必填，请选择" style="width: 100%">
          <el-option label="是" value="T" />
          <el-option label="否" value="F" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" placeholder="选填，请填写备注" maxlength="300" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { saveParkingBoxArea, listParkingAreas } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddParkingBoxArea',
  props: {
    visible: { type: Boolean, default: false },
    boxId: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      parkingAreas: [],
      form: {
        paId: '',
        defaultArea: '',
        remark: ''
      },
      rules: {
        paId: [
          { required: true, message: '停车场不能为空', trigger: 'change' }
        ],
        defaultArea: [
          { required: true, message: '默认停车场不能为空', trigger: 'change' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) { this.loadParkingAreas() }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },
    async loadParkingAreas() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listParkingAreas({ page: 1, row: 100, communityId })
        this.parkingAreas = res.data || []
      } catch (e) { /* ignore */ }
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        this.form.communityId = (getCurrentCommunity() || {}).communityId || ''
        this.form.boxId = this.boxId
        await saveParkingBoxArea(this.form)
        this.$message.success('添加成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e || '添加失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
