import request from '@/utils/request'

// 创建对话
export function newChat() {
    return request({
        url: '/business/ai_doctor/chat/new',
        method: 'post'
    })
}

// 对话
export function chat(data) {
    return request({
        url: '/business/chat/aiChat',
        method: 'post',
        data: data
    })
}

export function sendChatMessage(data) {
    return request({
        url: '/ai/chat',
        method: 'post',
        data
    })
}
