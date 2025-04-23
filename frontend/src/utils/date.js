/**
 * 日期格式化工具函数
 */

/**
 * 将日期格式化为指定格式的字符串
 * @param {Date|string|number} date 日期对象、日期字符串或时间戳
 * @param {string} [format='YYYY-MM-DD'] 格式字符串
 * @returns {string} 格式化后的日期字符串
 */
export function formatDateWithPattern(date, format = 'YYYY-MM-DD') {
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

/**
 * 统一的日期格式化函数
 * @param {string|Date} dateInput - 日期字符串或Date对象
 * @param {string} format - 格式化模式，可选值: 'datetime'(默认), 'date', 'time'
 * @returns {string} 格式化后的日期字符串
 */
export const formatDate = (dateInput, format = 'datetime') => {
  if (!dateInput) return '';
  
  try {
    let date;
    if (dateInput instanceof Date) {
      date = dateInput;
    } else {
      date = new Date(dateInput);
    }
    
    if (isNaN(date.getTime())) {
      console.warn('无效的日期输入:', dateInput);
      return '无效日期';
    }
    
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    
    switch (format) {
      case 'date':
        return `${year}-${month}-${day}`;
      case 'time':
        return `${hours}:${minutes}:${seconds}`;
      case 'datetime':
      default:
        return `${year}-${month}-${day} ${hours}:${minutes}`;
    }
  } catch (error) {
    console.error('日期格式化错误:', error);
    return String(dateInput);
  }
};

/**
 * 解析日期字符串为Date对象
 * @param {string} dateStr - 日期字符串
 * @returns {Date|null} 解析后的Date对象，解析失败则返回null
 */
export const parseDate = (dateStr) => {
  if (!dateStr) return null;
  
  try {
    const date = new Date(dateStr);
    return isNaN(date.getTime()) ? null : date;
  } catch (error) {
    console.error('日期解析错误:', error);
    return null;
  }
};

/**
 * 获取两个日期之间的天数差
 * @param {Date|string} date1 - 第一个日期
 * @param {Date|string} date2 - 第二个日期
 * @returns {number} 天数差
 */
export const daysBetween = (date1, date2) => {
  const d1 = date1 instanceof Date ? date1 : new Date(date1);
  const d2 = date2 instanceof Date ? date2 : new Date(date2);
  
  // 确保日期有效
  if (isNaN(d1.getTime()) || isNaN(d2.getTime())) {
    console.error('无效的日期输入');
    return 0;
  }
  
  // 清除时间部分，只保留日期
  const utc1 = Date.UTC(d1.getFullYear(), d1.getMonth(), d1.getDate());
  const utc2 = Date.UTC(d2.getFullYear(), d2.getMonth(), d2.getDate());
  
  // 计算天数差
  const MS_PER_DAY = 1000 * 60 * 60 * 24;
  return Math.floor((utc2 - utc1) / MS_PER_DAY);
};

/**
 * 检查日期是否在指定范围内
 * @param {Date|string} date - 要检查的日期
 * @param {Date|string} startDate - 开始日期
 * @param {Date|string} endDate - 结束日期
 * @returns {boolean} 是否在范围内
 */
export const isDateInRange = (date, startDate, endDate) => {
  const d = date instanceof Date ? date : new Date(date);
  const start = startDate instanceof Date ? startDate : new Date(startDate);
  const end = endDate instanceof Date ? endDate : new Date(endDate);
  
  // 确保日期有效
  if (isNaN(d.getTime()) || isNaN(start.getTime()) || isNaN(end.getTime())) {
    return false;
  }
  
  return d >= start && d <= end;
};

export default {
  formatDate,
  formatDateWithPattern,
  getRelativeTime
}; 