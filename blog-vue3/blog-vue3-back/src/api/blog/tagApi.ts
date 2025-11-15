import request from '@/utils/http'
import { TagRecordResult, TagListResult } from '@/types/blog/tag'
import { CodeMsgResult } from '@/types/axios'

// 标签
export class TagService {
  // 查询标签列表
  static listTag(query: any) {
    return request.get<TagRecordResult>({
      url: '/blog/tag/admin/tags',
      params: query
    })
  }

  // 新增标签
  static addTag(data: any) {
    return request.post({
      url: '/blog/tag/admin/tags',
      data: data
    })
  }

  // 修改标签
  static updateTag(data: any) {
    return request.post({
      url: '/blog/tag/admin/tags',
      data: data
    })
  }

  // 删除标签
  static deleteTag(id: any) {
    return request.del({
      url: '/blog/tag/admin/tags/',
      headers: {
        'Content-Type': 'application/json'
      },
      data: id
    })
  }

  // 搜索标签
  static searchTags(keywords: String) {
    return request.get<TagListResult>({
      url: '/api/admin/tags/getTagDictionary',
      params: keywords
    })
  }
}
