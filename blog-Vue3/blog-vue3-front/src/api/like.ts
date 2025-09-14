import { fullRequest, request } from "./http/index";

export const addLike = (data?: object) => {
  return fullRequest({
    method:  'post',
    url: '/api/front/likes/addLike',
    data: data
  })
}

export const cancelLike = (data?: object) => {
  return fullRequest({
    method:  'post',
    url: '/api/front/likes/deleteLike',
    data: data
  })
}

export const getIsLikeByIdOrIpAndType = (data?: object) => {
  return fullRequest({
    method:  'post',
    url: '/api/front/likes/getIsLike',
    data: data
  })
}

export const getLikesById = (id?: string | number) => {
  return fullRequest({
    method:  'get',
    url: `/api/front/articles/getLikesById/${id}`,
  })
}
