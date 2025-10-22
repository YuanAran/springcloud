# 部门数据暂存使用说明

## 概述
部门管理模块会在获取部门数据后自动将 `deptId`、`deptName`、`deptCode` 三个字段暂存到 Vuex Store 中，供其他模块使用。

## 暂存的数据结构
```javascript
{
  deptId: Number,    // 部门ID
  deptName: String,  // 部门名称
  deptCode: String   // 部门编码
}
```

## 在其他模块中使用

### 1. 获取所有部门数据
```javascript
// 在组件的 computed 中
computed: {
  allDepartments() {
    return this.$store.getters.allDepartments
  }
}

// 或直接访问
const departments = this.$store.getters.allDepartments
```

### 2. 根据ID获取部门名称
```javascript
const deptName = this.$store.getters.getDepartmentNameById(deptId)
```

### 3. 根据编码获取部门信息
```javascript
const dept = this.$store.getters.getDepartmentByCode('LD001')
console.log(dept.deptName) // 部门名称
```

### 4. 在角色详细页面中使用示例
```vue
<script>
export default {
  computed: {
    // 获取所有部门用于下拉选择
    departmentOptions() {
      return this.$store.getters.allDepartments.map(dept => ({
        value: dept.deptId,
        label: dept.deptName
      }))
    }
  },
  methods: {
    // 显示部门名称
    showDepartmentName(deptId) {
      const name = this.$store.getters.getDepartmentNameById(deptId)
      console.log('部门名称：', name)
    }
  }
}
</script>
```

## 刷新部门数据
在部门管理页面点击"刷新"按钮，会重新从后端获取数据并更新暂存。

## 注意事项
1. 首次使用前，请确保已访问过部门管理页面，以触发数据的初始加载
2. 暂存的数据在页面刷新后会丢失，需要重新访问部门管理页面
3. 如果需要持久化存储，可以考虑使用 localStorage 或 sessionStorage
