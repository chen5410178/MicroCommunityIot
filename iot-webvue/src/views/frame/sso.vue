<template>
  <div class="sso-page">
    <div class="sso-card">
      <div v-if="loading" class="sso-loading">
        <i class="el-icon-loading"></i>
        <p>正在认证中，请稍候...</p>
      </div>
      <div v-else-if="errMsg" class="sso-error">
        <i class="el-icon-error"></i>
        <p>{{ errMsg }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import { ssoTokenLogin } from '@/api/frame/ssoApi'
import { listMyEnteredCommunitys } from '@/api/community/communityApi'
import { setUserInfo, setCommunityList, setCurrentCommunity } from '@/utils/auth'

export default {
  name: 'SsoPage',
  data() {
    return {
      loading: true,
      errMsg: ''
    }
  },
  mounted() {
    this.handleSsoLogin()
  },
  methods: {
    handleSsoLogin() {
      const token = this.$route.query.token
      const targetUrl = this.$route.query.targetUrl
      const communityId = this.$route.query.communityId || ''

      if (!token) {
        this.errMsg = '未包含token，请联系系统管理员'
        this.loading = false
        return
      }
      if (!targetUrl) {
        this.errMsg = '未包含targetUrl或未用encodeURIComponent编码'
        this.loading = false
        return
      }

      ssoTokenLogin({ hcAccessToken: token }).then(res => {
        let data = res
        if (typeof data === 'string') {
          try { data = JSON.parse(data) } catch (e) { data = res }
        }
        if (data.code !== 0) {
          this.errMsg = data.msg || '认证失败'
          this.loading = false
          return
        }
        if (data.data) {
          setUserInfo(data.data)
        }
        this.loadCommunityInfo(targetUrl, communityId)
      }).catch(err => {
        const msg = (err && err.msg) ? err.msg : '认证失败'
        this.errMsg = msg
        this.loading = false
      })
    },
    loadCommunityInfo(targetUrl, communityId) {
      listMyEnteredCommunitys({ _uId: 'ccdd00opikookjuhyyttvhnnjuuu', page: 1, row: 3 }).then(res => {
        let data = res
        if (typeof data === 'string') {
          try { data = JSON.parse(data) } catch (e) { data = res }
        }
        const communityList = (data && data.data) ? data.data : []
        if (communityList.length > 0) {
          setCommunityList(communityList)
          let targetCommunity = communityList[0]
          if (communityId) {
            const matched = communityList.find(c => c.communityId === communityId)
            if (matched) targetCommunity = matched
          }
          setCurrentCommunity(targetCommunity)
        }
        const decodedUrl = decodeURIComponent(targetUrl)
        this.$router.replace(decodedUrl)
      }).catch(() => {
        const decodedUrl = decodeURIComponent(targetUrl)
        this.$router.replace(decodedUrl)
      })
    }
  }
}
</script>

<style scoped>
.sso-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f0f2f5;
}
.sso-card {
  padding: 40px 60px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  text-align: center;
  min-width: 360px;
}
.sso-loading {
  color: #409eff;
}
.sso-loading i {
  font-size: 48px;
  margin-bottom: 16px;
}
.sso-loading p {
  font-size: 16px;
}
.sso-error {
  color: #f56c6c;
}
.sso-error i {
  font-size: 48px;
  margin-bottom: 16px;
}
.sso-error p {
  font-size: 16px;
}
</style>
