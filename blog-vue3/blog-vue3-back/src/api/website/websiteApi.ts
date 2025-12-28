import request from '@/utils/http'
import { WebsiteResult } from '@/types/website/website'

// 网站
class WebsiteService {
  // 获取网站信息
  static getWebsiteInfo() {
    return request.get<WebsiteResult>({
      url: '/admin/settings/getBlogConfig'
    })
  }

  // 更新网站信息
  static updateWebsiteInfo(data: any) {
    return request.post({
      url: '/admin/settings/updateWebsiteInfo',
      data
    })
  }
}

export default WebsiteService
