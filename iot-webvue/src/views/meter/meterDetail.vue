<template>
  <div class="meter-detail">
    <el-card shadow="never" class="info-card">
      <div slot="header" class="card-header">
        <span>水电表信息</span>
        <el-button size="small" @click="handleBack">返回</el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="info-item"><span class="info-label">水电表ID：</span>{{ info.machineId }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">水电表名称：</span>{{ info.machineName }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">水电表号：</span>{{ info.address }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">厂家名称：</span>{{ info.implBeanName }}</div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="info-row">
        <el-col :span="6">
          <div class="info-item"><span class="info-label">水电表模式：</span>{{ info.machineModelName }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">水电表类型：</span>{{ info.meterTypeName }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">房间：</span>{{ info.roomName }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">在线状态：</span>{{ info.stateName }}</div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="info-row">
        <el-col :span="6">
          <div class="info-item"><span class="info-label">本期读数：</span>{{ info.curDegrees }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">本期读数时间：</span>{{ info.curReadingTime }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">预存剩余读数：</span>{{ info.prestoreDegrees }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">心跳时间：</span>{{ info.heartbeatTime }}</div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="info-row">
        <el-col :span="6">
          <div class="info-item"><span class="info-label">每月抄表日：</span>{{ info.readDay }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">抄表小时：</span>{{ info.readHours }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">添加时间：</span>{{ info.createTime }}</div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="info-row" v-if="info.specs && info.specs.length > 0">
        <el-col :span="6" v-for="(item, index) in info.specs" :key="index">
          <div class="info-item"><span class="info-label">{{ item.specName }}：</span>{{ item.specValue }}</div>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="never" class="tab-card">
      <el-tabs v-model="currentTab">
        <el-tab-pane label="房屋详情" name="roomDetail">
          <div v-loading="roomLoading">
            <el-table :data="roomList" border stripe>
              <el-table-column prop="roomId" label="房屋ID" width="100" align="center" />
              <el-table-column label="名称" min-width="180">
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
            <el-pagination
              :current-page="roomPage.page"
              :page-size="roomPage.row"
              :total="roomTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleRoomSizeChange"
              @current-change="handleRoomPageChange"
              class="pagination-container"
            />
          </div>
        </el-tab-pane>
        <el-tab-pane label="抄表记录" name="readRecord">
          <div v-loading="readLoading">
            <el-table :data="readList" border stripe>
              <el-table-column prop="roomName" label="房屋" min-width="120" />
              <el-table-column prop="typeName" label="表类型" min-width="100" />
              <el-table-column prop="machineName" label="表名称" min-width="120" />
              <el-table-column prop="address" label="表号" min-width="120" />
              <el-table-column prop="curDegrees" label="抄表读数" min-width="100" align="center" />
              <el-table-column prop="curReadingTime" label="读表时间" min-width="150" />
              <el-table-column prop="remark" label="说明" min-width="150" />
            </el-table>
            <el-pagination
              :current-page="readPage.page"
              :page-size="readPage.row"
              :total="readTotal"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleReadSizeChange"
              @current-change="handleReadPageChange"
              class="pagination-container"
            />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { listMeterMachines, listMeterMachineCharges } from '@/api/meter/meterApi'
import { queryAdminRooms } from '@/api/meter/meterApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'MeterDetail',
  data() {
    return {
      machineId: '',
      roomId: '',
      currentTab: 'roomDetail',
      info: {},
      roomLoading: false,
      roomList: [],
      roomTotal: 0,
      roomPage: { page: 1, row: 10 },
      readLoading: false,
      readList: [],
      readTotal: 0,
      readPage: { page: 1, row: 10 }
    }
  },
  mounted() {
    this.machineId = this.$route.query.machineId || ''
    this.roomId = this.$route.query.roomId || ''
    if (!this.machineId) {
      this.$router.go(-1)
      return
    }
    this.loadMachineInfo()
    this.loadRoomData()
  },
  methods: {
    async loadMachineInfo() {
      try {
        const community = getCurrentCommunity()
        const res = await listMeterMachines({
          machineId: this.machineId,
          communityId: community.communityId,
          page: 1,
          row: 10
        })
        if (res.data && res.data.length > 0) {
          this.info = res.data[0]
          this.info.machineModelName = this.info.machineModel == '1001' ? '充值模式' : '抄表模式'
        }
      } catch (e) {
        console.error('加载水电表信息失败', e)
      }
    },
    async loadRoomData() {
      if (!this.roomId) return
      this.roomLoading = true
      try {
        const community = getCurrentCommunity()
        const res = await queryAdminRooms({
          roomId: this.roomId,
          communityId: community.communityId,
          page: this.roomPage.page,
          row: this.roomPage.row
        })
        this.roomList = res.data || []
        this.roomTotal = res.records || 0
      } catch (e) {
        console.error('加载房屋信息失败', e)
      } finally {
        this.roomLoading = false
      }
    },
    async loadReadData() {
      this.readLoading = true
      try {
        const community = getCurrentCommunity()
        const res = await listMeterMachineCharges({
          machineId: this.machineId,
          communityId: community.communityId,
          detailType: '2002',
          page: this.readPage.page,
          row: this.readPage.row
        })
        this.readList = res.data || []
        this.readTotal = res.records || 0
      } catch (e) {
        console.error('加载抄表记录失败', e)
      } finally {
        this.readLoading = false
      }
    },
    handleRoomSizeChange(val) {
      this.roomPage.row = val
      this.loadRoomData()
    },
    handleRoomPageChange(val) {
      this.roomPage.page = val
      this.loadRoomData()
    },
    handleReadSizeChange(val) {
      this.readPage.row = val
      this.loadReadData()
    },
    handleReadPageChange(val) {
      this.readPage.page = val
      this.loadReadData()
    },
    handleBack() {
      this.$router.go(-1)
    }
  },
  watch: {
    currentTab(val) {
      if (val === 'readRecord' && this.readList.length === 0) {
        this.loadReadData()
      }
    }
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
