import { request } from '@/utils/request';


export function fetchBuryingPointList(params) {
  return request({
    method:"get",
    url:"/burying_point/list",
    params
  });
}
