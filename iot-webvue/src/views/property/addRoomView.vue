<template>
  <div class="add-room-view">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>添加房屋</span>
        <el-button size="small" @click="$router.go(-1)">返回</el-button>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px" class="form-container">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="楼栋" prop="floorId">
              <el-select v-model="form.floorId" placeholder="请选择楼栋" style="width: 100%" @change="loadUnits">
                <el-option v-for="f in floors" :key="f.floorId" :label="f.floorNum + '栋'" :value="f.floorId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单元" prop="unitId">
              <el-select v-model="form.unitId" placeholder="请选择单元" style="width: 100%">
                <el-option v-for="u in units" :key="u.unitId" :label="u.unitNum + '单元'" :value="u.unitId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房屋编号" prop="roomNum">
              <el-input v-model="form.roomNum" placeholder="请填写房屋编号" maxlength="64" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="楼层" prop="layer">
              <el-input v-model="form.layer" placeholder="请填写楼层" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="房屋户型" prop="apartment">
              <el-input v-model="form.apartment" placeholder="请填写户型，如：10102" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房屋类型" prop="roomSubType">
              <el-select v-model="form.roomSubType" placeholder="请选择" style="width: 100%">
                <el-option v-for="item in roomSubTypes" :key="item.statusCd" :label="item.name" :value="item.statusCd" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" v-if="form.roomSubType !== '110'">
          <el-col :span="12">
            <el-form-item label="租金">
              <el-input v-model="form.roomRent" placeholder="请填写租金" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="售价">
              <el-input v-model="form.unitPrice" placeholder="请填写售价" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="建筑面积" prop="builtUpArea">
              <el-input v-model="form.builtUpArea" type="number" placeholder="请填写建筑面积" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="室内面积" prop="roomArea">
              <el-input v-model="form.roomArea" placeholder="请填写室内面积" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="算费系数" prop="feeCoefficient">
              <el-input v-model="form.feeCoefficient" placeholder="请填写算费系数" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房屋状态" prop="state">
              <el-select v-model="form.state" style="width: 100%">
                <el-option label="未销售" value="2002" />
                <el-option label="已入住" value="2001" />
                <el-option label="已交房" value="2003" />
                <el-option label="已装修" value="2005" />
                <el-option label="未入住" value="2004" />
                <el-option label="装修中" value="2009" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="业主" v-if="form.state !== '2002'">
          <el-input v-model="form.ownerName" placeholder="点击选择业主" readonly @focus="searchOwnerVisible = true">
            <el-button slot="append" icon="el-icon-search" @click="searchOwnerVisible = true" />
          </el-input>
        </el-form-item>
        <div v-for="attr in form.attrs" :key="attr.specCd">
          <el-form-item :label="attr.specName" :required="attr.required === 'Y'">
            <el-input v-if="attr.specType === '2233'" v-model="attr.value" :placeholder="attr.specHoldplace" />
            <el-select v-else-if="attr.specType === '3344'" v-model="attr.value" :placeholder="attr.specHoldplace" style="width: 100%">
              <el-option v-for="v in attr.values" :key="v.value" :label="v.valueName" :value="v.value" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="请填写备注信息" maxlength="200" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">保存</el-button>
          <el-button @click="$router.go(-1)">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 选择业主弹窗 -->
    <el-dialog :visible.sync="searchOwnerVisible" title="选择业主" width="700px" append-to-body>
      <el-form :inline="true" :model="ownerQuery">
        <el-form-item>
          <el-input v-model="ownerQuery.name" placeholder="业主名称" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="ownerQuery.link" placeholder="联系方式" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchOwners">搜索</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="ownerList" border stripe highlight-current-row @row-click="selectOwner">
        <el-table-column prop="ownerId" label="业主ID" width="120" />
        <el-table-column prop="name" label="名称" width="100" />
        <el-table-column prop="link" label="联系方式" width="120" />
        <el-table-column prop="idCard" label="身份证" width="180" />
      </el-table>
      <span slot="footer">
        <el-button @click="searchOwnerVisible = false">关闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { saveRoom } from '@/api/property/roomApi'
