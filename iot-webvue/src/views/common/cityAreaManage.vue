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
        <div class="white-bg padding-left padding-right padding-top border-radius-top">
          <div class="detail-header">
            <div class="text-title">地区详情</div>
          </div>
          <div v-if="detail.id" class="margin-top">
            <el-row>
              <el-col :span="6">
                <div class="form-group">
                  <label class="col-form-label">地区名称：</label>
                  <label>{{ detail.areaName || '-' }}</label>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="form-group">
                  <label class="col-form-label">地区编码：</label>
                  <label>{{ detail.areaCode || '-' }}</label>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="form-group">
                  <label class="col-form-label">等级：</label>
                  <label v-if="detail.areaLevel === '101'">省级</label>
                  <label v-else-if="detail.areaLevel === '202'">市州</label>
                  <label v-else-if="detail.areaLevel === '303'">区县</label>
                  <label v-else>地区</label>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="form-group">
                  <label class="col-form-label">父级编码：</label>
                  <label>{{ detail.parentAreaCode || '-' }}</label>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="form-group">
                  <label class="col-form-label">父级名称：</label>
                  <label>{{ detail.parentAreaName || '-' }}</label>
                </div>
              </el-col>
              <el-col :span="6">
                <div class="form-group">
                  <label class="col-form-label">创建时间：</label>
                  <label>{{ detail.createTime || '-' }}</label>
                </div>
              </el-col>
            </el-row>
          </div>
          <div v-else class="empty-detail">
            <p>请选择左侧地区节点查看详情</p>
          </div>
        </div>
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
      addVisible: false,
      editVisible: false,
      delVisible: false,
      parentData: null,
      editData: null,
      delData: null
    }
  },
  methods: {
    async handleNodeClick(node) {
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
    handleSuccess() {
      this.$refs.treeRef && this.$refs.treeRef.refresh()
      this.detail = {}
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
.white-bg {
  background: #fff;
  min-height: 500px;
}
.padding-left { padding-left: 16px; }
.padding-right { padding-right: 16px; }
.padding-top { padding-top: 16px; }
.border-radius-top { border-radius: 4px 4px 0 0; }
.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.text-title {
  font-size: 16px;
  font-weight: bold;
}
.margin-top { margin-top: 16px; }
.form-group {
  margin-bottom: 16px;
}
.col-form-label {
  display: block;
  font-size: 13px;
  color: #666;
  margin-bottom: 2px;
}
.form-group label:last-child {
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
