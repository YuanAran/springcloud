<template>
  <div class="dict-management">
    <div class="page-header">
      <h2>字典管理</h2>
      <p>管理系统中的数据字典</p>
    </div>

    <!-- 操作栏 -->
    <div class="operation-bar">
      <el-button type="primary" plain icon="el-icon-plus" @click="showAddDialog">
        新增字典
      </el-button>
      <el-button type="danger" plain icon="el-icon-delete" :disabled="selectedRows.length === 0" @click="batchDelete">
        批量删除
      </el-button>

      <div class="search-box">
        <div class="custom-input-container">
          <i class="el-icon-search search-icon"></i>
          <input
            type="text"
            v-model="searchKeyword"
            placeholder="搜索字典名称/类型"
            @input="handleSearch"
            class="custom-input">
          <i class="el-icon-circle-close clear-icon" 
             v-if="searchKeyword" 
             @click="clearSearch"></i>
        </div>
      </div>
    </div>

    <!-- 字典表格（自定义表格） -->
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
              <th style="width:100px;">字典编号</th>
              <th>字典名称</th>
              <th>字典类型</th>
              <th style="width:110px;">状态</th>
              <th style="width:220px;">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, idx) in displayedRows" :key="row.id" :class="{selected: selectedRows.includes(row)}" @click="toggleRowSelection(row)">
              <td class="selection-column">
                <el-checkbox :value="selectedRows.includes(row)" @change="toggleRowSelection(row)"></el-checkbox>
              </td>
              <!-- 字典编号：从1开始的自增显示，与当前排序一致 -->
              <td>{{ idx + 1 }}</td>
              <td>
                <span class="name-tag">{{ row.dictName }}</span>
              </td>
              <td>
                <el-link type="primary" @click.stop="onTypeClick(row)">{{ row.dictCode }}</el-link>
              </td>
              <td>
                <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                  {{ row.status === 1 ? '正常' : '停用' }}
                </el-tag>
              </td>
              <td class="action-column">
                <el-button class="action-btn" size="mini" type="text" icon="el-icon-plus" @click.stop="showAddDialog(row)">新增</el-button>
                <el-button class="action-btn" size="mini" type="text" icon="el-icon-edit" @click.stop="showEditDialog(row)">修改</el-button>
                <el-button class="action-btn danger" size="mini" type="text" icon="el-icon-delete" @click.stop="deleteOne(row)">删除</el-button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="520px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="form" label-width="90px">
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="form.dictName" placeholder="请输入字典名称"></el-input>
        </el-form-item>
        <el-form-item label="字典类型" prop="dictCode">
          <el-input v-model="form.dictCode" placeholder="请输入字典类型（dictCode）"></el-input>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width:100%">
            <el-option label="正常" :value="1"></el-option>
            <el-option label="停用" :value="0"></el-option>
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

