<template>
  <div class="invoice-manage-page">
    <el-row :gutter="16">
      <el-col :span="4">
        <el-card shadow="never" class="left-card">
          <el-menu :default-active="activeMenu" class="invoice-menu" @select="handleMenuSelect">
            <el-menu-item index="order">
              <i class="el-icon-document" />
              <span slot="title">发票管理</span>
            </el-menu-item>
            <el-menu-item index="red">
              <i class="el-icon-refresh-left" />
              <span slot="title">发票红冲</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>
      <el-col :span="20">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item v-if="activeMenu === 'order'">
              <el-input v-model="queryParams.buyerName" placeholder="请输入抬头名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-select v-model="queryParams.state" placeholder="请选择状态" clearable style="width: 160px">
                <el-option
                  v-for="item in stateOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item v-if="activeMenu === 'order'">
              <el-input v-model="queryParams.invoiceNo" placeholder="请输入发票号" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <InvoiceOrderRecord v-if="activeMenu === 'order'" ref="invoiceOrderRecord" :conditions="queryParams" />
          <InvoiceRedRecord v-else ref="invoiceRedRecord" :conditions="queryParams" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import InvoiceOrderRecord from '@/components/property/InvoiceOrderRecord.vue'
import InvoiceRedRecord from '@/components/property/InvoiceRedRecord.vue'

export default {
  name: 'InvoiceManage',
  components: { InvoiceOrderRecord, InvoiceRedRecord },
  data() {
    return {
      activeMenu: 'order',
      queryParams: {
        phone: '',
        buyerName: '',
        state: '',
        invoiceNo: ''
      },
      orderStateOptions: [
        { value: 'AUDIT', label: '待审核' },
        { value: 'WAIT', label: '待开票' },
        { value: 'DONE', label: '已开票' },
        { value: 'FAIL', label: '开票失败' },
        { value: 'RED', label: '已红冲' }
      ],
      redStateOptions: [
        { value: 'WAIT', label: '待冲红' },
        { value: 'DONE', label: '已冲红' }
      ]
    }
  },
  computed: {
    stateOptions() {
      return this.activeMenu === 'red' ? this.redStateOptions : this.orderStateOptions
    }
  },
  methods: {
    handleMenuSelect(index) {
      this.activeMenu = index
      this.queryParams.state = ''
    },
    handleSearch() {
      const target = this.activeMenu === 'red' ? this.$refs.invoiceRedRecord : this.$refs.invoiceOrderRecord
      if (target) {
        target.reload()
      }
    },
    handleReset() {
      this.queryParams.phone = ''
      this.queryParams.buyerName = ''
      this.queryParams.state = ''
      this.queryParams.invoiceNo = ''
      this.handleSearch()
    }
  }
}
</script>

<style scoped>
.left-card {
  min-height: 400px;
}

.invoice-menu {
  border-right: none;
}

.search-card {
  margin-bottom: 16px;
}

.table-card {
  margin-bottom: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
