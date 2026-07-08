<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="删除确认"
    width="400px"
    @close="handleClose"
  >
    <div style="text-align: center; padding: 20px 0;">
      <i class="el-icon-warning" style="font-size: 48px; color: #f56c6c;" />
      <p style="margin-top: 20px; font-size: 16px;">
        确认是否删除车辆 <strong>{{ form.carNum }}</strong> 吗？
      </p>
      <p style="color: #909399; font-size: 14px; margin-top: 8px;">此操作不可恢复</p>
    </div>
    <span slot="footer">
      <el-button @click="handleClose">点错了</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确认删除</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { deleteOwnerCar } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'DeleteOwnerCar',
  data() {
    return {
      dialogVisible: false,
      loading: false,
      form: {
        carId: '',
        memberId: '',
        carNum: ''
      }
    }
  },
  methods: {
    open(car) {
      this.dialogVisible = true
      this.form = {
        carId: car.carId || '',
        memberId: car.memberId || '',
        carNum: car.carNum || ''
      }
    },
    handleClose() {
      this.dialogVisible = false
    },
    async handleSubmit() {
      this.loading = true
      try {
        this.form.communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await deleteOwnerCar(this.form)
        if (res.code === 0) {
          this.$message.success('删除成功')
          this.$emit('success')
          this.handleClose()
        } else {
          this.$message.error(res.msg || '删除失败')
        }
      } catch {
        this.$message.error('删除失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
