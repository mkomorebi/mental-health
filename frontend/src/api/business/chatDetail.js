import request from '@/utils/request'

// 查询对话明细列表
export function listChatDetail(query) {
  return request({
    url: '/business/chatDetail/list',
    method: 'get',
    params: query
  })
}

// 查询对话明细详细
export function getChatDetail(id) {
  return request({
    url: '/business/chatDetail/' + id,
    method: 'get'
  })
}

// 新增对话明细
export function addChatDetail(data) {
  return request({
    url: '/business/chatDetail',
    method: 'post',
    data: data
  })
}

// 修改对话明细
export function updateChatDetail(data) {
  return request({
    url: '/business/chatDetail',
    method: 'put',
    data: data
  })
}

// 删除对话明细
export function delChatDetail(id) {
  return request({
    url: '/business/chatDetail/' + id,
    method: 'delete'
  })
}

// 查询对话明细
export function listByChatId(chatId) {
  return request({
    url: '/business/chatDetail/listByRecordId/' + chatId,
    method: 'get'
  })
}
