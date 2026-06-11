<template>
  <div class="system-info-manage">
    <el-card shadow="never" class="detail-card">
      <div slot="header" class="card-header">
        <span>系统配置</span>
        <el-button type="primary" size="small" @click="handleEdit">修改</el-button>
      </div>
      <div v-loading="loading">
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="info-item">
              <span class="info-label">标题名称：</span>
              <span class="info-value">{{ info.systemTitle }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="info-label">副标题：</span>
              <span class="info-value">{{ info.subSystemTitle }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="info-label">简写名称：</span>
              <span class="info-value">{{ info.systemSimpleTitle }}</span>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="info-item">
              <span class="info-label">公司名称：</span>
              <span class="info-value">{{ info.companyName }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="info-label">logo地址：</span>
              <span class="info-value">{{ info.logoUrl }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="info-label">静态url：</span>
              <span class="info-value">{{ info.imgUrl }}</span>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="info-item">
              <span class="info-label">默认小区编号：</span>
              <span class="info-value">{{ info.defaultCommunityId }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="info-label">业主标题：</span>
              <span class="info-value">{{ info.ownerTitle }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="info-label">物业手机标题：</span>
              <span class="info-value">{{ info.propertyTitle }}</span>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="info-item">
              <span class="info-label">qq地图key：</span>
              <span class="info-value">{{ info.qqMapKey }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="info-label">商城地址：</span>
              <span class="info-value">{{ info.mallUrl }}</span>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>
    <EditSystemInfo :visible.sync="editVisible" :edit-data="info" @success="handleSuccess" />
  </div>
</template>

<script>
import { listSystemInfo } from '@/api/login/systemApi'
import EditSystemInfo from '@/components/frame/EditSystemInfo.vue'

export default {
  name: 'SystemInfoManage',
  components: { EditSystemInfo },
  data() {
    return {
      loading: false,
      editVisible: false,
      info: {
        systemId: '',
        systemTitle: '',
        subSystemTitle: '',
        systemSimpleTitle: '',
        companyName: '',
        logoUrl: '',
        imgUrl: '',
        defaultCommunityId: '',
        ownerTitle: '',
        propertyTitle: '',
        qqMapKey: '',
        mallUrl: ''
      }
    }
  },
  mounted() {
    this.fetchData()
  },
  methods: {
    async fetchData() {
      this.loading = true
      try {
        const res = await listSystemInfo({ page: 1, row: 1 })
        if (res.data && res.data.length > 0) {
          this.info = res.data[0]
        }
      } finally {
        this.loading = false
      }
    },
    handleEdit() {
      this.editVisible = true
    },
    handleSuccess() {
      this.fetchData()
    }
  }
}
</script>

<style scoped>
.detail-card { margin-bottom: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.info-item { padding: 12px 0; font-size: 14px; }
.info-label { color: #909399; }
.info-value { color: #303133; margin-left: 4px; }
</style>
