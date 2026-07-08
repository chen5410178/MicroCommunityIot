<template>
  <div class="city-area-manage">
    <div class="manage-container">
      <div class="tree-panel">
        <CityAreaTree
          ref="treeRef"
          @node-click="handleNodeClick"
          @open-add="handleOpenAdd"
          @open-edit="handleOpenEdit"
          @open-delete="handleOpenDelete"
        />
      </div>
      <div class="detail-panel">
        <el-card shadow="never" class="detail-card">
          <div slot="header" class="detail-header">
            <span class="text-title">地区详情</span>
            <div v-if="currentNode && !isRootNode" class="detail-actions">
              <el-button type="primary" size="small" @click="handleEditClick">修改</el-button>
              <el-button type="danger" size="small" @click="handleDeleteClick">删除</el-button>
            </div>
          </div>
          <div v-if="detail.id">
            <el-row :gutter="16">
              <el-col :span="6">
                <div class="form-group">
                  <span class="col-form-label">地区名称：</span>
                  <span class="col-form-value">{{ detail.areaName || '-' }}</span>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="form-group">
                  <span class="col-form-label">地区编码：</span>
                  <span class="col-form-value">{{ detail.areaCode || '-' }}</span>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="form-group">
                  <span class="col-form-label">等级：</span>
                  <span class="col-form-value" v-if="detail.areaLevel === '101'">省级</span>
                  <span class="col-form-value" v-else-if="detail.areaLevel === '202'">市州</span>
                  <span class="col-form-value" v-else-if="detail.areaLevel === '303'">区县</span>
                  <span class="col-form-value" v-else>地区</span>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="form-group">
                  <span class="col-form-label">父级编码：</span>
                  <span class="col-form-value">{{ detail.parentAreaCode || '-' }}</span>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="form-group">
                  <span class="col-form-label">父级名称：</span>
                  <span class="col-form-value">{{ detail.parentAreaName || '-' }}</span>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="form-group">
                  <span class="col-form-label">创建时间：</span>
                  <span class="col-form-value">{{ detail.createTime || '-' }}</span>
                </div>
              </el-col>
            </el-row>
          </div>
          <div v-else class="empty-detail">
            <p>请选择左侧地区节点查看详情</p>
          </div>
        </el-card>
      </div>
    </div>

    <AddCityArea :visible.sync="addVisible" :parent-data="parentData" @success="handleSuccess" />
    <EditCityArea :visible.sync="editVisible" :edit-data="editData" @success="handleSuccess" />
    <DeleteCityArea :visible.sync="delVisible" :del-data="delData" @success="handleSuccess" />
  </div>
</template>

<script>
import { listAreas } from '@/api/common/cityAreaApi'
import CityAreaTree from '@/components/common/CityAreaTree.vue'
import AddCityArea from '@/components/common/AddCityArea.vue'
import EditCityArea from '@/components/common/EditCityArea.vue'
import DeleteCityArea from '@/components/common/DeleteCityArea.vue'

export default {
  name: 'CityAreaManage',
  components: { CityAreaTree, AddCityArea, EditCityArea, DeleteCityArea },
  data() {
    return {
      detail: {},
      currentNode: null,
      addVisible: false,
      editVisible: false,
      delVisible: false,
      parentData: null,
      editData: null,
      delData: null
    }
  },
  computed: {
    isRootNode() {
      return !this.currentNode || this.currentNode.areaCode === '0'
    }
  },
  methods: {
    async handleNodeClick(node) {
      this.currentNode = node
      try {
        const res = await listAreas({ id: node.id, areaCode: node.areaCode, page: 1, row: 1 })
        if (res.data && res.data.length > 0) {
          this.detail = res.data[0]
        } else {
          this.detail = {
            id: node.id,
            areaCode: node.areaCode,
            areaName: node.text,
            areaLevel: node.areaLevel,
            parentAreaCode: node.parentAreaCode || '',
            parentAreaName: node.parentAreaName || '',
            createTime: node.createTime || ''
          }
        }
      } catch {
        this.$message.error('获取地区详情失败')
      }
    },
    handleOpenAdd(data) {
      this.parentData = data
      this.addVisible = true
    },
    handleOpenEdit(data) {
      this.editData = { ...data }
      this.editVisible = true
    },
    handleOpenDelete(data) {
      this.delData = { ...data }
      this.delVisible = true
    },
    handleEditClick() {
      if (!this.currentNode) return
      this.handleOpenEdit(this.currentNode)
    },
    handleDeleteClick() {
      if (!this.currentNode) return
      this.handleOpenDelete(this.currentNode)
    },
    handleSuccess() {
      this.$refs.treeRef && this.$refs.treeRef.refresh()
      this.detail = {}
      this.currentNode = null
    }
  }
}
</script>

<style scoped>
.city-area-manage {
  height: 100%;
}
.manage-container {
  display: flex;
  height: 100%;
  gap: 16px;
}
.tree-panel {
  width: 240px;
  flex-shrink: 0;
}
.detail-panel {
  flex: 1;
  min-width: 0;
}
.detail-card {
  height: auto;
}
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.text-title {
  font-size: 14px;
}
.detail-actions {
  display: flex;
  gap: 6px;
}
.form-group {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
}
.col-form-label {
  font-size: 13px;
  color: #666;
  margin-right: 4px;
  white-space: nowrap;
}
.col-form-value {
  font-size: 14px;
  color: #333;
}
.empty-detail {
  text-align: center;
  color: #909399;
  padding: 100px 0;
  font-size: 14px;
}
</style>
