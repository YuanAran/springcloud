<template>
  <div class="post-management">
    <div class="page-header">
      <h2>岗位管理</h2>
      <p>管理系统岗位信息，支持增删改查功能</p>
    </div>

    <!-- 操作栏 -->
    <div class="operation-bar">
      <el-button type="primary" plain icon="el-icon-plus" @click="showAddDialog">新增岗位</el-button>
      <el-button 
        type="danger" 
        plain 
        icon="el-icon-delete" 
        :disabled="selectedPosts.length === 0"
        @click="handleBatchDelete">
        批量删除 ({{ selectedPosts.length }})
      </el-button>
      <el-button type="success" plain icon="el-icon-refresh" @click="fetchPosts">刷新</el-button>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input 
        v-model="searchQuery" 
        placeholder="请输入岗位名称或编码搜索"
        prefix-icon="el-icon-search"
        clearable
        @clear="handleSearch"
        @keyup.enter.native="handleSearch"
        style="width: 300px; margin-right: 10px;">
      </el-input>
      <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
    </div>

    <!-- 表格区域 -->
    <div class="custom-table-wrapper">
      <div class="table-container" v-loading="loading">
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
              <th>岗位编码</th>
              <th>岗位名称</th>
              <th>显示顺序</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="post in posts" :key="post.postId" 
                :class="{ 'selected': selectedPosts.includes(post) }"
                @click="toggleRowSelection(post)">
              <td class="selection-column">
                <el-checkbox 
                  :value="selectedPosts.includes(post)"
                  @change="toggleRowSelection(post)">
                </el-checkbox>
              </td>
              <td>
                <el-tag size="small" type="primary">{{ post.postCode }}</el-tag>
              </td>
              <td>{{ post.postName }}</td>
              <td>{{ post.sort }}</td>
              <td>{{ post.createTime || '-' }}</td>
              <td>
                <el-button type="text" size="small" @click.stop="handleEdit(post)">编辑</el-button>
                <el-button type="text" size="small" style="color: #f56c6c;" @click.stop="handleDelete(post)">删除</el-button>
              </td>
            </tr>
          </tbody>
        </table>
        <el-empty v-if="posts.length === 0 && !loading" description="暂无数据"></el-empty>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
      </el-pagination>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog 
      :title="dialogTitle" 
      :visible.sync="dialogVisible" 
      width="600px"
      @close="resetForm">
      <el-form :model="postForm" :rules="formRules" ref="postForm" label-width="100px">
        <el-form-item label="岗位编码" prop="postCode">
          <el-input v-model="postForm.postCode" placeholder="请输入岗位编码"></el-input>
        </el-form-item>
        <el-form-item label="岗位名称" prop="postName">
          <el-input v-model="postForm.postName" placeholder="请输入岗位名称"></el-input>
        </el-form-item>
        <el-form-item label="显示顺序" prop="sort">
          <el-input-number v-model="postForm.sort" :min="0" :max="9999"></el-input-number>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getAllPost, addPost, updatePost, deletePost } from '@/api/post'

