import { fullRequest, request } from "./http/index";

export const updatePassword = (data?: object) => {
  return fullRequest({
    method:  'post',
    url: '/api/front/likes/addLike',
    data: data
  })
}