import request from '@/utils/http'

// 相册
class PhotoAlbumService {
  // 获取相册列表
  static listPhotoAlbum() {
    return request.get({
      url: '/admin/photoAlbum/list'
    })
  }

  // 新增相册
  static addAlbum(data: { albumName: string; description?: string; albumCover?: string }) {
    return request.post({
      url: '/admin/photoAlbum/add',
      data
    })
  }

  // 修改相册
  static updateAlbum(data: { id: number; albumName?: string; description?: string; albumCover?: string; sortOrder?: number; isVisible?: boolean }) {
    return request.put({
      url: '/admin/photoAlbum/update',
      data
    })
  }

  // 删除相册
  static deleteAlbum(id: number) {
    return request.del({
      url: `/admin/photoAlbum/delete/${id}`
    })
  }

  // 获取相册详情（含照片）
  static getAlbumDetail(id: number) {
    return request.get({
      url: `/admin/photoAlbum/${id}`
    })
  }

  // 添加图片到相册
  static addImageToAlbum(albumId: number, imageId: number) {
    return request.post({
      url: '/admin/photoAlbum/addImage',
      data: { albumId, imageId }
    })
  }

  // 从相册移除图片
  static removeImageFromAlbum(albumId: number, imageId: number) {
    return request.post({
      url: '/admin/photoAlbum/removeImage',
      data: { albumId, imageId }
    })
  }
}

export default PhotoAlbumService
