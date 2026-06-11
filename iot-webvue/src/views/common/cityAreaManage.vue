<template>
  <div class="city-area-manage">
    <div class="row">
      <div class="col-md-3">
        <el-card shadow="never" class="tree-card">
          <CityAreaTree
            ref="treeRef"
            @node-click="handleNodeClick"
            @open-add="handleOpenAdd"
            @open-edit="handleOpenEdit"
            @open-delete="handleOpenDelete"
          />
        </el-card>
      </div>
      <div class="col-md-9">
        <el-card shadow="never" class="detail-card">
          <div slot="header" class="card-header">
            <span>地区详情</span>
          </div>
          <div v-if="detail.id" class="detail-content">
            <el-form :model="detail" label-width="100px">
              <el-row :gutter="20">
                <el-col :span="8">
                  <el-form-item label="地区名称：">
                    <span>{{ detail.areaName || '-' }}</span>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="地区编码：">
                    <span>{{ detail.areaCode || '-' }}</span>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="等级：">
                    <span v-if="detail.areaLevel === '101'">省级</span>
                    <span v-else-if="detail.areaLevel === '202'">市州</span>
                    <span v-else-if="detail.areaLevel === '303'">区县</span>
                    <span v-else>地区</span>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="父级编码：">
                    <span>{{ detail.parentAreaCode || '-' }}</span>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="父级名称：">
                    <span>{{ detail.parentAreaName || '-' }}</span>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="创建时间：">
                    <span>{{ detail.createTime || '-' }}</span>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
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
  padding: 16px;
}
.tree-card {
  min-height: 500px;
}
.detail-card {
  min-height: 500px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.empty-detail {
  text-align: center;
  color: #909399;
  padding: 100px 0;
  font-size: 14px;
}
</style>
