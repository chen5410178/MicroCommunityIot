<template>
  <el-card shadow="never" class="index-card">
    <template #header>
      <span>设备信息</span>
    </template>
    <div class="index-grid">
      <div class="index-item">
        <div class="index-icon"><i class="el-icon-lock"></i></div>
        <div class="index-number">{{ data.accessControlCount || 0 }}
          <span class="index-unit">个</span>
          <span class="offline">（离线 {{ data.accessControlOfflineCount || 0 }}）</span>
        </div>
        <div class="index-label">门禁</div>
      </div>
      <div class="index-item">
        <div class="index-icon"><i class="el-icon-files"></i></div>
        <div class="index-number">{{ data.barrierCount || 0 }}
          <span class="index-unit">个</span>
          <span class="offline">（离线 {{ data.barrierOfflineCount || 0 }}）</span>
        </div>
        <div class="index-label">道闸</div>
      </div>
      <div class="index-item">
        <div class="index-icon"><i class="el-icon-odometer"></i></div>
        <div class="index-number">{{ data.chargeCount || 0 }}
          <span class="index-unit">个</span>
          <span class="offline">（离线 {{ data.chargeOfflineCount || 0 }}）</span>
        </div>
        <div class="index-label">充电桩</div>
      </div>
      <div class="index-item">
        <div class="index-icon"><i class="el-icon-data-analysis"></i></div>
        <div class="index-number">{{ data.meterCount || 0 }}
          <span class="index-unit">个</span>
          <span class="offline">（离线 {{ data.meterOfflineCount || 0 }}）</span>
        </div>
        <div class="index-label">水电表</div>
      </div>
    </div>
    <div class="index-bottom">
      <div class="index-bottom-item">
        <div class="index-sub-icon"><i class="el-icon-upload2"></i></div>
        <div>
          <div class="index-number">{{ data.liftCount || 0 }}
            <span class="index-unit">个</span>
            <span class="offline">（离线 {{ data.liftOfflineCount || 0 }}）</span>
          </div>
          <div class="index-link">电梯</div>
        </div>
      </div>
      <div class="index-bottom-item">
        <div class="index-sub-icon"><i class="el-icon-sunny"></i></div>
        <div>
          <div class="index-number">{{ data.lampCount || 0 }}
            <span class="index-unit">个</span>
            <span class="offline">（离线 {{ data.lampOfflineCount || 0 }}）</span>
          </div>
          <div class="index-link">路灯</div>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script>
import { queryMachineIndex } from '@/api/index/indexApi'

export default {
  name: 'IndexMachine',
  props: {
    communityId: { type: String, default: '' }
  },
  data() {
    return {
      data: {}
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
      queryMachineIndex({
        page: 1, row: 10,
        communityId: this.communityId
      }).then(res => {
        if (res && res.data) {
          this.data = res.data
        } else {
          this.data = res || {}
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
.index-grid {
  display: flex;
  justify-content: space-around;
  padding: 8px 0 16px;
  border-bottom: 1px solid #f0f0f0;
  flex-wrap: wrap;
}
.index-item {
  text-align: center;
  flex: 1;
  min-width: 100px;
}
.index-icon {
  font-size: 28px;
  color: #1890ff;
  margin-bottom: 4px;
}
.index-number {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}
.offline {
  font-size: 12px;
  color: #f5222d;
  font-weight: normal;
}
.index-label {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}
.index-unit {
  font-size: 13px;
  font-weight: normal;
  color: #999;
}
.index-bottom {
  display: flex;
  padding-top: 16px;
  gap: 16px;
}
.index-bottom-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #fafafa;
  border-radius: 4px;
}
.index-sub-icon {
  font-size: 24px;
  color: #1890ff;
}
.index-link {
  font-size: 13px;
  color: #1890ff;
  cursor: pointer;
  margin-top: 4px;
}
</style>
