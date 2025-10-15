<template>
  <div class="dict-item-management">
    <div class="page-header">
      <div class="header-content">
        <div class="header-left">
          <el-button type="text" icon="el-icon-arrow-left" @click="goBack" class="back-btn">返回</el-button>
          <h2>字典项管理</h2>
        </div>
        <p>
          字典：
          <el-tag type="info" size="small" style="margin-right:8px;">ID: {{ dictId || '-' }}</el-tag>
          <el-tag type="primary" size="small">编码: {{ dictCode || '-' }}</el-tag>
        </p>
      </div>
    </div>

    <div class="operation-bar">
      <el-button type="primary" plain icon="el-icon-plus" @click="showAddDialog">新增字典项</el-button>
      <el-button type="danger" plain icon="el-icon-delete" :disabled="selectedRows.length===0" @click="batchDelete">批量删除</el-button>

      <div class="search-box">
        <div class="custom-input-container">
          <i class="el-icon-search search-icon"></i>
          <input type="text" class="custom-input" v-model="searchKeyword" placeholder="搜索标签/键值" @input="handleSearch">
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
              <th style="width:100px;">字典编码</th>
              <th>字典标签</th>
              <th>字典键值</th>
              <th style="width:120px;">字典排序</th>
              <th style="width:110px;">状态</th>
              <th style="width:220px;">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, idx) in displayedRows" :key="row.id" :class="{selected: selectedRows.includes(row)}" @click="toggleRowSelection(row)">
              <td class="selection-column">
                <el-checkbox :value="selectedRows.includes(row)" @change="toggleRowSelection(row)"></el-checkbox>
              </td>
              <!-- 字典编码：与上个页面的字典编号一致，按当前排序从1开始自增显示 -->
              <td>{{ idx + 1 }}</td>
              <td><span class="name-tag">{{ row.dictLabel }}</span></td>
              <td>{{ row.dictValue }}</td>
              <td>{{ row.dictSort }}</td>
              <td>
                <el-tag :type="row.status===1?'success':'danger'" size="small">{{ row.status===1?'正常':'停用' }}</el-tag>
              </td>
              <td class="action-column">
                <el-button class="action-btn" type="text" size="mini" icon="el-icon-edit" @click.stop="showEditDialog(row)">修改</el-button>
                <el-button class="action-btn danger" type="text" size="mini" icon="el-icon-delete" @click.stop="deleteOne(row)">删除</el-button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="520px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="字典标签" prop="dictLabel">
          <el-input v-model="form.dictLabel" placeholder="请输入字典标签"></el-input>
        </el-form-item>
        <el-form-item label="字典键值" prop="dictValue">
          <el-input v-model="form.dictValue" placeholder="请输入字典键值"></el-input>
        </el-form-item>
        <el-form-item label="字典排序" prop="dictSort">
          <el-input-number v-model="form.dictSort" :min="0" :max="999999" :step="1" style="width:100%;"></el-input-number>
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
import { getUserId } from '@/utils/auth'

