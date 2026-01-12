import request from '@/utils/http'
import { CommentRecordResult } from '@/types/message/comment'
import { CodeMsgResult } from '@/types/axios'

// 评论
export class CommentService {
  // 评论列表
  static getCommentList(data: any) {
    return request.post<CommentRecordResult>({
      url: '/admin/comments/getAdminCommentPage',
      data: data
    })
  }

  // 删除评论
  static deleteComment(data: Array<number>) {
    return request.del({
      url: `/blog/comment/admin/comments`,
      headers: {
        'Content-Type': 'application/json'
      },
      data
    })
  }

  // 评论审核
  static passComment(data: any) {
    return request.post({
      url: `/admin/comments/updateCommentsStatusByIds`,
      data
    })
  }
}
