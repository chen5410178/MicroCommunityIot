<template>
  <el-card shadow="never" class="index-card">
    <template #header>
      <span>事件</span>
    </template>
    <div class="event-list" ref="eventList">
      <div v-if="events.length === 0" class="empty-tip">当前没有事件发生</div>
      <div v-for="(item, index) in events" :key="index" class="event-item">
        <div class="event-title">
          {{ item.objTypeName }}({{ item.objectName }}){{ item.eventTypeName }}({{ item.fromType === 'PERSON' ? '人员创建' : '平台创建' }})
          <span v-if="item.eventPoolStaffList && item.eventPoolStaffList.length > 0">
            {{ item.eventPoolStaffList[0].staffName || '暂未分配' }}
          </span>
          <span class="event-state" :class="stateClass(item.state)">
            {{ stateName(item.state) }}
          </span>
        </div>
        <div class="event-time">{{ item.createTime }}</div>
      </div>
    </div>
  </el-card>
</template>

<script>
import { listEvent } from '@/api/index/indexApi'

export default {
  name: 'IndexEvent',
  props: {
    communityId: { type: String, default: '' }
  },
  data() {
    return {
      events: []
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
      listEvent({
        page: 1, row: 10,
        communityId: this.communityId
      }).then(res => {
        if (res && res.data) {
          this.events = Array.isArray(res.data) ? res.data : []
        } else if (Array.isArray(res)) {
          this.events = res
        }
      }).catch(() => {})
    },
    stateName(state) {
      const map = { W: '待处理', D: '处理中', TC: '正常处理完成', NC: '超时处理完成' }
      return map[state] || state
    },
    stateClass(state) {
      const map = { W: 'state-pending', D: 'state-processing', TC: 'state-done', NC: 'state-timeout' }
      return map[state] || ''
    }
  }
}
</script>

<style scoped>
.index-card {
  height: 100%;
}
.event-list {
  max-height: 280px;
  overflow-y: auto;
}
.event-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}
.event-item:last-child {
  border-bottom: none;
}
.event-title {
  flex: 1;
  font-size: 13px;
  color: #333;
  line-height: 1.6;
}
.event-time {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  margin-left: 12px;
}
.event-state {
  display: inline-block;
  padding: 2px 6px;
  border-radius: 2px;
  font-size: 12px;
  margin-left: 6px;
}
.state-pending { color: #faad14; background: #fffbe6; }
.state-processing { color: #1890ff; background: #e6f7ff; }
.state-done { color: #52c41a; background: #f6ffed; }
.state-timeout { color: #f5222d; background: #fff1f0; }
.empty-tip {
  text-align: center;
  color: #999;
  padding: 40px 0;
  font-size: 14px;
}
</style>
