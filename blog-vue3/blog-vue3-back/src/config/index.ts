import { createBaseConfig } from './core/base-config'
import { envConfig } from './env/env-config'
import { SystemConfig } from './types'

// 系统配置
const config: SystemConfig = {
  ...createBaseConfig(),
  ...envConfig
}

// 冻结对象防止运行时修改
export default Object.freeze(config)

// 读取localStorage并解密
export const localStorageConfig = {
    ENCRYPTION: true,
    prefix: "blog-vu3-back"
};