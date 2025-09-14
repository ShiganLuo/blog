import { fullRequest, request } from "./http/index";

export const getFriendLinks = (data?: object) => {
  return fullRequest({
    method:  'post',
    url: '/api/front/settings/getAllFriendLink',
    data: data
  })
}

export const addFriendLinks = (data?: object) => {
  return fullRequest({
    method:  'post',
    url: '/api/front/settings/addFriendLink',
    data: data
  })
}

export const updateFriendLinks = (data?: object) => {
  return fullRequest({
    method:  'post',
    url: '/api/front/settings/updateFriendLink',
    data: data
  })
}