import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    // 暂存部门数据
    cachedDepartments: [],
    // 暂存菜单数据
    cachedMenus: []
  },
  
  mutations: {
    // 设置部门数据
    SET_DEPARTMENTS(state, departments) {
      state.cachedDepartments = departments.map(dept => ({
        deptId: dept.id || dept.deptId,
        deptName: dept.name || dept.deptName,
        deptCode: dept.code || dept.deptCode
      }))
    },
    
    // 清空部门数据
    CLEAR_DEPARTMENTS(state) {
      state.cachedDepartments = []
    },
    
    // 设置菜单数据
    SET_MENUS(state, menus) {
      state.cachedMenus = menus.map(menu => ({
        menuId: menu.menuId,
        menuName: menu.menuName,
        menuType: menu.menuType,
        parentId: menu.parentId
      }))
    },
    
    // 清空菜单数据
    CLEAR_MENUS(state) {
      state.cachedMenus = []
    }
  },
  
  actions: {
    // 更新部门数据
    updateDepartments({ commit }, departments) {
      commit('SET_DEPARTMENTS', departments)
    },
    
    // 清空部门数据
    clearDepartments({ commit }) {
      commit('CLEAR_DEPARTMENTS')
    },
    
    // 更新菜单数据
    updateMenus({ commit }, menus) {
      commit('SET_MENUS', menus)
    },
    
    // 清空菜单数据
    clearMenus({ commit }) {
      commit('CLEAR_MENUS')
    }
  },
  
  getters: {
    // 获取所有缓存的部门数据
    allDepartments: state => state.cachedDepartments,
    
    // 根据ID获取部门名称
    getDepartmentNameById: state => id => {
      const dept = state.cachedDepartments.find(d => d.deptId === id)
      return dept ? dept.deptName : ''
    },
    
    // 根据编码获取部门信息
    getDepartmentByCode: state => code => {
      return state.cachedDepartments.find(d => d.deptCode === code)
    },
    
    // 获取所有缓存的菜单数据
    allMenus: state => state.cachedMenus,
    
    // 根据ID获取菜单名称
    getMenuNameById: state => id => {
      const menu = state.cachedMenus.find(m => m.menuId === id)
      return menu ? menu.menuName : ''
    }
  }
})
