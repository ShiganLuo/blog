import request from '@/utils/http'
import { PhotoAlbumRecordResult, UploadPhotoResult } from '@/types/photo/photo'

// 照片
export class PhotoService {
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

  // 更新照片信息
  static updatePhoto(data: any) {
    return request.post<UploadPhotoResult>({
      url: '',
      data: data
    })
  }

  // 上传照片
  static uploadPhoto(data: any) {
    return request.post<UploadPhotoResult>({
      url: '/admin/image/uploadImage',
      data: data
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

  /**
   * 获取图片素材库列表（支持按文件名搜索）
   * @param fileName 文件名关键词（可选）
   * @returns 图片列表
   */
  static listImages(fileName?: string) {
    return request.get({
      url: '/admin/image/list',
      params: fileName ? { fileName } : {}
    })
  }
}

