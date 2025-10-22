import request from '@/utils/request'

// 获取所有角色
export function getAllRole() {
  return request({
    url: '/sys_role/getAllRole',
    method: 'get'
  })
}

// 添加角色
export function addRole(data) {
  return request({
    url: '/sys_role/addRole',
    method: 'post',
    data
  })
}

// 更新角色
export function updateRole(data) {
  return request({
    url: '/sys_role/updateRole',
    method: 'post',
    data
  })
}

// 删除角色
export function deleteRole(data) {
  return request({
    url: '/sys_role/deleteRole',
    method: 'post',
    data
  })
}

// 获取角色详细信息（包含菜单和部门）
export function getRoleVo(roleId) {
  return request({
    url: '/sys_role/getRoleVo',
    method: 'post',
    data: { roleId }
  })
}
