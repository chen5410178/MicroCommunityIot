<template>
  <div class="shop-unbind-owner">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>商铺退租</span>
      </div>
      <el-form :model="form" ref="formRef" label-width="110px" style="max-width: 560px">
        <el-form-item label="商铺编号">
          <el-input :value="form.shopNum" disabled />
        </el-form-item>
        <el-form-item label="当前业主">
          <el-input :value="form.ownerName || '-'" disabled />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input :value="form.link || '-'" disabled />
        </el-form-item>
        <el-form-item label="租赁起止时间">
          <el-input :value="rentTimeText" disabled />
        </el-form-item>
        <el-form-item label="退租备注">
          <el-input v-model="form.remark" type="textarea" placeholder="请填写退租原因" maxlength="200" />
        </el-form-item>
        <el-form-item>
          <el-button type="danger" @click="handleSubmit" :loading="loading">确认退租</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { deleteBuildingShopOwner } from '@/api/shop/shopApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ShopUnbindOwner',
  data() {
    return {
      loading: false,
      form: {
        shopId: '', shopNum: '',
        ownerId: '', ownerName: '', link: '',
        startTime: '', endTime: '', remark: ''
      }
    }
  },
  computed: {
    rentTimeText() {
      if (!this.form.startTime && !this.form.endTime) return '-'
      return (this.form.startTime || '-') + ' ~ ' + (this.form.endTime || '-')
    }
  },
  mounted() {
    this.form.shopId = this.$route.query.shopId || ''
    this.form.shopNum = this.$route.query.shopNum || ''
    this.form.ownerId = this.$route.query.ownerId || ''
    this.form.ownerName = this.$route.query.ownerName || ''
    this.form.link = this.$route.query.link || ''
    this.form.startTime = this.$route.query.startTime || ''
    this.form.endTime = this.$route.query.endTime || ''
    if (!this.form.ownerId && !this.form.ownerName) {
      this.$message.warning('该商铺暂无绑定业主')
    }
  },
  methods: {
    async handleSubmit() {
      if (!this.form.shopId) {
        this.$message.warning('参数缺失')
        return
      }
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const submitData = {
          shopId: this.form.shopId,
          communityId
        }
        if (this.form.ownerId) {
          submitData.ownerId = this.form.ownerId
        }
        await deleteBuildingShopOwner(submitData)
        this.$message.success('退租成功')
        this.$router.back()
      } catch (e) { this.$message.error(e.message || '退租失败') }
      finally { this.loading = false }
    }
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
