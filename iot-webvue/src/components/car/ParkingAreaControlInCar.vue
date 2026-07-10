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
        <el-table-column prop="ciId" label="进出场ID" min-width="120" align="center" />
        <el-table-column label="车辆状态" min-width="100" align="center">
          <template slot-scope="{ row }">{{ row.stateName }}</template>
        </el-table-column>
        <el-table-column label="车牌号" min-width="120" align="center">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="editCarNum(row)">{{ row.carNum }}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="paNum" label="停车场" min-width="120" align="center" />
        <el-table-column label="计费规则" min-width="120" align="center">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" @click="viewFeeConfig(row.configId)">{{ row.feeName }}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="carTypeName" label="车牌类型" min-width="100" align="center" />
        <el-table-column prop="inTime" label="进场时间" min-width="160" align="center" />
        <el-table-column label="在场时间" min-width="140" align="center">
          <template slot-scope="{ row }">{{ row.hours }}小时{{ row.min }}分</template>
        </el-table-column>
        <el-table-column label="停车费用" min-width="140" align="center">
          <template slot-scope="{ row }">
            <template v-if="row.carType !== '1001'">
              {{ row.payCharge }}
              (<el-button type="text" size="small" @click="carInPayFee(row)">收费出场</el-button>)
            </template>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" align="center">
          <template slot-scope="{ row }">
            <el-button type="text" size="small" style="color: #f56c6c" @click="exceptionCarOutConfirm(row)">异常出场</el-button>
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
    <el-dialog :visible.sync="editNumVisible" title="修改车牌号" width="400px">
      <el-input v-model="updateCarNum" placeholder="必填，请填写车牌号" />
      <span slot="footer">
        <el-button @click="editNumVisible = false">取消</el-button>
        <el-button type="primary" @click="doUpdateCarNum">提交</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listCarInParkingArea, updateCarInoutNum, exceptionCarOut, listTempCarFeeConfigs } from '@/api/car/carApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ParkingAreaControlInCar',
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
      },
      editNumVisible: false,
      updateCarNum: '',
      currentCiId: '',
      currentCarIn: {}
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
        const res = await listCarInParkingArea({
          ...this.queryParams,
          paId: this.paId,
          communityId
        })
        this.list = res.data || []
        this.total = res.records || 0
      } catch (e) {
        console.error('获取在场车辆失败', e)
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.queryParams.page = 1
      this.loadData()
    },
    handleReset() {
      this.queryParams = { page: 1, row: 10, carNum: '', state: '', carType: '', startTime: '', endTime: '' }
      this.loadData()
    },
    handleSizeChange(val) {
      this.queryParams.row = val
      this.loadData()
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.loadData()
    },
    editCarNum(row) {
      this.updateCarNum = row.carNum
      this.currentCiId = row.ciId
      this.editNumVisible = true
    },
    async doUpdateCarNum() {
      if (!this.updateCarNum) {
        this.$message.warning('未包含车牌号')
        return
      }
      const communityId = (getCurrentCommunity() || {}).communityId || ''
      try {
        const res = await updateCarInoutNum({ carNum: this.updateCarNum, ciId: this.currentCiId, communityId })
        if (res.code === 0) {
          this.$message.success(res.msg || '修改成功')
          this.editNumVisible = false
          this.loadData()
        } else {
          this.$message.error(res.msg || '修改失败')
        }
      } catch (e) {
        this.$message.error('修改失败')
      }
    },
    async viewFeeConfig(configId) {
      const communityId = (getCurrentCommunity() || {}).communityId || ''
      try {
        const res = await listTempCarFeeConfigs({ page: 1, row: 1, communityId, configId })
        const data = (res.data || [])[0]
        if (data) {
          const info = {
            '收费规则': data.ruleName,
            '车辆类型': data.carTypeName,
            '开始时间': data.startTime,
            '结束时间': data.endTime
          };
          (data.tempCarFeeConfigAttrs || []).forEach(item => {
            info[item.specName] = item.value
          })
          this.$alert(Object.entries(info).map(([k, v]) => `${k}: ${v}`).join('<br>'), data.feeName + ' 费用项', {
            dangerouslyUseHTMLString: true
          })
        }
      } catch (e) { /* ignore */ }
    },
    carInPayFee(carIn) {
      this.$emit('show-custom-inout', {
        type: '1102',
        carNum: carIn.carNum,
        amount: carIn.payCharge,
        payCharge: carIn.payCharge,
        paId: this.paId
      })
    },
    exceptionCarOutConfirm(carIn) {
      this.currentCarIn = carIn
      this.$confirm('请确定异常出场！', '确认操作', { type: 'warning' }).then(() => {
        this.doExceptionCarOut()
      }).catch(() => {})
    },
    async doExceptionCarOut() {
      if (!this.currentCarIn.ciId) return
      const communityId = (getCurrentCommunity() || {}).communityId || ''
      try {
        const res = await exceptionCarOut({ ciId: this.currentCarIn.ciId, communityId })
        if (res.code === 0) {
          this.$message.success(res.msg || '操作成功')
          this.loadData()
        } else {
          this.$message.error(res.msg || '操作失败')
        }
      } catch (e) {
        this.$message.error('操作失败')
      }
    }
  }
}
</script>

<style scoped>
.tab-component { padding: 0; }
.pagination-container { text-align: right; margin-top: 16px; }
</style>
