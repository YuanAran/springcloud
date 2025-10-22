<template>
  <div class="role-management">
    <div class="page-header">
      <h2>角色管理</h2>
      <p>管理系统角色信息</p>
    </div>

    <div class="operation-bar">
      <el-button type="primary" plain icon="el-icon-plus" @click="showAddDialog">新增角色</el-button>
      <el-button type="danger" plain icon="el-icon-delete" :disabled="selectedRoles.length === 0" @click="batchDelete">批量删除</el-button>

      <div class="search-box">
        <div class="custom-input-container">
          <i class="el-icon-search search-icon"></i>
          <input type="text" v-model="searchKeyword" placeholder="搜索角色名称或编码" @input="handleSearch" class="custom-input">
          <i class="el-icon-circle-close clear-icon" v-if="searchKeyword" @click="clearSearch"></i>
        </div>
      </div>
    </div>

    <div class="custom-table-wrapper">
      <div class="table-container">
        <table class="custom-table">
          <thead>
            <tr>
              <th class="selection-column">
                <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange"></el-checkbox>
              </th>
              <th>角色名称</th>
              <th>角色编码</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="role in paginatedRoles" :key="role.roleId" :class="{ 'selected': selectedRoles.includes(role) }" @click="toggleRowSelection(role)">
              <td class="selection-column">
                <el-checkbox :value="selectedRoles.includes(role)" @change="toggleRowSelection(role)"></el-checkbox>
              </td>
              <td><el-tag size="small" type="primary">{{ role.roleName }}</el-tag></td>
              <td>{{ role.roleCode }}</td>
              <td><el-tag :type="role.status === '1' ? 'success' : 'danger'" size="small">{{ role.status === '1' ? '正常' : '停用' }}</el-tag></td>
              <td class="action-column">
                <el-button class="action-btn" size="mini" type="text" icon="el-icon-view" @click.stop="goToDetail(role)">详细</el-button>
                <el-button class="action-btn" size="mini" type="text" icon="el-icon-edit" @click.stop="showEditDialog(role)">编辑</el-button>
                <el-button class="action-btn danger" size="mini" type="text" icon="el-icon-delete" @click.stop="handleDelete(role)">删除</el-button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <div class="pagination">
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage" :page-sizes="[10, 20, 50, 100]" :page-size="pageSize" layout="total, sizes, prev, pager, next, jumper" :total="totalRoles"></el-pagination>
    </div>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px" @close="resetForm">
      <el-form :model="roleForm" :rules="roleRules" ref="roleForm" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称"></el-input>
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="roleForm.roleCode" placeholder="请输入角色编码" :disabled="isEdit"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="roleForm.status">
            <el-radio label="1">正常</el-radio>
            <el-radio label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="roleForm.description" type="textarea" :rows="3" placeholder="请输入角色描述信息"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">{{ isEdit ? '更新' : '创建' }}</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { getAllRole, addRole, updateRole, deleteRole } from '@/api/role'

