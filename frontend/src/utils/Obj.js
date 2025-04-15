/**
 *
 * @returns 判断手机类型
 */
function isMobile() {
    return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)
}

/**
 *
 * @returns 获取localstorge 中 conversations数组 聊天数据
 */
function getLocalStorage(name) {
    return JSON.parse(localStorage.getItem(name))
}
function setLocalStorage(name, obj) {
  return localStorage.setItem(name, JSON.stringify(obj))
}
function delLocalStorage(name) {
  return localStorage.removeItem(name)
}
/**
 *
 * @returns 获取localstorge 中 conversations数组 聊天数据
 */
function getConversations(name) {
  return localStorage.getItem(name)
}

/**
 *
 * @returns 随机生成id
 */
function generateRandomString(length) {
    var chars = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    var result = '';
    for (var i = length; i > 0; --i) {
      result += chars[Math.floor(Math.random() * chars.length)];
    }
    return result;
  }

export {
    isMobile,
    getLocalStorage,
    setLocalStorage,
    delLocalStorage,
    generateRandomString,
    getConversations
}
