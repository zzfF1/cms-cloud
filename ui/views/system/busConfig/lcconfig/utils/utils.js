// utils.js
import { ElMessageBox } from 'element-plus';

/**
 * 深拷贝对象
 * @param {Object} obj 需要拷贝的对象
 * @returns {Object} 拷贝后的新对象
 */
export const deepClone = (obj) => {
  return JSON.parse(JSON.stringify(obj));
};

/**
 * 确认对话框
 * @param {string} message 提示信息
 * @param {string} title 标题
 * @param {object} options 选项
 * @returns {Promise} 确认结果
 */
export const confirmDialog = (message, title = '提示', options = {}) => {
  return ElMessageBox.confirm(message, title, {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
    ...options
  });
};

/**
 * 处理有未保存更改的情况
 * @param {boolean} hasChanges 是否有未保存的更改
 * @param {Function} callback 确认后的回调函数
 */
export const confirmIfHasChanges = async (hasChanges, callback) => {
  if (hasChanges) {
    try {
      await confirmDialog('有未保存的修改，是否继续？');
      callback();
    } catch (error) {
      // 用户取消操作
    }
  } else {
    callback();
  }
};
