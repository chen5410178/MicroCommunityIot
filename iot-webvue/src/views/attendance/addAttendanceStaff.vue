<template>
  <div class="add-attendance-staff">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>添加考勤员工</span>
        <el-button size="small" @click="handleBack">返回</el-button>
      </div>
      <el-form :model="form" ref="formRef" label-width="100px" style="max-width: 600px">
        <el-form-item label="员工名称" required>
          <el-input v-model="form.staffName" disabled placeholder="必填，请选择员工">
            <el-button slot="append" @click="openChooseStaff">选择</el-button>
          </el-input>
        </el-form-item>
        <el-form-item label="考勤人脸">
          <el-upload
            :show-file-list="false"
            :http-request="handleUpload"
            accept="image/*"
            action=""
          >
            <el-button size="small" type="primary">上传图片</el-button>
          </el-upload>
          <img v-if="form.facePath" :src="form.facePath" style="width: 100px; height: 100px; margin-top: 8px; object-fit: cover" />
        </el-form-item>
        <el-form-item>
          <el-button @click="handleBack">返回</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="loading">提交</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <ChooseAttendanceStaff :visible.sync="chooseVisible" @selected="onStaffSelected" />
  </div>
</template>

<script>
import { saveAttendanceStaff } from '@/api/attendance/attendanceApi'
import ChooseAttendanceStaff from '@/components/attendance/ChooseAttendanceStaff.vue'
import request from '@/utils/request'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddAttendanceStaff',
  components: { ChooseAttendanceStaff },
  data() {
    return {
      loading: false,
      chooseVisible: false,
      form: {
        machineId: '',
        staffId: '',
        staffName: '',
        facePath: ''
      }
    }
  },
  mounted() {
    this.form.machineId = this.$route.query.machineId || ''
  },
  methods: {
    openChooseStaff() {
      this.chooseVisible = true
    },
    onStaffSelected(staff) {
      this.form.staffId = staff.staffId
      this.form.staffName = staff.staffName
    },
    async handleUpload(param) {
      const formData = new FormData()
      formData.append('uploadFile', param.file)
      formData.append('communityId', (getCurrentCommunity() || {}).communityId)
      try {
        const res = await request({
          url: '/upload/uploadFile',
          method: 'post',
          data: formData,
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        if (res.code === 0) {
          this.form.facePath = res.data && res.data.url
          this.$message.success('上传成功')
        } else {
          this.$message.error(res.msg || '上传失败')
        }
      } catch {
        this.$message.error('上传失败')
      }
    },
    handleBack() {
      this.$router.back()
    },
    async handleSubmit() {
      if (!this.form.staffId) {
        this.$message.warning('请选择员工')
        return
      }
      if (!this.form.machineId) {
        this.$message.warning('设备ID不能为空')
        return
      }
      this.loading = true
      try {
        const data = {
          ...this.form,
          communityId: (getCurrentCommunity() || {}).communityId
        }
        await saveAttendanceStaff(data)
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
</style>
