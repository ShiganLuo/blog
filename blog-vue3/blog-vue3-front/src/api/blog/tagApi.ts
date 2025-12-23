import request from "@/utils/http/index";
import { type TagListResponse } from "@/types/blog/tag"
export class TagService {
    static getAllTag() {
        return request.get<TagListResponse>({
            url: '/front/tags/getAllTags'
        })
    }
}

