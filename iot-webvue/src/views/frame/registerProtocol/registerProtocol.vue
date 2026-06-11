<template>
  <div class="register-protocol">
    <el-card shadow="never">
      <div slot="header" class="card-header">
        <span>注册协议</span>
      </div>
      <el-form label-width="100px">
        <el-form-item label="用户协议">
          <quill-editor
            v-model="form.userProtocol"
            :options="editorOptions"
            class="editor-container"
          />
        </el-form-item>
        <el-form-item label="商家协议">
          <quill-editor
            v-model="form.merchantProtocol"
            :options="editorOptions"
            class="editor-container"
          />
        </el-form-item>
        <el-form-item>
          <div class="btn-wrap">
            <el-button type="primary" :loading="loading" @click="handleUpdate">修改</el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { listRegisterProtocol, updateRegisterProtocol } from '@/api/login/systemApi'
import { quillEditor } from 'vue-quill-editor'
import 'quill/dist/quill.core.css'
import 'quill/dist/quill.snow.css'
import 'quill/dist/quill.bubble.css'

export default {
  name: 'RegisterProtocol',
  components: { quillEditor },
  data() {
    return {
      form: {
        protolcolId: '',
        userProtocol: '',
        merchantProtocol: ''
      },
      editorOptions: {
        modules: {
          toolbar: [
            ['bold', 'italic', 'underline', 'strike'],
            [{ color: [] }],
            [{ list: 'ordered' }, { list: 'bullet' }],
            ['link'],
            ['clean']
          ]
        },
        placeholder: '请输入内容',
        theme: 'snow'
      },
      loading: false
    }
  },
  mounted() {
    this.fetchData()
  },
  methods: {
    fetchData() {
      listRegisterProtocol({ page: 1, row: 1 }).then(res => {
        if (res && res.data && res.data.length > 0) {
          const data = res.data[0]
          this.form.protolcolId = data.protolcolId
          this.form.userProtocol = data.userProtocol || ''
          this.form.merchantProtocol = data.merchantProtocol || ''
        }
      }).catch(() => {
        this.$message.error('获取注册协议失败')
      })
    },
    handleUpdate() {
      this.loading = true
      updateRegisterProtocol({
        protolcolId: this.form.protolcolId,
        userProtocol: this.form.userProtocol,
        merchantProtocol: this.form.merchantProtocol
      }).then(res => {
        if (res.code === 0) {
          this.$message.success('修改成功')
          this.fetchData()
        } else {
          this.$message.error(res.msg || '修改失败')
        }
        this.loading = false
      }).catch(err => {
        this.loading = false
        const msg = err && err.msg ? err.msg : '修改失败'
        this.$message.error(msg)
      })
    }
  }
}
</script>

<style scoped>
.register-protocol {
  height: 100%;
}
.card-header {
  font-size: 16px;
  font-weight: bold;
}
.editor-container {
  line-height: normal;
}
.editor-container >>> .ql-editor {
  min-height: 300px;
}
.btn-wrap {
  width: 100%;
  text-align: right;
}
</style>
