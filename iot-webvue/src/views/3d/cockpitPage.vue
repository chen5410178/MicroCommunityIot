<template>
  <div class="cockpit-wrapper">
    <div class="cockpit-header">
      数字物联驾驶舱
    </div>
    <div id="webgl-scene" class="webgl-scene"></div>
    <div class="cockpit-left">
      <div class="cockpit-menu">
        <div
          class="c-menu-item"
          :class="{ active: item.sceneId == sceneId }"
          v-for="(item, index) in sceneList"
          :key="index"
        >
          <div class="title flex justify-start" @click="_changeScene(item)">
            <div class="bg" :class="{ 'bg-a': item.sceneId == sceneId }">
              <span class="fa fa-globe"></span>
            </div>
            <div class="t-text">{{ item.sceneName }}</div>
          </div>
        </div>
      </div>
    </div>
    <div class="cockpit-right">
      <div v-if="curType == 'floor'">
        <CockpitRightFloor />
      </div>
      <div v-if="curType == 'room'">
        <CockpitRightRoom />
      </div>
    </div>
    <div class="cockpit-bottom"></div>
  </div>
</template>

<script>
import Cockpit3d from '@/utils/three/Cockpit3d'
import { initEvent, computeSceneObject, eventBus } from '@/utils/three/CockpitCore'
import { listDtScenes } from '@/api/3d/cockpitApi'
import CockpitRightFloor from '@/components/3d/CockpitRightFloor.vue'
import CockpitRightRoom from '@/components/3d/CockpitRightRoom.vue'

export default {
  name: 'CockpitPage',
  components: { CockpitRightFloor, CockpitRightRoom },
  data() {
    return {
      scene: null,
      sceneId: '',
      sceneList: [],
      curType: ''
    }
  },
  mounted() {
    const communityId = this.$store.state.app.currentCommunity.communityId
    this.communityId = communityId

    const _scene = new Cockpit3d('webgl-scene')
    _scene.addJpgBackgroup({ path: '/img/3d_bg.jpg' })
    this.scene = _scene

    initEvent()

    eventBus.$on('cockpit.curType', (param) => {
      this.curType = param.curType
      const _data = param.data
      if (param.curType == 'floor') {
        eventBus.$emit('cockpitRightFLoor.notify', _data)
      } else if (param.curType == 'room') {
        eventBus.$emit('cockpitRightRoom.notify', _data)
      }
    })

    this._listDtScenes()
  },
  beforeDestroy() {
    eventBus.$off('cockpit.curType')
    if (this.scene && this.scene.renderer) {
      this.scene.renderer.dispose()
    }
  },
  methods: {
    async _listDtScenes() {
      try {
        const res = await listDtScenes({
          page: 1,
          row: 15,
          communityId: this.communityId,
          sceneId: this.sceneId
        })
        this.sceneList = res.data || []
        if (this.sceneList.length > 0) {
          this._changeScene(this.sceneList[0])
        }
      } catch (err) {
        console.error('加载场景列表失败', err)
      }
    },
    _changeScene(_data) {
      this.sceneId = _data.sceneId
      this.curType = ''
      if (!_data.modalJson) {
        return
      }
      const _scene = this.scene
      _scene.resetScene()
      _scene.addGltfObject({ path: _data.modalJson }).then(() => {
        computeSceneObject(_scene, {
          sceneId: _data.sceneId,
          communityId: this.communityId
        })
      })
    }
  }
}
</script>

<style scoped>
.cockpit-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 9999;
  background: #031117;
  overflow: hidden;
}
.webgl-scene {
  width: 100%;
  height: 100%;
}
.cockpit-header {
  position: absolute;
  left: 0;
  top: 0;
  z-index: 2;
  width: 100%;
  height: 7.6vh;
  background: url('~@/assets/bg-top.png') no-repeat;
  background-size: 100% 100%;
  text-align: center;
  line-height: 7.6vh;
  font-size: 32px;
  font-weight: bold;
  color: #fff;
  letter-spacing: 0.2em;
}
.cockpit-left {
  width: 20.8vw;
  height: 100%;
  position: absolute;
  left: 0;
  top: 0;
  z-index: 2;
  box-sizing: border-box;
  padding: 6vh 0 6vh 1.2vw;
  background: linear-gradient(90deg, rgba(3, 17, 23, 1), rgba(3, 17, 23, 0));
}
.cockpit-right {
  width: 20.8vw;
  height: 100%;
  position: absolute;
  right: 0;
  top: 0;
  z-index: 2;
  box-sizing: border-box;
  padding: 6vh 1.2vw 6vh 0;
  background: linear-gradient(270deg, rgba(3, 17, 23, 1), rgba(3, 17, 23, 0));
}
.cockpit-right .view-box {
  position: absolute;
  width: 24rem;
  height: 91.14vh;
  background: url('~@/assets/3d/bg-right.png') no-repeat;
  background-size: 100% 100%;
  right: 0.175rem;
  top: 8.036vh;
  padding-top: 0.1rem;
  box-sizing: border-box;
  z-index: 9;
}
.cockpit-right .view-box .vb-c {
  color: #fff;
}
.cockpit-right .view-box .vb-c .r-title {
  font-size: 18px;
  font-weight: 700;
}
.cockpit-right .view-box .vb-c .r-body {
  font-size: 14px;
}
.cockpit-menu {
  width: 80%;
  height: 6.575rem;
  margin-left: 30px;
  margin-top: 30px;
  z-index: 9;
  color: #838A9B;
}
.cockpit-menu .c-menu-item {
  height: 90px;
}
.cockpit-menu .active {
  color: #c17e14;
}
.cockpit-menu .active .bg-a {
  background: url('~@/assets/3d/active.png');
  background-size: 100% 100%;
}
.cockpit-menu .title {
  cursor: pointer;
  font-size: 18px;
  font-weight: 700;
}
.cockpit-menu .title .bg {
  width: 120px;
  height: 60px;
  padding: 9px 0 15px 25px;
  font-size: 24px;
}
.cockpit-menu .title .t-text {
  position: relative;
  top: 15px;
  left: -58px;
}
.cockpit-right .lift::-webkit-scrollbar {
  display: none;
}
.flex { display: flex; }
.justify-start { justify-content: flex-start; }
.justify-between { justify-content: space-between; }
.justify-center { justify-content: center; }
.flex-wrap { flex-wrap: wrap; }
.text-center { text-align: center; }
.margin-top { margin-top: 24px; }
.margin-top-sm { margin-top: 12px; }
.margin-bottom { margin-bottom: 8px; }
.padding { padding: 12px; }
</style>
