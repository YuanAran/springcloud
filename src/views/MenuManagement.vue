<template>
  <div class="menu-management">
    <div class="page-header">
      <h2>菜单管理</h2>
      <p>管理系统菜单结构，支持目录、菜单、按钮三种类型</p>
    </div>

    <div class="operation-bar">
      <el-button type="primary" plain icon="el-icon-plus" @click="showAddDialog">新增菜单</el-button>
      <el-button type="success" plain icon="el-icon-refresh" @click="refreshList">刷新</el-button>
      <el-button type="info" plain icon="el-icon-sort" @click="expandAll">展开全部</el-button>
      <el-button type="warning" plain icon="el-icon-sort" @click="collapseAll">收起全部</el-button>
    </div>

    <!-- 菜单树形表格 -->
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
              <th>菜单名称</th>
              <th>组件路由</th>
              <th>路由地址</th>
              <th>菜单类型</th>
              <th>权限标识</th>
              <th>排序</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="menu in treeMenus" :key="menu.menuId" 
                :class="{ 
                  'selected': selectedMenus.includes(menu),
                  'tree-row': true,
                  'expanded': menu.expanded,
                  'has-children': menu.children && menu.children.length > 0
                }"
                :style="{ paddingLeft: (menu.level * 20) + 'px' }"
                @click="toggleRowSelection(menu)">
              
              <td class="selection-column">
                <el-checkbox 
                  :value="selectedMenus.includes(menu)"
                  @change="toggleRowSelection(menu)">
                </el-checkbox>
              </td>
              
              <td class="menu-name-cell">
                <div class="menu-name-wrapper">
                  <span 
                    v-if="menu.hasChildren"
                    class="expand-icon"
                    @click.stop="toggleExpand(menu)">
                    <i :class="menu.expanded ? 'el-icon-caret-bottom' : 'el-icon-caret-right'"></i>
                  </span>
                  <span v-else class="expand-placeholder"></span>
                  <el-tag size="small" type="primary">{{ menu.menuName }}</el-tag>
                </div>
              </td>
              
              <td>{{ menu.component || '-' }}</td>
              <td>{{ menu.path || '-' }}</td>
              <td>
                <el-tag 
                  :type="getMenuTypeTagType(menu.menuType)" 
                  size="small">
                  {{ getMenuTypeText(menu.menuType) }}
                </el-tag>
              </td>
              <td>{{ menu.perms || '-' }}</td>
              <td>{{ menu.sort }}</td>
              <td>
                <el-tag 
                  :type="menu.status==='1'?'success':'danger'" 
                  size="small">
                  {{ menu.status==='1'?'有效':'无效' }}
                </el-tag>
              </td>
              <td class="action-column">
                <el-button
                  class="action-btn"
                  size="mini"
                  type="text"
                  icon="el-icon-edit"
                  @click.stop="editNode(menu)">
                  修改
                </el-button>
                <el-button
                  class="action-btn"
                  size="mini"
                  type="text"
                  icon="el-icon-plus"
                  @click.stop="addChildNode(menu)">
                  新增
                </el-button>
                <el-button
                  class="action-btn danger"
                  size="mini"
                  type="text"
                  icon="el-icon-delete"
                  @click.stop="deleteNode(menu)">
                  删除
                </el-button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称"></el-input>
        </el-form-item>
        <el-form-item label="组件路由" prop="component">
          <el-input v-model="form.component" placeholder="请输入组件路由，如：@/views/UserManagement"></el-input>
        </el-form-item>
        <el-form-item label="路由地址" prop="path">
          <el-input v-model="form.path" placeholder="请输入路由地址，如：/home/user-management"></el-input>
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-select v-model="form.menuType" placeholder="请选择菜单类型" style="width:100%">
            <el-option label="目录" value="D"></el-option>
            <el-option label="菜单" value="M"></el-option>
            <el-option label="按钮" value="B"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="权限标识" prop="perms">
          <el-input v-model="form.perms" placeholder="请输入权限标识，如：system:user:list"></el-input>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="999" :step="1" style="width:100%;"></el-input-number>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width:100%">
            <el-option label="有效" :value="1"></el-option>
            <el-option label="无效" :value="0"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">{{ isEdit ? '更新' : '创建' }}</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'
