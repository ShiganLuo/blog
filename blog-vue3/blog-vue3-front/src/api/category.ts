import { fullRequest, request } from "./http/index";

export const getAllCategory = (data?: object) => {
  return fullRequest({
    method:  'get',
    url: '/api/front/categories/getAllcategories',
    data: data
  })
}