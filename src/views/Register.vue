<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h2>用户注册</h2>
        <p>创建您的账号，开始使用我们的服务</p>
      </div>

      <el-form
        :model="registerForm"
        :rules="registerRules"
        ref="registerForm"
        class="register-form"
        @keyup.enter.native="handleRegister">

        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            prefix-icon="el-icon-user"
            size="large">
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="el-icon-lock"
            size="large"
            show-password>
          </el-input>
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            prefix-icon="el-icon-lock"
            size="large"
            show-password>
          </el-input>
        </el-form-item>

        <el-form-item prop="nickname">
          <el-input
            v-model="registerForm.nickname"
            placeholder="请输入姓名"
            prefix-icon="el-icon-s-custom"
            size="large">
          </el-input>
        </el-form-item>

        <el-form-item prop="sex" label="性别">
          <el-radio-group v-model="registerForm.sex" size="large">
            <el-radio :label="0">男</el-radio>
            <el-radio :label="1">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="register-button"
            :loading="loading"
            @click="handleRegister">
            {{ loading ? '注册中...' : '注册' }}
          </el-button>
        </el-form-item>

        <div class="login-link">
          <span>已有账号？</span>
          <el-link type="primary" @click="goToLogin">立即登录</el-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'RegisterPage',
  data() {
    // 确认密码验证器
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }

    return {
      loading: false,
      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        nickname: '',
        sex: 0 // 默认选择男
      },
      registerRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
          { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        nickname: [
          { required: true, message: '请输入姓名', trigger: 'blur' },
          { min: 2, max: 10, message: '姓名长度在 2 到 10 个字符', trigger: 'blur' }
        ],
        sex: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ]
      }
    }
  },
  methods: {
    async handleRegister() {
      this.$refs.registerForm.validate(async (valid) => {
        if (valid) {
          this.loading = true

          try {
            // 发送注册请求到后端
            const response = await request.post('/auth/register', {
              username: this.registerForm.username,
              password: this.registerForm.password,
              nickname: this.registerForm.nickname,
              sex: this.registerForm.sex
            })

            // 注册成功
            if (response === '注册成功' || (typeof response === 'object' && response.message === '注册成功')) {
              this.$message.success('注册成功！请登录')

              // 跳转到登录页面
              this.$router.push('/login')
            } else {
              this.$message.error('注册失败：' + (response.message || '未知错误'))
            }

          } catch (error) {
            // 注册失败处理
            console.error('注册错误：', error)

            if (error.response && error.response.data) {
              if (typeof error.response.data === 'string') {
                this.$message.error('注册失败：' + error.response.data)
              } else if (error.response.data.message) {
                this.$message.error('注册失败：' + error.response.data.message)
              } else {
                this.$message.error('注册失败，请稍后重试')
              }
            } else {
              this.$message.error('注册失败，请检查网络连接')
            }
          } finally {
            this.loading = false
          }

        } else {
          this.$message.error('请检查输入信息')
          return false
        }
      })
    },

    goToLogin() {
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-box {
  width: 100%;
  max-width: 450px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  padding: 40px;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  color: #2c3e50;
  margin-bottom: 10px;
  font-size: 28px;
  font-weight: 600;
}

.register-header p {
  color: #7f8c8d;
  font-size: 14px;
  margin: 0;
}

.register-form {
  margin-top: 20px;
}

.register-form .el-form-item {
  margin-bottom: 20px;
}

.register-form .el-input {
  width: 100%;
}

.register-form .el-radio-group {
  width: 100%;
}

.register-form .el-radio {
  margin-right: 30px;
}

.register-button {
  width: 100%;
  height: 45px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 6px;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #7f8c8d;
}

.login-link span {
  margin-right: 5px;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .register-box {
    padding: 30px 20px;
  }

  .register-header h2 {
    font-size: 24px;
  }

  .register-form .el-radio {
    margin-right: 20px;
  }
}
</style>
