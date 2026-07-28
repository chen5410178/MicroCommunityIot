<template>
  <div class="tab-component">
    <el-card shadow="never">
      <el-form :inline="true" :model="queryParams" size="small">
        <el-form-item>
          <el-input v-model="queryParams.carNum" placeholder="必填，请填写车牌号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.state" placeholder="请选择车辆状态" clearable style="width: 180px">
            <el-option label="进场状态" value="100300" />
            <el-option label="支付完成" value="100400" />
            <el-option label="离场状态" value="100500" />
            <el-option label="支付超时重新支付" value="100600" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select v-model="queryParams.carType" placeholder="请选择车牌类型" clearable style="width: 160px">
            <el-option label="临时车" value="T" />
            <el-option label="出售车辆" value="S" />
            <el-option label="月租车" value="H" />
            <el-option label="白名单" value="W" />
            <el-option label="黑名单" value="B" />
            <el-option label="内部车" value="I" />
            <el-option label="免费车" value="NM" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-date-picker v-model="queryParams.startTime" type="date" placeholder="开始时间" value-format="yyyy-MM-dd" style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-date-picker v-model="queryParams.endTime" type="date" placeholder="结束时间" value-format="yyyy-MM-dd" style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="list" v-loading="loading" border stripe size="small">
        <el-table-column label="进场图" width="80" align="center">
          <template slot-scope="{ row }">
            <el-image v-if="row.photoJpg" :src="row.photoJpg" fit="cover" style="width:60px;height:60px" :preview-src-list="[row.photoJpg]" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="ciId" label="进出场编号" min-width="120" align="center" />
        <el-table-column label="进出方向" min-width="140" align="center">
          <template slot-scope="{ row }">
            {{ row.carInout === '3306' ? '进场' : '出场' }}({{ row.stateName }})
            <div>{{ row.machineName }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="carNum" label="车牌号" min-width="120" align="center" />
        <el-table-column prop="paNum" label="停车场" min-width="120" align="center" />
        <el-table-column label="计费规则" min-width="120" align="center">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="viewFeeConfig(row.configId)">{{ row.feeName }}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="carTypeName" label="车牌类型" min-width="100" align="center" />
        <el-table-column prop="inTime" label="进场时间" min-width="160" align="center" />
        <el-table-column label="出场时间" min-width="160" align="center">
          <template slot-scope="{ row }">{{ row.carInout !== '3307' ? '-' : row.openTime }}</template>
        </el-table-column>
        <el-table-column label="停车时间" min-width="140" align="center">
          <template slot-scope="{ row }">{{ row.hours }}小时{{ row.min }}分</template>
        </el-table-column>
        <el-table-column label="收费金额" min-width="120" align="center">
          <template slot-scope="{ row }">
            <template v-if="row.carType === '1001'">-</template>
            <template v-else>
              {{ row.payCharge }}
              <el-button
                v-if="row.inState === '100300' || row.inState === '100400' || row.inState === '100600'"
                type="text" size="small" @click="carInPayFee(row)">出场</el-button>
            </template>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="说明" min-width="100" align="center" />
        <el-table-column label="操作" width="100" align="center">
          <template slot-scope="{ row }">
            <el-button
              v-if="row.state === '100500' && row.carInout === '3306'"
              type="text" size="small" style="color: #f56c6c" @click="exceptionCarIn(row)">异常入场</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        :current-page="queryParams.page"
        :page-size="queryParams.row"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        class="pagination-container"
      />
    </el-card>
  </div>
</template>

<script>
import { listCarInoutDetails, listTempCarFeeConfigs } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ParkingAreaControlCarInouts',
  props: {
    paId: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      queryParams: {
        page: 1, row: 10, carNum: '', state: '', carType: '',
        startTime: '', endTime: ''
      }
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listCarInoutDetails({
          ...this.queryParams,
          paId: this.paId,
          communityId
        })
        this.list = res.data || []
        this.total = res.total || 0
      } catch (e) {
        console.error('获取出入场明细失败', e)
      } finally {
        this.loading = false
      }
    },
    handleSearch() { this.queryParams.page = 1; this.loadData() },
    handleReset() {
      this.queryParams = { page: 1, row: 10, carNum: '', state: '', carType: '', startTime: '', endTime: '' }
      this.loadData()
    },
    handleSizeChange(val) { this.queryParams.row = val; this.loadData() },
    handlePageChange(val) { this.queryParams.page = val; this.loadData() },
    async viewFeeConfig(configId) {
      const communityId = (getCurrentCommunity() || {}).communityId || ''
      try {
        const res = await listTempCarFeeConfigs({ page: 1, row: 1, communityId, configId })
        const data = (res.data || [])[0]
        if (data) {
          const info = { '收费规则': data.ruleName, '车辆类型': data.carTypeName, '开始时间': data.startTime, '结束时间': data.endTime };
          (data.tempCarFeeConfigAttrs || []).forEach(item => { info[item.specName] = item.value })
          this.$alert(Object.entries(info).map(([k, v]) => `${k}: ${v}`).join('<br>'), data.feeName + ' 费用项', { dangerouslyUseHTMLString: true })
        }
      } catch (e) { /* ignore */ }
    },
    carInPayFee(carIn) {
      this.$emit('show-custom-inout', { type: '1102', carNum: carIn.carNum, amount: carIn.payCharge, payCharge: carIn.payCharge, paId: this.paId })
    },
    exceptionCarIn(row) {
      this.$confirm('请确定异常入场！', '确认操作', { type: 'warning' }).then(() => { /* TODO */ }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.tab-component { padding: 0; }
.pagination-container { text-align: right; margin-top: 16px; }
</style>
