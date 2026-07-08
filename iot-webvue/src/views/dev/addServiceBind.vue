<template>
  <div class="add-service-bind">
    <el-card shadow="never" class="box-card">
      <div slot="header" class="card-title">
        <span>应用信息</span>
      </div>
      <div class="info-row">
        <div class="info-item">
          <span class="label">应用名称：</span>
          <span class="value">{{ appInfo.name || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">应用ID：</span>
          <span class="value">{{ appInfo.appId || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">备注：</span>
          <span class="value">{{ appInfo.remark || '-' }}</span>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="box-card select-service-card">
      <div slot="header" class="card-title select-service-header">
        <span>选择服务</span>
        <el-button type="primary" size="small" @click="showSelectDialog">选择服务</el-button>
      </div>
      <div class="info-row">
        <div class="info-item">
          <span class="label">服务编码：</span>
          <span class="value">{{ selectedService.serviceCode || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">服务名称：</span>
          <span class="value">{{ selectedService.name || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">服务ID：</span>
          <span class="value">{{ selectedService.serviceId || '-' }}</span>
        </div>
        <div class="info-item">
          <span class="label">调用地址：</span>
          <span class="value">{{ selectedService.url || '-' }}</span>
        </div>
      </div>
    </el-card>

    <div class="page-actions">
      <el-button @click="$router.go(-1)">返回</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">提交</el-button>
    </div>

    <SelectService ref="selectServiceRef" @select="handleServiceSelect" />
  </div>
</template>

<script>
import { listApps, bindingService } from '@/api/dev/devApi'
import SelectService from '@/components/dev/SelectService.vue'

export default {
  name: 'AddServiceBind',
  components: { SelectService },
  data() {
    return {
      submitting: false,
      appInfo: {
        appId: '',
        name: '',
        remark: ''
      },
      selectedService: {
        serviceId: '',
        name: '',
        serviceCode: '',
        url: '',
        method: '',
        timeout: '',
        retryCount: '',
        messageQueueName: '',
        isInstance: ''
      }
    }
  },
  created() {
    const { appId } = this.$route.query
    if (appId) {
      this.appInfo.appId = appId
      this.fetchAppInfo()
    }
  },
  methods: {
    async fetchAppInfo() {
      try {
        const res = await listApps({ page: 1, row: 1, appId: this.appInfo.appId })
        if (res.data && res.data.length > 0) {
          this.appInfo = res.data[0]
        }
      } catch { /* ignore */ }
    },
    showSelectDialog() {
      this.$refs.selectServiceRef.openDialog()
    },
    handleServiceSelect(row) {
      Object.assign(this.selectedService, row)
    },
    async handleSubmit() {
      if (!this.selectedService.serviceId) {
        this.$message.warning('请选择服务')
        return
      }
      this.submitting = true
      try {
        const appInfo = { flowComponent: 'App', appId: this.appInfo.appId, name: this.appInfo.name }
        const serviceInfo = {
          flowComponent: 'Service',
          serviceId: this.selectedService.serviceId,
          name: this.selectedService.name,
          serviceCode: this.selectedService.serviceCode,
          url: this.selectedService.url,
          method: this.selectedService.method,
          timeout: this.selectedService.timeout,
          retryCount: this.selectedService.retryCount,
          messageQueueName: this.selectedService.messageQueueName,
          isInstance: this.selectedService.isInstance
        }
        const routeInfo = {
          flowComponent: 'addRouteView',
          orderTypeCd: '1',
          invokeLimitTimes: '1000',
          invokeModel: 'S'
        }
        await bindingService({ data: [appInfo, serviceInfo, routeInfo] })
        this.$message.success('服务绑定成功')
        this.$router.go(-1)
      } catch (e) {
        this.$message.error(e.msg || '服务绑定失败')
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped>
.add-service-bind {
  width: 100%;
  padding: 20px 0 0 0;
}
.box-card {
  width: 100%;
  box-sizing: border-box;
}
.select-service-card {
  margin-top: 16px;
}
.card-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}
.select-service-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.info-row {
  display: flex;
  flex-wrap: nowrap;
  margin-top: 12px;
}
.info-item {
  flex: 1 1 0;
  min-width: 0;
  display: flex;
  align-items: center;
  margin-right: 24px;
}
.info-item:last-child {
  margin-right: 0;
}
.info-item .label {
  color: #909399;
  font-size: 14px;
  white-space: nowrap;
}
.info-item .value {
  color: #303133;
  font-size: 14px;
  word-break: break-all;
  flex: 1;
}
.page-actions {
  width: 100%;
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
}
</style>
