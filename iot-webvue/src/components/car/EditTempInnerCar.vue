<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改临时内部车"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="车牌号" prop="carNum">
        <el-input v-model="form.carNum" placeholder="必填，请填写车牌号" maxlength="10" />
      </el-form-item>
      <el-form-item label="开始时间" prop="startTime">
        <el-date-picker
          v-model="form.startTime"
          type="datetime"
          placeholder="必填，请选择开始时间"
          value-format="yyyy-MM-dd HH:mm:ss"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="结束时间" prop="endTime">
        <el-date-picker
          v-model="form.endTime"
          type="datetime"
          placeholder="必填，请选择结束时间"
          value-format="yyyy-MM-dd HH:mm:ss"
          style="width: 100%"
        />
      </el-form-item>
      <el-form-item label="说明" prop="remark">
        <el-input v-model="form.remark" placeholder="请填写说明" maxlength="255" type="textarea" :rows="3" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateTempInnerCar } from '@/api/car/carApi'

export default {
  name: 'EditTempInnerCar',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    const validateCarNum = (rule, value, callback) => {
      if (!value) {
        callback(new Error('车牌号不能为空'))
      } else if (!/^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤川青藏琼宁][A-HJ-NP-Z][A-HJ-NP-Z0-9]{4,5}[A-HJ-NP-Z0-9挂学警港澳]$/.test(value)) {
        callback(new Error('车牌号格式不正确'))
      } else {
        callback()
      }
    }
    const validateEndTime = (rule, value, callback) => {
      if (!value) {
        callback(new Error('结束时间不能为空'))
      } else if (this.form.startTime && new Date(value) <= new Date(this.form.startTime)) {
        callback(new Error('结束时间必须大于开始时间'))
      } else {
        callback()
      }
    }
    return {
      dialogVisible: false,
      loading: false,
      form: {
        ticId: '',
        carNum: '',
        startTime: '',
        endTime: '',
        remark: '',
        configId: '',
        feeName: ''
      },
      rules: {
        carNum: [
          { required: true, validator: validateCarNum, trigger: 'blur' }
        ],
        startTime: [
          { required: true, message: '开始时间不能为空', trigger: 'change' }
        ],
        endTime: [
          { required: true, validator: validateEndTime, trigger: 'change' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.form = {
          ticId: this.editData.ticId || '',
          carNum: this.editData.carNum || '',
          startTime: this.editData.startTime || '',
          endTime: this.editData.endTime || '',
          remark: this.editData.remark || '',
          configId: this.editData.configId || '',
          feeName: this.editData.feeName || ''
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
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        this.form.communityId = this.$store.state.app.currentCommunity.communityId || ''
        await updateTempInnerCar(this.form)
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
