<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="服务绑定"
    width="800px"
    append-to-body
    @close="handleClose"
  >
    <div class="bind-app-service">
      <div class="bind-section">
        <div class="section-title">已选应用</div>
        <el-tag type="success" size="medium">{{ appName }}</el-tag>
      </div>
      <div class="bind-section">
        <div class="section-title">选择服务</div>
        <el-form :inline="true" :model="serviceSearch">
          <el-form-item>
            <el-input v-model="serviceSearch.name" placeholder="请输入服务名称" clearable style="width: 200px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleServiceSearch">搜索</el-button>
          </el-form-item>
        </el-form>
        <el-table
          ref="serviceTable"
          :data="serviceList"
          v-loading="serviceLoading"
          border
          stripe
          max-height="300"
          highlight-current-row
          @current-change="handleServiceSelect"
        >
          <el-table-column prop="serviceId" label="服务ID" width="100" />
          <el-table-column prop="name" label="服务名称" min-width="150" />
          <el-table-column prop="serviceCode" label="服务编码" width="120" />
          <el-table-column prop="url" label="调用地址" min-width="180" />
        </el-table>
        <el-pagination
          :current-page="serviceSearch.page"
          :page-size="serviceSearch.row"
          :total="serviceTotal"
          :page-sizes="[10, 20, 50]"
          layout="total, prev, pager, next"
          @size-change="handleServiceSizeChange"
          @current-change="handleServicePageChange"
          class="pagination-container"
        />
      </div>
      <div class="bind-section">
        <div class="section-title">绑定参数</div>
        <el-form :model="bindForm" :rules="bindRules" ref="bindFormRef" label-width="120px">
          <el-form-item label="订单类型" prop="orderTypeCd">
            <el-select v-model="bindForm.orderTypeCd" placeholder="请选择订单类型" style="width: 100%">
              <el-option label="请求订单" value="1" />
              <el-option label="办理订单" value="2" />
              <el-option label="查询订单" value="3" />
              <el-option label="异步处理" value="4" />
            </el-select>
          </el-form-item>
          <el-form-item label="调用次数限制" prop="invokeLimitTimes">
            <el-input-number v-model="bindForm.invokeLimitTimes" :min="1" :max="99999" style="width: 100%" />
          </el-form-item>
          <el-form-item label="调用方式" prop="invokeModel">
            <el-select v-model="bindForm.invokeModel" placeholder="请选择调用方式" style="width: 100%">
              <el-option label="同步调用" value="S" />
              <el-option label="异步调用" value="A" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="submitting">确定绑定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { listServices, bindingService } from '@/api/dev/devApi'

export default {
  name: 'BindAppService',
  props: {
    visible: { type: Boolean, default: false },
    appId: { type: String, default: '' },
    appName: { type: String, default: '' }
  },
  data() {
    return {
      dialogVisible: false,
      submitting: false,
      serviceLoading: false,
      serviceList: [],
      serviceTotal: 0,
      selectedService: null,
      serviceSearch: {
        page: 1,
        row: 10,
        name: ''
      },
      bindForm: {
        orderTypeCd: '1',
        invokeLimitTimes: 1000,
        invokeModel: 'S'
      },
      bindRules: {
        orderTypeCd: [{ required: true, message: '请选择订单类型', trigger: 'change' }],
        invokeLimitTimes: [{ required: true, message: '请输入调用次数', trigger: 'blur' }],
        invokeModel: [{ required: true, message: '请选择调用方式', trigger: 'change' }]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.initData()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    initData() {
      this.serviceSearch = { page: 1, row: 10, name: '' }
      this.selectedService = null
      this.bindForm = { orderTypeCd: '1', invokeLimitTimes: 1000, invokeModel: 'S' }
      this.fetchServices()
    },
    async fetchServices() {
      this.serviceLoading = true
      try {
        const res = await listServices(this.serviceSearch)
        this.serviceList = res.data || []
        this.serviceTotal = res.total || 0
      } finally {
        this.serviceLoading = false
      }
    },
    handleServiceSearch() {
      this.serviceSearch.page = 1
      this.fetchServices()
    },
    handleServiceSelect(row) {
      this.selectedService = row
    },
    handleServiceSizeChange(val) {
      this.serviceSearch.row = val
      this.fetchServices()
    },
    handleServicePageChange(val) {
      this.serviceSearch.page = val
      this.fetchServices()
    },
    async handleSubmit() {
      if (!this.selectedService) {
        this.$message.warning('请选择服务')
        return
      }
      try {
        await this.$refs.bindFormRef.validate()
      } catch { return }
      this.submitting = true
      try {
        const appInfo = { flowComponent: 'App', appId: this.appId, name: this.appName }
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
          orderTypeCd: this.bindForm.orderTypeCd,
          invokeLimitTimes: String(this.bindForm.invokeLimitTimes),
          invokeModel: this.bindForm.invokeModel
        }
        await bindingService({ data: [appInfo, serviceInfo, routeInfo] })
        this.$message.success('服务绑定成功')
        this.$emit('success')
        this.dialogVisible = false
      } catch (e) {
        this.$message.error(e.msg || '服务绑定失败')
      } finally {
        this.submitting = false
      }
    },
    handleClose() {
      this.dialogVisible = false
      if (this.$refs.bindFormRef) {
        this.$refs.bindFormRef.resetFields()
      }
    }
  }
}
</script>

<style scoped>
.bind-app-service { padding: 0; }
.bind-section { margin-bottom: 20px; }
.bind-section:last-child { margin-bottom: 0; }
.section-title { font-size: 14px; font-weight: 600; color: #303133; margin-bottom: 12px; padding-left: 8px; border-left: 3px solid #409eff; }
.pagination-container { margin-top: 12px; text-align: right; }
</style>