export default {
  name: 'RoleManagement',
  data() {
    return {
      loading: false,
      submitLoading: false,
      roles: [],
      filteredRoles: [],
      selectedRoles: [],
      searchKeyword: '',
      currentPage: 1,
      pageSize: 10,
      totalRoles: 0,
      dialogVisible: false,
      isEdit: false,
      roleForm: {
        roleId: null,
        roleName: '',
        roleCode: '',
        status: '1',
        description: ''
      },
      roleRules: {
        roleName: [
          { required: true, message: '请输入角色名称', trigger: 'blur' },
          { min: 2, max: 20, message: '角色名称长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        roleCode: [
          { required: true, message: '请输入角色编码', trigger: 'blur' },
          { pattern: /^[A-Z_]+$/, message: '角色编码只能包含大写字母和下划线', trigger: 'blur' }
        ],
        status: [{ required: true, message: '请选择状态', trigger: 'change' }]
      },
      checkAll: false,
      isIndeterminate: false
    }
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑角色' : '新增角色'
    },
    paginatedRoles() {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.filteredRoles.slice(start, end)
    }
  },
  mounted() {
    this.fetchRoles()
  },
  methods: {
    async fetchRoles() {
      this.loading = true
      try {
        const response = await getAllRole()
        let processedRoles = []
        if (response && Array.isArray(response)) {
          processedRoles = response.map((role, index) => ({
            ...role,
            roleId: role.roleId || `temp_${index}_${Date.now()}`,
            roleName: role.roleName || `角色${index + 1}`,
            roleCode: role.roleCode || '',
            status: role.status || '1',
            description: role.description || ''
          }))
        }
        this.roles = processedRoles
        this.totalRoles = processedRoles.length
        this.filterRoles()
      } catch (error) {
        console.error('获取角色列表失败：', error)
        this.$message.error('获取角色列表失败')
      } finally {
        this.loading = false
      }
    },
    filterRoles() {
      if (!this.searchKeyword || this.searchKeyword.trim() === '') {
        this.filteredRoles = [...this.roles]
      } else {
        const keyword = this.searchKeyword.toLowerCase().trim()
        this.filteredRoles = this.roles.filter(role => {
          const roleName = role.roleName ? role.roleName.toLowerCase() : ''
          const roleCode = role.roleCode ? role.roleCode.toLowerCase() : ''
          return roleName.includes(keyword) || roleCode.includes(keyword)
        })
      }
      this.totalRoles = this.filteredRoles.length
    },
    handleSearch() {
      this.currentPage = 1
      this.filterRoles()
    },
    clearSearch() {
      this.searchKeyword = ''
      this.handleSearch()
    },
    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1
    },
    handleCurrentChange(val) {
      this.currentPage = val
    },
    showAddDialog() {
      this.isEdit = false
      this.dialogVisible = true
      this.resetForm()
    },
    goToDetail(role) {
      // 跳转到详细页面，通过query传递角色数据
      this.$router.push({
        name: 'RoleDetail',
        params: { roleId: role.roleId },
        query: { roleData: encodeURIComponent(JSON.stringify(role)) }
      })
    },
    showEditDialog(role) {
      this.isEdit = true
      this.dialogVisible = true
      this.roleForm = {
        roleId: role.roleId,
        roleName: role.roleName,
        roleCode: role.roleCode,
        status: role.status || '1',
        description: role.description || ''
      }
    },
    resetForm() {
      this.roleForm = {
        roleId: null,
        roleName: '',
        roleCode: '',
        status: '1',
        description: ''
      }
      if (this.$refs.roleForm) {
        this.$refs.roleForm.resetFields()
      }
    },
    async submitForm() {
      this.$refs.roleForm.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true
          try {
            if (this.isEdit) {
              await updateRole({
                roleId: this.roleForm.roleId,
                roleName: this.roleForm.roleName,
                roleCode: this.roleForm.roleCode,
                status: this.roleForm.status,
                description: this.roleForm.description
              })
              this.$message.success('角色更新成功')
            } else {
              await addRole({
                roleName: this.roleForm.roleName,
                roleCode: this.roleForm.roleCode,
                status: this.roleForm.status,
                description: this.roleForm.description
              })
              this.$message.success('角色创建成功')
            }
            this.dialogVisible = false
            this.fetchRoles()
          } catch (error) {
            console.error('操作失败：', error)
            this.$message.error('操作失败：' + (error.response?.data?.message || '请检查网络连接'))
          } finally {
            this.submitLoading = false
          }
        }
      })
    },
    handleDelete(role) {
      this.$confirm(`确定要删除角色 "${role.roleName}" 吗？`, '删除角色', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteRole([role.roleId])
          this.$message.success('角色删除成功')
          this.fetchRoles()
        } catch (error) {
          this.$message.error('角色删除失败：' + (error.response?.data?.message || '请检查网络连接'))
        }
      }).catch(() => {})
    },
    batchDelete() {
      if (this.selectedRoles.length === 0) {
        this.$message.warning('请选择要删除的角色')
        return
      }
      const roleNames = this.selectedRoles.map(r => r.roleName).join('、')
      this.$confirm(`确定要删除选中的 ${this.selectedRoles.length} 个角色吗？\n角色：${roleNames}`, '批量删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const roleIds = this.selectedRoles.map(role => role.roleId)
          await deleteRole(roleIds)
          this.$message.success(`批量删除成功，共删除 ${this.selectedRoles.length} 个角色`)
          this.fetchRoles()
          this.selectedRoles = []
          this.checkAll = false
        } catch (error) {
          this.$message.error('批量删除失败：' + (error.response?.data?.message || '请检查网络连接'))
        }
      }).catch(() => {})
    },
    toggleRowSelection(role) {
      const index = this.selectedRoles.indexOf(role)
      if (index > -1) {
        this.selectedRoles.splice(index, 1)
      } else {
        this.selectedRoles.push(role)
      }
      this.updateCheckAllStatus()
    },
    handleCheckAllChange(val) {
      if (val) {
        this.selectedRoles = [...this.paginatedRoles]
      } else {
        this.selectedRoles = []
      }
      this.updateCheckAllStatus()
    },
    updateCheckAllStatus() {
      const total = this.paginatedRoles.length
      const selected = this.selectedRoles.length
      this.checkAll = selected === total && total > 0
      this.isIndeterminate = selected > 0 && selected < total
    }
  }
}
</script>

<style scoped>
.role-management {
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
}
.custom-table th:last-child {
  border-right: none;
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
  width: 260px;
  text-align: left;
}
.action-btn {
  padding: 0 6px !important;
  height: 24px;
  line-height: 22px;
  font-size: 12px;
}
.action-btn + .action-btn {
  margin-left: 4px;
}
.action-btn.danger {
  color: #f56c6c;
}
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
.permission-tree {
  max-height: 400px;
  overflow-y: auto;
  padding: 10px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}
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
