export interface Album {
  id: number
  albumName: string
  albumCover: string
  description: string
  photoCount?: number
}

export type AlbumList = Album[]

export interface AlbumPhoto {
  imageId: number
  filePath: string
  fileName: string
  sortOrder: number
}

export interface AlbumDetail {
  id: number
  albumName: string
  description: string
  albumCover: string
  photos: AlbumPhoto[]
}

export type PhotoList = AlbumPhoto[]
