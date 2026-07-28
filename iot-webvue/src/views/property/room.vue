<template>
  <div class="list-room-manage">
    <div class="toolbar">
      <el-button size="mini" @click="$refs.floorUnitTree.$emit('addFloor')">添加楼栋</el-button>
      <el-button size="mini" @click="handleEditFloor">修改楼栋</el-button>
      <el-button size="mini" @click="handleDeleteFloor">删除楼栋</el-button>
      <el-button size="mini" @click="handleAddUnit">添加单元</el-button>
      <el-button size="mini" @click="handleEditUnit">修改单元</el-button>
      <el-button size="mini" @click="handleDeleteUnit">删除单元</el-button>
      <el-button size="mini" style="margin-left:12px" @click="handleImportRoom">房产导入</el-button>
      <el-button size="mini" type="primary" @click="handleAddRoom">添加房屋</el-button>
    </div>
    <div class="content-wrapper">
      <div class="tree-panel">
        <FloorUnitTree
          ref="floorUnitTree"
          @switchFloor="handleSwitchFloor"
          @switchUnit="handleSwitchUnit"
          @addFloor="handleAddFloor"
          @editFloor="handleEditFloor"
          @deleteFloor="handleDeleteFloor"
          @addUnit="handleAddUnit"
          @editUnit="handleEditUnit"
          @deleteUnit="handleDeleteUnit"
        />
      </div>
      <div class="main-panel">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.roomId" placeholder="楼栋-单元-房屋" clearable style="width: 180px" />
            </el-form-item>
            <el-form-item>
              <el-select v-model="queryParams.state" placeholder="请选择状态" clearable style="width: 140px">
                <el-option v-for="item in stateOptions" :key="item.statusCd" :label="item.name" :value="item.statusCd" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-select v-model="queryParams.roomSubType" placeholder="请选择类型" clearable style="width: 140px">
                <el-option v-for="item in roomSubTypeOptions" :key="item.statusCd" :label="item.name" :value="item.statusCd" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-select v-model="queryParams.isAll" style="width: 140px" @change="handleIsAllChange">
                <el-option label="当前楼栋单元" value="N" />
                <el-option label="全部楼栋单元" value="Y" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <el-table :data="roomList" v-loading="loading" border stripe style="width: 100%">
            <el-table-column label="房屋"  fixed="left">
              <template slot-scope="{ row }">
                {{ row.floorNum }}-{{ row.unitNum }}-{{ row.roomNum }}
              </template>
            </el-table-column>
            <el-table-column label="楼层" >
              <template slot-scope="{ row }">{{ row.layer }}层</template>
            </el-table-column>
            <el-table-column prop="ownerName" label="业主">
              <template slot-scope="{ row }">{{ row.ownerName || '-' }}</template>
            </el-table-column>
            <el-table-column prop="roomSubTypeName" label="类型" />
            <el-table-column label="建筑/室内面积">
              <template slot-scope="{ row }">{{ row.builtUpArea }}/{{ row.roomArea }}㎡</template>
            </el-table-column>
            <el-table-column prop="stateName" label="状态"/>
            <el-table-column label="入住时间">
              <template slot-scope="{ row }">{{ row.startTime || '-' }}</template>
            </el-table-column>
            <el-table-column label="住户"  align="center">
              <template slot-scope="{ row }">
                <el-button type="text" size="small" @click="handleViewMembers(row)">
                  {{ row.memberCount || 0 }}
                </el-button>
              </template>
            </el-table-column>
            <el-table-column v-for="col in dynamicColumns" :key="col" :label="col" >
              <template slot-scope="{ row }">
                {{ getDynamicColumnValue(row, col) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button type="text" size="small" @click="handleEditRoom(row)">修改</el-button>
                <el-button type="text" size="small" style="color: #f56c6c" @click="handleDeleteRoom(row)">删除</el-button>
                <el-button type="text" size="small" @click="handleBindOwner(row)">绑定</el-button>
                <el-button v-if="row.ownerId && row.state !== '2002'" type="text" size="small" style="color: #f56c6c" @click="handleUnBindOwner(row)">解绑</el-button>
                <el-button type="text" size="small" @click="handleRoomDetail(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            :current-page="queryParams.page"
            :page-size="queryParams.row"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
            class="pagination-container"
          />
        </el-card>
      </div>
    </div>

    <!-- 弹窗组件 -->
    <EditRoom :visible.sync="editRoomVisible" :edit-data="currentRoom" @success="handleSuccess" />
    <DeleteRoom :visible.sync="delRoomVisible" :del-data="currentRoom" @success="handleSuccess" />
    <AddFloor :visible.sync="addFloorVisible" @success="handleFloorSuccess" />
    <EditFloor :visible.sync="editFloorVisible" :edit-data="currentFloor" @success="handleFloorSuccess" />
    <DeleteFloor :visible.sync="delFloorVisible" :del-data="currentFloor" @success="handleFloorSuccess" />
    <AddUnit :visible.sync="addUnitVisible" :floor-id="currentFloorId" @success="handleFloorSuccess" />
    <EditUnit :visible.sync="editUnitVisible" :edit-data="currentUnit" @success="handleFloorSuccess" />
    <DeleteUnit :visible.sync="delUnitVisible" :del-data="currentUnit" @success="handleFloorSuccess" />
    <ImportOwnerRoom :visible.sync="importVisible" @success="handleSuccess" />
    <OwnerRooms :visible.sync="ownerRoomsVisible" :room-data="currentRoom" />
    <OwnerMembers :visible.sync="ownerMembersVisible" :owner-data="currentOwner" />
    <OwnerCars :visible.sync="ownerCarsVisible" :owner-data="currentOwner" />
  </div>
</template>

<script>
import { queryRooms } from '@/api/property/roomApi'
import { listDicts } from '@/api/system/systemApi'
import { listAttrSpecs } from '@/api/property/propertyApi'
import { queryFloors } from '@/api/property/floorApi'
import { queryUnits } from '@/api/property/unitApi'
import FloorUnitTree from '@/components/property/FloorUnitTree.vue'
import EditRoom from '@/components/property/EditRoom.vue'
import DeleteRoom from '@/components/property/DeleteRoom.vue'
import AddFloor from '@/components/property/AddFloor.vue'
import EditFloor from '@/components/property/EditFloor.vue'
import DeleteFloor from '@/components/property/DeleteFloor.vue'
import AddUnit from '@/components/property/AddUnit.vue'
import EditUnit from '@/components/property/EditUnit.vue'
import DeleteUnit from '@/components/property/DeleteUnit.vue'
import ImportOwnerRoom from '@/components/property/ImportOwnerRoom.vue'
import OwnerRooms from '@/components/property/OwnerRooms.vue'
import OwnerMembers from '@/components/property/OwnerMembers.vue'
import OwnerCars from '@/components/property/OwnerCars.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'ListRoomManage',
  components: {
    FloorUnitTree, EditRoom, DeleteRoom,
    AddFloor, EditFloor, DeleteFloor,
    AddUnit, EditUnit, DeleteUnit,
    ImportOwnerRoom, OwnerRooms, OwnerMembers, OwnerCars
  },
  data() {
    return {
      loading: false,
      roomList: [],
      total: 0,
      stateOptions: [],
      roomSubTypeOptions: [],
      dynamicColumns: [],
      queryParams: {
        page: 1, row: 10,
        floorId: '', unitId: '', roomId: '',
        state: '', roomSubType: '', flag: 0,
        isAll: 'N'
      },
      editRoomVisible: false, delRoomVisible: false,
      addFloorVisible: false, editFloorVisible: false, delFloorVisible: false,
      addUnitVisible: false, editUnitVisible: false, delUnitVisible: false,
      importVisible: false,
      ownerRoomsVisible: false, ownerMembersVisible: false, ownerCarsVisible: false,
      currentRoom: null, currentFloor: null, currentUnit: null,
      currentFloorId: '', currentOwner: {}
    }
  },
  mounted() {
    this.loadDicts()
    this.fetchRooms()
  },
  methods: {
    async loadDicts() {
      try {
        const [stateRes, typeRes] = await Promise.all([
          listDicts({ tableName: 'building_room', tableColumns: 'state' }),
          listDicts({ tableName: 'building_room', tableColumns: 'room_sub_type' })
        ])
        this.stateOptions = stateRes || []
        this.roomSubTypeOptions = typeRes || []
      } catch (e) { this.$message.error('加载字典失败') }
    },
    async fetchRooms() {
      this.loading = true
      try {
        const communityInfo = getCurrentCommunity() || {}
        const params = { ...this.queryParams, communityId: communityInfo.communityId }
        const allNum = this.queryParams.roomId || ''
        const allNums = allNum.split('-')
        if (allNums.length >= 3) {
          params.floorNum = allNums[0].trim()
          params.unitNum = allNums[1].trim()
          params.roomNum = allNums[2].trim()
          delete params.roomId
          delete params.unitId
          delete params.floorId
        }
        const res = await queryRooms(params)
        this.roomList = res.data || []
        this.total = res.total || 0
        this.buildDynamicColumns()
      } catch (e) { this.$message.error(e.message || '查询失败') }
      finally { this.loading = false }
    },
    async buildDynamicColumns() {
      try {
        const res = await listAttrSpecs({ tableName: 'building_room_attr', page: 1, row: 100 })
        const specs = Array.isArray(res) ? res : (res.data || [])
        this.dynamicColumns = specs.filter(s => s.listShow === 'Y').map(s => s.specName)
      } catch (e) {
        this.dynamicColumns = []
      }
    },
    getDynamicColumnValue(row, col) {
      if (!row.roomAttrDto || !row.roomAttrDto.length) return ''
      const attr = row.roomAttrDto.find(a => a.specName === col)
      return attr ? attr.valueName : ''
    },
    handleSearch() { this.queryParams.page = 1; this.fetchRooms() },
    handleIsAllChange(val) {
      if (val === 'Y') {
        this.queryParams.floorId = ''
        this.queryParams.unitId = ''
      }
      this.handleSearch()
    },
    handleSwitchFloor({ floorId }) {
      this.queryParams.floorId = floorId
      this.queryParams.unitId = ''
      this.queryParams.flag = 1
      this.queryParams.isAll = 'N'
      this.fetchRooms()
    },
    handleSwitchUnit({ unitId }) {
      this.queryParams.floorId = ''
      this.queryParams.unitId = unitId
      this.queryParams.flag = 1
      this.queryParams.isAll = 'N'
      this.fetchRooms()
    },
    handleAddRoom() { this.$router.push('/pages/property/addRoomView') },
    handleEditRoom(row) { this.currentRoom = { ...row }; this.editRoomVisible = true },
    handleDeleteRoom(row) { this.currentRoom = { ...row }; this.delRoomVisible = true },
    handleBindOwner(row) { this.$router.push({ path: '/pages/owner/roomBindOwner', query: { roomId: row.roomId } }) },
    handleUnBindOwner(row) {
      const roomName = row.floorNum + '-' + row.unitNum + '-' + row.roomNum
      this.$router.push({ path: '/pages/owner/roomUnBindOwner', query: { roomId: row.roomId, roomName } })
    },
    handleRoomDetail(row) { this.$router.push({ path: '/pages/property/roomDetail', query: { roomId: row.roomId } }) },
    handleViewMembers(row) {
      this.currentOwner = { ownerId: row.ownerId, roomId: row.roomId }
      this.ownerMembersVisible = true
    },
    // Floor operations
    handleAddFloor() { this.addFloorVisible = true },
    async handleEditFloor() {
      if (!this.queryParams.floorId) { this.$message.warning('请先选择楼栋'); return }
      try {
        const communityInfo = getCurrentCommunity() || {}
        const res = await queryFloors({ communityId: communityInfo.communityId, floorId: this.queryParams.floorId, page: 1, row: 1 })
        if (res.data && res.data.length) { this.currentFloor = { ...res.data[0] }; this.editFloorVisible = true }
      } catch (e) { this.$message.error('加载楼栋信息失败') }
    },
    async handleDeleteFloor() {
      if (!this.queryParams.floorId) { this.$message.warning('请先选择楼栋'); return }
      try {
        const communityInfo = getCurrentCommunity() || {}
        const res = await queryFloors({ communityId: communityInfo.communityId, floorId: this.queryParams.floorId, page: 1, row: 1 })
        if (res.data && res.data.length) { this.currentFloor = { ...res.data[0] }; this.delFloorVisible = true }
      } catch (e) { this.$message.error('加载楼栋信息失败') }
    },
    // Unit operations
    handleAddUnit() {
      if (!this.queryParams.floorId) { this.$message.warning('请先选择楼栋'); return }
      this.currentFloorId = this.queryParams.floorId
      this.addUnitVisible = true
    },
    async handleEditUnit() {
      if (!this.queryParams.unitId) { this.$message.warning('请先选择单元'); return }
      try {
        const communityInfo = getCurrentCommunity() || {}
        const res = await queryUnits({ communityId: communityInfo.communityId, unitId: this.queryParams.unitId })
        const units = Array.isArray(res) ? res : (res.data || [])
        if (units.length) { this.currentUnit = { ...units[0] }; this.editUnitVisible = true }
      } catch (e) { this.$message.error('加载单元信息失败') }
    },
    async handleDeleteUnit() {
      if (!this.queryParams.unitId) { this.$message.warning('请先选择单元'); return }
      try {
        const communityInfo = getCurrentCommunity() || {}
        const res = await queryUnits({ communityId: communityInfo.communityId, unitId: this.queryParams.unitId })
        const units = Array.isArray(res) ? res : (res.data || [])
        if (units.length) { this.currentUnit = { ...units[0] }; this.delUnitVisible = true }
      } catch (e) { this.$message.error('加载单元信息失败') }
    },
    handleImportRoom() { this.importVisible = true },
    handleSuccess() { this.fetchRooms() },
    handleFloorSuccess() {
      this.$refs.floorUnitTree && this.$refs.floorUnitTree.refresh()
      this.fetchRooms()
    },
    handleSizeChange(val) { this.queryParams.row = val; this.fetchRooms() },
    handlePageChange(val) { this.queryParams.page = val; this.fetchRooms() }
  }
}
</script>

<style scoped>
.list-room-manage {
  display: flex;
  flex-direction: column;
  height: 100%;
}
.toolbar {
  padding: 8px 16px;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
  flex-wrap: wrap;
  display: flex;
  align-items: center;
}
.content-wrapper {
  display: flex;
  flex: 1;
  overflow: hidden;
  margin-top: 10px;
}
.tree-panel {
  margin-right: 16px;
  flex-shrink: 0;
}
.main-panel {
  flex: 1;
  overflow: auto;
}
.search-card {
  margin-bottom: 16px;
}
.table-card {
  margin-bottom: 0;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pagination-container {
  margin-top: 16px;
  text-align: right;
}
</style>
