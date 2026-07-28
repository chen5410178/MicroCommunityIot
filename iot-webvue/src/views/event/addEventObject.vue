<template>
  <div class="add-event-object">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>添加事件对象</span>
        <el-button size="small" @click="$router.back()">返回</el-button>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="对象类型" prop="objType">
          <el-select v-model="form.objType" placeholder="必填，请选择对象类型" style="width: 100%" @change="handleObjTypeChange">
            <el-option v-for="item in objTypeList" :key="item.statusCd" :label="item.name" :value="item.statusCd" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备对象" prop="objectIdAndNameList" v-if="form.objType">
          <div class="device-area">
            <el-input
              v-model="objectNameView"
              type="textarea"
              :rows="5"
              readonly
              placeholder="请点击选择按钮选择设备"
              style="width: calc(100% - 80px)"
            />
            <el-button style="margin-left: 10px" @click="handleSelectDevice">选择</el-button>
          </div>
        </el-form-item>
        <el-form-item label="状态" prop="state">
          <el-select v-model="form.state" placeholder="必填，请选择状态" style="width: 100%">
            <el-option label="生效" value="ON" />
            <el-option label="失效" value="OFF" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="选填，请填写备注" maxlength="512" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-dialog
      :visible.sync="deviceVisible"
      title="选择设备"
      width="700px"
      @close="deviceVisible = false"
    >
      <el-table
        :data="deviceList"
        v-loading="deviceLoading"
        border
        stripe
        @selection-change="handleDeviceSelectionChange"
        ref="deviceTable"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="machineId" label="设备编号" min-width="120" />
        <el-table-column prop="machineName" label="设备名称" min-width="150" />
        <el-table-column prop="machineCode" label="设备编码" min-width="120" />
      </el-table>
      <el-pagination
        :current-page="devicePage"
        :page-size="deviceRow"
        :total="deviceTotal"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleDeviceSizeChange"
        @current-change="handleDevicePageChange"
        class="pagination-container"
      />
      <span slot="footer">
        <el-button @click="deviceVisible = false">取消</el-button>
        <el-button type="primary" @click="handleDeviceConfirm">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { saveEventObject } from '@/api/event/eventApi'
import { listDicts } from '@/api/system/systemApi'
import { getCurrentCommunity } from '@/utils/auth'
import request from '@/utils/request'

export default {
  name: 'AddEventObject',
  data() {
    return {
      loading: false,
      ruleId: '',
      objTypeList: [],
      deviceList: [],
      deviceLoading: false,
      deviceVisible: false,
      devicePage: 1,
      deviceRow: 10,
      deviceTotal: 0,
      selectedDevices: [],
      form: {
        ruleId: '',
        objType: '',
        communityId: '',
        state: '',
        remark: '',
        objectIdAndNameList: []
      },
      rules: {
        objType: [
          { required: true, message: '对象类型不能为空', trigger: 'change' }
        ],
        objectIdAndNameList: [
          {
            validator: (rule, value, callback) => {
              if (!value || value.length === 0) {
                callback(new Error('设备对象不能为空'))
              } else {
                callback()
              }
            },
            trigger: 'change'
          }
        ],
        state: [
          { required: true, message: '状态不能为空', trigger: 'change' }
        ],
        remark: [
          { max: 512, message: '备注不能超过512', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    objectNameView() {
      return this.form.objectIdAndNameList.map(item => item.objectName).join('\n')
    }
  },
  mounted() {
    this.ruleId = this.$route.query.ruleId || ''
    this.form.ruleId = this.ruleId
    this.loadObjTypeDict()
  },
  methods: {
    async loadObjTypeDict() {
      try {
        const res = await listDicts({ tableName: 'event_object', tableColumns: 'obj_type' })
        this.objTypeList = res || []
      } catch { /* ignore */ }
    },
    async handleObjTypeChange() {
      this.form.objectIdAndNameList = []
      this.selectedDevices = []
    },
    async fetchDeviceList() {
      this.deviceLoading = true
      try {
        const community = getCurrentCommunity()
        const res = await request({
          url: '/eventMachine.listEventMachine',
          method: 'get',
          params: {
            page: this.devicePage,
            row: this.deviceRow,
            objType: this.form.objType,
            communityId: community.communityId
          }
        })
        this.deviceList = res.data || []
        this.deviceTotal = res.total || 0
      } catch {
        this.deviceList = []
        this.deviceTotal = 0
      } finally {
        this.deviceLoading = false
      }
    },
    handleSelectDevice() {
      this.selectedDevices = []
      this.devicePage = 1
      this.fetchDeviceList()
      this.deviceVisible = true
    },
    handleDeviceSelectionChange(selection) {
      this.selectedDevices = selection
    },
    handleDeviceConfirm() {
      const newItems = this.selectedDevices.map(m => ({
        objectId: m.machineId,
        objectName: m.machineName
      }))
      const existingIds = new Set(this.form.objectIdAndNameList.map(item => item.objectId))
      const uniqueNew = newItems.filter(item => !existingIds.has(item.objectId))
      this.form.objectIdAndNameList = [...this.form.objectIdAndNameList, ...uniqueNew]
      this.deviceVisible = false
    },
    handleDeviceSizeChange(val) {
      this.deviceRow = val
      this.fetchDeviceList()
    },
    handleDevicePageChange(val) {
      this.devicePage = val
      this.fetchDeviceList()
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const community = getCurrentCommunity()
        await saveEventObject({
          ...this.form,
          communityId: community.communityId
        })
        this.$message.success('添加成功')
        this.$router.back()
      } catch (e) {
        this.$message.error(e.msg || '添加失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.device-area { display: flex; align-items: flex-start; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
