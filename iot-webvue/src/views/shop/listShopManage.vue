<template>
  <div class="list-shop-manage">
    <div class="content-wrapper">
      <div class="tree-panel" :style="{ width: treeWidth + 'px', minWidth: treeWidth + 'px' }">
        <ShopLocationTree
          ref="shopLocationTree"
          @select="handleSelectLocation"
          @add="handleAddLocation"
          @edit="handleEditLocation"
          @delete="handleDeleteLocation"
        />
      </div>
      <div class="resize-handle" @mousedown="startResize">
        <div class="resize-line"></div>
      </div>
      <div class="main-panel">
        <el-card shadow="never" class="search-card">
          <div slot="header" class="card-header">
            <span>查询条件</span>
            <span v-if="currentLocation.locationName" class="location-tip">
              当前位置：{{ currentLocation.locationName }}
            </span>
          </div>
          <el-form :inline="true" :model="queryParams">
            <el-form-item>
              <el-input v-model="queryParams.shopNum" placeholder="商铺编号" clearable style="width: 150px" />
            </el-form-item>
            <el-form-item>
              <el-select v-model="queryParams.state" placeholder="状态" clearable style="width: 130px">
                <el-option label="空置" value="2008" />
                <el-option label="已出租" value="2006" />
                <el-option label="已出售" value="2007" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.ownerName" placeholder="业主姓名" clearable style="width: 130px" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="queryParams.link" placeholder="联系电话" clearable style="width: 140px" />
            </el-form-item>
            <el-form-item>
              <el-date-picker
                v-model="rentTimeRange"
                type="daterange"
                value-format="yyyy-MM-dd HH:mm:ss"
                range-separator="至"
                start-placeholder="租赁开始"
                end-placeholder="租赁结束"
                style="width: 260px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        <el-card shadow="never" class="table-card">
          <div slot="header" class="card-header">
            <span>商铺信息</span>
            <div>
              <el-button size="small" @click="handleImportShop">商铺导入</el-button>
              <el-button size="small" type="primary" @click="handleAddShop">新增商铺</el-button>
            </div>
          </div>
          <el-table :data="shopList" v-loading="loading" border stripe style="width: 100%">
            <el-table-column prop="shopNum" label="商铺编号" min-width="110" fixed="left">
              <template slot-scope="{ row }">
                <el-link type="primary" :underline="false" @click="handleShopDetail(row)">{{ row.shopNum }}</el-link>
              </template>
            </el-table-column>
            <el-table-column prop="locationName" label="所在位置" min-width="120" />
            <el-table-column label="建筑面积/使用面积" min-width="130">
              <template slot-scope="{ row }">
                {{ row.builtUpArea || '-' }}/{{ row.shopArea || '-' }}㎡
              </template>
            </el-table-column>
            <el-table-column prop="ownerName" label="业主" min-width="100">
              <template slot-scope="{ row }">
                <el-link v-if="row.ownerId" type="primary" :underline="false" @click="handleOwnerDetail(row)">{{ row.ownerName }}</el-link>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="link" label="联系电话" min-width="120">
              <template slot-scope="{ row }">{{ row.link || '-' }}</template>
            </el-table-column>
            <el-table-column label="状态" min-width="90" align="center">
              <template slot-scope="{ row }">
                <el-tag :type="stateMap[row.state] && stateMap[row.state].type" size="small">
                  {{ (stateMap[row.state] && stateMap[row.state].name) || row.state }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="租赁起止时间" min-width="170">
              <template slot-scope="{ row }">
                <span v-if="row.startTime">{{ row.startTime }} ~ {{ row.endTime || '' }}</span>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="shopRent" label="租金" min-width="90">
              <template slot-scope="{ row }">{{ row.shopRent || '0.00' }}</template>
            </el-table-column>
            <el-table-column prop="createTime" label="建档时间" min-width="150" />
            <el-table-column label="操作" width="230" fixed="right" align="center">
              <template slot-scope="{ row }">
                <el-button type="text" size="small" @click="handleEditShop(row)">修改</el-button>
                <el-button v-if="row.state === '2008'" type="text" size="small" @click="handleBindOwner(row)">交铺</el-button>
                <el-button v-else type="text" size="small" style="color: #f56c6c" @click="handleUnbindOwner(row)">退租</el-button>
                <el-button type="text" size="small" style="color: #f56c6c" @click="handleDeleteShop(row)">删除</el-button>
                <el-button type="text" size="small" @click="handleShopDetail(row)">详情</el-button>
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
    <AddLocation :visible.sync="addLocationVisible" :parent-id="addParentId" :parent-name="addParentName" :level="addParentLevel" @success="handleLocationSuccess" />
    <EditLocation :visible.sync="editLocationVisible" :edit-data="currentLocation" @success="handleLocationSuccess" />
    <DeleteLocation :visible.sync="delLocationVisible" :edit-data="currentLocation" @success="handleLocationSuccess" />
    <AddShop :visible.sync="addShopVisible" :location-id="currentLocation.locationId" :location-name="currentLocationPath" @success="fetchList" />
    <EditShop :visible.sync="editShopVisible" :edit-data="currentShop" @success="fetchList" />
    <DeleteShop :visible.sync="delShopVisible" :del-data="currentShop" @success="fetchList" />
    <ImportShop ref="importShopRef" @success="fetchList" />
  </div>
</template>

<script>
import { listBuildingShops } from '@/api/shop/shopApi'
import { getCurrentCommunity } from '@/utils/auth'
import ShopLocationTree from '@/components/shop/ShopLocationTree.vue'
import AddLocation from '@/components/shop/AddLocation.vue'
import EditLocation from '@/components/shop/EditLocation.vue'
import DeleteLocation from '@/components/shop/DeleteLocation.vue'
import AddShop from '@/components/shop/AddShop.vue'
import EditShop from '@/components/shop/EditShop.vue'
import DeleteShop from '@/components/shop/DeleteShop.vue'
import ImportShop from '@/components/shop/ImportShop.vue'

export default {
  name: 'ListShopManage',
  components: {
    ShopLocationTree, AddLocation, EditLocation, DeleteLocation,
    AddShop, EditShop, DeleteShop, ImportShop
  },
  data() {
    return {
      loading: false,
      shopList: [],
      total: 0,
      treeWidth: 240,
      minTreeWidth: 200,
      maxTreeWidth: 600,
      isResizing: false,
      startX: 0,
      startWidth: 0,
      queryParams: {
        page: 1, row: 10,
        shopNum: '', locationId: '',
        state: '', ownerName: '', link: '',
        rentTimeStart: '', rentTimeEnd: ''
      },
      rentTimeRange: [],
      stateMap: {
        '2006': { name: '已出租', type: 'warning' },
        '2007': { name: '已出售', type: 'info' },
        '2008': { name: '空置', type: 'success' }
      },
      currentLocation: { locationId: '', locationName: '', level: 0 },
      currentLocationPath: '',
      currentShop: null,
      addLocationVisible: false,
      editLocationVisible: false,
      delLocationVisible: false,
      addShopVisible: false,
      editShopVisible: false,
      delShopVisible: false,
      addParentId: '-1',
      addParentName: '',
      addParentLevel: 0
    }
  },
  mounted() {
    this.restoreTreeWidth()
    document.addEventListener('mousemove', this.onMouseMove)
    document.addEventListener('mouseup', this.onMouseUp)
    this.fetchList()
  },
  beforeDestroy() {
    document.removeEventListener('mousemove', this.onMouseMove)
    document.removeEventListener('mouseup', this.onMouseUp)
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const communityId = (getCurrentCommunity() || {}).communityId || ''
        const params = { ...this.queryParams, communityId }
        const res = await listBuildingShops(params)
        this.shopList = res.data || []
        this.total = res.total || 0
      } catch (e) { this.$message.error(e.message || '查询失败') }
      finally { this.loading = false }
    },
    handleSearch() {
      this.queryParams.page = 1
      if (this.rentTimeRange && this.rentTimeRange.length === 2) {
        this.queryParams.rentTimeStart = this.rentTimeRange[0]
        this.queryParams.rentTimeEnd = this.rentTimeRange[1]
      } else {
        this.queryParams.rentTimeStart = ''
        this.queryParams.rentTimeEnd = ''
      }
      this.fetchList()
    },
    handleReset() {
      this.queryParams = {
        page: 1, row: 10,
        shopNum: '', locationId: '',
        state: '', ownerName: '', link: '',
        rentTimeStart: '', rentTimeEnd: ''
      }
      this.rentTimeRange = []
      this.fetchList()
    },
    handleSizeChange(val) {
      this.queryParams.row = val
      this.fetchList()
    },
    handlePageChange(val) {
      this.queryParams.page = val
      this.fetchList()
    },
    handleSelectLocation(data) {
      this.currentLocation = { ...this.currentLocation, ...data }
      this.queryParams.locationId = data.locationId || ''
      this.queryParams.page = 1
      this.fetchList()
    },
    handleAddLocation(data) {
      if (data && data.addType === 'child') {
        this.addParentId = data.locationId
        this.addParentName = data.locationName
        this.addParentLevel = data.level || 0
      } else {
        this.addParentId = '-1'
        this.addParentName = ''
        this.addParentLevel = 0
      }
      this.addLocationVisible = true
    },
    handleEditLocation(node) {
      this.currentLocation = { ...node }
      this.editLocationVisible = true
    },
    handleDeleteLocation(node) {
      this.currentLocation = { ...node }
      this.delLocationVisible = true
    },
    handleLocationSuccess() {
      this.$refs.shopLocationTree && this.$refs.shopLocationTree.refresh()
      this.fetchList()
    },
    handleAddShop() {
      if (!this.currentLocation.locationId) {
        this.$message.warning('请先在左侧位置树中选择商铺位置')
        return
      }
      const treeData = (this.$refs.shopLocationTree && this.$refs.shopLocationTree.treeData) || []
      this.currentLocationPath = this.getFullPath(treeData, this.currentLocation.locationId) || this.currentLocation.locationName || ''
      this.addShopVisible = true
    },
    handleEditShop(row) {
      this.currentShop = row
      this.editShopVisible = true
    },
    handleDeleteShop(row) {
      this.currentShop = row
      this.delShopVisible = true
    },
    handleBindOwner(row) {
      this.$router.push({
        path: '/pages/shop/shopBindOwner',
        query: {
          shopId: row.shopId,
          shopNum: row.shopNum,
          locationName: row.locationName,
          builtUpArea: row.builtUpArea,
          shopArea: row.shopArea,
          shopRent: row.shopRent
        }
      })
    },
    handleUnbindOwner(row) {
      this.$router.push({
        path: '/pages/shop/shopUnbindOwner',
        query: {
          shopId: row.shopId,
          shopNum: row.shopNum,
          ownerId: row.ownerId || '',
          ownerName: row.ownerName || '',
          link: row.link || '',
          startTime: row.startTime || '',
          endTime: row.endTime || ''
        }
      })
    },
    handleShopDetail(row) {
      this.$router.push({
        path: '/pages/shop/shopDetail',
        query: { shopId: row.shopId, shopNum: row.shopNum }
      })
    },
    handleOwnerDetail(row) {
      this.$router.push({
        path: '/pages/owner/ownerDetail',
        query: { ownerId: row.ownerId }
      })
    },
    handleImportShop() {
      this.$refs.importShopRef.open()
    },
    getFullPath(nodes, targetId, parentPath) {
      parentPath = parentPath || ''
      for (let i = 0; i < nodes.length; i++) {
        const node = nodes[i]
        if (node.locationId === 'all') {
          const found = this.getFullPath(node.children || [], targetId, '')
          if (found) return found
          continue
        }
        const currentPath = parentPath ? parentPath + '-' + node.locationName : node.locationName
        if (node.locationId === targetId) {
          return currentPath
        }
        if (node.children && node.children.length > 0) {
          const found = this.getFullPath(node.children, targetId, currentPath)
          if (found) return found
        }
      }
      return ''
    },
    restoreTreeWidth() {
      const saved = localStorage.getItem('shopsListTreeWidth')
      if (saved) {
        const w = parseInt(saved)
        if (w >= this.minTreeWidth && w <= this.maxTreeWidth) this.treeWidth = w
      }
    },
    saveTreeWidth() {
      localStorage.setItem('shopsListTreeWidth', this.treeWidth.toString())
    },
    startResize(e) {
      e.preventDefault()
      this.isResizing = true
      this.startX = e.clientX
      this.startWidth = this.treeWidth
      document.body.style.cursor = 'col-resize'
      document.body.style.userSelect = 'none'
    },
    onMouseMove(e) {
      if (!this.isResizing) return
      const newWidth = this.startWidth + (e.clientX - this.startX)
      if (newWidth >= this.minTreeWidth && newWidth <= this.maxTreeWidth) this.treeWidth = newWidth
    },
    onMouseUp() {
      if (this.isResizing) {
        this.isResizing = false
        this.saveTreeWidth()
        document.body.style.cursor = ''
        document.body.style.userSelect = ''
      }
    }
  }
}
</script>

<style scoped>
.content-wrapper {
  display: flex;
  gap: 4px;
  align-items: flex-start;
}
.tree-panel {
  overflow: hidden;
}
.resize-handle {
  width: 8px;
  cursor: col-resize;
  display: flex;
  align-items: center;
  justify-content: center;
  align-self: stretch;
}
.resize-handle:hover .resize-line {
  background-color: #409eff;
}
.resize-line {
  width: 2px;
  height: 30px;
  background-color: #dcdfe6;
  border-radius: 1px;
}
.main-panel {
  flex: 1;
  min-width: 0;
}
.search-card { margin-bottom: 12px; }
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.location-tip {
  color: #409eff;
  font-size: 13px;
}
.table-card { margin-bottom: 0; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
