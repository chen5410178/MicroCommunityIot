<template>
  <div class="login-page">
    <div class="logo">
      <img src="@/assets/logo.png" alt="logo" />
    </div>
    <h2 class="headerfont">&nbsp;</h2>
    <div class="login">
      <div class="app">
        <img src="@/assets/logico.png" alt="" />
      </div>
      <div class="log">
        <h3>{{ systemTitle || 'HCIOT' }}</h3>
        <div>
          <input
            type="text"
            v-model="loginForm.username"
            placeholder="请输入用户名"
            @keyup.enter="handleLogin"
          />
        </div>
        <div>
          <input
            type="password"
            v-model="loginForm.passwd"
            maxlength="64"
            placeholder="请输入密码"
            @keyup.enter="handleLogin"
          />
        </div>
        <div class="slider-verify-wrapper">
          <SliderVerify ref="sliderRef" @success="onSliderSuccess" />
        </div>
        <div class="p-button">
          <button @click="handleLogin" :disabled="loading">
            {{ loading ? '登录中...' : '登 录' }}
          </button>
        </div>
      </div>
    </div>
    <div class="copyright">
      &copy;2020-2025
      <span>{{ companyName || 'java110团队' }}</span>
    </div>
  </div>
</template>

<script>
import { getSysInfo } from '@/api/login/loginApi'
import { listSystemInfo } from '@/api/login/systemApi'
import { listMyStore, listMyEnteredCommunitys, queryStaffPrivileges } from '@/api/community/communityApi'
import SliderVerify from '@/components/frame/SliderVerify'

export default {
  name: 'Login',
  components: { SliderVerify },
  data() {
    return {
      systemTitle: '',
      companyName: '',
      sliderX: null,
      loginForm: {
        username: '',
        passwd: ''
      },
      loading: false
    }
  },
  mounted() {
    this.loadSysInfo()
  },
  methods: {
    loadSysInfo() {
      listSystemInfo({ page: 1, row: 1 }).then(res => {
        let data = res
        if (typeof data === 'string') {
          try { data = JSON.parse(data) } catch (e) {}
        }
        if (data && data.data && data.data.length > 0) {
          const sysInfo = data.data[0]
          this.systemTitle = sysInfo.systemTitle
          this.companyName = sysInfo.companyName
          this.$store.commit('app/SET_SYSTEM_INFO', sysInfo)
        }
      }).catch(() => {
        this.systemTitle = 'HCIOT'
      })
    },
    onSliderSuccess(sliderX) {
      this.sliderX = sliderX
      this.handleLogin()
    },
    handleLogin() {
      if (!this.loginForm.username) {
        this.$message.warning('用户名不能为空')
        return
      }
      if (!this.loginForm.passwd) {
        this.$message.warning('密码不能为空')
        return
      }
      if (!this.sliderX && this.sliderX !== 0) {
        this.$message.warning('请完成滑块验证')
        return
      }
      this.loading = true
      this.$store.dispatch('user/login', {
        username: this.loginForm.username,
        passwd: this.loginForm.passwd,
        sliderX: this.sliderX
      }).then(() => {
        this.loadPostLoginData()
      }).catch(err => {
        this.loading = false
        this.sliderX = null
        const msg = err && err.msg ? err.msg : '登录失败'
        this.$message.error(msg)
        this.$refs.sliderRef && this.$refs.sliderRef.refresh()
      })
    },
    loadPostLoginData() {
      listMyStore({ _uId: 'ccdd00opikookjuhyyttvhnnjuuu' }).then(() => {
        return queryStaffPrivileges({ a: 'HC' })
      }).then(res => {
        const privileges = []
        if (res && res.datas) {
          res.datas.forEach(item => privileges.push(item.pId))
        }
        this.$store.commit('user/SET_PRIVILEGES', privileges)
        return listMyEnteredCommunitys({ _uId: 'ccdd00opikookjuhyyttvhnnjuuu', page: 1, row: 3 })
      }).then(res => {
        const communityList = res && res.data ? res.data : []
        if (communityList.length > 0) {
          this.$store.commit('app/SET_COMMUNITY_LIST', communityList)
          this.$store.commit('app/SET_CURRENT_COMMUNITY', communityList[0])
          const redirect = this.$route.query.redirect || '/pages/index/index'
          this.$router.push(redirect)
        } else {
          this.$message.warning('运营团队未分配小区，请联系运营团队')
          this.loading = false
        }
      }).catch(err => {
        this.loading = false
        this.sliderX = null
        const msg = err && err.msg ? err.msg : '初始化数据失败'
        this.$message.error(msg)
        this.$refs.sliderRef && this.$refs.sliderRef.refresh()
      })
    }
  }
}
</script>

<style src="@/styles/login.css"></style>
