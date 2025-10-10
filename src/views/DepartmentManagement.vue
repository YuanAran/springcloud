<template>
  <div class="department-management">
    <div class="page-header">
      <h2>部门管理</h2>
      <p>管理系统中的所有部门信息</p>
    </div>

    <!-- 操作栏 -->
    <div class="operation-bar">
      <el-button type="primary" plain icon="el-icon-plus" @click="showAddDialog()">
        新增部门
      </el-button>
      <el-button type="danger" plain icon="el-icon-delete" :disabled="selectedDepartments.length === 0" @click="batchDelete">
        批量删除
      </el-button>

      <div class="search-box">
        <div class="custom-input-container">
          <i class="el-icon-search search-icon"></i>
          <input
            type="text"
            v-model="searchKeyword"
            placeholder="搜索部门名称"
            @input="handleSearch"
            class="custom-input">
          <i class="el-icon-circle-close clear-icon" 
             v-if="searchKeyword" 
             @click="clearSearch"></i>
        </div>
      </div>
    </div>


    <!-- 部门树形表格 -->
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
              <th>部门名称</th>
              <th>部门编码</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="department in treeDepartments" :key="department.id" 
                :class="{ 
                  'selected': selectedDepartments.includes(department),
                  'tree-row': true,
                  'expanded': department.expanded,
                  'has-children': department.children && department.children.length > 0
                }"
                :style="{ paddingLeft: (department.level * 20) + 'px' }"
                @click="toggleRowSelection(department)">
              
              <td class="selection-column">
                <el-checkbox 
                  :value="selectedDepartments.includes(department)"
                  @change="toggleRowSelection(department)">
                </el-checkbox>
              </td>
              
              <td class="department-name-cell">
                <div class="department-name-wrapper">
                  <span 
                    v-if="department.hasChildren"
                    class="expand-icon"
                    @click.stop="toggleExpand(department)">
                    <i :class="department.expanded ? 'el-icon-caret-bottom' : 'el-icon-caret-right'"></i>
                  </span>
                  <span v-else class="expand-placeholder"></span>
                  <el-tag size="small" type="primary">{{ department.name }}</el-tag>
                </div>
              </td>
              
              <td>{{ department.code }}</td>
              <td>
                <el-tag 
                  :type="department.status === 1 ? 'success' : 'danger'" 
                  size="small">
                  {{ department.status === 1 ? '正常' : '停用' }}
                </el-tag>
              </td>
              <td class="action-column">
                <el-button
                  class="action-btn"
                  size="mini"
                  type="text"
                  icon="el-icon-plus"
                  @click.stop="showAddDialog(department)">
                  新增
                </el-button>
                <el-button
                  class="action-btn"
                  size="mini"
                  type="text"
                  icon="el-icon-edit"
                  @click.stop="showEditDialog(department)">
                  编辑
                </el-button>
                <el-button
                  class="action-btn danger"
                  size="mini"
                  type="text"
                  icon="el-icon-delete"
                  @click.stop="deleteDepartment(department)">
                  删除
                </el-button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>


    <!-- 新增/编辑部门对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="500px"
      @close="resetForm">

      <el-form
        :model="departmentForm"
        :rules="departmentRules"
        ref="departmentForm"
        label-width="80px">

        <el-form-item label="上级部门">
          <template v-if="!isEdit">
            <el-select v-model="departmentForm.parentId" placeholder="选择上级部门（可不选为顶级）" filterable style="width: 100%" @change="onParentChange">
              <el-option :label="'无（顶级）'" :value="null"></el-option>
              <el-option
                v-for="opt in departmentOptions"
                :key="opt.value"
                :label="opt.label"
                :value="opt.value"/>
            </el-select>
          </template>
          <template v-else>
            <el-input v-model="departmentForm.parentName" placeholder="无" disabled></el-input>
          </template>
        </el-form-item>

        <el-form-item label="部门名称" prop="name">
          <el-input v-model="departmentForm.name" placeholder="请输入部门名称"></el-input>
        </el-form-item>

        <el-form-item label="部门编码" prop="code">
          <el-input v-model="departmentForm.code" placeholder="请输入部门编码"></el-input>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-select v-model="departmentForm.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="正常" :value="1"></el-option>
            <el-option label="停用" :value="0"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="描述" prop="description">
          <el-input 
            v-model="departmentForm.description" 
            type="textarea" 
            :rows="3"
            placeholder="请输入部门描述">
          </el-input>
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
  name: 'DepartmentManagement',
  data() {
    return {
      loading: false,
      submitLoading: false,
      departments: [],
      filteredDepartments: [],
      selectedDepartments: [],
      searchKeyword: '',

      // 对话框
      dialogVisible: false,
      isEdit: false,
      departmentForm: {
        id: null,
        parentId: null,
        parentName: '',
        name: '',
        code: '',
        status: 1, // 默认正常状态
        description: '',
        // 隐藏字段：后端返回，提交时回传
        dept_ip: ''
      },

      // 表单验证规则
      departmentRules: {
        name: [
          { required: true, message: '请输入部门名称', trigger: 'blur' },
          { min: 2, max: 20, message: '部门名称长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入部门编码', trigger: 'blur' },
          { min: 2, max: 10, message: '部门编码长度在 2 到 10 个字符', trigger: 'blur' },
          { pattern: /^[A-Z0-9]+$/, message: '部门编码只能包含大写字母和数字', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
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
      return this.isEdit ? '编辑部门' : '新增部门'
    },
    
    // 树形结构部门数据
    treeDepartments() {
      return this.buildTreeStructure(this.departments)
    },

    // 下拉选项：所有部门，带层级缩进
    departmentOptions() {
      const options = []
      const traverse = (items, level = 0) => {
        items.forEach(item => {
          const prefix = level > 0 ? ''.padStart(level * 2, ' ') + '└ ' : ''
          options.push({ value: item.id, label: `${prefix}${item.name}` })
          if (item.children && item.children.length) {
            traverse(item.children, level + 1)
          }
        })
      }
      traverse(this.departments, 0)
      return options
    }
  },

  mounted() {
    this.fetchDepartments()
  },

  methods: {
    // 获取某个部门名称
    getDeptNameById(id) {
      if (!id) return ''
      const stack = [...this.departments]
      while (stack.length) {
        const d = stack.shift()
        if (d.id === id) return d.name
        if (d.children && d.children.length) stack.push(...d.children)
      }
      return ''
    },

    // 获取部门列表
    async fetchDepartments() {
      this.loading = true
      try {
        const list = await request.get('/sys_dept/getAllDept')
        // 允许后端返回数组或包装对象
        const raw = Array.isArray(list) ? list : (list?.data || [])
        this.departments = this.normalizeDepartments(raw)
        this.tableKey++
      } catch (error) {
        console.error('获取部门列表失败，使用模拟数据：', error)
        const mockData = this.getMockDepartments()
        this.departments = mockData
        this.tableKey++
      } finally {
        this.loading = false
      }
    },

    // 将后端列表标准化并按 dept_code 构造成树（通过前缀关系）
    normalizeDepartments(items) {
      if (!Array.isArray(items)) return []
      // 标准化字段（后端字段：deptId, deptCode, deptName, status 为字符串'1'/'0'）
      const nodes = items.map((it, idx) => {
        const id = it.deptId || it.id || it.dept_id || idx + 1
        const code = it.deptCode || it.code || it.dept_code || ''
        const name = it.deptName || it.name || it.dept_name || ''
        const statusNum = (it.status === undefined || it.status === null)
          ? 1
          : (String(it.status) === '1' ? 1 : 0)
        return {
          id,
          code,
          name,
          parentId: it.parentId || null,
          status: statusNum,
          description: it.description || it.remark || '',
          dept_ip: it.dept_ip || '',
          expanded: false,
          level: 0,
          children: []
        }
      }).filter(n => n.code)

      // 按 code 长度升序，确保先处理父级
      nodes.sort((a, b) => a.code.length - b.code.length)

      const codeToNode = new Map(nodes.map(n => [n.code, n]))
      const idToNode = new Map(nodes.map(n => [n.id, n]))
      const roots = []

      nodes.forEach(n => {
        // 若后端未给 parentId，按编码前缀推断
        let parent = null
        if (n.parentId && idToNode.has(n.parentId)) {
          parent = idToNode.get(n.parentId)
        } else {
          for (const [code, node] of codeToNode.entries()) {
            if (code === n.code) continue
            if (n.code.startsWith(code)) {
              if (!parent || code.length > parent.code.length) parent = node
            }
          }
          if (parent && !n.parentId) n.parentId = parent.id
        }
        if (parent) {
          n.level = (parent.level || 0) + 1
          parent.children.push(n)
        } else {
          n.level = 0
          roots.push(n)
        }
      })

      return roots
    },

    // 构建树形结构（根据 expanded 展示扁平列表）
    buildTreeStructure(departments) {
      const result = []
      const buildTree = (items, level = 0) => {
        items.forEach(item => {
          const department = { 
            ...item, 
            level: item.level ?? level, 
            expanded: item.expanded || false,
            hasChildren: item.children && item.children.length > 0
          }
          result.push(department)
          if (item.children && item.children.length > 0 && item.expanded) {
            buildTree(item.children, (item.level ?? level) + 1)
          }
        })
      }
      buildTree(departments)
      return result
    },

    // 切换展开/收起
    toggleExpand(department) {
      this.updateDepartmentExpanded(this.departments, department.id, !department.expanded)
      this.$forceUpdate()
    },

    // 递归更新部门的展开状态
    updateDepartmentExpanded(departments, targetId, expanded) {
      for (let i = 0; i < departments.length; i++) {
        if (departments[i].id === targetId) {
          departments[i].expanded = expanded
          return true
        }
        if (departments[i].children && departments[i].children.length > 0) {
          if (this.updateDepartmentExpanded(departments[i].children, targetId, expanded)) {
            return true
          }
        }
      }
      return false
    },

    // 搜索（可后续实现基于名称/编码在树里定位并临时展开）
    filterDepartments() {},
    handleSearch() { this.filterDepartments() },

    // 显示新增对话框（可从行内触发设定上级）
    showAddDialog(parentDepartment) {
      this.isEdit = false
      this.dialogVisible = true
      const parentId = parentDepartment ? parentDepartment.id : null
      const parentName = parentDepartment ? parentDepartment.name : ''
      this.departmentForm = {
        id: null,
        parentId,
        parentName,
        name: '',
        code: '',
        status: 1,
        description: '',
        dept_ip: ''
      }
      if (this.$refs.departmentForm) {
        this.$refs.departmentForm.clearValidate()
      }
    },

    // 显示编辑对话框（保留 dept_ip 与 parent 信息）
    showEditDialog(department) {
      this.isEdit = true
      this.dialogVisible = true
      const parentName = this.getDeptNameById(department.parentId)
      this.departmentForm = {
        id: department.id,
        parentId: department.parentId || null,
        parentName,
        name: department.name,
        code: department.code,
        status: department.status !== undefined ? department.status : 1,
        description: department.description || '',
        dept_ip: department.dept_ip || ''
      }
    },

    // 重置表单（dept_ip 为空或沿用上次值由后端校验）
    resetForm() {
      this.departmentForm = {
        id: null,
        parentId: null,
        parentName: '',
        name: '',
        code: '',
        status: 1, // 默认正常状态
        description: '',
        dept_ip: ''
      }
      if (this.$refs.departmentForm) {
        this.$refs.departmentForm.resetFields()
      }
    },

    // 提交表单
    async submitForm() {
      this.$refs.departmentForm.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true
          try {
            if (this.isEdit) {
              await this.updateDepartment()
            } else {
              await this.createDepartment()
            }
            this.dialogVisible = false
            await this.fetchDepartments()
          } catch (error) {
            console.error('操作失败：', error)
          } finally {
            this.submitLoading = false
          }
        }
      })
    },

    // 创建部门（包含 dept_ip 回传 与 parentId）
    async createDepartment() {
      try {
        await request.post('/sys_dept/addDept', {
          deptName: this.departmentForm.name,
          deptCode: this.departmentForm.code,
          parentId: this.departmentForm.parentId,
          status: String(this.departmentForm.status),
          description: this.departmentForm.description,
          dept_ip: this.departmentForm.dept_ip || ''
        })
        this.$message.success('部门创建成功')
      } catch (error) {
        this.$message.error('部门创建失败：' + (error.response?.data?.message || '请检查网络连接'))
        throw error
      }
    },

    // 更新部门（包含 dept_ip 回传 与 parentId）
    async updateDepartment() {
      try {
        await request.post('/sys_dept/addDept', {
          deptId: this.departmentForm.id,
          deptName: this.departmentForm.name,
          deptCode: this.departmentForm.code,
          parentId: this.departmentForm.parentId,
          status: String(this.departmentForm.status),
          description: this.departmentForm.description,
          dept_ip: this.departmentForm.dept_ip || ''
        })
        this.$message.success('部门更新成功')
      } catch (error) {
        this.$message.error('部门更新失败：' + (error.response?.data?.message || '请检查网络连接'))
        throw error
      }
    },

    // 删除部门
    async deleteDepartment(department) {
      this.$confirm(`确定要删除部门 "${department.name}" 吗？`, '删除部门', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await request.post('/sys_dept/deleteDept', [department.id])
          this.$message.success('部门删除成功')
          this.fetchDepartments()
        } catch (error) {
          this.$message.error('部门删除失败：' + (error.response?.data?.message || '请检查网络连接'))
        }
      }).catch(() => {
        // 用户取消删除
      })
    },

    // 批量删除
    batchDelete() {
      if (this.selectedDepartments.length === 0) {
        this.$message.warning('请选择要删除的部门')
        return
      }
      this.$confirm(`确定要删除选中的 ${this.selectedDepartments.length} 个部门吗？`, '批量删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const deptIds = this.selectedDepartments.map(dept => dept.id)
          await request.post('/sys_dept/deleteDept', deptIds)
          this.$message.success(`批量删除成功，共删除 ${this.selectedDepartments.length} 个部门`)
          this.fetchDepartments()
          this.selectedDepartments = []
          this.checkAll = false
          this.isIndeterminate = false
        } catch (error) {
          this.$message.error('批量删除失败：' + (error.response?.data?.message || '请检查网络连接'))
        }
      }).catch(() => {
        // 用户取消删除
      })
    },

    // 格式化日期（暂保留以便后续需要）
    formatDate(dateString) {
      if (!dateString) return '-'
      const date = new Date(dateString)
      if (isNaN(date.getTime())) return '-'
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      const seconds = String(date.getSeconds()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
    },

    // 排序功能（当前未使用创建时间列，保留通用实现）
    sortBy(field) {
      if (this.sortField === field) {
        this.sortOrder = this.sortOrder === 'asc' ? 'desc' : 'asc'
      } else {
        this.sortField = field
        this.sortOrder = 'asc'
      }
      this.filteredDepartments.sort((a, b) => {
        let aVal = a[field]
        let bVal = b[field]
        if (this.sortOrder === 'asc') {
          return aVal > bVal ? 1 : -1
        } else {
          return aVal < bVal ? 1 : -1
        }
      })
    },

    // 切换行选择
    toggleRowSelection(department) {
      const index = this.selectedDepartments.indexOf(department)
      if (index > -1) {
        this.selectedDepartments.splice(index, 1)
      } else {
        this.selectedDepartments.push(department)
      }
      this.updateCheckAllStatus()
    },

    // 全选/取消全选
    handleCheckAllChange(val) {
      if (val) {
        this.selectedDepartments = [...this.treeDepartments]
      } else {
        this.selectedDepartments = []
      }
      this.updateCheckAllStatus()
    },

    // 更新全选状态
    updateCheckAllStatus() {
      const total = this.treeDepartments.length
      const selected = this.selectedDepartments.length
      this.checkAll = selected === total && total > 0
      this.isIndeterminate = selected > 0 && selected < total
    },

    // 清空搜索
    clearSearch() {
      this.searchKeyword = ''
      this.handleSearch()
    },

    // 选择上级部门时同步显示名称
    onParentChange(val) {
      this.departmentForm.parentName = this.getDeptNameById(val)
    }
  }
}
</script>

<style scoped>
.department-management {
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

.dialog-footer {
  text-align: right;
}

/* 自定义表格样式 - 模拟Element UI */
.custom-table-wrapper {
  margin-top: 16px;
}

.table-container {
  border-radius: 4px;
  overflow-x: auto;
  max-height: 60vh;
  overflow-y: auto;
  border: 1px solid #ebeef5;
}

.custom-table {
  width: 100%;
  min-width: 800px;
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
  position: relative;
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
  color: #606266;
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
  width: 220px;
  text-align: left;
}

/* 轻量化行内操作按钮 */
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

/* 缩小 mini 按钮整体尺寸（兼容其它 mini 按钮） */
.el-button--mini {
  padding: 2px 6px;
  font-size: 12px;
}

/* 搜索输入样式 */
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
}
</style>
