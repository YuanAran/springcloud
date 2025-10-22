<template>
  <div class="role-detail">
    <!-- 左上角角色基本信息 -->
    <div class="role-info-card">
      <div class="card-header">
        <h3>{{ roleInfo.roleName }}</h3>
        <el-tag :type="roleInfo.status === '1' ? 'success' : 'danger'" size="medium">
          {{ roleInfo.status === '1' ? '正常' : '停用' }}
        </el-tag>
      </div>
      <div class="card-content">
        <div class="info-row">
          <span class="info-label">角色编码：</span>
          <span class="info-value">{{ roleInfo.roleCode }}</span>
        </div>
        <div class="info-row">
          <span class="info-label">角色ID：</span>
          <span class="info-value">{{ roleInfo.roleId }}</span>
        </div>
        <div class="info-row" v-if="roleInfo.description">
          <span class="info-label">备注：</span>
          <span class="info-value">{{ roleInfo.description }}</span>
        </div>
      </div>
      <div class="card-actions">
        <el-button size="small" icon="el-icon-back" @click="goBack">返回列表</el-button>
        <el-button size="small" type="primary" icon="el-icon-edit" @click="editRole">编辑角色</el-button>
      </div>
    </div>

    <!-- 详细信息区域 -->
    <div class="detail-content">
      <el-tabs v-model="activeTab" type="border-card">
        <!-- 权限信息 -->
        <el-tab-pane label="权限信息" name="permission">
          <div class="tab-content">
            <div class="section-header">
              <h4>菜单权限</h4>
              <el-button size="small" type="primary" plain icon="el-icon-edit" @click="editPermission">编辑权限</el-button>
            </div>
            
            <div class="permission-list" v-if="permissions.length > 0">
              <el-tree
                :data="permissions"
                :props="treeProps"
                default-expand-all
                node-key="menuId">
                <span class="custom-tree-node" slot-scope="{ node, data }">
                  <span>
                    <i :class="getMenuIcon(data.type)"></i>
                    {{ node.label }}
                  </span>
                  <el-tag size="mini" :type="getMenuTypeTag(data.type)">
                    {{ getMenuTypeText(data.type) }}
                  </el-tag>
                </span>
              </el-tree>
            </div>
            <el-empty v-else description="暂无权限数据" :image-size="100"></el-empty>
          </div>
        </el-tab-pane>

        <!-- 部门信息 -->
        <el-tab-pane label="部门信息" name="department">
          <div class="tab-content">
            <div class="section-header">
              <h4>数据权限部门</h4>
              <div class="action-buttons">
                <el-button size="small" type="primary" plain icon="el-icon-plus" @click="showAddDepartment">新增部门</el-button>
                <el-button size="small" type="danger" plain icon="el-icon-delete" @click="showDeleteDepartment">删除部门</el-button>
                <el-button size="small" type="warning" plain icon="el-icon-edit" @click="showUpdateDepartment">更新部门</el-button>
              </div>
            </div>
            
            <div class="department-list" v-if="departments.length > 0">
              <el-tree
                :data="departments"
                :props="deptTreeProps"
                default-expand-all
                node-key="deptId">
                <span class="custom-tree-node" slot-scope="{ node, data }">
                  <span>
                    <i class="el-icon-office-building"></i>
                    {{ node.label }}
                  </span>
                  <el-tag size="mini" type="info">{{ data.deptCode }}</el-tag>
                </span>
              </el-tree>
            </div>
            <el-empty v-else description="暂无部门数据" :image-size="100"></el-empty>
          </div>
        </el-tab-pane>

        <!-- 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <div class="tab-content">
            <div class="basic-info">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="角色ID">{{ roleInfo.roleId }}</el-descriptions-item>
                <el-descriptions-item label="角色名称">
                  <el-tag type="primary">{{ roleInfo.roleName }}</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="角色编码">{{ roleInfo.roleCode }}</el-descriptions-item>
                <el-descriptions-item label="状态">
                  <el-tag :type="roleInfo.status === '1' ? 'success' : 'danger'">
                    {{ roleInfo.status === '1' ? '正常' : '停用' }}
                  </el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="创建时间" :span="2">
                  {{ roleInfo.createTime || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="备注" :span="2">
                  {{ roleInfo.description || '-' }}
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 编辑权限对话框 -->
    <el-dialog title="编辑菜单权限" :visible.sync="permissionDialogVisible" width="600px">
      <el-tree
        ref="permissionTree"
        :data="allMenus"
        show-checkbox
        node-key="menuId"
        :props="treeProps"
        :default-checked-keys="checkedPermissions"
        default-expand-all>
      </el-tree>
      <div slot="footer" class="dialog-footer">
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePermission">保存</el-button>
      </div>
    </el-dialog>

    <!-- 新增部门对话框 -->
    <el-dialog title="新增数据权限部门" :visible.sync="addDepartmentDialogVisible" width="800px">
      <div class="dialog-hint">
        <i class="el-icon-info"></i>
        选择需要添加的部门（已添加的部门不会显示）
      </div>
      <div class="department-table-container" v-loading="loadingDepts">
        <div class="custom-table-wrapper">
          <table class="custom-table">
            <thead>
              <tr>
                <th class="selection-column">
                  <el-checkbox
                    :indeterminate="isIndeterminateDept"
                    v-model="checkAllDept"
                    @change="handleCheckAllDeptChange">
                  </el-checkbox>
                </th>
                <th>部门名称</th>
                <th>部门编码</th>
              </tr>
            </thead>
            <tbody>
              <template v-for="dept in flatAvailableDepartments">
                <tr :key="dept.deptId" 
                    :class="{ 'selected': selectedDeptCodes.includes(dept.deptCode) }"
                    @click="toggleDeptSelection(dept)">
                  <td class="selection-column">
                    <el-checkbox
                      :value="selectedDeptCodes.includes(dept.deptCode)"
                      @change="toggleDeptSelection(dept)">
                    </el-checkbox>
                  </td>
                  <td>
                    <div class="department-name-wrapper" :style="{ paddingLeft: (dept.level * 20) + 'px' }">
                      <span v-if="dept.hasChildren" class="expand-icon" @click.stop="toggleDeptExpand(dept)">
                        <i :class="dept.expanded ? 'el-icon-caret-bottom' : 'el-icon-caret-right'"></i>
                      </span>
                      <span v-else class="expand-placeholder"></span>
                      <el-tag size="small" type="primary">{{ dept.deptName }}</el-tag>
                    </div>
                  </td>
                  <td>{{ dept.deptCode }}</td>
                </tr>
              </template>
            </tbody>
          </table>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addDepartmentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAddDepartment" :loading="submitLoading">确定添加</el-button>
      </div>
    </el-dialog>

    <!-- 删除部门对话框 -->
    <el-dialog title="删除数据权限部门" :visible.sync="deleteDepartmentDialogVisible" width="800px">
      <div class="dialog-hint">
        <i class="el-icon-warning"></i>
        选择需要删除的部门（删除后该角色将失去对这些部门的数据权限）
      </div>
      <div class="department-table-container">
        <div class="custom-table-wrapper">
          <table class="custom-table">
            <thead>
              <tr>
                <th class="selection-column">
                  <el-checkbox
                    :indeterminate="isIndeterminateDelete"
                    v-model="checkAllDelete"
                    @change="handleCheckAllDeleteChange">
                  </el-checkbox>
                </th>
                <th>部门名称</th>
                <th>部门编码</th>
              </tr>
            </thead>
            <tbody>
              <template v-for="dept in flatDepartments">
                <tr :key="dept.deptId" 
                    :class="{ 'selected': selectedDeleteDepts.some(d => d.deptCode === dept.deptCode) }"
                    @click="toggleDeleteDeptSelection(dept)">
                  <td class="selection-column">
                    <el-checkbox
                      :value="selectedDeleteDepts.some(d => d.deptCode === dept.deptCode)"
                      @change="toggleDeleteDeptSelection(dept)">
                    </el-checkbox>
                  </td>
                  <td>
                    <div class="department-name-wrapper" :style="{ paddingLeft: (dept.level * 20) + 'px' }">
                      <span v-if="dept.hasChildren" class="expand-icon" @click.stop="toggleDeptExpand(dept)">
                        <i :class="dept.expanded ? 'el-icon-caret-bottom' : 'el-icon-caret-right'"></i>
                      </span>
                      <span v-else class="expand-placeholder"></span>
                      <el-tag size="small" type="danger">{{ dept.deptName }}</el-tag>
                    </div>
                  </td>
                  <td>{{ dept.deptCode }}</td>
                </tr>
              </template>
            </tbody>
          </table>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="deleteDepartmentDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="saveDeleteDepartment" :loading="submitLoading">确定删除</el-button>
      </div>
    </el-dialog>

    <!-- 更新部门对话框 -->
    <el-dialog title="更新数据权限部门" :visible.sync="updateDepartmentDialogVisible" width="800px">
      <div class="dialog-hint">
        <i class="el-icon-edit"></i>
        重新选择该角色的数据权限部门（未选中的部门将被移除）
      </div>
      <div class="department-table-container" v-loading="loadingDepts">
        <div class="custom-table-wrapper">
          <table class="custom-table">
            <thead>
              <tr>
                <th class="selection-column">
                  <el-checkbox
                    :indeterminate="isIndeterminateDept"
                    v-model="checkAllDept"
                    @change="handleCheckAllDeptChange">
                  </el-checkbox>
                </th>
                <th>部门名称</th>
                <th>部门编码</th>
              </tr>
            </thead>
            <tbody>
              <template v-for="dept in flatAllDepartments">
                <tr :key="dept.deptId" 
                    :class="{ 'selected': selectedDeptCodes.includes(dept.deptCode) }"
                    @click="toggleDeptSelection(dept)">
                  <td class="selection-column">
                    <el-checkbox
                      :value="selectedDeptCodes.includes(dept.deptCode)"
                      @change="toggleDeptSelection(dept)">
                    </el-checkbox>
                  </td>
                  <td>
                    <div class="department-name-wrapper" :style="{ paddingLeft: (dept.level * 20) + 'px' }">
                      <span v-if="dept.hasChildren" class="expand-icon" @click.stop="toggleDeptExpand(dept)">
                        <i :class="dept.expanded ? 'el-icon-caret-bottom' : 'el-icon-caret-right'"></i>
                      </span>
                      <span v-else class="expand-placeholder"></span>
                      <el-tag size="small" type="warning">{{ dept.deptName }}</el-tag>
                    </div>
                  </td>
                  <td>{{ dept.deptCode }}</td>
                </tr>
              </template>
            </tbody>
          </table>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="updateDepartmentDialogVisible = false">取消</el-button>
        <el-button type="warning" @click="saveUpdateDepartment" :loading="submitLoading">确定更新</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getRoleVo } from '@/api/role'

export default {
  name: 'RoleDetail',
  data() {
    return {
      activeTab: 'permission',
      roleInfo: {
        roleId: '',
        roleName: '',
        roleCode: '',
        status: '1',
        description: '',
        createTime: ''
      },
      // 权限数据
      permissions: [],
      // 部门数据
      departments: [],
      treeProps: {
        children: 'children',
        label: 'menuName'
      },
      deptTreeProps: {
        children: 'children',
        label: 'deptName'
      },
      // 编辑对话框
      permissionDialogVisible: false,
      allMenus: [],
      checkedPermissions: [],
      // 部门对话框
      addDepartmentDialogVisible: false,
      deleteDepartmentDialogVisible: false,
      updateDepartmentDialogVisible: false,
      // 部门编辑相关
      allDepartmentsTree: [],
      availableDepartments: [],
      selectedDeptCodes: [],
      selectedDeleteDepts: [],
      checkAllDept: false,
      isIndeterminateDept: false,
      checkAllDelete: false,
      isIndeterminateDelete: false,
      loadingDepts: false,
      submitLoading: false
    }
  },
  computed: {
    // 将所有部门树扁平化（用于更新对话框）
    flatAllDepartments() {
      const result = []
      const traverse = (items, level = 0) => {
        items.forEach(item => {
          const flatItem = {
            ...item,
            level,
            hasChildren: item.children && item.children.length > 0,
            expanded: item.expanded !== false
          }
          result.push(flatItem)
          if (flatItem.expanded && item.children && item.children.length > 0) {
            traverse(item.children, level + 1)
          }
        })
      }
      traverse(this.allDepartmentsTree)
      return result
    },
    // 可添加的部门扁平化（用于新增对话框）
    flatAvailableDepartments() {
      const result = []
      const traverse = (items, level = 0) => {
        items.forEach(item => {
          const flatItem = {
            ...item,
            level,
            hasChildren: item.children && item.children.length > 0,
            expanded: item.expanded !== false
          }
          result.push(flatItem)
          if (flatItem.expanded && item.children && item.children.length > 0) {
            traverse(item.children, level + 1)
          }
        })
      }
      traverse(this.availableDepartments)
      return result
    },
    // 已有部门扁平化（用于删除对话框）
    flatDepartments() {
      const result = []
      const traverse = (items, level = 0) => {
        items.forEach(item => {
          const flatItem = {
            ...item,
            level,
            hasChildren: item.children && item.children.length > 0,
            expanded: item.expanded !== false
          }
          result.push(flatItem)
          if (flatItem.expanded && item.children && item.children.length > 0) {
            traverse(item.children, level + 1)
          }
        })
      }
      traverse(this.departments)
      return result
    }
  },
  mounted() {
    this.loadRoleDetail()
  },
  methods: {
    // 将扁平数组转换为树形结构（通用方法，基于parentId）
    buildTree(flatArray, idKey = 'id', parentIdKey = 'parentId') {
      if (!flatArray || !Array.isArray(flatArray)) {
        return []
      }
      
      // 创建映射表
      const map = {}
      const tree = []
      
      // 第一次遍历：创建所有节点的映射
      flatArray.forEach(item => {
        map[item[idKey]] = { ...item, children: [] }
      })
      
      // 第二次遍历：建立父子关系
      flatArray.forEach(item => {
        const node = map[item[idKey]]
        const parentId = item[parentIdKey]
        
        // 如果parentId为null、0或undefined，说明是根节点
        if (parentId === null || parentId === 0 || parentId === undefined || parentId === '') {
          tree.push(node)
        } else if (map[parentId]) {
          // 否则添加到父节点的children中
          map[parentId].children.push(node)
        } else {
          // 如果找不到父节点，也当作根节点
          tree.push(node)
        }
      })
      
      // 清理空的children数组
      const cleanEmptyChildren = (nodes) => {
        nodes.forEach(node => {
          if (node.children && node.children.length === 0) {
            delete node.children
          } else if (node.children && node.children.length > 0) {
            cleanEmptyChildren(node.children)
          }
        })
      }
      
      cleanEmptyChildren(tree)
      return tree
    },
    
    // 根据deptCode构建部门树形结构
    buildDeptTreeByCode(flatArray) {
      if (!flatArray || !Array.isArray(flatArray)) {
        return []
      }
      
      // 创建映射表
      const map = {}
      const tree = []
      
      // 先创建所有节点的映射
      flatArray.forEach(item => {
        map[item.deptCode] = { ...item, children: [] }
      })
      
      // 按照deptCode长度排序，确保父节点先处理
      const sortedArray = [...flatArray].sort((a, b) => {
        return (a.deptCode || '').length - (b.deptCode || '').length
      })
      
      // 建立父子关系
      sortedArray.forEach(item => {
        const node = map[item.deptCode]
        const deptCode = item.deptCode || ''
        
        // 查找父节点：从当前code逐步缩短3位查找
        let parentCode = null
        for (let len = deptCode.length - 3; len >= 3; len -= 3) {
          const possibleParentCode = deptCode.substring(0, len)
          if (map[possibleParentCode]) {
            parentCode = possibleParentCode
            break
          }
        }
        
        if (parentCode && map[parentCode]) {
          // 找到父节点，添加到父节点的children中
          map[parentCode].children.push(node)
        } else {
          // 没有父节点，是根节点
          tree.push(node)
        }
      })
      
      // 清理空的children数组
      const cleanEmptyChildren = (nodes) => {
        nodes.forEach(node => {
          if (node.children && node.children.length === 0) {
            delete node.children
          } else if (node.children && node.children.length > 0) {
            cleanEmptyChildren(node.children)
          }
        })
      }
      
      cleanEmptyChildren(tree)
      return tree
    },
    
    async loadRoleDetail() {
      // 从路由query获取角色信息（用于显示基本信息）
      if (this.$route.query.roleData) {
        try {
          this.roleInfo = JSON.parse(decodeURIComponent(this.$route.query.roleData))
        } catch (e) {
          console.error('解析角色数据失败', e)
        }
      }
      
      // 从后端加载完整的角色详情、权限、部门数据
      const roleId = this.$route.params.roleId
      if (roleId) {
        try {
          const response = await getRoleVo(roleId)
          
          // 更新菜单权限（转换为树形结构）
          if (response.menus && Array.isArray(response.menus)) {
            this.permissions = this.buildTree(response.menus, 'menuId', 'parentId')
          }
          
          // 更新部门数据（根据deptCode转换为树形结构）
          if (response.depts && Array.isArray(response.depts)) {
            this.departments = this.buildDeptTreeByCode(response.depts)
          }
          
          console.log('角色详情加载成功', response)
        } catch (error) {
          console.error('加载角色详情失败', error)
          this.$message.error('加载角色详情失败：' + (error.message || '请检查网络连接'))
        }
      }
    },
    goBack() {
      this.$router.push('/home/role-management')
    },
    editRole() {
      this.$message.info('编辑角色功能')
      // TODO: 跳转到编辑页面或打开编辑对话框
    },
    editPermission() {
      this.permissionDialogVisible = true
      // TODO: 加载所有菜单和已选中的权限
      this.allMenus = [...this.permissions]
      this.checkedPermissions = [2, 3, 4]
    },
    savePermission() {
      // const checkedKeys = this.$refs.permissionTree.getCheckedKeys()
      this.$message.success('权限保存成功（模拟）')
      this.permissionDialogVisible = false
      // TODO: 调用后端接口保存权限
      // await updateRolePermission({ roleId: this.roleInfo.roleId, menuIds: checkedKeys })
    },
    
    // 显示新增部门对话框
    showAddDepartment() {
      this.loadingDepts = true
      this.addDepartmentDialogVisible = true
      this.selectedDeptCodes = []
      
      // 从store获取所有部门数据
      const allDepts = this.$store.getters.allDepartments || []
      
      if (allDepts.length === 0) {
        this.$message.warning('暂无部门数据，请先访问部门管理页面加载数据')
        this.addDepartmentDialogVisible = false
        this.loadingDepts = false
        return
      }
      
      // 过滤掉已经添加的部门
      const existingCodes = this.getAllDeptCodes(this.departments)
      const available = allDepts.filter(d => !existingCodes.includes(d.deptCode))
      
      // 构建成树形结构
      this.availableDepartments = this.buildDeptTreeByCode(available.map(d => ({
        deptId: d.deptId,
        deptName: d.deptName,
        deptCode: d.deptCode,
        expanded: true
      })))
      
      this.loadingDepts = false
    },
    
    // 显示删除部门对话框
    showDeleteDepartment() {
      if (this.departments.length === 0) {
        this.$message.warning('当前角色暂无部门数据')
        return
      }
      this.deleteDepartmentDialogVisible = true
      this.selectedDeleteDepts = []
    },
    
    // 显示更新部门对话框
    showUpdateDepartment() {
      this.loadingDepts = true
      this.updateDepartmentDialogVisible = true
      
      // 从store获取所有部门数据
      const allDepts = this.$store.getters.allDepartments || []
      
      if (allDepts.length === 0) {
        this.$message.warning('暂无部门数据，请先访问部门管理页面加载数据')
        this.updateDepartmentDialogVisible = false
        this.loadingDepts = false
        return
      }
      
      // 构建成树形结构
      this.allDepartmentsTree = this.buildDeptTreeByCode(allDepts.map(d => ({
        deptId: d.deptId,
        deptName: d.deptName,
        deptCode: d.deptCode,
        expanded: true
      })))
      
      // 初始化已选中的部门编码
      this.selectedDeptCodes = this.getAllDeptCodes(this.departments)
      this.updateCheckAllDeptStatus()
      
      this.loadingDepts = false
    },
    
    // 获取所有部门编码（递归）
    getAllDeptCodes(deptTree) {
      const codes = []
      const traverse = (items) => {
        items.forEach(item => {
          if (item.deptCode) {
            codes.push(item.deptCode)
          }
          if (item.children && item.children.length > 0) {
            traverse(item.children)
          }
        })
      }
      traverse(deptTree)
      return codes
    },
    
    // 保存新增部门
    saveAddDepartment() {
      if (this.selectedDeptCodes.length === 0) {
        this.$message.warning('请选择要添加的部门')
        return
      }
      
      this.submitLoading = true
      
      // TODO: 调用后端接口添加部门
      // await addRoleDept({ roleId: this.roleInfo.roleId, deptCodes: this.selectedDeptCodes })
      
      setTimeout(() => {
        this.$message.success(`成功添加 ${this.selectedDeptCodes.length} 个部门（模拟）`)
        this.addDepartmentDialogVisible = false
        this.submitLoading = false
        // 重新加载角色详情
        // this.loadRoleDetail()
      }, 500)
    },
    
    // 保存删除部门
    saveDeleteDepartment() {
      if (this.selectedDeleteDepts.length === 0) {
        this.$message.warning('请选择要删除的部门')
        return
      }
      
      const deptNames = this.selectedDeleteDepts.map(d => d.deptName).join('、')
      this.$confirm(`确定要删除选中的 ${this.selectedDeleteDepts.length} 个部门吗？<br/>部门：${deptNames}`, '删除部门', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        dangerouslyUseHTMLString: true
      }).then(() => {
        this.submitLoading = true
        
        const deptCodes = this.selectedDeleteDepts.map(d => d.deptCode)
        console.log('准备删除的部门编码：', deptCodes)
        // TODO: 调用后端接口删除部门
        // await deleteRoleDept({ roleId: this.roleInfo.roleId, deptCodes: deptCodes })
        
        setTimeout(() => {
          this.$message.success(`成功删除 ${this.selectedDeleteDepts.length} 个部门（模拟）`)
          this.deleteDepartmentDialogVisible = false
          this.submitLoading = false
          // 重新加载角色详情
          // this.loadRoleDetail()
        }, 500)
      }).catch(() => {})
    },
    
    // 保存更新部门
    saveUpdateDepartment() {
      this.submitLoading = true
      
      // TODO: 调用后端接口更新部门权限
      // await updateRoleDept({ roleId: this.roleInfo.roleId, deptCodes: this.selectedDeptCodes })
      
      setTimeout(() => {
        this.$message.success('部门更新成功（模拟）')
        this.updateDepartmentDialogVisible = false
        this.submitLoading = false
        // 重新加载角色详情
        // this.loadRoleDetail()
      }, 500)
    },
    
    // 切换部门选中状态
    toggleDeptSelection(dept) {
      const index = this.selectedDeptCodes.indexOf(dept.deptCode)
      if (index > -1) {
        this.selectedDeptCodes.splice(index, 1)
      } else {
        this.selectedDeptCodes.push(dept.deptCode)
      }
      this.updateCheckAllDeptStatus()
    },
    
    // 全选/取消全选
    handleCheckAllDeptChange(val) {
      if (val) {
        this.selectedDeptCodes = this.flatAllDepartments.map(d => d.deptCode)
      } else {
        this.selectedDeptCodes = []
      }
      this.updateCheckAllDeptStatus()
    },
    
    // 更新全选状态
    updateCheckAllDeptStatus() {
      const total = this.flatAllDepartments.length
      const selected = this.selectedDeptCodes.length
      this.checkAllDept = selected === total && total > 0
      this.isIndeterminateDept = selected > 0 && selected < total
    },
    
    // 切换展开/折叠
    toggleDeptExpand(dept) {
      dept.expanded = !dept.expanded
      this.$forceUpdate()
    },
    
    // 删除对话框 - 切换选中状态
    toggleDeleteDeptSelection(dept) {
      const index = this.selectedDeleteDepts.findIndex(d => d.deptCode === dept.deptCode)
      if (index > -1) {
        this.selectedDeleteDepts.splice(index, 1)
      } else {
        this.selectedDeleteDepts.push(dept)
      }
      this.updateCheckAllDeleteStatus()
    },
    
    // 删除对话框 - 全选/取消全选
    handleCheckAllDeleteChange(val) {
      if (val) {
        this.selectedDeleteDepts = [...this.flatDepartments]
      } else {
        this.selectedDeleteDepts = []
      }
      this.updateCheckAllDeleteStatus()
    },
    
    // 删除对话框 - 更新全选状态
    updateCheckAllDeleteStatus() {
      const total = this.flatDepartments.length
      const selected = this.selectedDeleteDepts.length
      this.checkAllDelete = selected === total && total > 0
      this.isIndeterminateDelete = selected > 0 && selected < total
    },
    getMenuIcon(type) {
      const iconMap = {
        directory: 'el-icon-folder',
        menu: 'el-icon-menu',
        button: 'el-icon-setting'
      }
      return iconMap[type] || 'el-icon-document'
    },
    getMenuTypeTag(type) {
      const tagMap = {
        directory: 'warning',
        menu: 'success',
        button: 'info'
      }
      return tagMap[type] || 'info'
    },
    getMenuTypeText(type) {
      const textMap = {
        directory: '目录',
        menu: '菜单',
        button: '按钮'
      }
      return textMap[type] || type
    }
  }
}
</script>

