<template>
  <div class="add-event-staff">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>添加事件通知员工</span>
        <el-button size="small" @click="$router.back()">返回</el-button>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="物业员工" prop="staffIdAndNameList">
          <div class="staff-area">
            <el-input
              v-model="staffNameView"
              type="textarea"
              :rows="5"
              readonly
              placeholder="请点击选择按钮选择员工"
              style="width: calc(100% - 80px)"
            />
            <el-button style="margin-left: 10px" @click="handleSelectStaff">选择</el-button>
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
      :visible.sync="staffVisible"
      title="选择员工"
      width="700px"
      @close="staffVisible = false"
    >
      <el-table
        :data="staffList"
        v-loading="staffLoading"
        border
        stripe
        @selection-change="handleStaffSelectionChange"
        ref="staffTable"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="userId" label="员工ID" min-width="120" />
        <el-table-column prop="name" label="员工名称" min-width="150" />
        <el-table-column prop="tel" label="联系电话" min-width="120" />
      </el-table>
      <el-pagination
        :current-page="staffPage"
        :page-size="staffRow"
        :total="staffTotal"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleStaffSizeChange"
        @current-change="handleStaffPageChange"
        class="pagination-container"
      />
      <span slot="footer">
        <el-button @click="staffVisible = false">取消</el-button>
        <el-button type="primary" @click="handleStaffConfirm">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { saveEventStaff, listCommunityStaffs } from '@/api/event/eventApi'
import { getCurrentCommunity } from '@/utils/auth'

export default {
  name: 'AddEventStaff',
  data() {
    return {
      loading: false,
      ruleId: '',
      staffList: [],
      staffLoading: false,
      staffVisible: false,
      staffPage: 1,
      staffRow: 10,
      staffTotal: 0,
      selectedStaffs: [],
      form: {
        ruleId: '',
        communityId: '',
        state: '',
        remark: '',
        staffIdAndNameList: []
      },
      rules: {
        staffIdAndNameList: [
          {
            validator: (rule, value, callback) => {
              if (!value || value.length === 0) {
                callback(new Error('物业员工不能为空'))
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
    staffNameView() {
      return this.form.staffIdAndNameList.map(item => item.staffName).join('\n')
    }
  },
  mounted() {
    this.ruleId = this.$route.query.ruleId || ''
    this.form.ruleId = this.ruleId
  },
  methods: {
    async fetchStaffList() {
      this.staffLoading = true
      try {
        const community = getCurrentCommunity()
        const res = await listCommunityStaffs({
          page: this.staffPage,
          row: this.staffRow,
          communityId: community.communityId
        })
        this.staffList = res.data || []
        this.staffTotal = res.total || 0
      } catch {
        this.staffList = []
        this.staffTotal = 0
      } finally {
        this.staffLoading = false
      }
    },
    handleSelectStaff() {
      this.selectedStaffs = []
      this.staffPage = 1
      this.fetchStaffList()
      this.staffVisible = true
    },
    handleStaffSelectionChange(selection) {
      this.selectedStaffs = selection
    },
    handleStaffConfirm() {
      const newItems = this.selectedStaffs.map(s => ({
        staffId: s.userId,
        staffName: s.name
      }))
      const existingIds = new Set(this.form.staffIdAndNameList.map(item => item.staffId))
      const uniqueNew = newItems.filter(item => !existingIds.has(item.staffId))
      this.form.staffIdAndNameList = [...this.form.staffIdAndNameList, ...uniqueNew]
      this.staffVisible = false
    },
    handleStaffSizeChange(val) {
      this.staffRow = val
      this.fetchStaffList()
    },
    handleStaffPageChange(val) {
      this.staffPage = val
      this.fetchStaffList()
    },
    async handleSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch { return }
      this.loading = true
      try {
        const community = getCurrentCommunity()
        await saveEventStaff({
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
.staff-area { display: flex; align-items: flex-start; }
.pagination-container { margin-top: 16px; text-align: right; }
</style>
