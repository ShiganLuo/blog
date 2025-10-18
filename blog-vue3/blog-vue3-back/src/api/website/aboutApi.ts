import request from '@/utils/http'
import { CodeMsgResult, BaseObjectResult } from '@/types/axios'

export interface About{
  content: string
}

// 关于
class AboutService {
  // 关于
  static getAbout() {
    return request.get<About>({
      url: '/blog/website/about'
    })
  }

  // 修改关于
  static updateAbout(data: any) {
    return request.put({
      url: '/blog/website/admin/about',
      data
    })
  }
}

export default AboutService
