import request from "@/utils/http/index";

export class LikeService {
  static addLike(data?: object) {
    return request.post({
      url: '/api/front/likes/addLike',
      data
    })
  }

  static cancelLike(data?: object) {
    return request.post({
      url: '/api/front/likes/deleteLike',
      data
    })
  }

  static getIsLikeByIdOrIpAndType(data?: object) {
    return request.post<boolean>({
      url: '/api/front/likes/getIsLike',
      data
    })
  }

  static getLikesById(id?: string | number) {
    return request.get({
      url: `/api/front/articles/getLikesById/${id}`
    })
  }
}
