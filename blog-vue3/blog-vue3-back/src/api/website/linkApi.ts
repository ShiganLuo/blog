import request from '@/utils/http'
import { FriendLinkRecordResult } from '@/types/website/link'

// 友链管理
export class FriendLinkService {
  // 查询友链管理列表
  static listLink(query: any) {
    return request.get<FriendLinkRecordResult>({
      url: '/admin/link/getAllFriendLink',
      params: query
    })
  }

  // 新增或修改友链管理
  static addOrUpdateLink(data: any) {
    return request.post({
      url: '/admin/link/addOrUpdateFriendLink',
      data: data
    })
  }

  // 删除友链管理
  static deleteLink(id: any) {
    return request.del({
      url: '/admin/link/del',
      headers: {
        'Content-Type': 'application/json'
      },
      data: id
    })
  }
}
