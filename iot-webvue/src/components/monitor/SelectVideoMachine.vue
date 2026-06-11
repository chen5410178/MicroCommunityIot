<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="选择摄像头"
    width="700px"
    @close="handleClose"
  >
    <el-row :gutter="16">
      <el-col :span="10">
        <div class="panel-title">监控区域</div>
        <ul class="area-list">
          <li
            v-for="item in areas"
            :key="item.maId"
            :class="{ active: currentAreaId === item.maId }"
            @click="handleAreaClick(item)"
          >{{ item.maName }}</li>
          <li v-if="areas.length === 0" class="empty">暂无区域</li>
        </ul>
      </el-col>
      <el-col :span="14">
        <div class="panel-title">摄像头</div>
        <ul class="machine-list">
          <li
            v-for="item in machines"
            :key="item.machineId"
            @click="handleMachineClick(item)"
          >{{ item.machineName }}</li>
          <li v-if="machines.length === 0" class="empty">暂无摄像头</li>
        </ul>
      </el-col>
    </el-row>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { listMonitorAreas, listMonitorMachines } from '@/api/monitor/monitorApi'

export default {
  name: 'SelectVideoMachine',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      areas: [],
      machines: [],
      currentAreaId: ''
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.loadAreas()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadAreas() {
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listMonitorAreas({ page: 1, row: 100, communityId })
        if (res.code === 0) {
          this.areas = res.data || []
          if (this.areas.length > 0) {
            this.handleAreaClick(this.areas[0])
          }
        }
      } catch (e) { /* ignore */ }
    },
    async loadMachines() {
      try {
        const communityId = this.$store.state.app.currentCommunity.communityId || ''
        const res = await listMonitorMachines({ page: 1, row: 100, communityId, maId: this.currentAreaId })
        if (res.code === 0) {
          this.machines = res.data || []
        }
      } catch (e) { /* ignore */ }
    },
    handleAreaClick(item) {
      this.currentAreaId = item.maId
      this.loadMachines()
    },
    handleMachineClick(item) {
      this.$emit('selected', item)
      this.handleClose()
    },
    handleClose() {
      this.dialogVisible = false
      this.areas = []
      this.machines = []
      this.currentAreaId = ''
    }
  }
}
</script>

<style scoped>
.panel-title { font-size: 14px; font-weight: 600; padding: 8px 12px; background: #f5f7fa; border-bottom: 1px solid #ebeef5; }
.area-list, .machine-list { list-style: none; padding: 0; margin: 0; max-height: 360px; overflow-y: auto; }
.area-list li, .machine-list li { padding: 10px 12px; cursor: pointer; border-bottom: 1px solid #f0f0f0; transition: background 0.2s; }
.area-list li:hover, .machine-list li:hover { background: #ecf5ff; }
.area-list li.active { background: #409eff; color: #fff; }
.area-list li.empty, .machine-list li.empty { color: #999; cursor: default; text-align: center; padding: 40px 0; }
.area-list li.empty:hover, .machine-list li.empty:hover { background: transparent; }
</style>
