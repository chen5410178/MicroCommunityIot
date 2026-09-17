<template>
  <div class="invoice-apply-page">
    <el-card shadow="never" class="phone-card">
      <div slot="header" class="card-header">
        <span>申请开票</span>
        <el-button size="small" @click="handleBack">返回</el-button>
      </div>
      <el-form :inline="true" :model="form">
        <el-form-item label="手机号">
          <el-input
            v-model="form.phone"
            placeholder="请输入开票手机号"
            maxlength="11"
            clearable
            style="width: 220px"
            @blur="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
        </el-form-item>
        <el-form-item>
          <span class="tip-text">按手机号带出商城发票抬头与未开票充电订单</span>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="headings.length > 0" shadow="never" class="heading-card">
      <div slot="header" class="card-header">
        <span>商城发票抬头</span>
      </div>
      <el-table :data="headings" border stripe>
        <el-table-column label="抬头类型" width="100" align="center">
          <template slot-scope="{ row }">{{ row.headingType || '-' }}</template>
        </el-table-column>
        <el-table-column prop="headingName" label="抬头名称" min-width="180" />
        <el-table-column label="税号" min-width="180">
          <template slot-scope="{ row }">{{ row.taxNo || '-' }}</template>
        </el-table-column>
        <el-table-column label="默认" width="80" align="center">
          <template slot-scope="{ row }">{{ row.isDefault === '1' ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template slot-scope="{ row }">
            <el-button size="small" type="text" @click="applyHeading(row)">回填</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card shadow="never" class="form-card">
      <div slot="header" class="card-header">
        <span>开票信息</span>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="130px">
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="票据种类" prop="invoiceType">
              <el-select v-model="form.invoiceType" style="width: 100%" @change="handleInvoiceTypeChange">
                <el-option label="普票" value="普票" />
                <el-option label="专票" value="专票" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="抬头类型" prop="headingType">
              <el-select v-model="form.headingType" :disabled="form.invoiceType === '专票'" style="width: 100%">
                <el-option label="个人" value="个人" />
                <el-option label="企业" value="公司" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="抬头名称" prop="headingName">
              <el-input v-model="form.headingName" placeholder="请输入抬头名称" maxlength="128" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="纳税人识别号" prop="taxNo">
              <el-input v-model="form.taxNo" placeholder="企业抬头必填" maxlength="64" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="地址电话" prop="buyerAddress">
              <el-input v-model="form.buyerAddress" placeholder="选填" maxlength="256" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="接收电子发票" maxlength="128" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="开户行及账号" prop="buyerBankNo">
              <el-input v-model="form.buyerBankNo" placeholder="专票必填" maxlength="64" />
            </el-form-item>
          </el-col>
          <el-col :span="16">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" placeholder="选填" maxlength="200" show-word-limit />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <el-card shadow="never" class="order-card">
      <div slot="header" class="card-header">
        <span>未开票充电订单</span>
        <span class="tip-text">已选 {{ selectedOrders.length }} 单 · 合计 ¥{{ selectedAmount.toFixed(2) }}</span>
      </div>
      <el-table
        ref="orderTable"
        :data="unInvoicedOrders"
        v-loading="orderLoading"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="充电桩" min-width="160">
          <template slot-scope="{ row }">{{ row.machineName || row.machineCode || '-' }}</template>
        </el-table-column>
        <el-table-column label="端口" min-width="100" align="center">
          <template slot-scope="{ row }">{{ row.portName || '-' }}</template>
        </el-table-column>
        <el-table-column label="开始时间" min-width="160">
          <template slot-scope="{ row }">{{ row.startTime || '-' }}</template>
        </el-table-column>
        <el-table-column label="结束时间" min-width="160">
          <template slot-scope="{ row }">{{ row.endTime || '-' }}</template>
        </el-table-column>
        <el-table-column label="金额" min-width="100" align="right">
          <template slot-scope="{ row }">¥{{ Number(row.amount || 0).toFixed(2) }}</template>
        </el-table-column>
      </el-table>
      <div class="submit-bar">
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交开票</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { listUnInvoicedOrders, queryInvoiceHeadings, saveInvoiceOrder } from '@/api/property/invoiceApi'

export default {
  name: 'InvoiceApply',
  data() {
    return {
      orderLoading: false,
      submitLoading: false,
      headings: [],
      unInvoicedOrders: [],
      selectedOrders: [],
      form: {
        phone: '',
        invoiceType: '普票',
        headingType: '个人',
        headingName: '',
        taxNo: '',
        buyerAddress: '',
        buyerBankNo: '',
        email: '',
        remark: ''
      }
    }
  },
  computed: {
    selectedAmount() {
      return this.selectedOrders.reduce((sum, row) => sum + Number(row.amount || 0), 0)
    },
    rules() {
      return {
        invoiceType: [{ required: true, message: '请选择票据种类', trigger: 'change' }],
        headingType: [{ required: true, message: '请选择抬头类型', trigger: 'change' }],
        headingName: [{ required: true, message: '请输入抬头名称', trigger: 'blur' }],
        taxNo: [{
          validator: (rule, value, callback) => {
            if (this.form.headingType === '公司' && !value) {
              callback(new Error('企业抬头必须填写税号'))
              return
            }
            callback()
          },
          trigger: 'blur'
        }],
        buyerBankNo: [{
          validator: (rule, value, callback) => {
            if (this.form.invoiceType === '专票' && !value) {
              callback(new Error('开具专票必须填写开户行及账号'))
              return
            }
            callback()
          },
          trigger: 'blur'
        }],
        email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]
      }
    }
  },
  methods: {
    handleBack() {
      this.$router.push('/pages/property/invoiceManage')
    },
    handleInvoiceTypeChange(val) {
      if (val === '专票') {
        this.form.headingType = '公司'
      }
    },
    // 失焦/点击查询：抬头查询 + 未开票订单查询
    handleQuery() {
      if (!/^1\d{10}$/.test(this.form.phone)) {
        this.$message.warning('请输入正确的手机号')
        return
      }
      this.loadHeadings()
      this.loadUnInvoicedOrders()
    },
    async loadHeadings() {
      try {
        const res = await queryInvoiceHeadings({ userPhone: this.form.phone, page: 1, row: 20 })
        this.headings = res.data || []
        if (this.headings.length > 0) {
          const defaultHeading = this.headings.find(item => item.isDefault === '1') || this.headings[0]
          this.applyHeading(defaultHeading)
        }
      } catch (e) {
        this.headings = []
        this.$message.error(e.message || '查询发票抬头失败')
      }
    },
    applyHeading(heading) {
      this.form.headingType = heading.headingType === '公司' ? '公司' : '个人'
      this.form.headingName = heading.headingName || ''
      this.form.taxNo = heading.taxNo || ''
      this.form.buyerAddress = heading.address || ''
      this.form.buyerBankNo = heading.bankNo || ''
      this.form.email = heading.email || ''
    },
    async loadUnInvoicedOrders() {
      this.orderLoading = true
      try {
        const res = await listUnInvoicedOrders({ phone: this.form.phone })
        const data = res.data || {}
        this.unInvoicedOrders = data.orders || []
        this.selectedOrders = []
      } catch (e) {
        this.unInvoicedOrders = []
        this.selectedOrders = []
        this.$message.error(e.message || '查询未开票订单失败')
      } finally {
        this.orderLoading = false
      }
    },
    handleSelectionChange(rows) {
      this.selectedOrders = rows
    },
    async handleSubmit() {
      if (!/^1\d{10}$/.test(this.form.phone)) {
        this.$message.warning('请输入正确的手机号')
        return
      }
      if (this.selectedOrders.length === 0) {
        this.$message.warning('请先勾选需要开票的订单')
        return
      }
      try {
        await this.$refs.formRef.validate()
      } catch (e) {
        return
      }
      this.submitLoading = true
      try {
        const res = await saveInvoiceOrder({
          phone: this.form.phone,
          orderIds: this.selectedOrders.map(row => row.orderId),
          headingType: this.form.headingType,
          headingName: this.form.headingName,
          taxNo: this.form.taxNo,
          buyerAddress: this.form.buyerAddress,
          buyerBankNo: this.form.buyerBankNo,
          email: this.form.email,
          invoiceType: this.form.invoiceType,
          remark: this.form.remark
        })
        const data = res.data || {}
        if (data.state === 'F') {
          // 商城未建单，物联网侧零落库，停留当前页面便于修正后重提
          this.$message.error(data.failReason || '开票失败，请稍后重试')
          return
        }
        if (data.state === 'FAIL') {
          this.$message.warning(data.failReason || '开票失败，可在开票记录中重新提交')
          this.handleBack()
          return
        }
        if (data.state === 'DONE') {
          this.$message.success('开票成功')
        } else if (data.state === 'AUDIT') {
          this.$message.success('已提交待审核')
        } else {
          this.$message.success('已提交待开票')
        }
        this.handleBack()
      } catch (e) {
        this.$message.error(e.message || '提交开票失败')
      } finally {
        this.submitLoading = false
      }
    }
  }
}
</script>

<style scoped>
.phone-card,
.heading-card,
.form-card {
  margin-bottom: 16px;
}

.order-card {
  margin-bottom: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tip-text {
  font-size: 13px;
  color: #909399;
}

.submit-bar {
  margin-top: 16px;
  text-align: right;
}
</style>
