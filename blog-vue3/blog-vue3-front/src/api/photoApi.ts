import request from '@/utils/http/index'
import { type AlbumList, type AlbumDetail } from '@/types/photo'

export class PhotoService {
  // 获取所有相册
  static getAllAlbum() {
    return request.get<AlbumList>({
      url: '/front/photoAlbum/list'
    })
  }

  // 获取相册详情（含照片）
  static getAlbumById(id: number) {
    return request.get<AlbumDetail>({
      url: `/front/photoAlbum/${id}`
    })
  }

  // 管理端：获取所有相册
  static adminGetAllAlbum() {
    return request.get<AlbumList>({
      url: '/admin/photoAlbum/list'
    })
  }

  // 管理端：创建相册
  static addAlbum(data: { albumName: string; description?: string; albumCover?: string }) {
    return request.post({
      url: '/admin/photoAlbum/add',
      data
    })
  }

  // 管理端：更新相册
  static updateAlbum(data: { id: number; albumName?: string; description?: string; albumCover?: string; sortOrder?: number; isVisible?: boolean }) {
    return request.put({
      url: '/admin/photoAlbum/update',
      data
    })
  }

  // 管理端：删除相册
  static deleteAlbum(id: number) {
    return request.del({
      url: `/admin/photoAlbum/delete/${id}`
    })
  }

  // 管理端：添加图片到相册
  static addImageToAlbum(albumId: number, imageId: number) {
    return request.post({
      url: '/admin/photoAlbum/addImage',
      data: { albumId, imageId }
    })
  }

  // 管理端：从相册移除图片
  static removeImageFromAlbum(albumId: number, imageId: number) {
    return request.post({
      url: '/admin/photoAlbum/removeImage',
      data: { albumId, imageId }
    })
  }
}
