import request from '@/utils/http'
import { WebsiteResult } from '@/types/website/website'


// 关于
class AboutService {
  // 关于
  static getAbout() {
    return request.get<WebsiteResult>({
      url: '/admin/settings/getBlogConfig'
    })
  }

  // 修改关于
  static updateAbout(data: any) {
    return request.post({
      url: '/admin/settings/updateWebsiteInfo',
      data
    })
  }
}

export default AboutService
