import request from "@/utils/http/index";
import { type MessageCompoentItemListResponse } from "@/types/message";
export class NotifyService {
    static getNotifylist(data?: object) {
        return request.post<MessageCompoentItemListResponse>({
            url: '/api/front/comments/getNotifyPage',
            data:data
        })
    }

    static updateNotify(data?: number | string) {
        return request.post({

        })
    }

    static deleteNotify(data?: number | string) {
        return request.post({
            url: ' ',
            data:data
        })
    }
}
