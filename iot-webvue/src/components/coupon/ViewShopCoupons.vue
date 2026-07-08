<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="商店停车券"
    width="800px"
  >
    <div class="table-responsive">
      <el-table :data="coupons" border stripe>
        <el-table-column prop="couponName" label="停车券" min-width="120" align="center" />
        <el-table-column prop="paName" label="停车场" min-width="120" align="center" />
        <el-table-column prop="typeCdName" label="停车券类型" min-width="100" align="center" />
        <el-table-column label="面值" min-width="120" align="center">
          <template slot-scope="{ row }">
            <span v-if="row.typeCd === '1001'">{{ row.value }}分钟</span>
            <span v-else-if="row.typeCd === '2002'">{{ row.value }}元</span>
            <span v-else-if="row.typeCd === '3003'">{{ row.value }}折</span>
            <span v-else-if="row.typeCd === '4004'">全免</span>
            <span v-else>{{ row.value }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" min-width="80" align="center">
          <template slot-scope="{ row }">{{ row.quantity }}张</template>
        </el-table-column>
      </el-table>
    </div>
  </el-dialog>
</template>

<script>
import { listShopParkingCoupons } from '@/api/coupon/couponApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ViewShopCoupons',
  props: {
    visible: { type: Boolean, default: false },
    shopData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      coupons: []
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.shopData) {
        this.loadCoupons()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadCoupons() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listShopParkingCoupons({
          page: 1,
          row: 100,
          communityId,
          shopId: this.shopData.shopId
        })
        this.coupons = res.data || []
      } catch { /* ignore */ }
    }
  }
}
</script>
