import axios from "axios";
import { ElMessage } from "element-plus";
import router from "@/router/index.ts";

const request = axios.create({
    baseURL: import.meta.env.VITE_APP_BASE_API || 'http://localhost:9090',
    timeout: 30000,  // 后台接口超时时间
    withCredentials: true  // 允许跨域请求携带凭证
})

// 添加调试日志
console.log('API Base URL:', import.meta.env.VITE_APP_BASE_API || 'http://localhost:9090');

// request 拦截器
// 可以自请求发送前对请求做一些处理
request.interceptors.request.use(config => {
    // 检查是否是FormData类型，如果是则不设置Content-Type
    if (!(config.data instanceof FormData)) {
        if (!config.headers['Content-Type']) {
            config.headers['Content-Type'] = 'application/json;charset=utf-8';
        }
    } else {
        // 对于FormData，删除Content-Type让浏览器自动设置
        delete config.headers['Content-Type'];
    }
    
    const user = JSON.parse(localStorage.getItem('xm-user') || '{}');
    if (user.token) {
        config.headers['token'] = user.token;
        console.log('Request with token:', user.token);
    }
    
    return config;
}, error => {
    return Promise.reject(error);
});

// response 拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
    async response => {
        // 检查响应状态
        if (response.status === 200) {
            let res = response.data;
            // 如果是返回的文件
            if (response.config.responseType === 'blob') {
                return res
            }
            // 当权限验证不通过的时候给出提示
            if (res.code === '401') {
                // 尝试刷新 token
                try {
                    const refreshRes = await axios.post(`${import.meta.env.VITE_APP_BASE_API}/refreshToken`, {
                        refreshToken: JSON.parse(localStorage.getItem("xm-user") || '{}').refreshToken
                    });
                    
                    if (refreshRes.data.code === '200') {
                        // 更新本地存储的 token
                        const userData = JSON.parse(localStorage.getItem("xm-user") || '{}');
                        userData.token = refreshRes.data.data.token;
                        localStorage.setItem('xm-user', JSON.stringify(userData));
                        
                        // 重新发送之前失败的请求
                        const config = response.config;
                        config.headers['token'] = refreshRes.data.data.token;
                        config.headers['Authorization'] = 'Bearer ' + refreshRes.data.data.token;
                        
                        return request(config);
                    } else {
                        // 刷新 token 失败，需要重新登录
                        ElMessage.error('登录状态已过期，请重新登录')
                        localStorage.removeItem('xm-user');
                        router.push('/login')
                    }
                } catch (error) {
                    console.error('刷新token失败:', error);
                    ElMessage.error('登录状态已过期，请重新登录')
                    localStorage.removeItem('xm-user');
                    router.push('/login')
                }
            }
            // 兼容服务端返回的字符串数据
            if (typeof res === 'string') {
                res = res ? JSON.parse(res) : res
            }
            return res;
        } else {
            console.error('请求错误:', response);
            throw new Error('请求失败，状态码:' + response.status);
        }
    },
    error => {
        // 处理错误
        console.error('请求失败:', error);
        if (error.response) {
            console.log('错误响应状态:', error.response.status);
            console.log('错误响应数据:', error.response.data);
            
            if (error.response.status === 401) {
                ElMessage.error('登录状态已过期，请重新登录')
                // 清除本地存储的用户信息
                localStorage.removeItem('xm-user');
                // 重定向到登录页
                router.push('/login')
            } else if (error.response.status === 404) {
                ElMessage.error('未找到请求接口')
            } else if (error.response.status === 500) {
                ElMessage.error('系统异常，请查看后端控制台报错')
            } else {
                ElMessage.error(error.message || '请求失败')
            }
        } else {
            ElMessage.error('网络连接异常，请检查您的网络')
        }
        return Promise.reject(error)
    }
)

// 封装 get 方法
export function get(url, params) {
    return request.get(url, {
        params,
    });
}

// 封装 post 方法
export function post(url, data, config) {
    return request.post(url, data, config);
}

export default request
