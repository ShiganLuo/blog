import request from "@/utils/http/index";
import { type TagListResponse } from "@/types/blog/tag"
export class TagService {
    static getAllTag( data?: object ) {
        return request.get<TagListResponse>({
            url: '/api/front/tags/getAllTags',
            data
        })
    }
}

