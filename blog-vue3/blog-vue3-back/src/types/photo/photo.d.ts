// 相册对象类型定义
export interface PhotoAlbumResult {
  id: number
  albumName: string
  description: string
  albumCover: string
  photoCount?: number
  sortOrder?: number
  isVisible?: boolean
}

export type PhotoAlbumListResult = PhotoAlbumResult[]

export interface AlbumPhoto {
  imageId: number
  filePath: string
  fileName: string
  sortOrder: number
}

export interface AlbumDetailResult {
  id: number
  albumName: string
  description: string
  albumCover: string
  photos: AlbumPhoto[]
}

export type UploadPhotoResult = {
  imageUrl: string
  imageId: number
}
