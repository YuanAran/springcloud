<template>
  <div class="user-management">
    <div class="page-header">
      <h2>用户管理</h2>
      <p>管理系统中的所有用户信息</p>
    </div>

    <!-- 操作栏 -->
    <div class="operation-bar">
      <el-button type="primary" icon="el-icon-plus" @click="showAddDialog">
        新增用户
      </el-button>
      <el-button type="danger" icon="el-icon-delete" :disabled="selectedUsers.length === 0" @click="batchDelete">
        批量删除
      </el-button>

      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索用户名或姓名"
          prefix-icon="el-icon-search"
          @input="handleSearch"
          clearable>
        </el-input>
      </div>
    </div>

    <!-- 用户表格 -->
    <el-table
      :data="filteredUsers"
      v-loading="loading"
      @selection-change="handleSelectionChange"
      stripe
      border
      style="width: 100%">

      <el-table-column type="selection" width="55"></el-table-column>

      <el-table-column prop="id" label="ID" width="80" sortable></el-table-column>

      <el-table-column prop="username" label="用户名" width="150" sortable>
        <template slot-scope="scope">
          <el-tag size="small">{{ scope.row.username }}</el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="nickname" label="姓名" width="120" sortable></el-table-column>

      <el-table-column prop="sex" label="性别" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.sex === 0 ? 'primary' : 'success'" size="small">
            {{ scope.row.sex === 0 ? '男' : '女' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="createTime" label="创建时间" width="180" sortable>
        <template slot-scope="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>

      <el-table-column label="操作" width="200" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="primary" icon="el-icon-edit" @click="showEditDialog(scope.row)">
            编辑
          </el-button>
          <el-button size="mini" type="danger" icon="el-icon-delete" @click="deleteUser(scope.row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalUsers">
      </el-pagination>
    </div>

    <!-- 新增/编辑用户对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="500px"
      @close="resetForm">

      <el-form
        :model="userForm"
        :rules="userRules"
        ref="userForm"
        label-width="80px">

        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" :disabled="isEdit" placeholder="请输入用户名"></el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" show-password></el-input>
        </el-form-item>

        <el-form-item label="姓名" prop="nickname">
          <el-input v-model="userForm.nickname" placeholder="请输入姓名"></el-input>
        </el-form-item>

        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="userForm.sex">
            <el-radio :label="0">男</el-radio>
            <el-radio :label="1">女</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">
          {{ isEdit ? '更新' : '创建' }}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'UserManagement',
  data() {
    return {
      loading: false,
      submitLoading: false,
      users: [],
      filteredUsers: [],
      selectedUsers: [],
      searchKeyword: '',

      // 分页
      currentPage: 1,
      pageSize: 10,
      totalUsers: 0,

      // 对话框
      dialogVisible: false,
      isEdit: false,
      userForm: {
        id: null,
        username: '',
        password: '',
        nickname: '',
        sex: 0
      },

      // 表单验证规则
      userRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
          { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
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

  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑用户' : '新增用户'
    }
  },

  mounted() {
    this.fetchUsers()
  },

  methods: {
    // 获取用户列表
    async fetchUsers() {
      this.loading = true
      try {
        const response = await request.get('/user/list', {
          params: {
            page: this.currentPage,
            size: this.pageSize
          }
        })

        if (response.data) {
          this.users = response.data
          this.totalUsers = response.total || response.data.length
          this.filterUsers()
        } else {
          // 如果后端还没有实现，使用模拟数据
          this.users = this.getMockUsers()
          this.totalUsers = this.users.length
          this.filterUsers()
        }
      } catch (error) {
        console.error('获取用户列表失败：', error)
        // 使用模拟数据
        this.users = this.getMockUsers()
        this.totalUsers = this.users.length
        this.filterUsers()
      } finally {
        this.loading = false
      }
    },

    // 模拟数据（用于演示）
    getMockUsers() {
      return [
        { id: 1, username: 'admin', nickname: '管理员', sex: 0, createTime: '2024-01-01 10:00:00' },
        { id: 2, username: 'user1', nickname: '张三', sex: 0, createTime: '2024-01-02 11:00:00' },
        { id: 3, username: 'user2', nickname: '李四', sex: 1, createTime: '2024-01-03 12:00:00' },
        { id: 4, username: 'user3', nickname: '王五', sex: 0, createTime: '2024-01-04 13:00:00' },
        { id: 5, username: 'user4', nickname: '赵六', sex: 1, createTime: '2024-01-05 14:00:00' }
      ]
    },

    // 搜索过滤
    filterUsers() {
      if (!this.searchKeyword) {
        this.filteredUsers = this.users
      } else {
        this.filteredUsers = this.users.filter(user =>
          user.username.toLowerCase().includes(this.searchKeyword.toLowerCase()) ||
          user.nickname.includes(this.searchKeyword)
        )
      }
    },

    // 搜索处理
    handleSearch() {
      this.filterUsers()
    },

    // 选择变化
    handleSelectionChange(selection) {
      this.selectedUsers = selection
    },

    // 分页大小变化
    handleSizeChange(val) {
      this.pageSize = val
      this.fetchUsers()
    },

    // 当前页变化
    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchUsers()
    },

    // 显示新增对话框
    showAddDialog() {
      this.isEdit = false
      this.dialogVisible = true
      this.resetForm()
    },

    // 显示编辑对话框
    showEditDialog(user) {
      this.isEdit = true
      this.dialogVisible = true
      this.userForm = {
        id: user.id,
        username: user.username,
        password: '',
        nickname: user.nickname,
        sex: user.sex
      }
    },

    // 重置表单
    resetForm() {
      this.userForm = {
        id: null,
        username: '',
        password: '',
        nickname: '',
        sex: 0
      }
      if (this.$refs.userForm) {
        this.$refs.userForm.resetFields()
      }
    },

    // 提交表单
    async submitForm() {
      this.$refs.userForm.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true
          try {
            if (this.isEdit) {
              await this.updateUser()
            } else {
              await this.createUser()
            }
            this.dialogVisible = false
            this.fetchUsers()
          } catch (error) {
            console.error('操作失败：', error)
          } finally {
            this.submitLoading = false
          }
        }
      })
    },

    // 创建用户
    async createUser() {
      try {
        await request.post('/user/create', {
          username: this.userForm.username,
          password: this.userForm.password,
          nickname: this.userForm.nickname,
          sex: this.userForm.sex
        })
        this.$message.success('用户创建成功')
      } catch (error) {
        this.$message.error('用户创建失败：' + (error.response?.data?.message || '请检查网络连接'))
        throw error
      }
    },

    // 更新用户
    async updateUser() {
      try {
        await request.put(`/user/update/${this.userForm.id}`, {
          nickname: this.userForm.nickname,
          sex: this.userForm.sex
        })
        this.$message.success('用户更新成功')
      } catch (error) {
        this.$message.error('用户更新失败：' + (error.response?.data?.message || '请检查网络连接'))
        throw error
      }
    },

    // 删除用户
    deleteUser(user) {
      this.$confirm(`确定要删除用户 "${user.username}" 吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await request.delete(`/user/delete/${user.id}`)
          this.$message.success('用户删除成功')
          this.fetchUsers()
        } catch (error) {
          this.$message.error('用户删除失败：' + (error.response?.data?.message || '请检查网络连接'))
        }
      })
    },

    // 批量删除
    batchDelete() {
      const usernames = this.selectedUsers.map(user => user.username).join('、')
      this.$confirm(`确定要删除选中的 ${this.selectedUsers.length} 个用户吗？\n用户：${usernames}`, '批量删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const ids = this.selectedUsers.map(user => user.id)
          await request.post('/user/batch-delete', { ids })
          this.$message.success('批量删除成功')
          this.fetchUsers()
        } catch (error) {
          this.$message.error('批量删除失败：' + (error.response?.data?.message || '请检查网络连接'))
        }
      })
    },

    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return '-'
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.user-management {
  background: white;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.page-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e8e8e8;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #262626;
  font-size: 20px;
  font-weight: 600;
}

.page-header p {
  margin: 0;
  color: #8c8c8c;
  font-size: 14px;
}

.operation-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.operation-bar .el-button {
  margin-right: 8px;
}

.search-box {
  width: 300px;
}

.pagination {
  margin-top: 16px;
  text-align: right;
}

.dialog-footer {
  text-align: right;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .operation-bar {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .search-box {
    width: 100%;
  }

  .pagination {
    text-align: center;
  }
}
</style>
