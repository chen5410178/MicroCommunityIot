<template>
  <el-dialog
    :visible.sync="dialogVisible"
    title="修改人员"
    width="700px"
    append-to-body
    @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-row>
        <el-col :span="12">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="form.name" placeholder="必填，请填写姓名" maxLength="10" @input="form.name = form.name.replace(/\s+/g, '')" />
          </el-form-item>
          <el-form-item label="性别" prop="sex">
            <el-select v-model="form.sex" placeholder="必填，请选择性别" style="width: 100%">
              <el-option label="男" value="0" />
              <el-option label="女" value="1" />
            </el-select>
          </el-form-item>
          <el-form-item label="类型" prop="ownerTypeCd">
            <el-select v-model="form.ownerTypeCd" placeholder="必填，请选择类型" style="width: 100%">
              <el-option label="业主" value="1001" />
              <el-option label="家庭成员" value="1002" />
              <el-option label="租客" value="1003" />
              <el-option label="其他" value="1005" />
            </el-select>
          </el-form-item>
          <el-form-item label="身份证" prop="idCard">
            <el-input v-model="form.idCard" placeholder="可选，请填写身份证" @change="handleIdCardChange" />
          </el-form-item>
          <el-form-item label="年龄" v-if="form.age">
            <span>{{ form.age }}</span>
          </el-form-item>
          <el-form-item label="家庭住址">
            <el-input v-model="form.address" placeholder="可填，请填写家庭住址" />
          </el-form-item>
        </el-col>
        <el-col :span="12" style="text-align:center">
          <div style="width: 100%; min-height: 150px;">
            <div v-show="form.ownerPhotoUrl">
              <img width="80%" height="150" :src="form.ownerPhotoUrl" alt="人员照片" @error="form.ownerPhotoUrl = ''" />
            </div>
            <div v-show="!form.ownerPhotoUrl && videoPlaying">
              <video ref="videoRef" style="width: 80%; height: 150px;" autoplay playsinline />
              <canvas ref="canvasRef" style="display:none;" />
            </div>
            <div v-show="!form.ownerPhotoUrl && !videoPlaying">
              <img width="80%" height="150" src="/img/noPhoto.jpg" alt="默认照片" />
            </div>
          </div>
          <div style="margin: 10px auto 0 auto;">
            <el-button size="small" v-if="form.ownerPhotoUrl" @click="reOpenCamera">重新拍照</el-button>
            <el-button size="small" type="primary" v-if="videoPlaying && !form.ownerPhotoUrl" @click="takePhoto">拍照</el-button>
            <el-button size="small" @click="handleUploadClick">上传照片</el-button>
            <input type="file" accept=".jpg" ref="fileInput" hidden @change="handleFileChange" />
          </div>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="手机" prop="link">
            <el-input v-model="form.link" placeholder="必填，请填写联系方式" maxLength="11" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" placeholder="可填，请填写备注" maxLength="200" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="门禁钥匙">
            <el-input v-model="form.cardNumber" placeholder="可填，请填写门禁钥匙" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-for="(item, index) in attrs" :key="item.specCd">
        <el-col :span="12" v-if="index % 2 === 0">
          <el-form-item :label="item.specName" v-if="item.specType === '2233'">
            <el-input v-model="item.value" :placeholder="item.specHoldplace" />
          </el-form-item>
          <el-form-item :label="item.specName" v-if="item.specType === '3344'">
            <el-select v-model="item.value" :placeholder="item.specHoldplace" style="width: 100%">
              <el-option v-for="v in item.values" :key="v.value" :label="v.valueName" :value="v.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="index + 1 < attrs.length && (index + 1) % 2 !== 0">
          <el-form-item :label="attrs[index + 1].specName" v-if="attrs[index + 1].specType === '2233'">
            <el-input v-model="attrs[index + 1].value" :placeholder="attrs[index + 1].specHoldplace" />
          </el-form-item>
          <el-form-item :label="attrs[index + 1].specName" v-if="attrs[index + 1].specType === '3344'">
            <el-select v-model="attrs[index + 1].value" :placeholder="attrs[index + 1].specHoldplace" style="width: 100%">
              <el-option v-for="v in attrs[index + 1].values" :key="v.value" :label="v.valueName" :value="v.value" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <span slot="footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit" :loading="loading">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { updateOwner } from '@/api/owner/ownerApi'