export default {
  name: 'DictionaryItemManagement',
  data() {
    return {
      dictId: null,
      dictCode: '',

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
        dictLabel: '',
        dictValue: '',
        dictSort: 0,
        status: 1
      },
      rules: {
        dictLabel: [ { required: true, message: '请输入字典标签', trigger: 'blur' } ],
        dictValue: [ { required: true, message: '请输入字典键值', trigger: 'blur' } ],
        dictSort: [ { required: true, message: '请输入字典排序', trigger: 'change' } ],
        status: [ { required: true, message: '请选择状态', trigger: 'change' } ]
      }
    }
  },
  computed: {
    dialogTitle() { return this.isEdit ? '修改字典项' : '新增字典项' },
    displayedRows() { return this.filtered }
  },
  mounted() {
    // 从路由或本地读取 dictId、dictCode
    const q = this.$route?.query || {}
    this.dictId = q.dictId || localStorage.getItem('current_dict_id') || null
    this.dictCode = q.dictCode || localStorage.getItem('current_dict_code') || ''
    if (q.dictCode) {
      try { localStorage.setItem('current_dict_code', String(q.dictCode)) } catch(e) { console.warn('persist dictCode failed', e) }
    }
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        if (!this.dictId) {
          this.$message.warning('缺少字典ID，无法获取数据')
          return
        }
        // 调用后端接口获取字典项列表
        const data = await request.get('/sys_dict/item/selectByDictId', {
          params: { dictId: this.dictId }
        })
        const arr = Array.isArray(data) ? data : (data?.data || [])
        this.rows = (arr || []).map((d, i) => ({
          id: d.id || d.dictItemId || i + 1,
          itemId: d.itemId || d.id || d.dictItemId || String(i + 1), // 保存后端传来的itemId
          dictLabel: d.itemText || d.dictLabel || d.label || '',
          dictValue: d.itemValue || d.dictValue || d.value || '',
          dictSort: d.sort || d.dictSort || 0,
          status: d.status !== undefined ? (String(d.status) === '1' ? 1 : 0) : 1
        }))
        this.filtered = [...this.rows]
      } catch (e) {
        this.$message.error('获取字典项列表失败：' + (e.response?.data?.message || '网络错误'))
        console.warn('fetchList error:', e)
      } finally {
        this.loading = false
      }
    },
    // 搜索
    handleSearch() {
      const kw = this.searchKeyword.trim().toLowerCase()
      if (!kw) { this.filtered = [...this.rows]; return }
      this.filtered = this.rows.filter(r =>
        (r.dictLabel || '').toLowerCase().includes(kw) ||
        (r.dictValue || '').toLowerCase().includes(kw)
      )
    },
    clearSearch() { this.searchKeyword=''; this.handleSearch() },

    // 选择
    toggleRowSelection(row) {
      const idx = this.selectedRows.indexOf(row)
      if (idx > -1) {
        this.selectedRows.splice(idx, 1)
      } else {
        this.selectedRows.push(row)
      }
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
      this.form = { id: null, dictLabel: '', dictValue: '', dictSort: this.rows.length + 1, status: 1 }
      if (this.$refs.form) this.$refs.form.clearValidate()
    },
    showEditDialog(row) {
      this.isEdit = true
      this.dialogVisible = true
      this.form = { 
        id: row.itemId, // 使用itemId作为表单的id
        dictLabel: row.dictLabel, 
        dictValue: row.dictValue, 
        dictSort: row.dictSort, 
        status: row.status 
      }
      if (this.$refs.form) this.$refs.form.clearValidate()
    },
    async submitForm() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.submitLoading = true
        try {
          if (this.isEdit) {
            // 更新字典项
            await request.post('/sys_dict/item/update', {
              itemId: this.form.id, // 使用itemId作为更新参数
              itemText: this.form.dictLabel,
              itemValue: this.form.dictValue,
              sort: this.form.dictSort,
              status: String(this.form.status),
              updateBy: (getUserId() !== undefined && getUserId() !== null) ? String(getUserId()) : null
            })
            this.$message.success('更新成功')
          } else {
            // 新增字典项
            await request.post('/sys_dict/item/insert', {
              dictId: this.dictId,
              itemText: this.form.dictLabel,
              itemValue: this.form.dictValue,
              sort: this.form.dictSort,
              status: String(this.form.status),
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
    async deleteOne(row) {
      this.$confirm(`确定要删除字典项 "${row.dictLabel}" 吗？`, '删除字典项', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await request.post('/sys_dict/item/delete', [row.itemId]) // 单个删除也使用数组格式
          this.$message.success('删除成功')
          await this.fetchList()
        } catch(e) {
          this.$message.error('删除失败：' + (e.response?.data?.message || '网络错误'))
          console.warn('deleteOne error:', e)
        }
      }).catch(()=>{})
    },
    async batchDelete() {
      if (this.selectedRows.length===0) { this.$message.warning('请选择要删除的字典项'); return }
      this.$confirm(`确定要删除选中的 ${this.selectedRows.length} 条字典项吗？`, '批量删除', {
        confirmButtonText: '确定', cancelButtonText:'取消', type:'warning'
      }).then(async ()=>{
        try {
          // 批量删除
          const itemIds = this.selectedRows.map(row => row.itemId)
          await request.post('/sys_dict/item/delete', itemIds) // 批量删除参数是以itemId为元素的数组
          this.$message.success(`批量删除成功，共删除 ${this.selectedRows.length} 条`)
          await this.fetchList()
          this.selectedRows=[]
          this.updateCheckAll()
        } catch(e) {
          this.$message.error('批量删除失败：' + (e.response?.data?.message || '网络错误'))
          console.warn('batchDelete error:', e)
        }
      }).catch(()=>{})
    },
    goBack() {
      // 清空本地存储的字典ID和编码
      localStorage.removeItem('current_dict_id')
      localStorage.removeItem('current_dict_code')
      this.$router.push('/home/dict-management')
    }
  }
}
</script>

