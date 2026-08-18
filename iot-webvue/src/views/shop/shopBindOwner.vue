<template>
  <div class="shop-bind-owner">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>商铺移交</span>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="110px" style="max-width: 560px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="商铺编号">
              <el-input :value="form.shopNum" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所在位置">
              <el-input :value="form.locationName || '-'" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="建筑面积">
              <el-input :value="form.builtUpArea ? form.builtUpArea + '㎡' : '-'" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="使用面积">
              <el-input :value="form.shopArea ? form.shopArea + '㎡' : '-'" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="业主" prop="ownerName">
          <el-input v-model="form.ownerName" placeholder="请选择业主" readonly>
            <el-button slot="append" icon="el-icon-search" @click="searchOwnerVisible = true">选择业主</el-button>
          </el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="link">
          <el-input v-model="form.link" placeholder="业主联系电话" maxlength="20" />
        </el-form-item>
        <el-form-item label="租赁类型" prop="state">
          <el-radio-group v-model="form.state">
            <el-radio label="2006">出租</el-radio>
            <el-radio label="2007">出售</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="form.startTime"
                type="date"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="开始时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="form.endTime"
                type="date"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="结束时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="请填写备注" maxlength="200" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">确认移交</el-button>
          <el-button @click="$router.back()">返回</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <SearchOwner :visible.sync="searchOwnerVisible" @selected="handleOwnerSelected" />
  </div>
</template>

<script>
import { saveBuildingShopOwner } from '@/api/shop/shopApi'
import { getCurrentCommunity } from '@/utils/auth'
import SearchOwner from '@/components/monitor/SearchOwner.vue'

export default {
  name: 'ShopBindOwner',
  components: { SearchOwner },
  data() {
    return {
      loading: false,
      searchOwnerVisible: false,
      form: {
        shopId: '', shopNum: '', locationName: '',
        builtUpArea: '', shopArea: '',
        ownerId: '', ownerName: '', link: '',
        state: '2006', startTime: '', endTime: '', remark: ''
      },
      rules: {
        ownerName: [{ required: true, message: '请选择业主', trigger: 'blur' }],
        link: [{ required: true, message: '联系电话不能为空', trigger: 'blur' }],
        state: [{ required: true, message: '请选择租赁类型', trigger: 'change' }],
        startTime: [{ required: true, message: '开始时间不能为空', trigger: 'change' }],
        endTime: [{ required: true, message: '结束时间不能为空', trigger: 'change' }]
      }
    }
  },
  mounted() {
    this.form.shopId = this.$route.query.shopId || ''
    this.form.shopNum = this.$route.query.shopNum || ''
    this.form.locationName = this.$route.query.locationName || ''
    this.form.builtUpArea = this.$route.query.builtUpArea || ''
    this.form.shopArea = this.$route.query.shopArea || ''
    const now = new Date()
    this.form.startTime = this.formatDate(now)
    this.form.endTime = '2050-01-01 00:00:00'
  },
  watch: {
    'form.state'(val) {
      if (val === '2007') {
        this.form.endTime = '2050-01-01 00:00:00'
      }
    }
  },
  methods: {
    formatDate(date) {
      const y = date.getFullYear()
      const m = (date.getMonth() + 1).toString().padStart(2, '0')
      const d = date.getDate().toString().padStart(2, '0')
      return y + '-' + m + '-' + d + ' 00:00:00'
    },
    handleOwnerSelected(owner) {
      this.form.ownerId = owner.memberId || owner.ownerId || ''
      this.form.ownerName = owner.name || ''
      this.form.link = owner.link || ''
    },
    async handleSubmit() {
      try { await this.$refs.formRef.validate() } catch { return }
      if (!this.form.ownerId) {
        this.$message.warning('请选择业主')
        return
      }
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        await saveBuildingShopOwner({ ...this.form, communityId })
        this.$message.success('移交成功')
        this.$router.back()
      } catch (e) { this.$message.error(e.message || '移交失败') }
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
