import request from "@/utils/http/index";
import { type TalkItemListResponse } from "@/types/talk";
export class TalkService {
  static getTalkList(data?: object) {
    return request.post<TalkItemListResponse>({
      url: "/api/front/comments/getMessageTalkPage",
      data: data
    })
  }
}
