<template>
  <div class="visit-detail">
    <el-card shadow="never" class="info-card">
      <div slot="header" class="card-header">
        <span>拜访信息</span>
        <el-button size="small" @click="goBack">返回</el-button>
      </div>
      <div v-loading="loading">
        <el-row :gutter="16">
          <el-col :span="6">
            <div class="info-item">
              <span class="info-label">拜访ID：</span>
              <span class="info-value">{{ info.visitId }}</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="info-item">
              <span class="info-label">姓名：</span>
              <span class="info-value">{{ info.name }}</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="info-item">
              <span class="info-label">性别：</span>
              <span class="info-value">{{ info.visitGender == 0 ? '男' : '女' }}</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="info-item">
              <span class="info-label">电话号码：</span>
              <span class="info-value">{{ info.phoneNumber }}</span>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="16" class="info-row">
          <el-col :span="6">
            <div class="info-item">
              <span class="info-label">拜访房屋：</span>
              <span class="info-value">{{ info.roomName }}</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="info-item">
              <span class="info-label">拜访业主：</span>
              <span class="info-value">{{ info.ownerName || '-' }}</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="info-item">
              <span class="info-label">访客类型：</span>
              <span class="info-value">{{ info.visitTypeName }}</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="info-item">
              <span class="info-label">拜访事由：</span>
              <span class="info-value">{{ info.visitCase || '-' }}</span>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="16" class="info-row">
          <el-col :span="6">
            <div class="info-item">
              <span class="info-label">审核状态：</span>
              <span class="info-value">{{ info.stateName }}</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="info-item">
              <span class="info-label">审核备注：</span>
              <span class="info-value">{{ info.msg || '-' }}</span>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="info-item">
              <span class="info-label">添加时间：</span>
              <span class="info-value">{{ info.createTime }}</span>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>
    <el-card shadow="never" class="tab-card">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="进出记录" name="inout" />
      </el-tabs>
      <VisitDetailAccessControlInout v-if="activeTab === 'inout'" ref="inoutRef" :phone-number="phoneNumber" />
    </el-card>
  </div>
</template>

<script>
import { listVisits } from '@/api/accessControl/accessControlApi'
import VisitDetailAccessControlInout from '@/components/accessControl/VisitDetailAccessControlInout.vue'

export default {
  name: 'VisitDetail',
  components: { VisitDetailAccessControlInout },
  data() {
    return {
      loading: false,
      activeTab: 'inout',
      phoneNumber: '',
      info: {
        visitId: '',
        name: '',
        visitGender: '',
        phoneNumber: '',
        roomName: '',
        ownerName: '',
        visitTypeName: '',
        visitCase: '',
        stateName: '',
        msg: '',
        createTime: ''
      }
    }
  },
  mounted() {
    const visitId = this.$route.query.visitId
    this.phoneNumber = this.$route.query.phoneNumber || ''
    if (visitId) {
      this.loadVisitInfo(visitId)
    }
  },
  methods: {
    async loadVisitInfo(visitId) {
      this.loading = true
      try {
        const res = await listVisits({
          visitId: visitId,
          page: 1,
          row: 1,
          communityId: this.$store.state.app.currentCommunity.communityId
        })
        const data = res.data || []
        if (data.length > 0) {
          this.info = data[0]
          if (!this.phoneNumber) {
            this.phoneNumber = data[0].phoneNumber || ''
          }
        }
      } catch (e) {
        console.error('加载访客信息失败', e)
      } finally {
        this.loading = false
      }
    },
    handleTabClick() {},
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.info-card { margin-bottom: 16px; }
.info-row { margin-top: 16px; }
.info-item { display: flex; align-items: center; }
.info-label { color: #909399; flex-shrink: 0; }
.info-value { color: #303133; }
.tab-card { }
</style>
