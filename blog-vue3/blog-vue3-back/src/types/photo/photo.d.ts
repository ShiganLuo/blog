import { BaseArrayResult, BaseObjectResult, BasePageResult } from '../axios'

// 相册对象类型定义
export interface PhotoAlbumResult {
  id: number
  albumName: string
  albumDesc: string
  albumCover: string
  status: number
  photoCount?: number
  createTime?: string
}

export type PhotoAlbumListPageResult = BasePageResult<PhotoAlbumResult>
export type PhotoAlbumListResult = PhotoAlbumResult[]
export type PhotoAlbumRecordResult = {
  list: PhotoAlbumResult[]
  total: number
}

export type UploadPhotoResult = {
  imageUrl: string
  imageId: number
}