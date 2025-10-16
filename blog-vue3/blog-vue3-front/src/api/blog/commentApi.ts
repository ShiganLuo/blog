import request from "@/utils/http/index";

export class CommentSerivce {
  static addComment(data?: object) {
    return request.post({
      url: '/api/front/comments/addComment',
      data
    })
  }

  static frontGetCommentTotal(id: number | string) {
    return request.get({
      url: "/api/front/comments/getCommentTotal",
      params: id
    })
  }

  static frontGetComment(data?: object) {
    return request.post({
      url: '/api/front/comments/getCommentPage',
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
      url: "/api/front/comments/deleteCommentById",
      params:id
    })
  }
}

