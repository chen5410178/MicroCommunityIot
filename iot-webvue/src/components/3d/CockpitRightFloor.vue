<template>
  <div class="view-box">
    <div class="vb-c padding">
      <div class="r-title flex justify-between">
        <span>楼栋信息</span>
        <span></span>
      </div>
      <div class="r-body flex justify-between margin-top-sm">
        <div>
          <div>编号</div>
          <div style="color: #c17e14;">{{ info.floorNum }}</div>
        </div>
        <div>
          <div>名称</div>
          <div style="color: #c17e14;">{{ info.floorName }}</div>
        </div>
        <div>
          <div>面积</div>
          <div style="color: #c17e14;">{{ info.floorArea }}</div>
        </div>
      </div>
      <div class="r-body flex justify-between margin-top-sm">
        <div>
          <div>房屋数</div>
          <div style="color: #c17e14;">{{ info.roomCount }}</div>
        </div>
        <div>
          <div>空闲房屋数</div>
          <div style="color: #c17e14;">{{ info.freeRoomCount }}</div>
        </div>
        <div>
          <div>备注</div>
          <div style="color: #c17e14;">{{ info.remark || '无' }}</div>
        </div>
      </div>
      <div class="r-title flex justify-between margin-top">
        <span>楼层</span>
        <span></span>
      </div>
      <div class="flex justify-between flex-wrap text-center margin-top-sm">
        <div class="margin-bottom lift-item" v-for="index in info.layer" :key="index">
          <div @click="_changeLayer(index)" class="hand">
            <div v-if="index == info.curLayer">
              <img src="/img/3d/lift_active.png" width="30px">
            </div>
            <div v-else>
              <img src="/img/3d/lift_default.png" width="30px">
            </div>
            <div>{{ index }}F</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { queryFloors } from '@/api/3d/cockpitApi'
import { eventBus } from '@/utils/three/CockpitCore'

export default {
  name: 'CockpitRightFloor',
  data() {
    return {
      info: {
        floorId: '',
        layer: 0,
        curLayer: '',
        floorName: '',
        floorNum: '',
        floorArea: '',
        roomCount: '',
        freeRoomCount: '',
        remark: ''
      }
    }
  },
  mounted() {
    eventBus.$on('cockpitRightFLoor.notify', (data) => {
      this.info.floorId = data.objId
      this._loadFloor()
    })
  },
  beforeDestroy() {
    eventBus.$off('cockpitRightFLoor.notify')
  },
  methods: {
    _changeLayer(_layer) {
      this.info.curLayer = _layer
      eventBus.$emit('cockpitCore.loadRoomModal', { floorId: this.info.floorId, layer: _layer })
    },
    async _loadFloor() {
      try {
        const res = await queryFloors({
          page: 1,
          row: 1,
          communityId: this.$store.state.app.currentCommunity.communityId,
          floorId: this.info.floorId
        })
        if (res.data && res.data[0]) {
          Object.assign(this.info, res.data[0])
        }
      } catch (err) {
        console.error('加载楼栋信息失败', err)
      }
    }
  }
}
</script>

<style scoped>
.lift-item { width: 25%; }
.hand { cursor: pointer; }
</style>
