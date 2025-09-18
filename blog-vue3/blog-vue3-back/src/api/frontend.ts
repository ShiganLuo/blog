import { IFrontend } from '@/interface';
import { request } from '@/utils/request';

export function fetchFrontendList(params){
  return request<IFrontend>({
    method:"get",
    url:"/frontend/list",
    params
  });
}

export function fetchFindFrontend(id: number){
  return request<IFrontend>({
    method:"get",
    url: `/frontend/find/${id}`
  });
}

export function fetchCreateFrontend(data: IFrontend) {
  return request({
    url: `/frontend/create`,
    method: 'post',
    data,
  });
}

export function fetchUpdateFrontend(data: IFrontend) {
  return request({
    url: `/frontend/update/${data.id}`,
    method: 'put',
    data,
  });
}

export function fetchDeleteFrontend(id: number) {
  return request({
    url: `/frontend/delete/${id}`,
    method: 'delete',
  });
}
