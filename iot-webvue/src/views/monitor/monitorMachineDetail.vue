<template>
  <div class="monitor-machine-detail">
    <el-card shadow="never" class="info-card">
      <div slot="header" class="card-header">
        <span>监控信息</span>
        <el-button size="small" @click="handleBack">返回</el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="info-item"><span class="info-label">监控ID：</span>{{ info.machineId }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">名称：</span>{{ info.machineName }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">编号：</span>{{ info.machineCode }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">协议：</span>{{ info.protocol }}</div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="info-row">
        <el-col :span="6">
          <div class="info-item"><span class="info-label">位置：</span>{{ info.locationName }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">区域：</span>{{ info.maName }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">状态：</span>{{ info.stateName }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">在线状态：</span>{{ info.isOnlineStateName }}</div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="info-row">
        <el-col :span="6">
          <div class="info-item"><span class="info-label">心跳时间：</span>{{ info.heartbeatTime }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">创建时间：</span>{{ info.createTime }}</div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="info-row" v-if="info.lockMachineParamDtoList && info.lockMachineParamDtoList.length > 0">
        <el-col :span="6" v-for="(item, index) in info.lockMachineParamDtoList" :key="index">
          <div class="info-item"><span class="info-label">{{ item.specName }}：</span>{{ item.value }}</div>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="never" class="tab-card">
      <el-tabs v-model="currentTab" @tab-click="handleTabClick">
        <el-tab-pane label="监控区域" name="area">
          <div v-loading="areaLoading">
            <el-table :data="areaList" border stripe>
              <el-table-column prop="maId" label="区域编号" min-width="150" />
              <el-table-column prop="maName" label="区域名称" min-width="150" />
              <el-table-column label="备注" min-width="100" align="center">
                <template slot-scope="{ row }">
                  <el-button v-if="row.remark" size="small" type="text" @click="$alert(row.remark, '备注')">查看</el-button>
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="创建时间" min-width="160" />
            </el-table>
            <el-pagination
              :current-page="areaPage.page"
              :page-size="areaPage.row"
              :total="areaTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleAreaSizeChange"
              @current-change="handleAreaPageChange"
              class="pagination-container"
            />
          </div>
        </el-tab-pane>
        <el-tab-pane label="监控模型" name="model">
          <div v-loading="modelLoading">
            <el-table :data="modelList" border stripe>
              <el-table-column prop="modelName" label="模型名称" min-width="150" />
              <el-table-column prop="machineName" label="监控名称" min-width="150" />
              <el-table-column prop="machineCode" label="监控编号" min-width="150" />
              <el-table-column prop="locationName" label="监控位置" min-width="150" />
              <el-table-column prop="maName" label="监控区域" min-width="150" />
            </el-table>
            <el-pagination
              :current-page="modelPage.page"
              :page-size="modelPage.row"
              :total="modelTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleModelSizeChange"
              @current-change="handleModelPageChange"
              class="pagination-container"
            />
          </div>
        </el-tab-pane>
        <el-tab-pane label="监控告警" name="pool">
          <div v-loading="poolLoading">
            <el-table :data="poolList" border stripe>
              <el-table-column prop="mpId" label="编号" min-width="120" />
              <el-table-column prop="machineName" label="监控名称" min-width="150" />
              <el-table-column prop="machineCode" label="监控编码" min-width="150" />
              <el-table-column prop="modelName" label="监控模型" min-width="150" />
              <el-table-column prop="monitorTypeName" label="监控类型" min-width="120" />
              <el-table-column label="监控图片" width="100" align="center">
                <template slot-scope="{ row }">
                  <el-image
                    v-if="row.monitorPhoto"
                    style="width: 50px; height: 50px; border-radius: 4px;"
                    :src="row.monitorPhoto"
                    :preview-src-list="[row.monitorPhoto]"
                    fit="cover"
                  />
                  <span v-else>-</span>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="创建时间" min-width="160" />
              <el-table-column label="描述" min-width="100" align="center">
                <template slot-scope="{ row }">
                  <el-button v-if="row.remark" size="small" type="text" @click="$alert(row.remark, '描述')">查看</el-button>
                  <span v-else>-</span>
                </template>
              </el-table-column>
            </el-table>
            <el-pagination
              :current-page="poolPage.page"
              :page-size="poolPage.row"
              :total="poolTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handlePoolSizeChange"
              @current-change="handlePoolPageChange"
              class="pagination-container"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { listMonitorMachines, listMonitorAreas, listMonitorMachineModels, listMonitorPools } from '@/api/monitor/monitorApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'MonitorMachineDetail',
  data() {
    return {
      machineId: '',
      maId: '',
      needBack: false,
      currentTab: 'area',
      info: {},
      areaLoading: false,
      areaList: [],
      areaTotal: 0,
      areaPage: { page: 1, row: 10 },
      modelLoading: false,
      modelList: [],
      modelTotal: 0,
      modelPage: { page: 1, row: 10 },
      poolLoading: false,
      poolList: [],
      poolTotal: 0,
      poolPage: { page: 1, row: 10 }
    }
  },
  mounted() {
    this.machineId = this.$route.query.machineId || ''
    this.maId = this.$route.query.maId || ''
    this.needBack = this.$route.query.needBack === 'true' || this.$route.query.needBack === true
    if (!this.machineId) {
      this.$router.go(-1)
      return
    }
    this.loadMachineInfo()
    this.loadAreaData()
  },
  methods: {
    async loadMachineInfo() {
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listMonitorMachines({ machineId: this.machineId, page: 1, row: 1, communityId })
        if (res.data && res.data.length > 0) {
          this.info = res.data[0]
        }
      } catch (e) {
        console.error('加载监控信息失败', e)
      }
    },
    async loadAreaData() {
      this.areaLoading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listMonitorAreas({
          maId: this.maId,
          page: this.areaPage.page,
          row: this.areaPage.row,
          communityId
        })
        this.areaList = res.data || []
        this.areaTotal = res.records || 0
      } catch (e) {
        console.error('加载监控区域失败', e)
      } finally {
        this.areaLoading = false
      }
    },
    async loadModelData() {
      this.modelLoading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listMonitorMachineModels({
          machineId: this.machineId,
          page: this.modelPage.page,
          row: this.modelPage.row,
          communityId
        })
        this.modelList = res.data || []
        this.modelTotal = res.records || 0
      } catch (e) {
        console.error('加载监控模型失败', e)
      } finally {
        this.modelLoading = false
      }
    },
    async loadPoolData() {
      this.poolLoading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const res = await listMonitorPools({
          machineId: this.machineId,
          page: this.poolPage.page,
          row: this.poolPage.row,
          communityId
        })
        this.poolList = res.data || []
        this.poolTotal = res.records || 0
      } catch (e) {
        console.error('加载监控告警失败', e)
      } finally {
        this.poolLoading = false
      }
    },
    handleTabClick(tab) {
      if (tab.name === 'model' && this.modelList.length === 0) {
        this.loadModelData()
      } else if (tab.name === 'pool' && this.poolList.length === 0) {
        this.loadPoolData()
      }
    },
    handleBack() {
      this.$router.go(-1)
    },
    handleAreaSizeChange(val) { this.areaPage.row = val; this.loadAreaData() },
    handleAreaPageChange(val) { this.areaPage.page = val; this.loadAreaData() },
    handleModelSizeChange(val) { this.modelPage.row = val; this.loadModelData() },
    handleModelPageChange(val) { this.modelPage.page = val; this.loadModelData() },
    handlePoolSizeChange(val) { this.poolPage.row = val; this.loadPoolData() },
    handlePoolPageChange(val) { this.poolPage.page = val; this.loadPoolData() }
  }
}
</script>

<style scoped>
.info-card { margin-bottom: 16px; }
.tab-card { margin-bottom: 0; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.info-item { line-height: 32px; font-size: 14px; }
.info-label { color: #909399; }
.info-row { margin-top: 8px; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
