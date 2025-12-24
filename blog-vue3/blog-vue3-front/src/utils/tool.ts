// utils/geolocation.ts
import axios from 'axios'
import { Base64 } from "js-base64";
import { config } from "@/config/config"; // 确保你有 config，且里面有 ENCRYPTION 字段

export async function getVisitorLocation() {
  try {
    const response = await axios.get('https://ipapi.co/json/')
    return {
      ip: response.data.ip,
      city: response.data.city,
      country: response.data.country_name,
      latitude: response.data.latitude,
      longitude: response.data.longitude
    }
  } catch (error) {
    console.error('Failed to fetch location:', error)
    return null
  }
}
/**
 * 将度数转换为弧度
 * @param degrees 角度值
 * @returns 弧度值
 */
function rad(degrees: number): number {
    return degrees * Math.PI / 180.0;
}

/**
 * 计算两个经纬度坐标之间的距离（Haversine公式）
 * @param lat1 第一个点的纬度
 * @param lon1 第一个点的经度
 * @param lat2 第二个点的纬度
 * @param lon2 第二个点的经度
 * @returns 两点之间的距离（单位：千米）
 */
export function calculateDistance(
    lat1: number,
    lon1: number,
    lat2: number,
    lon2: number
  ) {
    const R = 6378; // 地球半径，单位：千米
    const radLat1 = rad(lat1);
    const radLat2 = rad(lat2);
    const a = radLat1 - radLat2;
    const b = rad(lon1) - rad(lon2);

    const sinHalfA = Math.sin(a / 2);
    const sinHalfB = Math.sin(b / 2);
    const cosRadLat1 = Math.cos(radLat1);
    const cosRadLat2 = Math.cos(radLat2);

    const s = 2 * Math.asin(
      Math.sqrt(
        sinHalfA * sinHalfA +
        cosRadLat1 * cosRadLat2 * sinHalfB * sinHalfB
      )
    );

    return R * s
}

