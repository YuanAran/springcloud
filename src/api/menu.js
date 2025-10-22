import request from '@/utils/request'

export function getMenuList(params) {
  return request({
    url: '/menu/list',
    method: 'get',
    params
  })
}

export function createMenu(data) {
  return request({
    url: '/menu/create',
    method: 'post',
    data
  })
}

export function updateMenu(data) {
  return request({
    url: '/menu/update',
    method: 'post',
    data
  })
}

export function deleteMenu(id) {
  return request({
    url: `/menu/delete/${id}`,
    method: 'post'
  })
}

export function getAllMenu() {
  return request({
    url: '/sys_menu/getAllMenu',
    method: 'get'
  })
}
