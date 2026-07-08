<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改停车券"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="form.name" placeholder="必填，请填写名称" maxlength="64" />
      </el-form-item>
      <el-form-item label="类型" prop="typeCd">
        <el-select v-model="form.typeCd" placeholder="请选择类型" style="width: 100%">
          <el-option label="时长减免" value="1001" />
          <el-option label="金额减免" value="2002" />
          <el-option label="折扣减免" value="3003" />
          <el-option label="全免" value="4004" />
        </el-select>
      </el-form-item>
      <el-form-item label="停车场" prop="paId">
        <el-select v-model="form.paId" placeholder="请选择停车场" style="width: 100%">
          <el-option v-for="item in parkingAreas" :key="item.paId" :label="item.num" :value="item.paId" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="form.typeCd === '1001'" label="时长（分钟）" prop="value">
        <el-input v-model="form.value" placeholder="必填，请填写时长（分钟）" />
      </el-form-item>
      <el-form-item v-else-if="form.typeCd === '2002'" label="金额（元）" prop="value">
        <el-input v-model="form.value" placeholder="必填，请填写金额（元）" />
      </el-form-item>
      <el-form-item v-else-if="form.typeCd === '3003'" label="折扣" prop="value">
        <el-input v-model="form.value" placeholder="必填，请填写折扣 应该小于1大于0 比如7折0.7" />
      </el-form-item>
      <el-form-item label="售价" prop="valuePrice">
        <el-input v-model="form.valuePrice" placeholder="必填，请填写售价" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateParkingCoupon, listParkingAreas } from '@/api/coupon/couponApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditParkingCoupon',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      parkingAreas: [],
      form: {
        couponId: '',
        name: '',
        typeCd: '',
        paId: '',
        value: '',
        valuePrice: ''
      },
      rules: {
        name: [
          { required: true, message: '名称不能为空', trigger: 'blur' },
          { max: 64, message: '名称不能超过64', trigger: 'blur' }
        ],
        typeCd: [
          { required: true, message: '类型不能为空', trigger: 'change' }
        ],
        paId: [
          { required: true, message: '停车场不能为空', trigger: 'change' }
        ],
        value: [
          { required: true, message: '面值不能为空', trigger: 'blur' }
        ],
        valuePrice: [
          { required: true, message: '售价不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.loadParkingAreas()
        if (this.editData) {
          this.form = {
            couponId: this.editData.couponId || '',
            name: this.editData.name || '',
            typeCd: this.editData.typeCd || '',
            paId: this.editData.paId || '',
            value: this.editData.value || '',
            valuePrice: this.editData.valuePrice || ''
          }
        }
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadParkingAreas() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listParkingAreas({ page: 1, row: 100, communityId })
        this.parkingAreas = res.data || []
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
      if (this.form.typeCd === '4004') {
        this.form.value = String(24 * 60)
      }
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await updateParkingCoupon({ ...this.form, communityId })
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
