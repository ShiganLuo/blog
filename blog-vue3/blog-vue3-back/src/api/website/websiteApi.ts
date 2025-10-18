import request from '@/utils/http'
import { WebsiteResult } from '@/types/website/website'
import { CodeMsgResult } from '@/types/axios'

// 网站
class WebsiteService {
  // 获取网站信息
  static getWebsiteInfo() {
    return request.get<WebsiteResult>({
      url: '/blog/website/admin/website/config'
    })
  }

  // 更新网站信息
  static updateWebsiteInfo(data: any) {
    return request.put({
      url: '/blog/website/admin/website/config',
      data
    })
  }
}

export default WebsiteService
