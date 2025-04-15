import request from '@/utils/request'

// 新增会话
export function addChatLog(data) {
    return request({
        url: '/chatLog/add',
        method: 'post',
        data: data
    })
}

// 更新会话
export function updateChatLog(data) {
    return request({
        url: '/chatLog/update',
        method: 'put',
        data: data
    })
}

// 删除单个会话
export function deleteChatLog(id) {
    return request({
        url: `/chatLog/delete/${id}`,
        method: 'delete'
    })
}

// 批量删除会话
export function deleteBatchChatLogs(ids) {
    return request({
        url: '/chatLog/delete/batch',
        method: 'delete',
        data: ids
    })
}

// 查询单个会话
export function getChatLogById(id) {
    return request({
        url: `/chatLog/selectById/${id}`,
        method: 'get'
    })
}

// 查询所有会话
export function getAllChatLogs(params) {
    return request({
        url: '/chatLog/selectAll',
        method: 'get',
        params
    })
}

// 分页查询会话
export function getChatLogsByPage(pageNum = 1, pageSize = 10) {
    return request({
        url: '/chatLog/selectPage',
        method: 'get',
        params: { pageNum, pageSize }
    })
}
