<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改月卡"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="名称" prop="cardName">
        <el-input v-model="form.cardName" placeholder="必填，请填写名称" maxlength="200" />
      </el-form-item>
      <el-form-item label="停车场" prop="paId">
        <el-select v-model="form.paId" placeholder="必填，请选择停车场" style="width: 100%">
          <el-option v-for="item in parkingAreas" :key="item.paId" :label="item.num" :value="item.paId" />
        </el-select>
      </el-form-item>
      <el-form-item label="月数" prop="cardMonth">
        <el-input v-model="form.cardMonth" type="number" placeholder="必填，请填写月数" maxlength="12" />
      </el-form-item>
      <el-form-item label="月价" prop="cardPrice">
        <el-input v-model="form.cardPrice" type="number" placeholder="必填，请填写月价" maxlength="12" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="选填，请填写备注" maxlength="300" rows="3" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateCarMonthCard, listParkingAreas } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditCarMonthCard',
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
        cardId: '',
        cardName: '',
        paId: '',
        cardMonth: '',
        cardPrice: '',
        remark: ''
      },
      rules: {
        cardName: [
          { required: true, message: '名称不能为空', trigger: 'blur' },
          { max: 200, message: '名称不能超过200', trigger: 'blur' }
        ],
        paId: [
          { required: true, message: '停车场不能为空', trigger: 'change' }
        ],
        cardMonth: [
          { required: true, message: '月数不能为空', trigger: 'blur' }
        ],
        cardPrice: [
          { required: true, message: '月价不能为空', trigger: 'blur' }
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
      if (val) {
        this.loadParkingAreas()
        if (this.editData) {
          this.$nextTick(() => {
            this.form = { ...this.editData }
          })
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
        await updateCarMonthCard(this.form)
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
