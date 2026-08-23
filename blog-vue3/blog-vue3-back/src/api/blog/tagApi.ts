import request from '@/utils/http'
import { TagRecordResult, TagListResult } from '@/types/blog/tag'

export class TagService {
  static listTag(query: any) {
    return request.post<TagRecordResult>({
      url: '/admin/tags/list',
      data: query
    })
  }

  static addTag(data: any) {
    return request.post({
      url: '/admin/tags/create',
      data: data
    })
  }

  static updateTag(data: any) {
    return request.post({
      url: '/admin/tags/uploadTag',
      data: data
    })
  }

  static deleteTag(id: any) {
    return request.del({
      url: '/admin/tags/' + id,
      headers: { 'Content-Type': 'application/json' }
    })
  }

  static searchTags(keywords: string) {
    return request.get<TagListResult>({
      url: '/admin/tags/getTagDictionary',
      params: { keyword: keywords }
    })
  }
}
