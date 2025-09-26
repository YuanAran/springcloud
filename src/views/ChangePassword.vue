<template>
  <div class="change-password-container">
    <div class="change-password-card">
      <div class="card-header">
        <h2>修改密码</h2>
        <p>请输入您的新密码</p>
      </div>

      <el-form
        ref="changePasswordForm"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
        class="password-form">

        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
            clearable>
            <i slot="prefix" class="el-icon-lock"></i>
          </el-input>
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
            clearable>
            <i slot="prefix" class="el-icon-lock"></i>
          </el-input>
        </el-form-item>

        <div class="button-group">
          <el-button @click="handleCancel">取消</el-button>
          <el-button
            type="primary"
            @click="handleConfirm"
            :loading="loading">
            确认修改
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'
import { getToken } from '@/utils/auth'

export default {
  name: 'ChangePassword',
  data() {
    // 确认密码验证函数
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }

    return {
      loading: false,
      passwordForm: {
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    async handleConfirm() {
      this.$refs.changePasswordForm.validate(async (valid) => {
        if (valid) {
          this.loading = true

          try {
            const token = getToken()
            if (!token) {
              this.$message.error('未找到登录信息，请重新登录')
              this.$router.push('/login')
              return
            }

            // 调用后端API修改密码
            const response = await request.post('/sys_user/updatePW', {
              password: this.passwordForm.newPassword
            }, {
              headers: {
                'Authorization': `Bearer ${token}`
              }
            })

            // 处理响应
            if (response === 1) {
              this.$message.success('密码修改成功！')

              // 返回上一页
              this.$router.go(-1)
            } else if (response === 0) {
              this.$message.error('密码修改失败，请重试')
            } else {
              this.$message.error('服务器响应异常，请重试')
            }

          } catch (error) {
            console.error('修改密码失败:', error)

            // 处理不同的错误情况
            if (error.response) {
              const status = error.response.status
              const message = error.response.data?.message || error.response.data?.msg || '修改密码失败'

              switch (status) {
                case 401:
                  this.$message.error('登录已过期，请重新登录')
                  this.$router.push('/login')
                  break
                case 403:
                  this.$message.error('没有权限执行此操作')
                  break
                case 400:
                  this.$message.error(message)
                  break
                default:
                  this.$message.error(message)
              }
            } else if (error.request) {
              this.$message.error('网络连接失败，请检查网络')
            } else {
              this.$message.error('修改密码失败，请重试')
            }
          } finally {
            this.loading = false
          }

        } else {
          this.$message.error('请检查输入信息')
        }
      })
    },

    handleCancel() {
      // 确认是否取消
      if (this.passwordForm.newPassword || this.passwordForm.confirmPassword) {
        this.$confirm('确定要取消修改密码吗？已输入的内容将丢失。', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '继续修改',
          type: 'warning'
        }).then(() => {
          this.$router.go(-1)
        }).catch(() => {
          // 用户选择继续修改，不做任何操作
        })
      } else {
        // 没有输入内容，直接返回
        this.$router.go(-1)
      }
    }
  }
}
</script>

<style scoped>
.change-password-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100%;
  background-color: #f0f2f5;
  padding: 20px;
}

.change-password-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 40px;
  width: 100%;
  max-width: 500px;
}

.card-header {
  text-align: center;
  margin-bottom: 30px;
}

.card-header h2 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  font-size: 24px;
  font-weight: 600;
}

.card-header p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.password-form {
  margin-top: 20px;
}

.password-form .el-form-item {
  margin-bottom: 25px;
}

.password-form .el-input {
  height: 45px;
}

.password-form .el-input__inner {
  height: 45px;
  line-height: 45px;
  border-radius: 6px;
  font-size: 14px;
}

.button-group {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 30px;
}

.button-group .el-button {
  min-width: 100px;
  height: 40px;
  border-radius: 6px;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .change-password-container {
    padding: 10px;
  }

  .change-password-card {
    padding: 30px 20px;
  }

  .card-header h2 {
    font-size: 20px;
  }

  .button-group {
    flex-direction: column;
  }

  .button-group .el-button {
    width: 100%;
  }
}
</style>
