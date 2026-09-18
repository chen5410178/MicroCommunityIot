<template>
  <div class="lock-machine-detail">
    <el-card shadow="never" class="info-card">
      <div slot="header" class="card-header">
        <span>门锁信息</span>
        <el-button size="small" @click="handleBack">返回</el-button>
      </div>
      <div v-loading="loading">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="info-item"><span class="info-label">门锁ID：</span>{{ info.machineId || '-' }}</div>
          </el-col>
          <el-col :span="6">
            <div class="info-item"><span class="info-label">门锁名称：</span>{{ info.machineName || '-' }}</div>
          </el-col>
          <el-col :span="6">
            <div class="info-item"><span class="info-label">门锁编号：</span>{{ info.machineCode || '-' }}</div>
          </el-col>
          <el-col :span="6">
            <div class="info-item"><span class="info-label">厂家名称：</span>{{ info.factoryName || '-' }}</div>
          </el-col>
        </el-row>
        <el-row :gutter="20" class="info-row">
          <el-col :span="6">
            <div class="info-item"><span class="info-label">剩余电量：</span>{{ info.charge ? info.charge + '%' : '-' }}</div>
          </el-col>
          <el-col :span="6">
            <div class="info-item"><span class="info-label">在线状态：</span>{{ info.stateName || '-' }}</div>
          </el-col>
          <el-col :span="6">
            <div class="info-item"><span class="info-label">心跳时间：</span>{{ info.heartbeatTime || '-' }}</div>
          </el-col>
          <el-col :span="6">
            <div class="info-item"><span class="info-label">创建时间：</span>{{ info.createTime || '-' }}</div>
          </el-col>
        </el-row>
        <el-row
          :gutter="20"
          class="info-row"
          v-if="info.lockMachineParamDtoList && info.lockMachineParamDtoList.length > 0"
        >
          <el-col :span="6" v-for="(item, index) in info.lockMachineParamDtoList" :key="index">
            <div class="info-item"><span class="info-label">{{ item.specName }}：</span>{{ item.value || '-' }}</div>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <el-card shadow="never" class="tab-card">
      <el-tabs v-model="currentTab">
        <el-tab-pane label="房屋信息" name="lockDetailRoom" />
        <el-tab-pane label="门锁授权" name="lockDetailPerson" />
        <el-tab-pane label="下发日志" name="lockDetailLog" />
        <el-tab-pane label="开门记录" name="lockDetailInout" />
      </el-tabs>
      <div class="tab-content">
        <LockDetailRoom v-if="currentTab === 'lockDetailRoom'" :machine-id="machineId" :room-id="roomId" />
        <LockDetailPerson v-else-if="currentTab === 'lockDetailPerson'" :machine-id="machineId" />
        <LockDetailLog v-else-if="currentTab === 'lockDetailLog'" :machine-id="machineId" />
        <LockDetailInout v-else-if="currentTab === 'lockDetailInout'" :machine-id="machineId" />
      </div>
    </el-card>
  </div>
</template>

<script>
import { listLockMachines } from '@/api/lock/lockApi'
import LockDetailRoom from '@/components/lock/LockDetailRoom.vue'
import LockDetailPerson from '@/components/lock/LockDetailPerson.vue'
import LockDetailLog from '@/components/lock/LockDetailLog.vue'
import LockDetailInout from '@/components/lock/LockDetailInout.vue'
import { getCommunityId } from '@/utils/auth'

export default {
  name: 'LockMachineDetail',
  components: { LockDetailRoom, LockDetailPerson, LockDetailLog, LockDetailInout },
  data() {
    return {
      machineId: '',
      roomId: '',
      currentTab: 'lockDetailRoom',
      loading: false,
      info: {}
    }
  },
  mounted() {
    this.machineId = this.$route.query.machineId || ''
    this.roomId = this.$route.query.roomId || ''
    if (this.machineId) {
      this.loadMachineInfo()
    }
  },
  methods: {
    async loadMachineInfo() {
      this.loading = true
      try {
        const res = await listLockMachines({
          machineId: this.machineId,
          communityId: getCommunityId(),
          page: 1,
          row: 1
        })
        if (res.data && res.data.length > 0) {
          this.info = res.data[0]
        }
      } catch (e) {
        this.info = {}
        this.$message.error(e.message || '获取门锁信息失败')
      } finally {
        this.loading = false
      }
    },
    handleBack() {
      this.$router.back()
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
.tab-content { min-height: 200px; }
</style>
