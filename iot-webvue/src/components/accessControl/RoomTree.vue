<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="选择房屋"
    width="500px"
    append-to-body
    @close="handleClose"
  >
    <el-tree
      ref="tree"
      :data="treeData"
      node-key="id"
      :load="loadNode"
      lazy
      highlight-current
      @node-click="handleNodeClick"
    >
      <span class="custom-tree-node" slot-scope="{ data }">
        <span>{{ data.label }}</span>
      </span>
    </el-tree>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleConfirm" :disabled="!selectedRoom">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { queryFloors } from '@/api/property/floorApi'
import { queryUnits } from '@/api/property/unitApi'
import { queryRoomsTree } from '@/api/property/roomApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'RoomTree',
  props: {
    visible: { type: Boolean, default: false }
  },
  data() {
    return {
      dialogVisible: false,
      treeData: [],
      selectedRoom: null
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val) {
        this.selectedRoom = null
        this.loadFloors()
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    async loadFloors() {
      try {
        const communityInfo = getCurrentCommunity() || {}
        const res = await queryFloors({ communityId: communityInfo.communityId, page: 1, row: 1000 })
        const floors = res.data || []
        this.treeData = floors.map(f => ({
          id: 'f_' + f.floorId,
          floorId: f.floorId,
          label: f.floorNum + '栋',
          children: []
        }))
      } catch (e) {
        console.error('加载楼栋失败', e)
      }
    },
    async loadNode(node, resolve) {
      const id = node.data.id
      try {
        const communityInfo = getCurrentCommunity() || {}
        if (id.startsWith('f_')) {
          const res = await queryUnits({ communityId: communityInfo.communityId, floorId: node.data.floorId, page: 1, row: 1000 })
          const units = res.data || []
          resolve(units.map(u => ({
            id: 'u_' + u.unitId,
            unitId: u.unitId,
            floorId: node.data.floorId,
            label: u.unitNum + '单元',
            children: []
          })))
        } else if (id.startsWith('u_')) {
          const res = await queryRoomsTree({ communityId: communityInfo.communityId, unitId: node.data.unitId, page: 1, row: 1000 })
          const rooms = res.data || []
          resolve(rooms.map(r => ({
            id: 'r_' + r.roomId,
            roomId: r.roomId,
            roomName: r.floorNum + '-' + r.unitNum + '-' + r.roomNum,
            label: r.roomNum + (r.ownerName ? ' (' + r.ownerName + ')' : ''),
            leaf: true
          })))
        } else {
          resolve([])
        }
      } catch (e) {
        resolve([])
      }
    },
    handleNodeClick(data) {
      if (data.id.startsWith('r_')) {
        this.selectedRoom = data
      } else {
        this.selectedRoom = null
      }
    },
    handleConfirm() {
      if (this.selectedRoom) {
        this.$emit('confirm', {
          roomId: this.selectedRoom.roomId,
          roomName: this.selectedRoom.roomName
        })
        this.dialogVisible = false
      }
    },
    handleClose() {
      this.dialogVisible = false
      this.selectedRoom = null
    }
  }
}
</script>