import { getUserId } from '@/utils/auth'

export default {
  name: 'MenuManagement',
  data() {
    return {
      loading: false,
      submitLoading: false,
      dialogVisible: false,
      isEdit: false,
      currentParent: null,
      
      // 菜单树数据
      menuTree: [],
      treeMenus: [],
      selectedMenus: [],
      checkAll: false,
      isIndeterminate: false,
      
      // 表单数据
      form: {
        menuId: null,
        menuName: '',
        component: '',
        path: '',
        menuType: '',
        perms: '',
        sort: 0,
        status: 1,
        parentId: 0
      },
      
      // 表单验证规则
      rules: {
        menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
        menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }],
        status: [{ required: true, message: '请选择状态', trigger: 'change' }]
      }
    }
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑菜单' : '新增菜单'
    }
  },
  mounted() {
    this.fetchList()
  },
  methods: {
    // 获取菜单列表
    async fetchList() {
      this.loading = true
      try {
        const data = await request.get('/sys_menu/getAllMenu')
        console.log('后端返回数据:', data)
        const arr = Array.isArray(data) ? data : (data?.data || [])
        console.log('处理后的数组:', arr)
        this.menuTree = this.buildMenuTree(arr)
        // 默认展开所有节点
        this.expandAllNodes(this.menuTree)
        this.treeMenus = this.flattenTree(this.menuTree)
        console.log('构建的菜单树:', this.menuTree)
        console.log('扁平化菜单:', this.treeMenus)
      } catch (e) {
        this.$message.error('获取菜单列表失败：' + (e.response?.data?.message || '网络错误'))
        console.warn('fetchList error:', e)
      } finally {
        this.loading = false
      }
    },
    
    // 构建菜单树
    buildMenuTree(menuList) {
      if (!Array.isArray(menuList)) {
        console.log('menuList不是数组:', menuList)
        return []
      }
      
      console.log('开始构建菜单树，菜单数量:', menuList.length)
      const menuMap = new Map()
      const rootMenus = []
      
      // 创建菜单映射
      menuList.forEach(menu => {
        console.log('处理菜单:', menu.menuName, 'parentId:', menu.parentId)
        menuMap.set(menu.menuId, {
          ...menu,
          children: []
        })
      })
      
      // 构建树形结构
      menuList.forEach(menu => {
        const menuNode = menuMap.get(menu.menuId)
        // 判断是否为根节点：parentId为0、null、undefined或空字符串
        if (menu.parentId === 0 || menu.parentId === '0' || !menu.parentId || menu.parentId === '') {
          // 根节点
          console.log('添加根节点:', menu.menuName)
          rootMenus.push(menuNode)
        } else {
          // 子节点
          const parent = menuMap.get(menu.parentId)
          if (parent) {
            console.log('添加子节点:', menu.menuName, '到父节点:', parent.menuName)
            parent.children.push(menuNode)
          } else {
            // 如果找不到父节点，也作为根节点处理
            console.log('找不到父节点，作为根节点处理:', menu.menuName)
            rootMenus.push(menuNode)
          }
        }
      })
      
      console.log('最终根节点数量:', rootMenus.length)
      return rootMenus
    },
    
    // 扁平化树形结构
    flattenTree(tree, level = 0) {
      let result = []
      tree.forEach(node => {
        const flatNode = {
          ...node,
          level: level,
          expanded: node.expanded || false,
          hasChildren: node.children && node.children.length > 0
        }
        result.push(flatNode)
        // 只有当父节点展开时才显示子节点
        if (node.children && node.children.length > 0 && node.expanded) {
          result = result.concat(this.flattenTree(node.children, level + 1))
        }
      })
      return result
    },
    
    // 展开/收起
    toggleExpand(menu) {
      this.updateMenuExpanded(this.menuTree, menu.menuId, !menu.expanded)
      this.treeMenus = this.flattenTree(this.menuTree)
      this.$forceUpdate()
    },
    
    // 递归更新菜单的展开状态
    updateMenuExpanded(menuTree, targetId, expanded) {
      for (let i = 0; i < menuTree.length; i++) {
        if (menuTree[i].menuId === targetId) {
          menuTree[i].expanded = expanded
          return true
        }
        if (menuTree[i].children && menuTree[i].children.length > 0) {
          if (this.updateMenuExpanded(menuTree[i].children, targetId, expanded)) {
            return true
          }
        }
      }
      return false
    },
    
    // 展开所有节点
    expandAllNodes(tree) {
      tree.forEach(node => {
        node.expanded = true
        if (node.children && node.children.length > 0) {
          this.expandAllNodes(node.children)
        }
      })
    },
    
    // 展开全部
    expandAll() {
      this.expandAllNodes(this.menuTree)
      this.treeMenus = this.flattenTree(this.menuTree)
      this.$forceUpdate()
    },
    
    // 收起全部
    collapseAll() {
      this.collapseAllNodes(this.menuTree)
      this.treeMenus = this.flattenTree(this.menuTree)
      this.$forceUpdate()
    },
    
    // 收起所有节点
    collapseAllNodes(tree) {
      tree.forEach(node => {
        node.expanded = false
        if (node.children && node.children.length > 0) {
          this.collapseAllNodes(node.children)
        }
      })
    },
    
    // 选择行
    toggleRowSelection(menu) {
      const idx = this.selectedMenus.indexOf(menu)
      if (idx > -1) {
        this.selectedMenus.splice(idx, 1)
      } else {
        this.selectedMenus.push(menu)
      }
      this.updateCheckAll()
    },
    
    handleCheckAllChange(val) {
      this.selectedMenus = val ? [...this.treeMenus] : []
      this.updateCheckAll()
    },
    
    updateCheckAll() {
      const total = this.treeMenus.length
      const selected = this.selectedMenus.length
      this.checkAll = selected === total && total > 0
      this.isIndeterminate = selected > 0 && selected < total
    },
    
    // 刷新列表
    refreshList() {
      this.fetchList()
      this.$message.success('刷新成功')
    },
    
    // 获取菜单类型文本
    getMenuTypeText(type) {
      const typeMap = {
        'D': '目录',
        'M': '菜单',
        'B': '按钮'
      }
      return typeMap[type] || type
    },
    
    // 获取菜单类型标签样式
    getMenuTypeTagType(type) {
      const typeMap = {
        'D': 'warning',
        'M': 'success',
        'B': 'info'
      }
      return typeMap[type] || 'info'
    },
    
    
    // 显示新增对话框
    showAddDialog(parent = null) {
      this.isEdit = false
      this.dialogVisible = true
      this.currentParent = parent
      this.form = {
        menuId: null,
        menuName: '',
        component: '',
        path: '',
        menuType: '',
        perms: '',
        sort: 0,
        status: 1,
        parentId: parent ? parent.menuId : 0
      }
      if (this.$refs.form) this.$refs.form.clearValidate()
    },
    
    // 编辑节点
    editNode(data) {
      this.isEdit = true
      this.dialogVisible = true
      this.currentParent = null
      this.form = {
        menuId: data.menuId,
        menuName: data.menuName,
        component: data.component,
        path: data.path,
        menuType: data.menuType,
        perms: data.perms,
        sort: data.sort,
        status: data.status,
        parentId: data.parentId
      }
      if (this.$refs.form) this.$refs.form.clearValidate()
    },
    
    // 添加子节点
    addChildNode(data) {
      this.showAddDialog(data)
    },
    
    // 提交表单
    async submitForm() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.submitLoading = true
        try {
          if (this.isEdit) {
            // 更新菜单
            await request.post('/sys_menu/updateMenu', {
              menuId: this.form.menuId,
              menuName: this.form.menuName,
              component: this.form.component,
              path: this.form.path,
              menuType: this.form.menuType,
              perms: this.form.perms,
              sort: this.form.sort,
              status: String(this.form.status),
              parentId: this.form.parentId,
              updateBy: (getUserId() !== undefined && getUserId() !== null) ? String(getUserId()) : null
            })
            this.$message.success('更新成功')
          } else {
            // 新增菜单
            await request.post('/sys_menu/addMenu', {
              menuName: this.form.menuName,
              component: this.form.component,
              path: this.form.path,
              menuType: this.form.menuType,
              perms: this.form.perms,
              sort: this.form.sort,
              status: String(this.form.status),
              parentId: this.form.parentId,
              createBy: (getUserId() !== undefined && getUserId() !== null) ? String(getUserId()) : null
            })
            this.$message.success('创建成功')
          }
          this.dialogVisible = false
          await this.fetchList()
        } catch(e) {
          this.$message.error('操作失败：' + (e.response?.data?.message || '网络错误'))
          console.warn('submitForm error:', e)
        } finally {
          this.submitLoading = false
        }
      })
    },
    
    // 删除节点
    async deleteNode(data) {
      // 收集所有要删除的菜单ID（包括子菜单）
      const menuIdsToDelete = this.collectAllMenuIds(data)
      const hasChildren = menuIdsToDelete.length > 1
      
      const confirmMessage = hasChildren 
        ? `确定要删除菜单 "${data.menuName}" 及其所有子菜单吗？这将删除 ${menuIdsToDelete.length} 个菜单项。`
        : `确定要删除菜单 "${data.menuName}" 吗？`
      
      this.$confirm(confirmMessage, '删除菜单', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await request.post('/sys_menu/deleteMenu', menuIdsToDelete)
          this.$message.success(`删除成功，共删除 ${menuIdsToDelete.length} 个菜单项`)
          await this.fetchList()
        } catch(e) {
          this.$message.error('删除失败：' + (e.response?.data?.message || '网络错误'))
          console.warn('deleteNode error:', e)
        }
      }).catch(()=>{})
    },
    
    // 收集所有菜单ID（包括子菜单）
    collectAllMenuIds(menu) {
      const ids = [menu.menuId]
      
      // 递归收集子菜单ID
      const collectChildren = (children) => {
        if (children && children.length > 0) {
          children.forEach(child => {
            ids.push(child.menuId)
            if (child.children && child.children.length > 0) {
              collectChildren(child.children)
            }
          })
        }
      }
      
      collectChildren(menu.children)
      return ids
    },
    
    // 重置表单
    resetForm() {
      this.form = {
        menuId: null,
        menuName: '',
        component: '',
        path: '',
        menuType: '',
        perms: '',
        sort: 0,
        status: 1,
        parentId: 0
      }
      this.currentParent = null
      if (this.$refs.form) this.$refs.form.clearValidate()
    }
  }
}
</script>

