import axios from 'axios'
import { v4 as uuidv4 } from 'uuid'

function formatDateTime(date) {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  const h = String(date.getHours()).padStart(2, '0')
  const min = String(date.getMinutes()).padStart(2, '0')
  const s = String(date.getSeconds()).padStart(2, '0')
  return `${y}${m}${d}${h}${min}${s}`
}

const service = axios.create({
  baseURL: '/iot/api',
  timeout: 30000
})

service.interceptors.request.use(config => {
  config.headers['APP-ID'] = '992023010100000001'
  config.headers['TRANSACTION-ID'] = uuidv4()
  config.headers['REQ-TIME'] = formatDateTime(new Date())
  const userInfo = sessionStorage.getItem('iot_webvue_user')
  const userId = userInfo ? (JSON.parse(userInfo).userId || '-1') : '-1'
  config.headers['USER-ID'] = userId
  config.headers['SIGN'] = ''
  config.headers['JAVA110-LANG'] = sessionStorage.getItem('iot_webvue_lang') || 'zh-cn'
  return config
})

service.interceptors.response.use(
  response => {
    if (response.headers['location']) {
      window.location.href = response.headers['location']
    }
    if (typeof response.data === 'string') {
      try {
        response.data = JSON.parse(response.data)
      } catch (e) {
        return response.data
      }
    }
    return response.data
  },
  error => {
    if (error.response && error.response.status === 401) {
      sessionStorage.clear()
      window.location.hash = '#/pages/frame/login'
    }
    if (error.response && error.response.data) {
      return Promise.reject(error.response.data)
    }
    return Promise.reject(error)
  }
)

export default service
