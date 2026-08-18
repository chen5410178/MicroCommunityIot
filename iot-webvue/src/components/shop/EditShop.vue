<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改商铺"
    width="560px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="商铺编号" prop="shopNum">
        <el-input v-model="form.shopNum" placeholder="请填写商铺编号" maxlength="50" />
      </el-form-item>
      <el-form-item label="所在位置" prop="locationId">
        <el-cascader
          v-model="locationPath"
          :options="locationOptions"
          :props="locationProps"
          placeholder="请选择位置"
          clearable
          style="width: 100%"
          @change="handleLocationChange"
        />
      </el-form-item>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="建筑面积" prop="builtUpArea">
            <el-input v-model="form.builtUpArea" placeholder="㎡" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="使用面积">
            <el-input v-model="form.shopArea" placeholder="㎡" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="租金">
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
import { listShopLocations, updateBuildingShop } from '@/api/shop/shopApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditShop',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: () => null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      locationOptions: [],
      locationPath: [],
      locationProps: {
        value: 'locationId',
        label: 'locationName',
        children: 'children',
        checkStrictly: true,
        emitPath: false
      },
      form: {
        shopId: '', shopNum: '', locationId: '', locationName: '',
        builtUpArea: '', shopArea: '', shopRent: '', remark: ''
      },
      rules: {
        shopNum: [{ required: true, message: '商铺编号不能为空', trigger: 'blur' }],
        locationId: [{ required: true, message: '请选择所在位置', trigger: 'change' }],
        builtUpArea: [{ required: true, message: '建筑面积不能为空', trigger: 'blur' }]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.loadLocations()
        if (this.editData) {
          this.form = {
            shopId: this.editData.shopId,
            shopNum: this.editData.shopNum,
            locationId: this.editData.locationId,
            locationName: this.editData.locationName,
            builtUpArea: this.editData.builtUpArea,
            shopArea: this.editData.shopArea,
            shopRent: this.editData.shopRent,
            remark: this.editData.remark || ''
          }
        }
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadLocations() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listShopLocations({ communityId })
        this.locationOptions = res.data || []
        this.locationPath = this.form.locationId ? [this.form.locationId] : []
      } catch (e) { /* 加载失败 */ }
    },
    handleLocationChange(val) {
      const node = this.findLocationNode(this.locationOptions, val)
      this.form.locationId = val || ''
      this.form.locationName = node ? node.locationName : ''
    },
    findLocationNode(list, id) {
      if (!list) return null
      for (const item of list) {
        if (item.locationId === id) return item
        const found = this.findLocationNode(item.children, id)
        if (found) return found
      }
      return null
    },
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
    },
    async handleSubmit() {
      try { await this.$refs.formRef.validate() } catch { return }
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await updateBuildingShop({ ...this.form, communityId })
        this.$message.success('修改成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) { this.$message.error(e.message || '修改失败') }
      finally { this.loading = false }
    }
  }
}
</script>