export function isMobile() {
  // 手机端
  let reg =
    /(phone|pad|iPhone|iPod|ios|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i;
  return reg.test(navigator.userAgent);
}

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

// 给一些数字转成k w
export function numberFormate(number: number | string | undefined): string {
  if (!number) return '0'; // 返回 '0' 字符串，而不是数字

  number = typeof number === "number" ? number : parseFloat(number as string);

  let res: string;
  if (number >= 10000) {
    res = (number / 10000).toFixed(1) + "w";
  } else if (number >= 1000) {
    res = (number / 1000).toFixed(1) + "k";
  } else {
    res = number.toString();
  }
  return res;
}

export function debounce(fn: (...args: any[]) => void, delay: number) {
  let timer: ReturnType<typeof setTimeout> | null = null;

  return function (this: unknown, ...args: any[]) {  // 使用 `this: unknown` 来显式声明 `this` 类型
    if (timer) {
      clearTimeout(timer);
    }
    timer = setTimeout(() => {
      fn.apply(this, args);  // `this` 已被明确为 `unknown`
    }, delay);
  };
}

export function randomFontColor() {
  return `rgb(${Math.round(Math.random() * 255)},${Math.round(Math.random() * 255)},${Math.round(
    Math.random() * 255
  )})`;
}

export function containHTML(text: string): boolean {
  const reg: RegExp = /<[^>]+>/g;
  const badJs: RegExp = /script|alert|window|prompt|location|href|iframe|onload|onerror/g;

  return reg.test(text) && !badJs.test(text);
}

export const _removeLocalItem = function (key: string | null | undefined): void {
  if (key === null || key === '' || key === undefined) {
    return;
  }

  const enObj = config.ENCRYPTION ? Base64.encode(key) : key;
  localStorage.removeItem(enObj);
};

/**
 * 根据时间欢迎
 * @param nickName 昵称（可选）
 * @returns 欢迎语句
 */
export function getWelcomeSay(nickName?: string): string {
  if (!nickName) {
    return "欢迎来到我的生活日志";
  }

  const now = new Date().getHours();

  if (now >= 0 && now < 5) {
    return `夜深了：${nickName}，请注意休息！`;
  } else if (now >= 5 && now < 9) {
    return `早上好：${nickName}，今天又是活力满满的一天！`;
  } else if (now >= 9 && now < 12) {
    return `上午好：${nickName}，请合理安排时间摸鱼！`;
  } else if (now >= 12 && now < 14) {
    return `中午好：${nickName}，现在正适合睡一觉`;
  } else if (now >= 14 && now < 18) {
    return `下午好：${nickName}`;
  } else if (now >= 18 && now < 20) {
    return `傍晚好：${nickName}，记得按时吃饭`;
  } else if (now >= 20 && now < 22) {
    return `晚上好: ${nickName}`;
  } else {
    return `晚上好: ${nickName}，记得按时碎觉哦`;
  }
}

// 保持光标在元素内容最后
export function keepLastIndex(dom: HTMLElement): void {
  let range;
  if (window.getSelection) {
    dom.focus(); // FF需先聚焦
    const selection = window.getSelection();
    if (!selection) return;

    range = document.createRange();
    range.selectNodeContents(dom);
    range.collapse(false); // 光标移至末尾

    selection.removeAllRanges();
    selection.addRange(range);
  } else if ((document as any).selection) {
    // IE <= 8
    range = (document as any).selection.createRange();
    range.moveToElementText(dom);
    range.collapse(false);
    range.select();
  }
}

// 获取当前光标位置
export function getCurrentIndex(): number {
  if (window.getSelection) {
    const selection = window.getSelection();
    return selection?.focusOffset ?? 0;
  } else if ((document as any).selection) {
    const selection = (document as any).selection.createRange();
    return selection?.focusOffset ?? 0;
  }
  return 0;
}

/**
 * 返回时间差（几秒/分钟/小时/天前）
 * @param time 时间字符串，例如 "2025-08-14T12:00:00.000000"
 * @returns 返回字符串，如 "5秒", "10分钟", "3小时", "2天" 或 undefined
 */
/**
 * 返回时间差（几秒/分钟/小时/天前）
 * @param time 时间字符串，例如 "2025-09-03T12:35:46"
 * @returns 返回字符串，如 "5秒", "10分钟", "3小时", "2天" 或 undefined
 */
export const returnTime = (time?: string): string | undefined => {
  if (!time) return;

  // 将 "T" 替换为空格，并确保连字符为斜杠，以兼容不同的浏览器和系统（包括iOS）
  const formattedTime = time.replace('T', ' ').replace(/-/g, "/");
  const times = new Date().getTime() - new Date(formattedTime).getTime();

  let res: string;

  // 1分钟（60秒）
  if (times < 60 * 1000) {
    const seconds = Math.max(0, Math.trunc(times / 1000));
    res = seconds + "秒";
  }
  // 1小时（3600秒）
  else if (times < 60 * 60 * 1000) {
    res = Math.trunc(times / (60 * 1000)) + "分钟";
  }
  // 1天（86400秒）
  else if (times < 24 * 60 * 60 * 1000) {
    res = Math.trunc(times / (60 * 60 * 1000)) + "小时";
  }
  // 超过1天
  else {
    res = Math.trunc(times / (24 * 60 * 60 * 1000)) + "天";
  }

  return res;
};

export function filterMessage(text: string): string {
  return text.replace(/&gt;/g, ">").replace(/&lt;/g, "<");
}

/**
 * 参数处理
 * @param {*} params  参数
 */
export function tansParams(params: any) {
  let result = ''
  for (const propName of Object.keys(params)) {
    const value = params[propName]
    const part = encodeURIComponent(propName) + '='
    if (value !== null && value !== '' && typeof value !== 'undefined') {
      if (typeof value === 'object') {
        for (const key of Object.keys(value)) {
          if (value[key] !== null && value[key] !== '' && typeof value[key] !== 'undefined') {
            const params = propName + '[' + key + ']'
            const subPart = encodeURIComponent(params) + '='
            result += subPart + encodeURIComponent(value[key]) + '&'
          }
        }
      } else {
        result += part + encodeURIComponent(value) + '&'
      }
    }
  }
  return result
}
