<template>
  <div class="edit-visit">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>修改访客</span>
        <el-button size="small" @click="goBack">返回</el-button>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" class="visit-form" v-loading="pageLoading">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" placeholder="必填，请填写姓名" maxlength="255" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="visitGender">
              <el-select v-model="form.visitGender" placeholder="必填，请选择性别" style="width: 100%">
                <el-option label="男" :value="0" />
                <el-option label="女" :value="1" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="电话号码" prop="phoneNumber">
              <el-input v-model="form.phoneNumber" placeholder="必填，请填写电话号码" maxlength="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="访客类型" prop="typeId">
              <el-select v-model="form.typeId" placeholder="必填，请选择访客类型" style="width: 100%">
                <el-option v-for="item in visitTypeList" :key="item.typeId" :label="item.name" :value="item.typeId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="拜访房屋" prop="roomName">
              <el-input v-model="form.roomName" disabled placeholder="必填，请选择拜访房屋">
                <el-button slot="append" @click="roomVisible = true">选择</el-button>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="拜访时间">
              <el-date-picker
                v-model="form.visitTime"
                type="datetime"
                placeholder="选填，请选择拜访时间"
                value-format="yyyy-MM-dd HH:mm:ss"
                clearable
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="通行车辆">
              <el-input v-model="form.carNum" placeholder="选填，请填写通行车牌" maxlength="20" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="拜访事由">
          <el-input v-model="form.visitCase" type="textarea" rows="4" placeholder="选填，请填写拜访事由" maxlength="255" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">保存</el-button>
          <el-button @click="goBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <RoomTree :visible="roomVisible" @confirm="onRoomSelected" />
  </div>
</template>

<script>
import { listVisits, updateVisit, listVisitTypes } from '@/api/accessControl/accessControlApi'
import RoomTree from '@/components/accessControl/RoomTree.vue'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'EditVisit',
  components: { RoomTree },
  data() {
    return {
      pageLoading: false,
      loading: false,
      roomVisible: false,
      visitTypeList: [],
      form: {
        visitId: '',
        name: '',
        visitGender: '',
        phoneNumber: '',
        typeId: '',
        roomName: '',
        roomId: '',
        visitTime: '',
        carNum: '',
        visitCase: '',
        state: '0',
        msg: ''
      },
      rules: {
        name: [
          { required: true, message: '姓名不能为空', trigger: 'blur' },
          { max: 255, message: '姓名不能超过255', trigger: 'blur' }
        ],
        visitGender: [{ required: true, message: '请选择性别', trigger: 'change' }],
        phoneNumber: [
          { required: true, message: '电话号码不能为空', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '电话号码格式错误', trigger: 'blur' }
        ],
        typeId: [{ required: true, message: '请选择访客类型', trigger: 'change' }],
        roomName: [{ required: true, message: '请选择拜访房屋', trigger: 'change' }],
        visitCase: [{ max: 255, message: '拜访事由不能超过255', trigger: 'blur' }]
      }
    }
  },
  mounted() {
    this.loadVisitTypes()
    const visitId = this.$route.query.visitId
    if (visitId) {
      this.form.visitId = visitId
      this.loadVisitData()
    }
  },
  methods: {
    async loadVisitTypes() {
      try {
        const res = await listVisitTypes({ page: -1, row: 100, communityId: (getCurrentCommunity() || {}).communityId })
        this.visitTypeList = res.data || []
      } catch (e) {
        console.error('加载访客类型失败', e)
      }
    },
    async loadVisitData() {
      this.pageLoading = true
      try {
        const res = await listVisits({
          visitId: this.form.visitId,
          page: 1,
          row: 1,
          communityId: (getCurrentCommunity() || {}).communityId
        })
        const data = res.data || []
        if (data.length > 0) {
          const visit = data[0]
          this.form = {
            ...this.form,
            ...visit
          }
        }
      } catch (e) {
        console.error('加载访客信息失败', e)
      } finally {
        this.pageLoading = false
      }
    },
    onRoomSelected(room) {
      this.form.roomId = room.roomId
      this.form.roomName = room.roomName
      this.roomVisible = false
    },
    goBack() {
      this.$router.go(-1)
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        await updateVisit({
          ...this.form,
          communityId: (getCurrentCommunity() || {}).communityId
        })
        this.$message.success('修改成功')
        this.$router.go(-1)
      } catch (e) {
        this.$message.error(e.msg || '修改失败')
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.visit-form { max-width: 900px; margin-top: 16px; }
</style>
