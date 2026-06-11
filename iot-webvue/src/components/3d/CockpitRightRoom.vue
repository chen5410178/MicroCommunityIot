<template>
  <div class="flex justify-start">
    <div class="flex justify-start flex-wrap lift" style="width: 60px;height: 90vh;overflow-y: auto;">
      <div class="margin-bottom text-center hand" style="width: 50%;" v-for="index in info.allLayer" :key="index">
        <div @click="_changeRoomLayer(index)">
          <div v-if="index == info.curLayer"><img src="/img/3d/lift_active.png" width="30px"></div>
          <div v-else><img src="/img/3d/lift_default.png" width="30px"></div>
          <div>{{ index }}F</div>
        </div>
      </div>
    </div>
    <div class="view-box" style="width: 20rem;">
      <div class="vb-c padding">
        <div></div>
        <div>
          <div class="r-title flex justify-between">
            <span>房屋信息</span>
            <span></span>
          </div>
          <div class="r-body flex justify-between margin-top-sm">
            <div>
              <div>编号</div>
              <div style="color: #c17e14;">{{ info.roomName }}</div>
            </div>
            <div>
              <div>楼层</div>
              <div style="color: #c17e14;">{{ info.layer }}</div>
            </div>
            <div>
              <div>房屋类型</div>
              <div style="color: #c17e14;">{{ info.roomSubTypeName }}</div>
            </div>
          </div>
          <div class="r-body flex justify-between margin-top-sm">
            <div>
              <div>建筑面积</div>
              <div style="color: #c17e14;">{{ info.builtUpArea }}</div>
            </div>
            <div>
              <div>室内面积</div>
              <div style="color: #c17e14;">{{ info.roomArea }}</div>
            </div>
            <div>
              <div>房屋状态</div>
              <div style="color: #c17e14;">{{ info.stateName }}</div>
            </div>
          </div>
          <div class="r-body flex justify-between margin-top-sm">
            <div>
              <div>交房时间</div>
              <div style="color: #c17e14;">{{ info.startTime }}</div>
            </div>
            <div>
              <div>业主</div>
              <div style="color: #c17e14;">{{ info.ownerName }}</div>
            </div>
            <div>
              <div>人员数量</div>
              <div style="color: #c17e14;">{{ info.memberCount }}</div>
            </div>
          </div>
          <div class="r-body flex justify-between margin-top-sm">
            <div>
              <div>说明</div>
              <div style="color: #c17e14;">{{ info.remark || '无' }}</div>
            </div>
            <div class="text-center">
              <div>房屋ID</div>
              <div style="color: #c17e14;">{{ info.roomId }}</div>
            </div>
            <div></div>
          </div>
        </div>
        <div v-if="info.ownerId">
          <div class="r-title flex justify-between margin-top">
            <span>人员信息</span>
            <span></span>
          </div>
          <div class="r-body flex justify-between margin-top-sm">
            <div>
              <div>名称</div>
              <div style="color: #c17e14;">{{ info.name }}</div>
            </div>
            <div>
              <div>电话</div>
              <div style="color: #c17e14;">{{ info.link }}</div>
            </div>
            <div>
              <div>性别</div>
              <div style="color: #c17e14;">{{ info.sex == 0 ? '男' : '女' }}</div>
            </div>
          </div>
          <div class="r-body flex justify-between margin-top-sm">
            <div>
              <div>身份证</div>
              <div style="color: #c17e14;">{{ info.idCard }}</div>
            </div>
            <div>
              <div>家庭住址</div>
              <div style="color: #c17e14;">{{ info.address }}</div>
            </div>
            <div>
              <div>人员类型</div>
              <div style="color: #c17e14;">{{ info.ownerTypeName }}</div>
            </div>
          </div>
          <div class="r-body flex justify-between margin-top-sm">
            <div>
              <div>人员ID</div>
              <div style="color: #c17e14;">{{ info.ownerId }}</div>
            </div>
          </div>
        </div>
        <div v-if="info.cars && info.cars.length > 0">
          <div class="r-title flex justify-between margin-top">
            <span>房屋车辆</span>
            <span></span>
          </div>
          <div class="r-body flex justify-between margin-top-sm" v-for="(item, index) in info.cars" :key="index">
            <div>
              <div>车牌号</div>
              <div style="color: #c17e14;">{{ item.carNum }}</div>
            </div>
            <div>
              <div>车位</div>
              <div style="color: #c17e14;">{{ item.num }}</div>
            </div>
            <div>
              <div>有效期</div>
              <div style="color: #c17e14;">
                {{ item.startTime }}<br>
                {{ item.endTime }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { queryRooms, queryOwners, queryOwnerCars } from '@/api/3d/cockpitApi'
import { eventBus } from '@/utils/three/CockpitCore'

const emptyInfo = {
  roomId: '',
  roomName: '',
  layer: '',
  roomSubTypeName: '',
  builtUpArea: '',
  roomArea: '',
  stateName: '',
  startTime: '',
  roomAttrDtos: [],
  url: '',
  ownerName: '',
  memberCount: 0,
  remark: '',
  ownerId: '',
  name: '',
  link: '',
  sex: '',
  idCard: '',
  address: '',
  ownerTypeName: '',
  cars: [],
  floorId: '',
  allLayer: 1,
  curLayer: 1
}

export default {
  name: 'CockpitRightRoom',
  data() {
    return {
      info: { ...emptyInfo }
    }
  },
  mounted() {
    eventBus.$on('cockpitRightRoom.notify', (data) => {
      this.clearInfo()
      this.info.roomId = data.roomId
      this.info.floorId = data.floorId
      this.info.curLayer = data.layer
      this._loadRoom()
      this._loadRoomFloor()
    })
  },
  beforeDestroy() {
    eventBus.$off('cockpitRightRoom.notify')
  },
  methods: {
    clearInfo() {
      this.info = { ...emptyInfo }
    },
    async _loadRoom() {
      try {
        const res = await queryRooms({
          page: 1,
          row: 1,
          communityId: this.$store.state.app.currentCommunity.communityId,
          roomId: this.info.roomId
        })
        if (res.data && res.data[0]) {
          Object.assign(this.info, res.data[0])
          if (res.data[0].ownerId) {
            this._loadOwner()
            this._loadCars()
          }
        }
      } catch (err) {
        console.error('加载房屋信息失败', err)
      }
    },
    async _loadOwner() {
      try {
        const res = await queryOwners({
          page: 1,
          row: 1,
          communityId: this.$store.state.app.currentCommunity.communityId,
          ownerId: this.info.ownerId
        })
        if (res.data && res.data[0]) {
          Object.assign(this.info, res.data[0])
        }
      } catch (err) {
        console.error('加载业主信息失败', err)
      }
    },
    async _loadCars() {
      try {
        const res = await queryOwnerCars({
          page: 1,
          row: 10,
          communityId: this.$store.state.app.currentCommunity.communityId,
          ownerId: this.info.ownerId
        })
        this.info.cars = res.data || []
      } catch (err) {
        console.error('加载车辆信息失败', err)
      }
    },
    async _loadRoomFloor() {
      try {
        const res = await queryRooms({
          page: 1,
          row: 1,
          communityId: this.$store.state.app.currentCommunity.communityId,
          floorId: this.info.floorId
        })
        if (res.data && res.data[0]) {
          this.info.allLayer = res.data[0].layer || 1
        }
      } catch (err) {
        console.error('加载楼栋信息失败', err)
      }
    },
    _changeRoomLayer(_curLayer) {
      this.info.curLayer = _curLayer
      eventBus.$emit('cockpitCore.loadRoomModal', { floorId: this.info.floorId, layer: _curLayer })
    }
  }
}
</script>

<style scoped>
.hand { cursor: pointer; }
.lift::-webkit-scrollbar { display: none; }
</style>
