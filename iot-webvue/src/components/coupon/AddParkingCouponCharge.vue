<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="添加停车券充电关联"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="商铺" prop="shopId">
        <el-select v-model="form.shopId" placeholder="必填，请选择商铺" style="width: 100%" @change="handleShopChange">
          <el-option v-for="item in shops" :key="item.shopId" :label="item.shopName" :value="item.shopId" />
        </el-select>
      </el-form-item>
      <el-form-item label="停车券" prop="couponShopId">
        <el-select v-model="form.couponShopId" placeholder="必填，请选择停车券" style="width: 100%">
          <el-option v-for="item in coupons" :key="item.couponShopId" :label="item.couponName" :value="item.couponShopId" />
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
import { saveParkingCouponCharge, listCommunityShops, listShopParkingCoupons } from '@/api/coupon/couponApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddParkingCouponCharge',
  props: {
    visible: { type: Boolean, default: false },
    machineId: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      shops: [],
      coupons: [],
      form: {
        shopId: '',
        couponShopId: ''
      },
      rules: {
        shopId: [
          { required: true, message: '商铺不能为空', trigger: 'change' }
        ],
        couponShopId: [
          { required: true, message: '停车券不能为空', trigger: 'change' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.loadShops()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadShops() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listCommunityShops({ page: 1, row: 100, communityId })
        this.shops = res.data || []
      } catch { /* ignore */ }
    },
    async handleShopChange(shopId) {
      if (!shopId) {
        this.coupons = []
        this.form.couponShopId = ''
        return
      }
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listShopParkingCoupons({ page: 1, row: 100, communityId, shopId })
        this.coupons = res.data || []
        this.form.couponShopId = ''
      } catch { /* ignore */ }
    },
    handleClose() {
      this.dialogVisible = false
      this.$refs.formRef && this.$refs.formRef.resetFields()
      this.coupons = []
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await saveParkingCouponCharge({
          ...this.form,
          machineId: this.machineId,
          communityId
        })
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
