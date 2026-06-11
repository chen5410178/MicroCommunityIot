<template>
  <el-dialog :visible.sync="dialogVisible" title="业主车辆" width="700px" @close="handleClose">
    <el-table :data="carList" v-loading="loading" border stripe>
      <el-table-column prop="carNum" label="车牌号" width="120" />
      <el-table-column prop="carBrand" label="品牌" width="100" />
      <el-table-column prop="carType" label="类型" width="80" />
      <el-table-column prop="carColor" label="颜色" width="80" />
      <el-table-column prop="createTime" label="登记时间" width="160" />
    </el-table>
    <span slot="footer">
      <el-button @click="handleClose">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { listOwnerCars } from '@/api/property/propertyApi'

export default {
  name: 'OwnerCars',
  props: { visible: { type: Boolean, default: false }, ownerData: { type: Object, default: () => ({}) } },
  data() {
    return { dialogVisible: false, loading: false, carList: [] }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) this.loadCars()
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    handleClose() { this.dialogVisible = false },
    async loadCars() {
      if (!this.ownerData.ownerId) { this.carList = []; return }
      this.loading = true
      try {
        const communityInfo = this.$store.state.app.currentCommunity || {}
        const res = await listOwnerCars({ communityId: communityInfo.communityId, ownerId: this.ownerData.ownerId, page: 1, row: 50 })
        this.carList = res.data || []
      } catch (e) { this.carList = [] }
      finally { this.loading = false }
    }
  }
}
</script>
