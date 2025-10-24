import request from '@/utils/request'

// 获取所有岗位
export function getAllPost() {
  return request({
    url: '/sys_post/getAllPost',
    method: 'get'
  })
}

// 获取岗位列表（带参数查询）
export function getPosts(data) {
  return request({
    url: '/sys_post/getPosts',
    method: 'post',
    data
  })
}

// 新增岗位
export function addPost(data) {
  return request({
    url: '/sys_post/addPost',
    method: 'post',
    data
  })
}

// 更新岗位
export function updatePost(data) {
  return request({
    url: '/sys_post/updatePost',
    method: 'post',
    data
  })
}

// 删除岗位
export function deletePost(data) {
  return request({
    url: '/sys_post/deletePost',
    method: 'post',
    data
  })
}
