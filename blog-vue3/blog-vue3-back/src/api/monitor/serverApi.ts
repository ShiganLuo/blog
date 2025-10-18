import request from '@/utils/http'
import { ServerResult } from '@/types/monitor/server'

// 服务监控
export class ServerService {
  // 获取服务信息
  static getServer() {
    return request.get<ServerResult>({
      url: '/monitor/server'
    })
  }
}
