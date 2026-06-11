<template>
  <div class="dashboard" v-if="currentCommunity && currentCommunity.communityId">
    <el-row :gutter="16" class="dashboard-row">
      <el-col :span="12">
        <index-community ref="indexCommunity" :community-id="currentCommunity.communityId" />
      </el-col>
      <el-col :span="12">
        <index-event ref="indexEvent" :community-id="currentCommunity.communityId" />
      </el-col>
    </el-row>
    <el-row :gutter="16" class="dashboard-row">
      <el-col :span="12">
        <index-machine ref="indexMachine" :community-id="currentCommunity.communityId" />
      </el-col>
      <el-col :span="12">
        <index-monitor-alarm ref="indexMonitorAlarm" :community-id="currentCommunity.communityId" />
      </el-col>
    </el-row>
  </div>
  <div v-else class="dashboard-empty">
    <el-empty description="请先选择小区" />
  </div>
</template>

<script>
import IndexCommunity from '@/components/index/indexCommunity/indexCommunity.vue'
import IndexEvent from '@/components/index/indexEvent/indexEvent.vue'
import IndexMachine from '@/components/index/indexMachine/indexMachine.vue'
import IndexMonitorAlarm from '@/components/index/indexMonitorAlarm/indexMonitorAlarm.vue'
import { mapState } from 'vuex'

export default {
  name: 'PropertyDashboard',
  components: { IndexCommunity, IndexEvent, IndexMachine, IndexMonitorAlarm },
  computed: {
    ...mapState('app', ['currentCommunity'])
  }
}
</script>

<style scoped>
.dashboard {
  padding: 0;
}
.dashboard-row {
  margin-bottom: 16px;
}
.dashboard-row:last-child {
  margin-bottom: 0;
}
.dashboard-empty {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}
</style>
