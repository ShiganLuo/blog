import request from "@/utils/http/index";
import { type MessageItemListResponse } from "@/types/message";
export class MessageService {
  static addMessage(data?: object) {
    return request.post({
      url: '/api/front/comments/addComment',
      data: data
    })
  }

  static updateMessage(data?: object) {
    return request.post({
      url: '/api/front/articles/getTimeLineArticle',
      data: data
    })
  }

  static getMessageTag(data?: object) {
    return request.post({
      url: '/api/front/articles/getTimeLineArticle',
      data: data
    })
  }

  static getMessageList(data?: object) {
    return request.post<MessageItemListResponse>({
      url: '/api/front/comments/getMessageTalkPage',
      data: data     
    })
  }

  static deleteMessage(data?: object) {
    return request.post({
      url: '/api/front/articles/getTimeLineArticle',
      data: data
    })
  }

  static getAllMessage(data?: object) {
    return request.post({
      url: '/api/front/comments/getMessagePage',
      data: data     
    })
  }
}