export default {
  name: 'PostManagement',
  data() {
    return {
      loading: false,
      submitLoading: false,
      allPosts: [], // 存储所有岗位数据
      posts: [], // 显示的岗位数据（经过搜索和分页）
      selectedPosts: [],
      checkAll: false,
      isIndeterminate: false,
      searchQuery: '',
      currentPage: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: '新增岗位',
      isEdit: false,
      postForm: {
        postId: '',
        postCode: '',
        postName: '',
        sort: 0
      },
      formRules: {
        postCode: [
          { required: true, message: '请输入岗位编码', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        postName: [
          { required: true, message: '请输入岗位名称', trigger: 'blur' },
          { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
        ],
        sort: [
          { required: true, message: '请输入显示顺序', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.fetchPosts()
  },
  methods: {
    // 获取所有岗位数据（首次加载或刷新时调用）
    async fetchPosts() {
      this.loading = true
      try {
        const response = await getAllPost()
        this.allPosts = response || []
        this.filterAndPaginatePosts()
        this.loading = false
      } catch (error) {
        console.error('获取岗位列表失败:', error)
        this.$message.error('获取岗位列表失败：' + (error.message || '未知错误'))
        this.loading = false
      }
    },
    
    // 前端过滤和分页
    filterAndPaginatePosts() {
      let filteredPosts = [...this.allPosts]
      
      // 根据搜索关键词过滤
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase()
        filteredPosts = filteredPosts.filter(post => {
          return (
            post.postName.toLowerCase().includes(query) ||
            post.postCode.toLowerCase().includes(query)
          )
        })
      }
      
      // 更新总数
      this.total = filteredPosts.length
      
      // 分页
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      this.posts = filteredPosts.slice(start, end)
      
      // 清空选中项
      this.selectedPosts = []
      this.updateCheckAllStatus()
    },

    // 显示新增对话框
    showAddDialog() {
      this.dialogTitle = '新增岗位'
      this.isEdit = false
      this.resetForm()
      this.dialogVisible = true
    },

    // 编辑岗位
    handleEdit(post) {
      this.dialogTitle = '编辑岗位'
      this.isEdit = true
      this.postForm = { ...post }
      this.dialogVisible = true
    },

    // 提交表单
    handleSubmit() {
      this.$refs.postForm.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true
          try {
            if (this.isEdit) {
              await updatePost(this.postForm)
              this.$message.success('岗位更新成功')
            } else {
              await addPost(this.postForm)
              this.$message.success('岗位新增成功')
            }
            this.dialogVisible = false
            this.fetchPosts()
          } catch (error) {
            this.$message.error('操作失败：' + (error.message || '未知错误'))
          } finally {
            this.submitLoading = false
          }
        }
      })
    },

    // 删除岗位
    handleDelete(post) {
      this.$confirm(`确定要删除岗位"${post.postName}"吗？`, '删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deletePost([post.postId])
          this.$message.success('删除成功')
          this.fetchPosts()
        } catch (error) {
          this.$message.error('删除失败：' + (error.message || '未知错误'))
        }
      }).catch(() => {})
    },

    // 批量删除
    handleBatchDelete() {
      const count = this.selectedPosts.length
      this.$confirm(`确定要删除选中的 ${count} 个岗位吗？`, '批量删除确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const postIds = this.selectedPosts.map(p => p.postId)
          await deletePost(postIds)
          this.$message.success(`成功删除 ${count} 个岗位`)
          this.fetchPosts()
        } catch (error) {
          this.$message.error('批量删除失败：' + (error.message || '未知错误'))
        }
      }).catch(() => {})
    },

    // 切换行选择
    toggleRowSelection(post) {
      const index = this.selectedPosts.indexOf(post)
      if (index > -1) {
        this.selectedPosts.splice(index, 1)
      } else {
        this.selectedPosts.push(post)
      }
      this.updateCheckAllStatus()
    },

    // 全选/取消全选
    handleCheckAllChange(val) {
      this.selectedPosts = val ? [...this.posts] : []
      this.isIndeterminate = false
    },

    // 更新全选状态
    updateCheckAllStatus() {
      const selectedCount = this.selectedPosts.length
      const totalCount = this.posts.length
      this.checkAll = selectedCount === totalCount && totalCount > 0
      this.isIndeterminate = selectedCount > 0 && selectedCount < totalCount
    },

    // 搜索功能
    handleSearch() {
      this.currentPage = 1 // 重置到第一页
      this.filterAndPaginatePosts()
    },

    // 分页
    handleSizeChange(val) {
      this.pageSize = val
      this.currentPage = 1 // 重置到第一页
      this.filterAndPaginatePosts()
    },

    handleCurrentChange(val) {
      this.currentPage = val
      this.filterAndPaginatePosts()
    },

    // 重置表单
    resetForm() {
      this.postForm = {
        postId: '',
        postCode: '',
        postName: '',
        sort: 0
      }
      if (this.$refs.postForm) {
        this.$refs.postForm.clearValidate()
      }
    }
  }
}
</script>

<style scoped>
.post-management {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: calc(100vh - 60px);
}

.page-header {
  background: white;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 24px;
}

.page-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.operation-bar {
  background: white;
  padding: 15px 20px;
  border-radius: 4px;
  margin-bottom: 15px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.search-bar {
  background: white;
  padding: 15px 20px;
  border-radius: 4px;
  margin-bottom: 15px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.custom-table-wrapper {
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.table-container {
  overflow-x: auto;
  min-height: 200px;
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

.pagination-container {
  background: white;
  padding: 15px 20px;
  border-radius: 4px;
  margin-top: 15px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: flex-end;
}
</style>