<style scoped>
.menu-management {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.page-header {
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e8e8e8;
}

.page-header h2 {
  margin: 0 0 6px;
  font-size: 20px;
  font-weight: 600;
  color: #262626;
}

.page-header p {
  margin: 0;
  color: #8c8c8c;
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

.custom-table-wrapper {
  margin-top: 8px;
}

.table-container {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
  max-height: 60vh;
  overflow-y: auto;
  min-width: 1200px;
}

.custom-table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
  font-size: 14px;
}

.custom-table th {
  background: #f5f7fa;
  color: #909399;
  font-weight: 500;
  padding: 12px 14px;
  text-align: left;
  border-bottom: 1px solid #ebeef5;
}

.custom-table td {
  padding: 12px 14px;
  color: #606266;
  border-bottom: 1px solid #f0f0f0;
}

.custom-table tr:nth-child(even) {
  background: #fafafa;
}

.custom-table tr:hover {
  background: #f5f7fa;
}

.custom-table tr.selected {
  background: #ecf5ff;
}

.selection-column {
  width: 55px;
  text-align: center;
}

.menu-name-cell {
  min-width: 200px;
}

.menu-name-wrapper {
  display: flex;
  align-items: center;
}

.expand-icon {
  margin-right: 8px;
  cursor: pointer;
  color: #c0c4cc;
  font-size: 14px;
}

.expand-placeholder {
  width: 22px;
  display: inline-block;
}

.tree-row {
  transition: all 0.3s;
}

.tree-row.has-children .menu-name-wrapper {
  font-weight: 500;
}

.action-column {
  text-align: left;
  width: 200px;
}

.action-btn {
  padding: 0 6px !important;
  height: 24px;
  line-height: 22px;
  font-size: 12px;
}

.action-btn + .action-btn {
  margin-left: 8px;
}

.action-btn.danger {
  color: #f56c6c;
}

.name-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  background: #f0f9ff;
  color: #1890ff;
  line-height: 20px;
}
</style>
