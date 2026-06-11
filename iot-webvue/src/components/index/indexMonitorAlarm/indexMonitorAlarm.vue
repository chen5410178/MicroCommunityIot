<template>
  <div class="index-alarm-card">
    <div class="index-title">监控告警</div>
    <ul ref="alarmList" class="alarm-list">
      <li v-if="alarms.length === 0" class="flex justify-between padding-top-sm">
        <div class="alarm-notice-title">当前没有告警</div>
        <div class="alarm-notice-time"></div>
      </li>
      <li v-for="(item, index) in alarms" :key="index" class="flex justify-between padding-top-sm">
        <div class="alarm-notice-title">
          {{ item.machineName }}({{ item.maName }}-{{ item.locationName }})发生{{ item.monitorTypeName }}
        </div>
        <div class="alarm-notice-time">{{ item.createTime }}</div>
      </li>
    </ul>
  </div>
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
.index-alarm-card {
  background-color: #FFFFFF;
  border-radius: 10px;
  height: 350px;
  padding: 15px;
  overflow-y: hidden;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}
.index-title {
  color: #404357;
  font-size: 20px;
  font-weight: bold;
}
.alarm-list {
  overflow-y: scroll;
  height: calc(100% - 30px);
  padding: 0;
  margin: 0;
  list-style: none;
}
.alarm-list li {
  list-style: none;
}
.alarm-notice-title {
  font-size: 14px;
  color: #544E42;
  width: 60%;
}
.alarm-notice-time {
  font-size: 14px;
  color: #9D9D9F;
}
.padding-top-sm {
  padding-top: 10px;
}
.flex {
  display: flex;
}
.justify-between {
  justify-content: space-between;
}
</style>
