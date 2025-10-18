import request from '@/utils/http'
import { PhotoAlbumRecordResult } from '@/types/photo/photo'
import { CodeMsgResult } from '@/types/axios'

// 照片
class PhotoService {
  // 获取照片列表
  static listPhoto(params: any) {
    return request.get<PhotoAlbumRecordResult>({
      url: '/blog/photo/admin/photos',
      params
    })
  }

  // 保存照片
  static savePhoto(data: any) {
    return request.post({
      url: '/blog/photo/admin/photos',
      data
    })
  }

  // 更新照片
  static updatePhoto(data: any) {
    return request.put({
      url: '/blog/photo/admin/photos',
      data
    })
  }

  // 移动照片相册
  static movePhotoAlbum(data: any) {
    return request.put({
      url: '/blog/photo/admin/photos/album',
      data
    })
  }

  // 逻辑删除照片
  static updatePhotoDelete(data: any) {
    return request.put({
      url: `/blog/photo/admin/photos/delete`,
      data
    })
  }

  // 删除照片
  static deletePhoto(id: any) {
    return request.del({
      url: `/blog/photo//admin/photos`,
      headers: {
        'Content-Type': 'application/json'
      },
      data: id
    })
  }
}

export default PhotoService
