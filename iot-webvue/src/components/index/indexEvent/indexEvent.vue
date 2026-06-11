<template>
  <div class="index-event-card">
    <div class="index-title">事件</div>
    <ul ref="eventList" class="event-list">
      <li v-if="events.length === 0" class="flex justify-between padding-top-sm">
        <div class="event-notice-title">当前没有事件发生</div>
        <div class="event-notice-time"></div>
      </li>
      <li v-for="(item, index) in events" :key="index" class="flex justify-between padding-top-sm">
        <div class="event-notice-title">
          {{ item.objTypeName }}({{ item.objectName }}){{ item.eventTypeName }}({{ item.fromType === 'PERSON' ? '人员创建' : '平台创建' }})
          <span v-if="item.eventPoolStaffList && item.eventPoolStaffList.length > 0">
            {{ item.eventPoolStaffList[0].staffName || '暂未分配' }}
          </span>
          (<span v-if="item.state === 'W'">待处理</span>
          <span v-else-if="item.state === 'D'">处理中</span>
          <span v-else-if="item.state === 'TC'">正常处理完成</span>
          <span v-else-if="item.state === 'NC'">超时处理完成</span>)
        </div>
        <div class="event-notice-time">{{ item.createTime }}</div>
      </li>
    </ul>
  </div>
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
    }
  }
}
</script>

<style scoped>
.index-event-card {
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
.event-list {
  overflow-y: scroll;
  height: calc(100% - 30px);
  padding: 0;
  margin: 0;
  list-style: none;
}
.event-list li {
  list-style: none;
}
.event-notice-title {
  font-size: 14px;
  color: #544E42;
  width: 60%;
}
.event-notice-time {
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
