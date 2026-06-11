<template>
  <div class="list-config-menu-manage">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>配置菜单</span>
      </div>
      <el-steps :active="index" align-center finish-status="success" class="steps-container">
        <el-step title="选择菜单组" />
        <el-step title="菜单信息" />
        <el-step title="权限信息" />
      </el-steps>
      <div class="step-content">
        <ViewMenuGroupInfo ref="step0Ref" v-if="index === 0" @notify="handleNotify" />
        <AddMenuView ref="step1Ref" v-if="index === 1" @notify="handleNotify" />
        <AddPrivilegeView ref="step2Ref" v-if="index === 2" @notify="handleNotify" />
      </div>
      <div class="step-footer">
        <el-button v-if="index > 0" @click="handlePrev">上一步</el-button>
        <el-button v-if="index < 2" type="primary" @click="handleNext">下一步</el-button>
        <el-button v-if="index === 2" type="primary" @click="handleFinish" :loading="submitLoading">完成</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { configMenu } from '@/api/dev/devApi'
import ViewMenuGroupInfo from '@/components/dev/ViewMenuGroupInfo.vue'
import AddMenuView from '@/components/dev/AddMenuView.vue'
import AddPrivilegeView from '@/components/dev/AddPrivilegeView.vue'

export default {
  name: 'ListConfigMenuManage',
  components: { ViewMenuGroupInfo, AddMenuView, AddPrivilegeView },
  data() {
    return {
      index: 0,
      infos: [],
      submitLoading: false
    }
  },
  methods: {
    handleNotify(data) {
      this.infos[this.index] = data
    },
    handlePrev() {
      if (this.index > 0) {
        this.index--
      }
    },
    handleNext() {
      const currentData = this.infos[this.index]
      if (!currentData) {
        this.$message.warning('请选择或填写必选信息')
        return
      }
      if (this.index < 2) {
        this.index++
      }
    },
    async handleFinish() {
      const currentData = this.infos[this.index]
      if (!currentData) {
        this.$message.warning('请选择或填写必选信息')
        return
      }
      if (this.infos.length < 3) {
        this.$message.warning('请完善所有步骤信息')
        return
      }
      this.submitLoading = true
      try {
        await configMenu({ data: this.infos })
        this.$message.success('处理成功')
        this.$router.go(-1)
      } catch (e) {
        this.$message.error(e.msg || '处理失败')
      } finally {
        this.submitLoading = false
      }
    }
  }
}
</script>

<style scoped>
.steps-container { margin-bottom: 30px; }
.step-content { padding: 20px 0; }
.step-footer { text-align: center; padding: 20px 0; }
</style>
