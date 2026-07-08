<template>
  <div class="lock-machine-detail">
    <el-card shadow="never" class="info-card">
      <div slot="header" class="card-header">
        <span>门锁信息</span>
        <el-button size="small" @click="handleBack">返回</el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="info-item"><span class="info-label">门锁ID：</span>{{ info.machineId }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">门锁名称：</span>{{ info.machineName }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">门锁编号：</span>{{ info.machineCode }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">厂家名称：</span>{{ info.factoryName }}</div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="info-row">
        <el-col :span="6">
          <div class="info-item"><span class="info-label">剩余电量：</span>{{ info.charge }}%</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">在线状态：</span>{{ info.stateName }}</div>
        </el-col>
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
      <el-tabs v-model="currentTab">
        <el-tab-pane label="房屋信息" name="roomInfo">
          <div v-loading="roomLoading">
            <el-table :data="roomList" border stripe>
              <el-table-column label="房屋" min-width="180">
                <template slot-scope="{ row }">
                  {{ row.floorNum }}-{{ row.unitNum }}-{{ row.roomNum }}
                </template>
              </el-table-column>
              <el-table-column prop="layer" label="楼层" width="80" align="center" />
              <el-table-column prop="roomSubTypeName" label="房屋类型" min-width="100" />
              <el-table-column prop="ownerName" label="业主" min-width="100" />
              <el-table-column label="建筑/室内面积" min-width="140" align="center">
                <template slot-scope="{ row }">
                  {{ row.builtUpArea }}/{{ row.roomArea }} 平方米
                </template>
              </el-table-column>
              <el-table-column prop="stateName" label="房屋状态" width="100" align="center" />
              <el-table-column prop="startTime" label="交房时间" min-width="150" />
            </el-table>
          </div>
        </el-tab-pane>
        <el-tab-pane label="门锁授权" name="lockPerson">
          <div v-loading="lockPersonLoading">
            <el-table :data="lockPersonList" border stripe>
              <el-table-column prop="personId" label="授权ID" min-width="120" />
              <el-table-column prop="personName" label="姓名" min-width="100" />
              <el-table-column prop="personTel" label="电话" min-width="120" />
              <el-table-column prop="stateName" label="状态" width="100" align="center" />
              <el-table-column prop="createTime" label="创建时间" min-width="160" />
            </el-table>
            <el-pagination
              :current-page="lockPersonPage.page"
              :page-size="lockPersonPage.row"
              :total="lockPersonTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleLockPersonSizeChange"
              @current-change="handleLockPersonPageChange"
              class="pagination-container"
            />
          </div>
        </el-tab-pane>
        <el-tab-pane label="下发日志" name="lockLog">
          <div v-loading="lockLogLoading">
            <el-table :data="lockLogList" border stripe>
              <el-table-column prop="logId" label="日志ID" min-width="120" />
              <el-table-column prop="machineName" label="门锁名称" min-width="120" />
              <el-table-column prop="stateName" label="状态" width="100" align="center" />
              <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
              <el-table-column prop="createTime" label="创建时间" min-width="160" />
            </el-table>
            <el-pagination
              :current-page="lockLogPage.page"
              :page-size="lockLogPage.row"
              :total="lockLogTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleLockLogSizeChange"
              @current-change="handleLockLogPageChange"
              class="pagination-container"
            />
          </div>
        </el-tab-pane>
        <el-tab-pane label="开门记录" name="lockInout">
          <div v-loading="lockInoutLoading">
            <el-table :data="lockInoutList" border stripe>
              <el-table-column prop="inoutId" label="记录ID" min-width="120" />
              <el-table-column prop="machineName" label="门锁名称" min-width="120" />
              <el-table-column prop="personName" label="开门人" min-width="100" />
              <el-table-column prop="stateName" label="状态" width="100" align="center" />
              <el-table-column prop="openTime" label="开门时间" min-width="160" />
              <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
            </el-table>
            <el-pagination
              :current-page="lockInoutPage.page"
              :page-size="lockInoutPage.row"
              :total="lockInoutTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleLockInoutSizeChange"
              @current-change="handleLockInoutPageChange"
              class="pagination-container"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { listLockMachines } from '@/api/lock/lockApi'
import { queryRooms } from '@/api/property/roomApi'
import { listLockPerson, listLockLog, listLockInout } from '@/api/property/lockApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'LockMachineDetail',
  data() {
    return {
      currentTab: 'roomInfo',
      info: {},
      roomLoading: false,
      roomList: [],
      roomTotal: 0,
      roomPage: { page: 1, row: 10 },
      lockPersonLoading: false,
      lockPersonList: [],
      lockPersonTotal: 0,
      lockPersonPage: { page: 1, row: 10 },
      lockLogLoading: false,
      lockLogList: [],
      lockLogTotal: 0,
      lockLogPage: { page: 1, row: 10 },
      lockInoutLoading: false,
      lockInoutList: [],
      lockInoutTotal: 0,
      lockInoutPage: { page: 1, row: 10 }
    }
  },
  computed: {
    communityId() {
      return (getCurrentCommunity() || {}).communityId
    },
    machineId() {
      return this.$route.query.machineId || ''
    },
    roomId() {
      return this.$route.query.roomId || ''
    }
  },
  mounted() {
    this.fetchDetail()
    this.fetchRoomInfo()
  },
  watch: {
    currentTab(val) {
      if (val === 'lockPerson') this.fetchLockPerson()
      else if (val === 'lockLog') this.fetchLockLog()
      else if (val === 'lockInout') this.fetchLockInout()
    }
  },
  methods: {
    async fetchDetail() {
      try {
        const res = await listLockMachines({
          machineId: this.machineId,
          communityId: this.communityId,
          page: 1,
          row: 1
        })
        if (res.data && res.data.length > 0) {
          this.info = res.data[0]
        }
      } catch (e) {
        console.error('获取门锁详情失败', e)
      }
    },
    async fetchRoomInfo() {
      this.roomLoading = true
      try {
        const res = await queryRooms({
          roomId: this.roomId,
          communityId: this.communityId,
          page: this.roomPage.page,
          row: this.roomPage.row
        })
        this.roomList = res.data || []
        this.roomTotal = res.records || 0
      } catch (e) {
        console.error('获取房屋信息失败', e)
      } finally {
        this.roomLoading = false
      }
    },
    async fetchLockPerson() {
      this.lockPersonLoading = true
      try {
        const res = await listLockPerson({
          machineId: this.machineId,
          communityId: this.communityId,
          page: this.lockPersonPage.page,
          row: this.lockPersonPage.row
        })
        this.lockPersonList = res.data || []
        this.lockPersonTotal = res.records || 0
      } catch (e) {
        console.error('获取门锁授权失败', e)
      } finally {
        this.lockPersonLoading = false
      }
    },
    async fetchLockLog() {
      this.lockLogLoading = true
      try {
        const res = await listLockLog({
          machineId: this.machineId,
          communityId: this.communityId,
          page: this.lockLogPage.page,
          row: this.lockLogPage.row
        })
        this.lockLogList = res.data || []
        this.lockLogTotal = res.records || 0
      } catch (e) {
        console.error('获取下发日志失败', e)
      } finally {
        this.lockLogLoading = false
      }
    },
    async fetchLockInout() {
      this.lockInoutLoading = true
      try {
        const res = await listLockInout({
          machineId: this.machineId,
          communityId: this.communityId,
          page: this.lockInoutPage.page,
          row: this.lockInoutPage.row
        })
        this.lockInoutList = res.data || []
        this.lockInoutTotal = res.records || 0
      } catch (e) {
        console.error('获取开门记录失败', e)
      } finally {
        this.lockInoutLoading = false
      }
    },
    handleBack() {
      this.$router.back()
    },
    handleLockPersonSizeChange(val) {
      this.lockPersonPage.row = val
      this.fetchLockPerson()
    },
    handleLockPersonPageChange(val) {
      this.lockPersonPage.page = val
      this.fetchLockPerson()
    },
    handleLockLogSizeChange(val) {
      this.lockLogPage.row = val
      this.fetchLockLog()
    },
    handleLockLogPageChange(val) {
      this.lockLogPage.page = val
      this.fetchLockLog()
    },
    handleLockInoutSizeChange(val) {
      this.lockInoutPage.row = val
      this.fetchLockInout()
    },
    handleLockInoutPageChange(val) {
      this.lockInoutPage.page = val
      this.fetchLockInout()
    }
  }
}
</script>

<style scoped>
.info-card { margin-bottom: 16px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.info-item { padding: 8px 0; font-size: 14px; }
.info-label { color: #909399; }
.info-row { margin-top: 12px; }
.tab-card { margin-bottom: 0; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