<style scoped>
.dict-item-management { background:#fff; border-radius:8px; padding:24px; box-shadow:0 2px 8px rgba(0,0,0,0.1); }
.page-header { margin-bottom:16px; padding-bottom:12px; border-bottom:1px solid #e8e8e8; }
.header-content { display: flex; justify-content: space-between; align-items: center; }
.header-left { display: flex; align-items: center; }
.back-btn { margin-right: 12px; font-size: 14px; color: #409eff; }
.back-btn:hover { color: #66b1ff; }
.page-header h2 { margin:0; font-size:20px; font-weight:600; color:#262626; }
.page-header p { margin:0; color:#8c8c8c; }

.operation-bar { display:flex; justify-content:space-between; align-items:center; margin-bottom:12px; }
.operation-bar .el-button { margin-right:8px; }
.search-box { width:300px; }

.custom-table-wrapper { margin-top:8px; }
.table-container { border:1px solid #ebeef5; border-radius:4px; overflow:hidden; max-height:60vh; overflow-y:auto; }
.custom-table { width:100%; border-collapse:collapse; background:#fff; font-size:14px; min-width:900px; }
.custom-table th { background:#f5f7fa; color:#909399; font-weight:500; padding:12px 14px; text-align:left; }
.custom-table td { padding:12px 14px; color:#606266; }
.custom-table tr:nth-child(even){ background:#fafafa; }
.custom-table tr:hover { background:#f5f7fa; }
.selection-column { width:55px; text-align:center; }
.action-column { text-align:left; width:220px; }
.action-btn { padding:0 6px !important; height:24px; line-height:22px; font-size:12px; }
.action-btn + .action-btn { margin-left:8px; }
.action-btn.danger { color:#f56c6c; }

.name-tag { display:inline-block; padding:2px 8px; border-radius:4px; background:#f0f9ff; color:#1890ff; line-height:20px; }

.custom-input-container { position:relative; display:inline-block; width:240px; }
.custom-input { width:100%; height:32px; padding:0 30px 0 30px; border:1px solid #dcdfe6; border-radius:4px; font-size:14px; color:#606266; background-color:#fff; outline:none; transition:border-color .2s; }
.custom-input:focus { border-color:#409eff; }
.custom-input::placeholder { color:#c0c4cc; }
.search-icon { position:absolute; left:8px; top:50%; transform:translateY(-50%); color:#c0c4cc; font-size:14px; pointer-events:none; }
.clear-icon { position:absolute; right:8px; top:50%; transform:translateY(-50%); color:#c0c4cc; font-size:14px; cursor:pointer; transition:color .2s; }
.clear-icon:hover { color:#909399; }
</style>
