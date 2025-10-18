import request from '@/utils/http'
import { TalkRecordResult, TalkResult } from '@/types/message/talk'

// 说说
class TalkService {
  // 获取说说列表
  static listBackTalks(params: any) {
    return request.get<TalkRecordResult>({
      url: '/blog/talk/admin/talks',
      params
    })
  }

  // 保存或修改说说
  static saveOrUpdateTalk(data: any) {
    return request.post({
      url: '/blog/talk/admin/talks',
      data
    })
  }

  // 删除说说
  static deleteTalks(talkIds: number[]) {
    return request.del({
      url: '/blog/talk/admin/talks',
      headers: {
        'Content-Type': 'application/json'
      },
      data: talkIds
    })
  }

  // 根据id获取后台说说
  static getBackTalkById(talkId: number) {
    return request.get<TalkResult>({
      url: `/blog/talk/admin/talks/${talkId}`
    })
  }
}

export default TalkService
