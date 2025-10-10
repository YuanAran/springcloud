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
        <div class="custom-input-container">
          <i class="el-icon-search search-icon"></i>
          <input
            type="text"
            v-model="searchKeyword"
            placeholder="搜索用户名或姓名"
            @input="handleSearch"
            class="custom-input">
          <i class="el-icon-circle-close clear-icon" 
             v-if="searchKeyword" 
             @click="clearSearch"></i>
        </div>
      </div>
    </div>


    <!-- 用户表格 - 使用原生HTML + Element UI样式 -->
    <div class="custom-table-wrapper">
      <div class="table-container">
        <table class="custom-table">
          <thead>
            <tr>
              <th class="selection-column">
                <el-checkbox 
                  :indeterminate="isIndeterminate"
                  v-model="checkAll"
                  @change="handleCheckAllChange">
                </el-checkbox>
              </th>
              <th class="sortable" @click="sortBy('username')">
                用户名
                <i class="el-icon-caret-top" v-if="sortField === 'username' && sortOrder === 'asc'"></i>
                <i class="el-icon-caret-bottom" v-if="sortField === 'username' && sortOrder === 'desc'"></i>
              </th>
              <th class="sortable" @click="sortBy('nickname')">
                姓名
                <i class="el-icon-caret-top" v-if="sortField === 'nickname' && sortOrder === 'asc'"></i>
                <i class="el-icon-caret-bottom" v-if="sortField === 'nickname' && sortOrder === 'desc'"></i>
              </th>
              <th>性别</th>
              <th class="sortable" @click="sortBy('createTime')">
                创建时间
                <i class="el-icon-caret-top" v-if="sortField === 'createTime' && sortOrder === 'asc'"></i>
                <i class="el-icon-caret-bottom" v-if="sortField === 'createTime' && sortOrder === 'desc'"></i>
              </th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in paginatedUsers" :key="user.id" 
                :class="{ 'selected': selectedUsers.includes(user) }"
                @click="toggleRowSelection(user)">
              <td class="selection-column">
                <el-checkbox 
                  :value="selectedUsers.includes(user)"
                  @change="toggleRowSelection(user)">
                </el-checkbox>
              </td>
              <td>
                <el-tag size="small" type="primary">{{ user.username }}</el-tag>
              </td>
              <td>{{ user.nickname }}</td>
              <td>
                <el-tag 
                  :type="user.sex === 1 ? 'success' : 'primary'" 
                  size="small">
                  {{ user.sex === 1 ? '女' : '男' }}
                </el-tag>
              </td>
              <td>{{ formatDate(user.createTime) }}</td>
              <td class="action-column">
                <el-button
                  size="mini"
                  type="primary"
                  icon="el-icon-edit"
                  @click.stop="showEditDialog(user)">
                  编辑
                </el-button>
                <el-button
                  size="mini"
                  type="danger"
                  icon="el-icon-delete"
                  @click.stop="deleteUser(user)">
                  删除
                </el-button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>


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
      },

      // 表格key属性
      tableKey: 0,
      
      // 排序相关
      sortField: '',
      sortOrder: 'asc',
      
      // 复选框状态
      checkAll: false,
      isIndeterminate: false
    }
  },

  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑用户' : '新增用户'
    },
    
    // 分页后的用户数据
    paginatedUsers() {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.filteredUsers.slice(start, end)
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
        const response = await request.get('/sys_user/getList', {
          params: {
            page: this.currentPage,
            size: this.pageSize
          }
        })

        console.log('获取用户列表成功，response:', response)
        console.log('用户数据长度:', response.length)

        // 检查第一个用户对象的字段结构
        if (response && response.length > 0) {
          const firstUser = response[0]
          console.log('=== 第一个用户对象的字段分析 ===')
          console.log('第一个用户完整对象:', firstUser)
          console.log('所有字段名:', Object.keys(firstUser))
          console.log('username字段:', firstUser.username)
          console.log('nickname字段:', firstUser.nickname)
          console.log('sex字段:', firstUser.sex)
          console.log('createTime字段:', firstUser.createTime)
          console.log('userId字段:', firstUser.userId)
          console.log('id字段:', firstUser.id)
        }

        // 处理用户数据，确保Element UI表格能正确渲染
        let processedUsers = []
        if (response && Array.isArray(response)) {
          processedUsers = response.map((user, index) => {
            // 为每个用户添加唯一标识符，解决Element UI表格渲染问题
            return {
              ...user,
              // 确保有唯一的key用于Vue的v-for渲染
              id: user.userId || user.id || `temp_${index}_${Date.now()}`,
              userId: user.userId || (index + 1),
              // 确保所有必要字段都存在且不为undefined
              username: user.username || `用户${index + 1}`,
              nickname: user.nickname || `用户${index + 1}`,
              sex: user.sex !== undefined ? user.sex : 0,
              createTime: user.createTime || new Date().toISOString()
            }
          })
        }

        console.log('处理后的用户数据:', processedUsers)
        console.log('处理后数据长度:', processedUsers.length)

        // 使用处理后的数据
        this.users = processedUsers
        this.totalUsers = processedUsers.length
        this.filterUsers()

        // 强制更新表格
        this.tableKey++
        this.$nextTick(() => {
          console.log('表格渲染状态检查：', this.filteredUsers.length)
          if (this.filteredUsers.length > 0) {
            console.log('filteredUsers第一个元素:', this.filteredUsers[0])
          }
        })
      } catch (error) {
        console.error('获取用户列表失败：', error)
        console.error('错误详情：', error.response || error.message)
        this.$message.error('获取用户列表失败，使用模拟数据演示')
        // 使用模拟数据
        this.users = this.getMockUsers()
        this.totalUsers = this.users.length
        this.filterUsers()
      } finally {
        this.loading = false
      }
    },

    // 模拟数据（用于演示）- 确保数据结构完整
    getMockUsers() {
      return [
        {
          id: 1,
          userId: 1,
          username: 'admin',
          nickname: '管理员',
          sex: 0,
          createTime: '2024-01-01T10:00:00.000+00:00'
        },
        {
          id: 2,
          userId: 2,
          username: 'yuan',
          nickname: '穆茂原',
          sex: 0,
          createTime: '2025-09-22T08:22:18.424+00:00'
        },
        {
          id: 3,
          userId: 3,
          username: 'user2',
          nickname: '李四',
          sex: 1,
          createTime: '2024-01-03T12:00:00.000+00:00'
        },
        {
          id: 4,
          userId: 4,
          username: 'user3',
          nickname: '王五',
          sex: 0,
          createTime: '2024-01-04T13:00:00.000+00:00'
        },
        {
          id: 5,
          userId: 5,
          username: 'user4',
          nickname: '赵六',
          sex: 1,
          createTime: '2024-01-05T14:00:00.000+00:00'
        }
      ]
    },

    // 搜索过滤
    filterUsers() {
      console.log('开始过滤用户，searchKeyword：', this.searchKeyword)
      console.log('原始users数组长度：', this.users.length)

      if (!this.searchKeyword || this.searchKeyword.trim() === '') {
        this.filteredUsers = [...this.users]  // 创建副本避免引用问题
      } else {
        const keyword = this.searchKeyword.toLowerCase().trim()
        this.filteredUsers = this.users.filter(user => {
          const username = user.username ? user.username.toLowerCase() : ''
          const nickname = user.nickname ? user.nickname : ''
          return username.includes(keyword) || nickname.includes(keyword)
        })
      }

      console.log('过滤后filteredUsers数组长度：', this.filteredUsers.length)
      console.log('过滤后的数据：', this.filteredUsers)
    },

    // 搜索处理
    handleSearch() {
      this.filterUsers()
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
        id: user.userId,  // 使用userId字段
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
        await request.post('/sys_user/insert', {
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
        const result = await request.post('/sys_user/update', {
          userId: this.userForm.id,
          username: this.userForm.username,
          nickname: this.userForm.nickname,
          sex: this.userForm.sex
        })
        // 后端返回int：非0成功，0失败
        const code = typeof result === 'number' ? result : 0
        if (code !== 0) {
          this.$message.success('用户更新成功')
        } else {
          this.$message.error('用户更新失败')
          // 抛出错误以便submitForm捕获并保持对话框不关闭
          throw new Error('UPDATE_FAILED')
        }
      } catch (error) {
        this.$message.error('用户更新失败：' + (error.response?.data?.message || '请检查网络连接'))
        throw error
      }
    },

    // 统一删除方法（支持单删和批量删除）
    async deleteUsers(users) {
      const userList = Array.isArray(users) ? users : [users]
      const usernames = userList.map(user => user.username)
      const displayNames = usernames.join('、')
      
      const confirmMessage = userList.length === 1 
        ? `确定要删除用户 "${displayNames}" 吗？`
        : `确定要删除选中的 ${userList.length} 个用户吗？\n用户：${displayNames}`
      
      const title = userList.length === 1 ? '删除用户' : '批量删除'
      
      this.$confirm(confirmMessage, title, {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          // 发送字符串数组到后端，如 ['u1', 'u2']
          await request.post('/sys_user/delete', usernames)
          
          const successMessage = userList.length === 1 
            ? '用户删除成功' 
            : `批量删除成功，共删除 ${userList.length} 个用户`
          
          this.$message.success(successMessage)
          this.fetchUsers()
          
          // 如果是批量删除，清空选中状态
          if (userList.length > 1) {
            this.selectedUsers = []
            this.allSelected = false
          }
        } catch (error) {
          const errorMessage = userList.length === 1 
            ? '用户删除失败：' 
            : '批量删除失败：'
          this.$message.error(errorMessage + (error.response?.data?.message || '请检查网络连接'))
        }
      }).catch(() => {
        // 用户取消删除，不需要做任何操作
        console.log('用户取消删除操作')
      })
    },
    
    // 删除单个用户
    deleteUser(user) {
      this.deleteUsers(user)
    },

    // 批量删除
    batchDelete() {
      if (this.selectedUsers.length === 0) {
        this.$message.warning('请选择要删除的用户')
        return
      }
      this.deleteUsers(this.selectedUsers)
    },

    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return '-'
      const date = new Date(dateString)
      // 检查日期是否有效
      if (isNaN(date.getTime())) return '-'

      // 格式化为 YYYY-MM-DD HH:mm:ss
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      const seconds = String(date.getSeconds()).padStart(2, '0')

      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    },
    
    // 排序功能
    sortBy(field) {
      if (this.sortField === field) {
        this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc'
      } else {
        this.sortField = field
        this.sortOrder = 'asc'
      }
      
      this.filteredUsers.sort((a, b) => {
        let aVal = a[field]
        let bVal = b[field]
        
        if (field === 'createTime') {
          aVal = new Date(aVal)
          bVal = new Date(bVal)
        }
        
        if (this.sortOrder === 'asc') {
          return aVal > bVal ? 1 : -1
        } else {
          return aVal < bVal ? 1 : -1
        }
      })
    },
    
    // 清空搜索
    clearSearch() {
      this.searchKeyword = ''
      this.handleSearch()
    },
    
    // 切换行选择
    toggleRowSelection(user) {
      const index = this.selectedUsers.indexOf(user)
      if (index > -1) {
        this.selectedUsers.splice(index, 1)
      } else {
        this.selectedUsers.push(user)
      }
      this.updateCheckAllStatus()
    },
    
    // 全选/取消全选
    handleCheckAllChange(val) {
      if (val) {
        this.selectedUsers = [...this.paginatedUsers]
      } else {
        this.selectedUsers = []
      }
      this.updateCheckAllStatus()
    },
    
    // 更新全选状态
    updateCheckAllStatus() {
      const total = this.paginatedUsers.length
      const selected = this.selectedUsers.length
      
      this.checkAll = selected === total && total > 0
      this.isIndeterminate = selected > 0 && selected < total
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

/* 自定义表格样式 - 模拟Element UI */
.custom-table-wrapper {
  margin-top: 16px;
}

.table-container {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
}

.custom-table {
  width: 100%;
  border-collapse: collapse;
  background-color: white;
  font-size: 14px;
}

.custom-table th {
  background-color: #f5f7fa;
  color: #909399;
  font-weight: 500;
  padding: 12px 8px;
  text-align: left;
  border-bottom: 1px solid #ebeef5;
  border-right: 1px solid #ebeef5;
  position: relative;
}

.custom-table th:last-child {
  border-right: none;
}

.custom-table th.sortable {
  cursor: pointer;
  user-select: none;
  transition: background-color 0.2s;
}

.custom-table th.sortable:hover {
  background-color: #e6f7ff;
}

.custom-table td {
  padding: 12px 8px;
  border-bottom: 1px solid #ebeef5;
  border-right: 1px solid #ebeef5;
  color: #606266;
}

.custom-table td:last-child {
  border-right: none;
}

.custom-table tr:nth-child(even) {
  background-color: #fafafa;
}

.custom-table tr:hover {
  background-color: #f5f7fa;
}

.custom-table tr.selected {
  background-color: #ecf5ff;
}

.selection-column {
  width: 55px;
  text-align: center;
}

.action-column {
  width: 200px;
  text-align: center;
}

/* 操作按钮样式 */
.el-button--mini {
  padding: 5px 8px;
  font-size: 12px;
}

.el-button--mini + .el-button--mini {
  margin-left: 8px;
}

/* 自定义搜索输入框样式 */
.custom-input-container {
  position: relative;
  display: inline-block;
  width: 240px;
}

.custom-input {
  width: 100%;
  height: 32px;
  padding: 0 30px 0 30px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  color: #606266;
  background-color: #fff;
  outline: none;
  transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
}

.custom-input:focus {
  border-color: #409eff;
}

.custom-input::placeholder {
  color: #c0c4cc;
}

.search-icon {
  position: absolute;
  left: 8px;
  top: 50%;
  transform: translateY(-50%);
  color: #c0c4cc;
  font-size: 14px;
  pointer-events: none;
}

.clear-icon {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  color: #c0c4cc;
  font-size: 14px;
  cursor: pointer;
  transition: color 0.2s;
}

.clear-icon:hover {
  color: #909399;
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
