import { fullRequest, request } from "./http/index";

export const addMessage = (data?: object) => {
  return fullRequest({
    method:  'post',
    url: '/api/front/comments/addComment',
    data: data
  })
}

export const updateMessage = (data?: object) => {
  return fullRequest({
    method:  'post',
    url: '/api/front/articles/getTimeLineArticle',
    data: data
  })
}

export const getMessageTag = (data?: object) => {
  return fullRequest({
    method:  'post',
    url: '/api/front/articles/getTimeLineArticle',
    data: data
  })
}

export const getMessageList = (data?: object) => {
  return fullRequest({
    method:  'post',
    url: '/api/front/comments/getMessageTalkPage',
    data: data
  })
}

export const deleteMessage = (data?: object) => {
  return fullRequest({
    method:  'post',
    url: '/api/front/articles/getTimeLineArticle',
    data: data
  })
}

export const getAllMessage = (data?: object) => {
  return fullRequest({
    method:  'post',
    url: '/api/front/comments/getMessagePage',
    data: data
  })
}
