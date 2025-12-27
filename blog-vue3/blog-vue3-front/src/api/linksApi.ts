import request from "@/utils/http/index";
import { type FriendLinkListResponse } from "@/types/link"
export class LinkService {
  static getFriendLinks(data?: object){
    return request.post<FriendLinkListResponse>({
      url: '/front/link/getAllFriendLink',
      data: data
    })
  }

  static addFriendLinks(data?: object) {
    return request.post({
      url: '/front/link/addFriendLink',
      data: data
    })
  }

  static updateFriendLinks(data?: object) {
    return request.post({
      url: '/front/link/updateFriendLink',
      data: data      
    })
  }
}