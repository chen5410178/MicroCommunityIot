<template>
  <el-card shadow="never" class="index-card">
    <template #header>
      <span>监控告警</span>
    </template>
    <div class="alarm-list" ref="alarmList">
      <div v-if="alarms.length === 0" class="empty-tip">当前没有告警</div>
      <div v-for="(item, index) in alarms" :key="index" class="alarm-item">
        <div class="alarm-title">
          {{ item.machineName }}({{ item.maName }}-{{ item.locationName }})发生{{ item.monitorTypeName }}
        </div>
        <div class="alarm-time">{{ item.createTime }}</div>
      </div>
    </div>
  </el-card>
</template>

<script>
import { listMonitorAlarm } from '@/api/index/indexApi'

export default {
  name: 'IndexMonitorAlarm',
  props: {
    communityId: { type: String, default: '' }
  },
  data() {
    return {
      alarms: []
    }
  },
  watch: {
    communityId: {
      immediate: true,
      handler(val) {
        if (val) this.loadData()
      }
    }
  },
  methods: {
    loadData() {
      listMonitorAlarm({
        page: 1, row: 10,
        communityId: this.communityId
      }).then(res => {
        if (res && res.data) {
          this.alarms = Array.isArray(res.data) ? res.data : []
        } else if (Array.isArray(res)) {
          this.alarms = res
        }
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.index-card {
  height: 100%;
}
.alarm-list {
  max-height: 280px;
  overflow-y: auto;
}
.alarm-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}
.alarm-item:last-child {
  border-bottom: none;
}
.alarm-title {
  flex: 1;
  font-size: 13px;
  color: #f5222d;
  line-height: 1.6;
}
.alarm-time {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  margin-left: 12px;
}
.empty-tip {
  text-align: center;
  color: #999;
  padding: 40px 0;
  font-size: 14px;
}
</style>