import { queryFloors } from '@/api/property/floorApi'
import { queryUnits } from '@/api/property/unitApi'
import { listDicts, listAttrSpecs, listAttrValues } from '@/api/property/propertyApi'
import { queryOwners } from '@/api/owner/ownerApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddRoomView',
  data() {
    return {
      loading: false,
      searchOwnerVisible: false,
      floors: [],
      units: [],
      roomSubTypes: [],
      ownerList: [],
      ownerQuery: { name: '', link: '' },
      form: {
        roomNum: '', layer: '', section: '0', apartment: '10102',
        builtUpArea: '', feeCoefficient: '1.00', state: '2002', remark: '',
        roomSubType: '110', roomArea: '', roomRent: '0', unitPrice: '0',
        floorId: '', unitId: '', ownerId: '', ownerName: '',
        communityId: '', attrs: []
      },
      rules: {
        floorId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
        unitId: [{ required: true, message: '请选择单元', trigger: 'change' }],
        roomNum: [{ required: true, message: '房屋编号不能为空', trigger: 'blur' }],
        layer: [{ required: true, message: '楼层不能为空', trigger: 'blur' }],
        apartment: [{ required: true, message: '户型不能为空', trigger: 'blur' }],
        builtUpArea: [{ required: true, message: '建筑面积不能为空', trigger: 'blur' }],
        roomArea: [{ required: true, message: '室内面积不能为空', trigger: 'blur' }],
        feeCoefficient: [{ required: true, message: '算费系数不能为空', trigger: 'blur' }],
        roomSubType: [{ required: true, message: '房屋类型不能为空', trigger: 'change' }],
        state: [{ required: true, message: '房屋状态不能为空', trigger: 'change' }]
      }
    }
  },
  mounted() {
    this.initData()
  },
  watch: {
    searchOwnerVisible(val) {
      if (val) {
        this.ownerQuery = { name: '', link: '' }
        this.ownerList = []
        this.searchOwners()
      }
    }
  },
  methods: {
    async initData() {
      const communityInfo = getCurrentCommunity() || {}
      this.form.communityId = communityInfo.communityId
      try {
        const floorRes = await queryFloors({ page: 1, row: 300 })
        this.floors = floorRes.data || []
      } catch (e) { /* ignore */ }
      try {
        const dictRes = await listDicts({ tableName: 'building_room', tableColumns: 'room_sub_type', page: 1, row: 500 })
        this.roomSubTypes = dictRes.data || []
      } catch (e) { /* ignore */ }
      try {
        const specRes = await listAttrSpecs({ tableName: 'building_room_attr', page: 1, row: 100 })
        const specs = Array.isArray(specRes) ? specRes : (specRes.data || [])
        this.form.attrs = specs.filter(s => s.specShow === 'Y').map(s => ({ ...s, value: '', values: [] }))
        for (const attr of this.form.attrs) {
          try {
            const valRes = await listAttrValues({ specCd: attr.specCd })
            attr.values = Array.isArray(valRes) ? valRes.filter(v => v.valueShow === 'Y') : ((valRes.data || []).filter(v => v.valueShow === 'Y'))
          } catch (e) { attr.values = [] }
        }
      } catch (e) { /* ignore */ }
    },
    async loadUnits() {
      if (!this.form.floorId) { this.units = []; return }
      try {
        const communityInfo = getCurrentCommunity() || {}
        const res = await queryUnits({ communityId: communityInfo.communityId, floorId: this.form.floorId, page: 1, row: 300 })
        this.units = Array.isArray(res) ? res : (res.data || [])
      } catch (e) { this.units = [] }
    },
    async searchOwners() {
      try {
        const communityInfo = getCurrentCommunity() || {}
        const res = await queryOwners({ ...this.ownerQuery, communityId: communityInfo.communityId, page: 1, row: 50, ownerTypeCd: '1001' })
        this.ownerList = Array.isArray(res) ? res : (res.data || [])
      } catch (e) { this.ownerList = [] }
    },
    selectOwner(row) {
      this.form.ownerId = row.memberId || row.ownerId
      this.form.ownerName = row.name
      this.searchOwnerVisible = false
    },
    async handleSubmit() {
      try { await this.$refs.formRef.validate() } catch { return }
      const missingAttr = this.form.attrs.find(a => a.required === 'Y' && !a.value)
      if (missingAttr) { this.$message.warning(missingAttr.specHoldplace || '请填写' + missingAttr.specName); return }
      this.loading = true
      try {
        await saveRoom(this.form)
        this.$message.success('添加成功')
        this.$router.go(-1)
      } catch (e) { this.$message.error(e.message || '添加失败') }
      finally { this.loading = false }
    }
  }
}
</script>

<style scoped>
.add-room-view {
  padding: 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.form-container {
  max-width: 900px;
  margin: 0 auto;
}
</style>
