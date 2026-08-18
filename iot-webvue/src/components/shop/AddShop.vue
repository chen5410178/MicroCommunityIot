<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="新增商铺"
    width="560px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="商铺编号" prop="shopNum">
        <el-input v-model="form.shopNum" placeholder="请填写商铺编号" maxlength="50" />
      </el-form-item>
      <el-form-item label="所在位置" prop="locationId">
        <el-input :value="form.locationName || '请先在左侧选择位置'" disabled />
      </el-form-item>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="建筑面积" prop="builtUpArea">
            <el-input v-model="form.builtUpArea" placeholder="㎡" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="使用面积" prop="shopArea">
            <el-input v-model="form.shopArea" placeholder="㎡" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="租金" prop="shopRent">
        <el-input v-model="form.shopRent" placeholder="请填写租金" />
      </el-form-item>
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
import { saveBuildingShop } from '@/api/shop/shopApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddShop',
  props: {
    visible: { type: Boolean, default: false },
    locationId: { type: String, default: '' },
    locationName: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: { shopNum: '', locationName: '', builtUpArea: '', shopArea: '', shopRent: '', remark: '' },
      rules: {
        shopNum: [{ required: true, message: '商铺编号不能为空', trigger: 'blur' }],
        builtUpArea: [{ required: true, message: '建筑面积不能为空', trigger: 'blur' }],
        shopArea: [{ required: true, message: '使用面积不能为空', trigger: 'blur' }],
        shopRent: [{ required: true, message: '租金不能为空', trigger: 'blur' }]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.form.locationName = this.locationName
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
      this.form = { shopNum: '', locationName: '', builtUpArea: '', shopArea: '', shopRent: '', remark: '' }
    },
    async handleSubmit() {
      if (!this.locationId) {
        this.$message.warning('请先在左侧位置树中选择商铺位置')
        return
      }
      try { await this.$refs.formRef.validate() } catch { return }
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const submitData = {
          ...this.form,
          locationId: this.locationId,
          locationName: this.locationName,
          communityId
        }
        await saveBuildingShop(submitData)
        this.$message.success('添加成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) { this.$message.error(e.message || '添加失败') }
      finally { this.loading = false }
    }
  }
}
</script>
