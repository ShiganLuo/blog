import request from "@/utils/http/index";
import {type CommentPage} from "@/types/comment"
// import { }
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

  static applyComment(data?: object) {
    return request.post({
      url: '',
      data
    })
  }

  static deleteComment(id?: number | string) {
    return request.get({
      url: `/front/comments/deleteCommentById/${id}`
    })
  }
}

