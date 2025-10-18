import request from '@/utils/http'
import { CommentRecordResult } from '@/types/message/comment'
import { CodeMsgResult } from '@/types/axios'

// 评论
export class CommentService {
  // 评论列表
  static getCommentList(params: any) {
    return request.get<CommentRecordResult>({
      url: '/blog/comment/admin/comments',
      params
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
    return request.put({
      url: `/blog/comment/admin/comments/review`,
      data
    })
  }
}
