<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="预约电梯"
    width="600px"
    @close="handleClose"
  >
    <el-form :model="form" ref="formRef" label-width="110px">
      <el-form-item label="预约方式" prop="resWay" :rules="[{ required: true, message: '预约方式不能为空', trigger: 'change' }]">
        <el-select v-model="form.resWay" placeholder="请选择预约方式" clearable style="width: 100%">
          <el-option
            v-for="item in resWayList"
            :key="item.statusCd"
            :label="item.name"
            :value="item.statusCd"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="人员类型" prop="personWay" :rules="[{ required: true, message: '人员类型不能为空', trigger: 'change' }]">
        <el-select v-model="form.personWay" placeholder="请选择人员类型" clearable style="width: 100%">
          <el-option
            v-for="item in personWayList"
            :key="item.statusCd"
            :label="item.name"
            :value="item.statusCd"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="房屋" v-if="form.personWay && form.personWay !== '2002'" prop="roomName">
        <el-input v-model="form.roomName" disabled placeholder="必填，请选择房屋">
          <el-button slot="append" @click="openRoomTree">选择</el-button>
        </el-input>
      </el-form-item>
      <el-form-item label="目标楼层" v-if="form.personWay && form.personWay !== '2002'" prop="targetLayer">
        <el-input-number v-model="form.targetLayer" placeholder="必填，请输入目标楼层" :min="1" style="width: 100%" controls-position="right" />
      </el-form-item>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>

    <RoomTree :visible.sync="roomTreeVisible" @confirm="handleRoomSelected" />
  </el-dialog>
</template>

<script>
import { reservationLiftMachine } from '@/api/lift/liftApi'
import { listDicts } from '@/api/dev/devApi'
import RoomTree from '@/components/accessControl/RoomTree.vue'

export default {
  name: 'ReservationLiftMachine',
  components: { RoomTree },
  props: {
    visible: { type: Boolean, default: false },
    machineData: { type: Object, default: null }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      roomTreeVisible: false,
      resWayList: [],
      personWayList: [],
      form: {
        machineId: '',
        resWay: '',
        personWay: '',
        roomId: '',
        roomName: '',
        targetLayer: ''
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.machineData) {
        this.form.machineId = this.machineData.machineId || ''
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  mounted() {
    this.loadDicts()
  },
  methods: {
    async loadDicts() {
      try {
        const [resWayRes, personWayRes] = await Promise.all([
          listDicts({ tableName: 'lift_machine_reservation', columnName: 'res_way' }),
          listDicts({ tableName: 'lift_machine_reservation', columnName: 'person_way' })
        ])
        this.resWayList = resWayRes.data || []
        this.personWayList = personWayRes.data || []
      } catch (e) {
        console.error('加载字典失败', e)
      }
    },
    openRoomTree() {
      this.roomTreeVisible = true
    },
    handleRoomSelected(data) {
      this.form.roomId = data.roomId
      this.form.roomName = data.roomName
    },
    handleClose() {
      this.dialogVisible = false
      this.form = {
        machineId: '',
        resWay: '',
        personWay: '',
        roomId: '',
        roomName: '',
        targetLayer: ''
      }
    },
    async handleSubmit() {
      if (!this.form.resWay) { this.$message.error('请选择预约方式'); return }
      if (!this.form.personWay) { this.$message.error('请选择人员类型'); return }
      this.loading = true
      try {
        const userInfo = JSON.parse(sessionStorage.getItem('iot_webvue_user') || '{}')
        const data = {
          ...this.form,
          personId: userInfo.userId || '',
          personName: userInfo.name || '',
          orgLayer: this.machineData ? this.machineData.curLayer : ''
        }
        await reservationLiftMachine(data)
        this.$message.success('预约成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) {
        this.$message.error(e.msg || '预约失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>