export default {
  name: 'DictionaryManagement',
  data() {
    return {
      loading: false,
      submitLoading: false,
      rows: [],
      filtered: [],
      selectedRows: [],
      checkAll: false,
      isIndeterminate: false,
      searchKeyword: '',

      dialogVisible: false,
      isEdit: false,
      form: {
        id: null,
        dictName: '',
        dictCode: '',
        status: 1
      },

      rules: {
        dictName: [
          { required: true, message: '请输入字典名称', trigger: 'blur' },
          { min: 2, max: 30, message: '长度在 2 到 30 个字符', trigger: 'blur' }
        ],
        dictCode: [
          { required: true, message: '请输入字典类型（dictCode）', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    dialogTitle() { return this.isEdit ? '修改字典' : '新增字典' },
    displayedRows() { return this.filtered } // 用于自增编号显示
  },
  mounted() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        // 后端接口约定：返回数组，每项包含 id, dictName, dictCode, status(1|0)
        const data = await request.get('/sys_dict/selectList')
        const arr = Array.isArray(data) ? data : (data?.data || [])
        this.rows = (arr || []).map((d, i) => ({
          id: d.id || d.dictId || i + 1,
          dictId: d.dictId || d.id || String(i + 1),
          dictName: d.dictName || d.name || '',
          dictCode: d.dictCode || d.type || '',
          status: d.status !== undefined ? (String(d.status) === '1' ? 1 : 0) : 1
        }))
        this.filtered = [...this.rows]
      } catch (e) {
        // 模拟数据
        this.rows = [
          { id: '1', dictId: '1', dictName: '性别', dictCode: 'gender', status: 1 },
          { id: '2', dictId: '2', dictName: '启用状态', dictCode: 'enable_status', status: 1 },
          { id: '3', dictId: '3', dictName: '颜色', dictCode: 'color', status: 0 }
        ]
        this.filtered = [...this.rows]
      } finally {
        this.loading = false
      }
    },

    // 搜索
    handleSearch() {
      const kw = this.searchKeyword.trim().toLowerCase()
      if (!kw) { this.filtered = [...this.rows]; return }
      this.filtered = this.rows.filter(r =>
        (r.dictName || '').toLowerCase().includes(kw) ||
        (r.dictCode || '').toLowerCase().includes(kw)
      )
    },
    clearSearch() { this.searchKeyword = ''; this.handleSearch() },

    // 选择
    toggleRowSelection(row) {
      const idx = this.selectedRows.indexOf(row)
      if (idx > -1) this.selectedRows.splice(idx, 1)
      else this.selectedRows.push(row)
      this.updateCheckAll()
    },
    handleCheckAllChange(val) {
      this.selectedRows = val ? [...this.displayedRows] : []
      this.updateCheckAll()
    },
    updateCheckAll() {
      const total = this.displayedRows.length
      const selected = this.selectedRows.length
      this.checkAll = selected === total && total > 0
      this.isIndeterminate = selected > 0 && selected < total
    },

    // 操作
    showAddDialog() {
      this.isEdit = false
      this.dialogVisible = true
      this.form = { id: null, dictName: '', dictCode: '', status: 1 }
      if (this.$refs.form) this.$refs.form.clearValidate()
    },
    showEditDialog(row) {
      this.isEdit = true
      this.dialogVisible = true
      this.form = { id: row.id, dictName: row.dictName, dictCode: row.dictCode, status: row.status }
      if (this.$refs.form) this.$refs.form.clearValidate()
    },
    async submitForm() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.submitLoading = true
        try {
          if (this.isEdit) {
            await request.post('/sys_dict/update', {
              dictId: this.form.id,
              dictName: this.form.dictName,
              dictCode: this.form.dictCode,
              status: String(this.form.status)
            })
            this.$message.success('更新成功')
          } else {
            await request.post('/sys_dict/insert', {
              dictName: this.form.dictName,
              dictCode: this.form.dictCode,
              status: String(this.form.status)
            })
            this.$message.success('创建成功')
          }
          this.dialogVisible = false
          await this.fetchList()
        } catch (e) {
          this.$message.error('操作失败：' + (e.response?.data?.message || '网络错误'))
        } finally {
          this.submitLoading = false
        }
      })
    },
    async deleteOne(row) {
      this.$confirm(`确定要删除字典 "${row.dictName}" 吗？`, '删除字典', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await request.post('/sys_dict/delete', [row.id])
          this.$message.success('删除成功')
          await this.fetchList()
        } catch (e) {
          this.$message.error('删除失败：' + (e.response?.data?.message || '网络错误'))
        }
      }).catch(() => {})
    },
    async batchDelete() {
      if (this.selectedRows.length === 0) {
        this.$message.warning('请选择要删除的字典')
        return
      }
      this.$confirm(`确定要删除选中的 ${this.selectedRows.length} 条字典吗？`, '批量删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const ids = this.selectedRows.map(r => r.id)
          await request.post('/sys_dict/delete', ids)
          this.$message.success(`批量删除成功，共删除 ${this.selectedRows.length} 条`)
          await this.fetchList()
          this.selectedRows = []
          this.updateCheckAll()
        } catch (e) {
          this.$message.error('批量删除失败：' + (e.response?.data?.message || '网络错误'))
        }
      }).catch(() => {})
    },

    // 点击类型链接
    onTypeClick(row) {
      // 跳转到字典项管理页，并传递 dictId、dictCode
      try { localStorage.setItem('current_dict_id', String(row.dictId)) } catch(e) { console.warn('persist dictId failed', e) }
      try { localStorage.setItem('current_dict_code', String(row.dictCode)) } catch(e) { console.warn('persist dictCode failed', e) }
      this.$router.push({ path: '/home/dict-item', query: { dictId: row.dictId, dictCode: row.dictCode } })
    }
  }
}
</script>

<style scoped>
.dict-management {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
.page-header { margin-bottom: 16px; padding-bottom: 12px; border-bottom: 1px solid #e8e8e8; }
.page-header h2 { margin: 0 0 6px; font-size: 20px; font-weight: 600; color: #262626; }
.page-header p { margin: 0; color: #8c8c8c; }

.operation-bar { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.operation-bar .el-button { margin-right: 8px; }
.search-box { width: 300px; }

.custom-table-wrapper { margin-top: 8px; }
.table-container { border: 1px solid #ebeef5; border-radius: 4px; overflow: hidden; max-height: 60vh; overflow-y: auto; }
.custom-table { width: 100%; border-collapse: collapse; background: #fff; font-size: 14px; min-width: 780px; }
.custom-table th { background: #f5f7fa; color: #909399; font-weight: 500; padding: 12px 14px; text-align: left; }
.custom-table td { padding: 12px 14px; color: #606266; }
.custom-table tr:nth-child(even) { background: #fafafa; }
.custom-table tr:hover { background: #f5f7fa; }
.selection-column { width: 55px; text-align: center; }
.action-column { text-align: left; width: 260px; }
.action-btn { padding: 0 6px !important; height: 24px; line-height: 22px; font-size: 12px; }
.action-btn + .action-btn { margin-left: 8px; }
.action-btn.danger { color: #f56c6c; }

/* 字典名称标签美化 */
.name-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  background: #f0f9ff;
  color: #1890ff;
  line-height: 20px;
}

/* 搜索输入 */
.custom-input-container { position: relative; display: inline-block; width: 240px; }
.custom-input { width: 100%; height: 32px; padding: 0 30px 0 30px; border: 1px solid #dcdfe6; border-radius: 4px; font-size: 14px; color: #606266; background-color: #fff; outline: none; transition: border-color .2s; }
.custom-input:focus { border-color: #409eff; }
.custom-input::placeholder { color: #c0c4cc; }
.search-icon { position: absolute; left: 8px; top: 50%; transform: translateY(-50%); color: #c0c4cc; font-size: 14px; pointer-events: none; }
.clear-icon { position: absolute; right: 8px; top: 50%; transform: translateY(-50%); color: #c0c4cc; font-size: 14px; cursor: pointer; transition: color .2s; }
.clear-icon:hover { color: #909399; }
</style>
