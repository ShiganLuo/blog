import { CacheModel } from 'billd-utils';
import { Base64 } from "js-base64";
// 读取localStorage并解密
export const config = {
    ENCRYPTION: true,
    prefix: "blog-vu3-back"
};
export const _getLocalItem = (key: string | null | undefined): any => {
  try {
    if (!key) {
      return "";
    }
    key = config.prefix + key;
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
        key = config.prefix + key;
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
  key = config.prefix + key;
  const enObj = config.ENCRYPTION ? Base64.encode(key) : key;
  localStorage.removeItem(enObj);
};
export default new CacheModel('vue3_blog_admin___');