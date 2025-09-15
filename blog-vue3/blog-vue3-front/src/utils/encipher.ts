import CryptoJS from "crypto-js";

// 十六位十六进制数作为密钥
const SECRET_KEY: CryptoJS.lib.WordArray = CryptoJS.enc.Utf8.parse("1234567812345678");
// 十六位十六进制数作为密钥偏移量
const SECRET_IV: CryptoJS.lib.WordArray = CryptoJS.enc.Utf8.parse("1234567812345678");

/**
 * 加密方法
 * @param data - 需要加密的数据（对象或字符串）
 * @returns 加密后的十六进制字符串
 */
export function _encrypt(data: object | string): string {
  let dataStr: string;
  
  if (typeof data === "object") {
    try {
      dataStr = JSON.stringify(data);
    } catch (error) {
      console.error("Encrypt error:", error);
      throw new Error("Failed to stringify data object");
    }
  } else {
    dataStr = data;
  }

  const dataHex: CryptoJS.lib.WordArray = CryptoJS.enc.Utf8.parse(dataStr);
  const encrypted: CryptoJS.lib.CipherParams = CryptoJS.AES.encrypt(dataHex, SECRET_KEY, {
    iv: SECRET_IV,
    mode: CryptoJS.mode.CBC,
    padding: CryptoJS.pad.Pkcs7,
  });

  return encrypted.ciphertext.toString();
}

/**
 * 解密方法
 * @param data - 需要解密的十六进制字符串
 * @returns 解密后的原始字符串
 * @throws 当解密失败时抛出错误
 */
export function _decrypt(data: string): string {
  try {
    const encryptedHexStr: CryptoJS.lib.WordArray = CryptoJS.enc.Hex.parse(data);
    const str: string = CryptoJS.enc.Base64.stringify(encryptedHexStr);
    const decrypt: CryptoJS.lib.WordArray = CryptoJS.AES.decrypt(str, SECRET_KEY, {
      iv: SECRET_IV,
      mode: CryptoJS.mode.CBC,
      padding: CryptoJS.pad.Pkcs7,
    });
    const decryptedStr: string = decrypt.toString(CryptoJS.enc.Utf8);
    
    if (!decryptedStr) {
      throw new Error("Decryption failed: empty result");
    }
    
    return decryptedStr;
  } catch (error) {
    console.error("Decrypt error:", error);
    throw new Error("Failed to decrypt data");
  }
}

// 类型扩展声明（可选）
declare module "crypto-js" {
  interface WordArray {
    toString(encoder?: typeof CryptoJS.enc.Utf8): string;
  }
}