<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加岗亭"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="岗亭名称" prop="boxName">
        <el-input v-model="form.boxName" placeholder="必填，请填写岗亭名称" maxlength="64" />
      </el-form-item>
      <el-form-item label="停车场" prop="paId">
        <el-select v-model="form.paId" placeholder="必填，请选择停车场" style="width: 100%">
          <el-option v-for="item in parkingAreas" :key="item.paId" :label="item.num" :value="item.paId" />
        </el-select>
      </el-form-item>
      <el-form-item label="临时车进场" prop="tempCarIn">
        <el-select v-model="form.tempCarIn" placeholder="必填，请选择" style="width: 100%">
          <el-option label="是" value="Y" />
          <el-option label="否" value="N" />
          <el-option label="需访客登记" value="V" />
        </el-select>
      </el-form-item>
      <el-form-item label="临时车审核" prop="tempAuth">
        <el-select v-model="form.tempAuth" placeholder="必填，请选择" style="width: 100%">
          <el-option label="是" value="Y" />
          <el-option label="否" value="N" />
        </el-select>
      </el-form-item>
      <el-form-item label="是否收费" prop="fee">
        <el-select v-model="form.fee" placeholder="必填，请选择" style="width: 100%">
          <el-option label="是" value="Y" />
          <el-option label="否" value="N" />
        </el-select>
      </el-form-item>
      <el-form-item label="已在场进场" prop="blueCarIn">
        <el-select v-model="form.blueCarIn" placeholder="必填，请选择" style="width: 100%">
          <el-option label="是" value="Y" />
          <el-option label="否" value="N" />
        </el-select>
      </el-form-item>
      <el-form-item label="未在场出场" prop="yelowCarIn">
        <el-select v-model="form.yelowCarIn" placeholder="必填，请选择" style="width: 100%">
          <el-option label="是" value="Y" />
          <el-option label="否" value="N" />
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
import { saveParkingBox, listParkingAreas } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddParkingBox',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      parkingAreas: [],
      form: {
        boxName: '',
        paId: '',
        tempCarIn: '',
        tempAuth: '',
        fee: '',
        blueCarIn: '',
        yelowCarIn: '',
        remark: ''
      },
      rules: {
        boxName: [
          { required: true, message: '岗亭名称不能为空', trigger: 'blur' },
          { max: 64, message: '岗亭名称不能超过64', trigger: 'blur' }
        ],
        paId: [
          { required: true, message: '停车场不能为空', trigger: 'change' }
        ],
        tempCarIn: [
          { required: true, message: '临时车进场不能为空', trigger: 'change' }
        ],
        tempAuth: [
          { required: true, message: '临时车审核不能为空', trigger: 'change' }
        ],
        fee: [
          { required: true, message: '是否收费不能为空', trigger: 'change' }
        ],
        blueCarIn: [
          { required: true, message: '已在场进场不能为空', trigger: 'change' }
        ],
        yelowCarIn: [
          { required: true, message: '未在场出场不能为空', trigger: 'change' }
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
        await saveParkingBox(this.form)
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
