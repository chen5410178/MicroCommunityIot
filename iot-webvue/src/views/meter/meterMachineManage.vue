<template>
  <div class="meter-machine-manage">
    <el-row :gutter="16">
      <el-col :span="4">
        <div class="type-tree-card">
          <ul class="type-list">
            <li
              class="type-item"
              :class="{ 'type-item-active': queryParams.meterType === '' }"
              @click="switchMeterType('')"
            >
              全部
            </li>
            <li
              v-for="(item, index) in meterTypes"
              :key="index"
              class="type-item"
              :class="{ 'type-item-active': queryParams.meterType === item.typeId }"
              @click="switchMeterType(item.typeId)"
            >
              {{ item.typeName }}
            </li>
          </ul>
        </div>
      </el-col>
      <el-col :span="20">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.machineNameLike" placeholder="请选择名称" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.address" placeholder="请选择表号" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-select v-model="queryParams.implBean" placeholder="请选择厂家" clearable style="width: 180px">
                <el-option
                  v-for="item in factorys"
                  :key="item.factoryId"
                  :label="item.factoryName"
                  :value="item.factoryId"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-select v-model="queryParams.machineModel" placeholder="请选择模式" clearable style="width: 150px">
                <el-option label="充值模式" value="1001" />
                <el-option label="抄表模式" value="2002" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.roomNameLike" placeholder="楼栋-单元-房屋" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>智能水电表</span>
            <div>
              <el-button size="small" @click="handleCustomRead">手工抄表</el-button>
              <el-button size="small" @click="handleSettingRead">设置抄表时间</el-button>
              <el-button size="small" @click="handleAdd">添加</el-button>
            </div>
          </div>
          <el-table :data="list" v-loading="loading" border stripe>
            <el-table-column prop="machineId" label="编号" width="100" align="center" />
            <el-table-column prop="machineName" label="名称" min-width="120" />
            <el-table-column prop="address" label="表号" min-width="120" />
            <el-table-column label="表类型" width="90" align="center">
              <template slot-scope="{ row }">
                {{ row.meterTypeName || _getMeterTypeName(row.meterType) }}
              </template>
            </el-table-column>
            <el-table-column label="模式" width="90" align="center">
              <template slot-scope="{ row }">
                {{ row.machineModel == '1001' ? '充值模式' : '抄表模式' }}
              </template>
            </el-table-column>
            <el-table-column prop="roomName" label="房屋" min-width="120" />
            <el-table-column prop="implBeanName" label="设备厂家" min-width="120" />
            <el-table-column label="当前度数" width="100" align="center">
              <template slot-scope="{ row }">
                <template v-if="row.machineModel == '1001'">
                  {{ row.degree > 0 ? row.degree : row.prestoreDegrees || '-' }}
                </template>
                <template v-else>-</template>
              </template>
            </el-table-column>
            <el-table-column label="剩余金额" width="100" align="center">
              <template slot-scope="{ row }">
                {{ row.curDegrees || '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="curReadingTime" label="读表时间" min-width="150" />
            <el-table-column prop="stateName" label="状态" width="80" align="center" />
            <el-table-column prop="heartbeatTime" label="心跳时间" min-width="150" />
            <el-table-column label="操作" width="300" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button size="small" type="text" @click="handleDetail(row)">详情</el-button>
                <el-button size="small" type="text" @click="handleEdit(row)">修改</el-button>
                <el-button size="small" type="text" style="color: #f56c6c" @click="handleDelete(row)">删除</el-button>
                <el-button size="small" type="text" @click="handleClean(row)">清零</el-button>
                <el-button size="small" type="text" @click="handleRecord(row)">记录</el-button>
                <el-button v-if="row.machineModel == '1001'" size="small" type="text" @click="handleCharge(row)">充值</el-button>
                <el-button
                  v-if="!row.controlType || row.controlType == 'OFF'"
                  size="small"
                  type="text"
                  @click="handleControlSwitch(row, 'ON')"
                >跳闸</el-button>
                <el-button
                  v-if="row.controlType == 'ON'"
                  size="small"
                  type="text"
                  @click="handleControlSwitch(row, 'OFF')"
                >合闸</el-button>
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
      </el-col>
    </el-row>

    <DeleteMeterMachine :visible.sync="delVisible" :del-data="currentRow" @success="handleSuccess" />
    <CleanMeterMachine :visible.sync="cleanVisible" :clean-data="currentRow" @success="handleSuccess" />
    <SettingMeterMachineRead :visible.sync="settingVisible" @success="handleSuccess" />
    <CustomReadMeterMachine :visible.sync="customReadVisible" @success="handleSuccess" />
    <ChargeMeter :visible.sync="chargeVisible" :charge-data="currentRow" @success="handleSuccess" />
  </div>
</template>

<script>
import { listMeterMachines, listMeterMachineFactory, listMeterTypes, meterMachineControlSwitch } from '@/api/meter/meterApi'
import { getCurrentCommunity } from '@/utils/auth'
import DeleteMeterMachine from '@/components/meter/DeleteMeterMachine.vue'
import CleanMeterMachine from '@/components/meter/CleanMeterMachine.vue'
import SettingMeterMachineRead from '@/components/meter/SettingMeterMachineRead.vue'
import CustomReadMeterMachine from '@/components/meter/CustomReadMeterMachine.vue'
import ChargeMeter from '@/components/meter/ChargeMeter.vue'

export default {
  name: 'MeterMachineManage',
  components: { DeleteMeterMachine, CleanMeterMachine, SettingMeterMachineRead, CustomReadMeterMachine, ChargeMeter },
  data() {
    return {
      loading: false,
      list: [],
      total: 0,
      meterTypes: [],
      factorys: [],
      delVisible: false,
      cleanVisible: false,
      settingVisible: false,
      customReadVisible: false,
      chargeVisible: false,
      currentRow: null,
      queryParams: {
        page: 1,
        row: 10,
        meterType: '',
        machineNameLike: '',
        address: '',
        implBean: '',
        machineModel: '',
        roomNameLike: ''
      }
    }
  },
  mounted() {
    this.fetchList()
    this.loadMeterTypes()
    this.loadFactorys()
  },
  methods: {
    _getMeterTypeName(meterType) {
      const item = this.meterTypes.find(t => t.typeId === meterType)
      return item ? item.typeName : '-'
    },
    async fetchList() {
      this.loading = true
      try {
        const community = getCurrentCommunity()
        const res = await listMeterMachines({
          ...this.queryParams,
          communityId: community.communityId
        })
        this.list = res.data || []
        this.total = res.records || 0
      } finally {
        this.loading = false
      }
    },
    async loadMeterTypes() {
      try {
        const community = getCurrentCommunity()
        const res = await listMeterTypes({ page: 1, row: 100, communityId: community.communityId })
        this.meterTypes = res.data || []
      } catch (e) {
        console.error('加载类型列表失败', e)
      }
    },
    async loadFactorys() {
      try {
        const community = getCurrentCommunity()
        const res = await listMeterMachineFactory({ page: 1, row: 500, communityId: community.communityId })
        this.factorys = res.data || []
      } catch (e) {
        console.error('加载厂家列表失败', e)
      }
    },
    switchMeterType(typeId) {
      this.queryParams.meterType = typeId
      this.handleSearch()
    },
    handleSearch() {
      this.queryParams.page = 1
      this.fetchList()
    },
    handleReset() {
      this.queryParams = {
        page: 1,
        row: 10,
        meterType: '',
        machineNameLike: '',
        address: '',
        implBean: '',
        machineModel: '',
        roomNameLike: ''
      }
      this.fetchList()
    },
    handleAdd() {
      this.$router.push({ path: '/pages/meter/addMeterMachine' })
    },
    handleEdit(row) {
      this.$router.push({ path: '/pages/meter/editMeterMachine', query: { machineId: row.machineId } })
    },
    handleDetail(row) {
      this.$router.push({ path: '/pages/meter/meterDetail', query: { machineId: row.machineId, roomId: row.roomId } })
    },
    handleRecord(row) {
      this.$router.push({ path: '/pages/meter/meterMachineCharge', query: { machineId: row.machineId } })
    },
    handleDelete(row) {
      this.currentRow = { ...row }
      this.delVisible = true
    },
    handleClean(row) {
      this.currentRow = { ...row }
      this.cleanVisible = true
    },
    handleCharge(row) {
      this.currentRow = { ...row }
      this.chargeVisible = true
    },
    handleCustomRead() {
      this.customReadVisible = true
    },
    handleSettingRead() {
      this.settingVisible = true
    },
    async handleControlSwitch(row, controlType) {
      try {
        await this.$confirm(`确定要${controlType == 'ON' ? '跳闸' : '合闸'}该设备吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
      } catch { return }
      try {
        const community = getCurrentCommunity()
        await meterMachineControlSwitch({
          machineId: row.machineId,
          controlType: controlType,
          communityId: community.communityId
        })
        this.$message.success(controlType == 'ON' ? '跳闸成功' : '合闸成功')
        this.fetchList()
      } catch (e) {
        this.$message.error(e.msg || '操作失败')
      }
    },
    handleSuccess() {
      this.fetchList()
    },
    handleSizeChange(val) {
      this.queryParams.row = val
      this.fetchList()
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.fetchList()
    }
  }
}
</script>

<style scoped>
.search-card { margin-bottom: 16px; }
.table-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.pagination-container { margin-top: 16px; text-align: right; }
.type-tree-card {
  background: #fff;
  border-radius: 4px;
  padding: 8px 0;
}
.type-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.type-item {
  padding: 10px 16px;
  cursor: pointer;
  border-radius: 4px;
  margin: 0 8px 5px;
  text-align: center;
  font-size: 14px;
  color: #303133;
  transition: all 0.3s;
}
.type-item:hover { background-color: #f5f7fa; }
.type-item-active {
  background-color: #409eff;
  color: white;
  font-weight: bold;
}
</style>
