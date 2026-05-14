import request from "@/utils/http/index";
import {type CommentPage} from "@/types/comment"

export class CommentSerivce {
  static addComment(data?: object) {
    return request.post({
      url: '/front/comments/addComment',
      data
    })
  }

  static frontGetCommentTotal(id: number | string) {
    return request.get({
      url: `/front/comments/getCommentTotal/${id}`
    })
  }

  static frontGetComment(data?: object) {
    return request.post<CommentPage>({
      url: '/front/comments/getCommentPage',
      data
    })
  }

  // TODO: 后端暂未实现申请评论接口，待实现后取消注释
  // static applyComment(data?: object) {
  //   return request.post({
  //     url: '/front/comments/applyComment',
  //     data
  //   })
  // }

  static deleteComment(id?: number | string) {
    return request.get({
      url: `/front/comments/deleteCommentById/${id}`
    })
  }
}