<style scoped>
.role-detail {
  padding: 20px;
}

/* 左上角角色信息卡片 */
.role-info-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  padding: 24px;
  color: white;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-header h3 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}

.card-content {
  margin-bottom: 20px;
}

.info-row {
  display: flex;
  margin-bottom: 12px;
  font-size: 14px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-label {
  width: 80px;
  opacity: 0.9;
  flex-shrink: 0;
}

.info-value {
  flex: 1;
  font-weight: 500;
}

.card-actions {
  display: flex;
  gap: 12px;
}

.card-actions .el-button {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
}

.card-actions .el-button:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* 详细内容区域 */
.detail-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.tab-content {
  padding: 20px;
  min-height: 400px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #f0f0f0;
}

.section-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

/* 权限列表 */
.permission-list,
.department-list {
  margin-top: 20px;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}

.custom-tree-node > span:first-child {
  display: flex;
  align-items: center;
  gap: 8px;
}

.custom-tree-node i {
  font-size: 16px;
}

/* 基本信息 */
.basic-info {
  padding: 20px;
}

/* 对话框样式 */
.dialog-footer {
  text-align: right;
}

.dialog-hint {
  padding: 12px;
  background-color: #f4f4f5;
  border-radius: 4px;
  margin-bottom: 16px;
  color: #606266;
  font-size: 14px;
}

.dialog-hint i {
  color: #409eff;
  margin-right: 8px;
}

.department-table-container {
  max-height: 500px;
  overflow-y: auto;
}

/* 表格样式 */
.custom-table-wrapper {
  margin-top: 0;
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
  cursor: pointer;
}

.custom-table tr.selected {
  background-color: #ecf5ff;
}

.selection-column {
  width: 55px;
  text-align: center;
}

.department-name-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.expand-icon {
  cursor: pointer;
  color: #606266;
  font-size: 14px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
}

.expand-icon:hover {
  color: #409eff;
}

.expand-placeholder {
  display: inline-block;
  width: 16px;
}

/* 响应式 */
@media (max-width: 768px) {
  .role-detail {
    padding: 12px;
  }
  
  .role-info-card {
    padding: 16px;
  }
  
  .card-header h3 {
    font-size: 20px;
  }
  
  .card-actions {
    flex-direction: column;
  }
  
  .card-actions .el-button {
    width: 100%;
  }
}
</style>