import { listAttrSpecs, listAttrValues } from '@/api/dev/devApi'
import { getCurrentCommunity } from '@/utils/auth'
import request from '@/utils/request'

export default {
  name: 'EditOwner',
  props: {
    visible: { type: Boolean, default: false },
    editData: { type: Object, default: null }
  },
  data() {
    const validatePhone = (rule, value, callback) => {
      if (!value) { callback(new Error('手机号不能为空')); return }
      if (!/^1[3-9]\d{9}$/.test(value)) { callback(new Error('手机号格式错误')); return }
      callback()
    }
    const validateName = (rule, value, callback) => {
      if (!value) { callback(new Error('名称不能为空')); return }
      if (value.length < 2 || value.length > 64) { callback(new Error('名称长度必须在2位至64位')); return }
      callback()
    }
    const validateIdCard = (rule, value, callback) => {
      if (!value) { callback(); return }
      if (value.length > 64) { callback(new Error('身份证格式不对')); return }
      const format = /^(([1][1-5])|([2][1-3])|([3][1-7])|([4][1-6])|([5][0-4])|([6][1-5])|([7][1])|([8][1-2]))\d{4}(([1][9]\d{2})|([2]\d{3}))(([0][1-9])|([1][0-2]))(([0][1-9])|([1-2][0-9])|([3][0-1]))\d{3}[0-9xX]$/
      if (!format.test(value)) { callback(new Error('身份证号码不合规')); return }
      const year = value.substr(6, 4)
      const month = value.substr(10, 2)
      const date = value.substr(12, 2)
      const time = Date.parse(month + '-' + date + '-' + year)
      const now_time = Date.parse(new Date())
      const dates = (new Date(year, month, 0)).getDate()
      if (time > now_time || date > dates) { callback(new Error('身份证号码不合规')); return }
      const c = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]
      const b = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2']
      const idArray = value.split('')
      let sum = 0
      for (let k = 0; k < 17; k++) { sum += parseInt(idArray[k]) * c[k] }
      if (idArray[17].toUpperCase() !== b[sum % 11].toUpperCase()) { callback(new Error('身份证校验码不合规')); return }
      callback()
    }
    return {
      dialogVisible: false,
      loading: false,
      attrs: [],
      videoPlaying: false,
      mediaStream: null,
      form: {
        ownerId: '', memberId: '', name: '', link: '', idCard: '',
        age: '', sex: '', ownerTypeCd: '', address: '', remark: '', cardNumber: '',
        ownerPhoto: '', ownerPhotoUrl: ''
      },
      rules: {
        name: [{ required: true, validator: validateName, trigger: 'blur' }],
        sex: [{ required: true, message: '性别不能为空', trigger: 'change' }],
        link: [{ required: true, validator: validatePhone, trigger: 'blur' }],
        idCard: [{ validator: validateIdCard, trigger: 'change' }],
        remark: [{ max: 200, message: '备注长度不能超过200位', trigger: 'blur' }]
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val
      if (val && this.editData) {
        this.loadAttrSpecs().then(() => {
          this.form = { ...this.editData }
          this.form.ownerPhoto = (this.editData.urls && this.editData.urls.length > 0) ? this.editData.urls[0] : ''
          this.form.ownerPhotoUrl = this.editData.url || ''
          if (this.editData.ownerAttrDtos && this.editData.ownerAttrDtos.length > 0) {
            this.editData.ownerAttrDtos.forEach(attrItem => {
              this.attrs.forEach(attr => {
                if (attrItem.specCd === attr.specCd) {
                  attr.attrId = attrItem.attrId
                  attr.value = attrItem.value
                }
              })
            })
          }
          if (this.form.idCard) {
            this.calcAgeFromIdCard(this.form.idCard)
          }
        })
      }
    },
    dialogVisible(val) { this.$emit('update:visible', val) }
  },
  methods: {
    startCamera() {
      const getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia || navigator.msGetUserMedia
      if (!getUserMedia) {
        this.videoPlaying = false
        return
      }
      if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
        navigator.mediaDevices.getUserMedia({ video: true, audio: false }).then(stream => {
          this.handleStream(stream)
        }).catch(() => {
          this.videoPlaying = false
        })
      } else {
        getUserMedia.call(navigator, { video: true, audio: false }, stream => {
          this.handleStream(stream)
        }, () => {
          this.videoPlaying = false
        })
      }
    },
    handleStream(stream) {
      this.mediaStream = stream
      this.videoPlaying = true
      this.$nextTick(() => {
        const video = this.$refs.videoRef
        if (!video) return
        try {
          video.srcObject = stream
        } catch (e) {
          const url = window.URL || window.webkitURL
          video.src = url ? url.createObjectURL(stream) : stream
        }
        video.play()
      })
    },
    closeCamera() {
      if (this.mediaStream) {
        this.mediaStream.getTracks().forEach(track => track.stop())
        this.mediaStream = null
      }
      this.videoPlaying = false
    },
    takePhoto() {
      const video = this.$refs.videoRef
      const canvas = this.$refs.canvasRef
      if (!video || !canvas) return
      let w = video.videoWidth
      let h = video.videoHeight
      if (h > 1080 || w > 1080) {
        const rate = h > w ? h / 1080 : w / 1080
        h = Math.floor(h / rate)
        w = Math.floor(w / rate)
      }
      canvas.width = w
      canvas.height = h
      canvas.getContext('2d').drawImage(video, 0, 0, w, h)
      canvas.toBlob(blob => {
        if (blob) { this.uploadBlob(blob) }
      }, 'image/jpeg', 0.3)
      this.closeCamera()
    },
    reOpenCamera() {
      this.form.ownerPhoto = ''
      this.form.ownerPhotoUrl = ''
      this.$nextTick(() => { this.startCamera() })
    },
    uploadBlob(blob) {
      const formData = new FormData()
      formData.append('uploadFile', blob, 'photo.jpg')
      const community = getCurrentCommunity()
      if (community) { formData.append('communityId', community.communityId) }
      request({ url: '/upload/uploadFile', method: 'post', data: formData, headers: { 'Content-Type': 'multipart/form-data' } }).then(res => {
        if (res.code === 0) {
          this.form.ownerPhoto = res.data && res.data.fileId
          this.form.ownerPhotoUrl = res.data && res.data.url
        } else {
          this.$message.error(res.msg || '上传失败')
        }
      }).catch(() => { this.$message.error('上传失败') })
    },
    async loadAttrSpecs() {
      try {
        const res = await listAttrSpecs({ specCd: 'building_owner_attr', page: 1, row: 100 })
        const specs = res.data || []
        this.attrs = []
        specs.forEach(item => {
          if (item.specShow === 'Y') {
            const attr = { ...item, value: '', values: [], attrId: '' }
            this.attrs.push(attr)
            if (item.specType === '3344') {
              this.loadAttrValues(item.specCd, attr.values)
            }
          }
        })
      } catch (e) { /* ignore */ }
    },
    async loadAttrValues(specCd, values) {
      try {
        const res = await listAttrValues({ specCd, page: 1, row: 100 })
        const list = res.data || []
        list.forEach(item => {
          if (item.valueShow === 'Y') { values.push(item) }
        })
      } catch (e) { /* ignore */ }
    },
    handleUploadClick() { this.$refs.fileInput && this.$refs.fileInput.click() },
    async handleFileChange(e) {
      const file = e.target.files && e.target.files[0]
      if (!file) return
      if (file.size > 1024 * 1024) { this.$message.error('图片大小不能超过 1MB!'); return }
      const formData = new FormData()
      formData.append('uploadFile', file)
      const community = getCurrentCommunity()
      if (community) { formData.append('communityId', community.communityId) }
      try {
        const res = await request({ url: '/upload/uploadFile', method: 'post', data: formData, headers: { 'Content-Type': 'multipart/form-data' } })
        if (res.code === 0) {
          this.form.ownerPhoto = res.data && res.data.fileId
          this.form.ownerPhotoUrl = res.data && res.data.url
          this.closeCamera()
        } else {
          this.$message.error(res.msg || '上传失败')
        }
      } catch (e) { this.$message.error('上传失败') }
      this.$refs.fileInput.value = ''
    },
    calcAgeFromIdCard(idCard) {
      const format = /^(([1][1-5])|([2][1-3])|([3][1-7])|([4][1-6])|([5][0-4])|([6][1-5])|([7][1])|([8][1-2]))\d{4}(([1][9]\d{2})|([2]\d{3}))(([0][1-9])|([1][0-2]))(([0][1-9])|([1-2][0-9])|([3][0-1]))\d{3}[0-9xX]$/
      if (!format.test(idCard)) return
      const year = parseInt(idCard.substr(6, 4))
      const month = parseInt(idCard.substr(10, 2))
      const date = parseInt(idCard.substr(12, 2))
      const today = new Date()
      let age = today.getFullYear() - year
      const m = today.getMonth() + 1
      if (m < month || (m === month && today.getDate() < date)) { age-- }
      this.form.age = String(age)
    },
    handleIdCardChange() {
      const idCard = this.form.idCard
      if (!idCard) return
      const format = /^(([1][1-5])|([2][1-3])|([3][1-7])|([4][1-6])|([5][0-4])|([6][1-5])|([7][1])|([8][1-2]))\d{4}(([1][9]\d{2})|([2]\d{3}))(([0][1-9])|([1][0-2]))(([0][1-9])|([1-2][0-9])|([3][0-1]))\d{3}[0-9xX]$/
      if (!format.test(idCard)) return
      const year = idCard.substr(6, 4)
      const month = idCard.substr(10, 2)
      const date = idCard.substr(12, 2)
      const time = Date.parse(month + '-' + date + '-' + year)
      const now_time = Date.parse(new Date())
      const dates = (new Date(year, month, 0)).getDate()
      if (time > now_time || date > dates) return
      const c = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2]
      const b = ['1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2']
      const idArray = idCard.split('')
      let sum = 0
      for (let k = 0; k < 17; k++) { sum += parseInt(idArray[k]) * c[k] }
      if (idArray[17].toUpperCase() !== b[sum % 11].toUpperCase()) return
      this.form.sex = (parseInt(idCard.substr(16, 1)) % 2 === 0) ? '1' : '0'
      this.calcAgeFromIdCard(idCard)
    },
    async handleSubmit() {
      try { await this.$refs.formRef.validate() } catch { return }
      this.loading = true
      try {
        const community = getCurrentCommunity()
        if (this.form.ownerPhotoUrl && this.form.ownerPhotoUrl.indexOf('http') !== -1) {
          this.form.ownerPhotoUrl = ''
        }
        const data = { ...this.form, communityId: community ? community.communityId : '' }
        if (data.name) data.name = data.name.trim()
        if (data.link) data.link = data.link.trim()
        if (data.idCard) data.idCard = data.idCard.trim()
        if (data.address) data.address = data.address.trim()
        if (data.remark) data.remark = data.remark.trim()
        if (data.cardNumber) data.cardNumber = data.cardNumber.trim()
        data.attrs = this.attrs.map(attr => ({ attrId: attr.attrId, specCd: attr.specCd, specId: attr.specId, value: attr.value }))
        await updateOwner(data)
        this.$message.success('修改成功')
        this.$emit('success')
        this.handleClose()
      } catch (e) { this.$message.error(e.msg || '修改失败') }
      finally { this.loading = false }
    },
    handleClose() {
      this.dialogVisible = false
      this.closeCamera()
      this.$refs.formRef && this.$refs.formRef.resetFields()
      this.attrs = []
    }
  }
}
</script>
