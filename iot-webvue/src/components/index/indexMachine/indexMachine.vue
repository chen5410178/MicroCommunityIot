<template>
  <div class="index-machine-card">
    <div class="index-title">设备信息</div>
    <div class="index-top">
      <div class="index-top-item">
        <div class="index-icon-box"><i class="el-icon-lock"></i></div>
        <div>
          <div class="index-number">{{ data.accessControlCount || 0 }}<span class="index-unit">个</span><span class="offline">（离线 {{ data.accessControlOfflineCount || 0 }}）</span></div>
          <div class="index-label">门禁</div>
        </div>
      </div>
      <div class="index-top-item">
        <div class="index-icon-box"><i class="el-icon-files"></i></div>
        <div>
          <div class="index-number">{{ data.barrierCount || 0 }}<span class="index-unit">个</span><span class="offline">（离线 {{ data.barrierOfflineCount || 0 }}）</span></div>
          <div class="index-label">道闸</div>
        </div>
      </div>
      <div class="index-top-item">
        <div class="index-icon-box"><i class="el-icon-odometer"></i></div>
        <div>
          <div class="index-number">{{ data.chargeCount || 0 }}<span class="index-unit">个</span><span class="offline">（离线 {{ data.chargeOfflineCount || 0 }}）</span></div>
          <div class="index-label">充电桩</div>
        </div>
      </div>
      <div class="index-top-item">
        <div class="index-icon-box"><i class="el-icon-data-analysis"></i></div>
        <div>
          <div class="index-number">{{ data.meterCount || 0 }}<span class="index-unit">个</span><span class="offline">（离线 {{ data.meterOfflineCount || 0 }}）</span></div>
          <div class="index-label">水电表</div>
        </div>
      </div>
    </div>
    <div class="index-bottom">
      <div class="index-bottom-item">
        <div class="index-bottom-left">
          <div class="index-name">电梯</div>
          <div class="index-name-icon"><i class="el-icon-upload2"></i></div>
        </div>
        <div class="index-bottom-right">
          <div class="index-number">{{ data.liftCount || 0 }}<span class="index-unit">个</span><span class="offline">（离线 {{ data.liftOfflineCount || 0 }}）</span></div>
          <div class="index-link" @click="toLift">查看电梯&gt;</div>
        </div>
      </div>
      <div class="index-bottom-item">
        <div class="index-bottom-left">
          <div class="index-name">路灯</div>
          <div class="index-name-icon"><i class="el-icon-sunny"></i></div>
        </div>
        <div class="index-bottom-right">
          <div class="index-number">{{ data.lampCount || 0 }}<span class="index-unit">个</span><span class="offline">（离线 {{ data.lampOfflineCount || 0 }}）</span></div>
          <div class="index-link" @click="toLamp">查看路灯&gt;</div>
        </div>
      </div>
    </div>
  </div>
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
    },
    toLift() {
      this.$router.push('/pages/lift/listLift?tab=电梯管理')
    },
    toLamp() {
      this.$router.push('/pages/lamp/listLamp?tab=路灯管理')
    }
  }
}
</script>

<style scoped>
.index-machine-card {
  background-color: #FFFFFF;
  border-radius: 10px;
  height: 350px;
  padding: 15px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}
.index-title {
  color: #404357;
  font-size: 20px;
  font-weight: bold;
}
.index-top {
  display: flex;
  justify-content: space-around;
  padding: 50px 0;
  border-bottom: 1px solid #f0f0f0;
}
.index-top-item {
  display: flex;
  justify-content: flex-start;
  align-items: center;
}
.index-icon-box {
  background-color: #F6F6F6;
  width: 50px;
  height: 50px;
  border-radius: 5px;
  text-align: center;
  line-height: 60px;
  margin-right: 10px;
}
.index-icon-box i {
  color: #00BF6F;
  font-weight: bold;
  font-size: 24px;
}
.index-number {
  color: #404357;
  font-size: 26px;
  font-weight: bold;
  white-space: nowrap;
}
.index-label {
  color: #9D9D9F;
}
.offline {
  color: #f5222d;
  font-size: 16px;
  font-weight: normal;
}
.index-unit {
  color: #9D9D9F;
  font-size: 14px;
}
.index-bottom {
  width: 90%;
  margin: 40px auto;
  display: flex;
  justify-content: space-between;
}
.index-bottom-item {
  width: 45%;
  display: flex;
  justify-content: space-between;
}
.index-bottom-left {
  text-align: center;
}
.index-bottom-right {
  text-align: center;
}
.index-name {
  color: #404357;
  font-size: 20px;
  font-weight: bold;
}
.index-name-icon {
  font-size: 30px;
  color: #00BF6F;
  font-weight: bold;
  margin-top: 5px;
}
.index-link {
  color: #9D9D9F;
  margin-top: 5px;
  cursor: pointer;
}
</style>
