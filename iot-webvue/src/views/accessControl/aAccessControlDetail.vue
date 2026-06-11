<template>
  <div class="a-access-control-detail">
    <el-card shadow="never" class="info-card">
      <div slot="header" class="card-header">
        <span>门禁详情</span>
        <el-button size="small" @click="handleBack">返回</el-button>
      </div>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="info-item"><span class="info-label">门禁ID：</span>{{ info.machineId }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">门禁编码：</span>{{ info.machineCode }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">门禁名称：</span>{{ info.machineName }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">厂家名称：</span>{{ info.implBeanName }}</div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="info-row">
        <el-col :span="6">
          <div class="info-item"><span class="info-label">IP：</span>{{ info.machineIp || '-' }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">Mac：</span>{{ info.machineMac || '-' }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">方向：</span>{{ info.direction == '3306' ? '进' : '出' }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">位置：</span>{{ info.locationName }}</div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="info-row">
        <el-col :span="6">
          <div class="info-item"><span class="info-label">是否在线：</span>{{ info.stateName }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">心跳时间：</span>{{ info.heartbeatTime }}</div>
        </el-col>
        <el-col :span="6">
          <div class="info-item"><span class="info-label">添加时间：</span>{{ info.createTime }}</div>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="never" class="tab-card">
      <el-tabs v-model="currentTab" @tab-click="handleTabClick">
        <el-tab-pane label="门禁人员" name="aAccessControlDetailFace">
          <AccessControlDetailFace :machine-id="machineId" ref="faceRef" />
        </el-tab-pane>
        <el-tab-pane label="楼栋授权" name="aAccessControlDetailFloor">
          <AccessControlDetailFloor :machine-id="machineId" ref="floorRef" />
        </el-tab-pane>
        <el-tab-pane label="部门授权" name="aAccessControlDetailOrg">
          <AccessControlDetailOrg :machine-id="machineId" ref="orgRef" />
        </el-tab-pane>
        <el-tab-pane label="下发日志" name="aAccessControlDetailLog">
          <AccessControlDetailLog :machine-id="machineId" ref="logRef" />
        </el-tab-pane>
        <el-tab-pane label="进出记录" name="aAccessControlDetailInout">
          <AccessControlDetailInout :machine-id="machineId" ref="inoutRef" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
import { listAdminAccessControl } from '@/api/accessControl/accessControlApi'
import AccessControlDetailFace from '@/components/accessControl/AccessControlDetailFace.vue'
import AccessControlDetailFloor from '@/components/accessControl/AccessControlDetailFloor.vue'
import AccessControlDetailOrg from '@/components/accessControl/AccessControlDetailOrg.vue'
import AccessControlDetailLog from '@/components/accessControl/AccessControlDetailLog.vue'
import AccessControlDetailInout from '@/components/accessControl/AccessControlDetailInout.vue'

export default {
  name: 'AAccessControlDetail',
  components: {
    AccessControlDetailFace,
    AccessControlDetailFloor,
    AccessControlDetailOrg,
    AccessControlDetailLog,
    AccessControlDetailInout
  },
  data() {
    return {
      machineId: '',
      currentTab: 'aAccessControlDetailFace',
      info: {}
    }
  },
  mounted() {
    this.machineId = this.$route.query.machineId || ''
    if (this.machineId) {
      this.loadMachineInfo()
    }
  },
  methods: {
    async loadMachineInfo() {
      try {
        const res = await listAdminAccessControl({
          machineId: this.machineId,
          page: 1,
          row: 1
        })
        if (res.data && res.data.length > 0) {
          this.info = res.data[0]
        }
      } catch (e) {
        console.error('获取门禁信息失败', e)
      }
    },
    handleTabClick() {},
    handleBack() {
      this.$router.go(-1)
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
</style>
