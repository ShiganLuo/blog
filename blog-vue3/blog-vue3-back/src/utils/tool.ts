import { Base64 } from "js-base64";
import { config } from "@/config/config"; // 确保你有 config，且里面有 ENCRYPTION 字段
import { ElMessageBox, ElMessage } from 'element-plus'
import type { ElMessageBoxOptions, Action } from 'element-plus'
import type { MessageParams, MessageHandler, MessageOptions } from 'element-plus'

// 读取localStorage并解密
export const _getLocalItem = (key: string | null | undefined): any => {
  try {
    if (!key) {
      return "";
    }

    let storageKey = config.ENCRYPTION ? Base64.encode(key) : key;
    let value = localStorage.getItem(storageKey);

    if (value === null || value === undefined || value === "") {
      return "";
    } else {
      const decodedValue = config.ENCRYPTION ? Base64.decode(value) : value;
      return JSON.parse(decodedValue);
    }
  } catch (err) {
    console.error(err);
    return ""; // 捕获异常时也返回空字符串，防止外面调用出错
  }
};

// 将数据存储到localStorage中，并在存储前对数据进行加密处理
export const _setLocalItem = (key: string, value: any): void => {
  try {
    if (key === "" || key === undefined) {
      return;
    }
    if (key) {
      if (value == 0) {
        value = JSON.stringify(value);
        localStorage.setItem(config.ENCRYPTION ? Base64.encode(key) : key, value);
        return;
      }
      if (value === null || value === undefined || value === "") {
        return;
      }
      // 编码
      let enObj = JSON.stringify(value);
      if (config.ENCRYPTION) {
        localStorage.setItem(Base64.encode(key), Base64.encode(enObj));
      } else {
        localStorage.setItem(key, enObj);
      }
    }
  } catch (err) {
    console.error(err);
  }
};


export const _removeLocalItem = function (key: string | null | undefined): void {
  if (key === null || key === '' || key === undefined) {
    return;
  }

  const enObj = config.ENCRYPTION ? Base64.encode(key) : key;
  localStorage.removeItem(enObj);
};


/**
 * 显示一个确认弹窗
 * @param content 弹窗内容
 * @param title 弹窗标题
 * @param type 弹窗类型，例如 'success', 'warning', 'info', 'error'
 * @returns Promise<Action>
 */
export function showModel(
  content: string = '提示内容',
  title: string = '提示',
  type: ElMessageBoxOptions['type'] = 'warning'
): Promise<Action> {
  return ElMessageBox.confirm(content, title, {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type,
  })
}

/**
 * 显示一个消息提示
 * @param message 消息内容
 * @param type 消息类型，例如 'success', 'warning', 'info', 'error'
 * @param customClass 自定义类名
 * @returns MessageHandler
 */
export function showMessage(
  message: MessageOptions['message'] = '提示内容',
  type: MessageOptions['type'] = 'success',
  customClass: string = ''
): MessageHandler {
  const options: MessageOptions = {
    message,
    type,
    customClass,
  }
  return ElMessage(options)
}