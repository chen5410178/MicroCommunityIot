<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="处理事件"
    width="700px"
    @close="handleClose"
  >
    <el-form label-width="110px">
      <el-form-item label="事件类型">
        <el-input v-model="event.eventTypeName" disabled />
      </el-form-item>
      <el-form-item label="事件描述">
        <el-input v-model="event.eventMsg" type="textarea" :rows="3" disabled />
      </el-form-item>
      <el-form-item label="对象类型">
        <el-input v-model="event.objTypeName" disabled />
      </el-form-item>
      <el-form-item label="对象名称">
        <el-input v-model="event.objectName" disabled />
      </el-form-item>
      <el-form-item label="事故原因">
        <el-input v-model="eventPoolStaff.eventReason" type="textarea" :rows="3" disabled />
      </el-form-item>
      <el-form-item label="处理方案">
        <el-input v-model="eventPoolStaff.eventResult" type="textarea" :rows="3" disabled />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">返回</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { listEventPool } from '@/api/event/eventApi'

export default {
  name: 'EventDetail',
  props: {
    visible: { type: Boolean, default: false },
    detailData: { type: Object, default: () => ({}) }
  },
  data() {
    return {
      dialogVisible: false,
      event: {},
      eventPoolStaff: {}
    }
  },
  watch: {
    visible(val) { this.dialogVisible = val },
    dialogVisible(val) { this.$emit('update:visible', val) },
    detailData: {
      handler(val) {
        if (val && val.epsId) {
          this.eventPoolStaff = val
          this.queryEvent(val.eventId)
        }
      },
      immediate: true
    }
  },
  methods: {
    async queryEvent(eventId) {
      if (!eventId) return
      try {
        const res = await listEventPool({ page: -1, row: 1, eventId })
        if (res.data && res.data.length > 0) {
          this.event = res.data[0]
        }
      } catch (e) {
        console.error('查询事件详情失败', e)
      }
    },
    handleClose() {
      this.dialogVisible = false
      this.event = {}
      this.eventPoolStaff = {}
    }
  }
}
</script>
