import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    // 暂存部门数据
    cachedDepartments: []
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
    }
  }
})
