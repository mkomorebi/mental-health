/**
 * 日期格式化工具函数
 */

/**
 * 将日期格式化为指定格式的字符串
 * @param {Date|string|number} date 日期对象、日期字符串或时间戳
 * @param {string} [format='YYYY-MM-DD'] 格式字符串
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date, format = 'YYYY-MM-DD') {
  if (!date) return '';
  
  // 如果是字符串或数字，转换为Date对象
  if (typeof date === 'string' || typeof date === 'number') {
    date = new Date(date);
  }
  
  // 如果转换失败或不是有效日期
  if (!(date instanceof Date) || isNaN(date.getTime())) {
    return '';
  }

  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const hours = date.getHours();
  const minutes = date.getMinutes();
  const seconds = date.getSeconds();

  // 补零函数
  const padZero = (num) => (num < 10 ? '0' + num : num);

  return format
    .replace(/YYYY/g, year)
    .replace(/MM/g, padZero(month))
    .replace(/DD/g, padZero(day))
    .replace(/HH/g, padZero(hours))
    .replace(/mm/g, padZero(minutes))
    .replace(/ss/g, padZero(seconds));
}

/**
 * 获取相对时间描述（如：刚刚、5分钟前、1小时前等）
 * @param {Date|string|number} date 日期对象、日期字符串或时间戳
 * @returns {string} 相对时间描述
 */
export function getRelativeTime(date) {
  if (!date) return '';
  
  // 转换为Date对象
  if (typeof date === 'string' || typeof date === 'number') {
    date = new Date(date);
  }
  
  // 如果转换失败或不是有效日期
  if (!(date instanceof Date) || isNaN(date.getTime())) {
    return '';
  }

  const now = new Date();
  const diff = now.getTime() - date.getTime();
  
  // 时间差（毫秒）
  const minute = 60 * 1000;
  const hour = 60 * minute;
  const day = 24 * hour;
  const week = 7 * day;
  const month = 30 * day;
  const year = 365 * day;
  
  if (diff < minute) {
    return '刚刚';
  } else if (diff < hour) {
    return Math.floor(diff / minute) + '分钟前';
  } else if (diff < day) {
    return Math.floor(diff / hour) + '小时前';
  } else if (diff < week) {
    return Math.floor(diff / day) + '天前';
  } else if (diff < month) {
    return Math.floor(diff / week) + '周前';
  } else if (diff < year) {
    return Math.floor(diff / month) + '个月前';
  } else {
    return Math.floor(diff / year) + '年前';
  }
}

export default {
  formatDate,
  getRelativeTime
}; 