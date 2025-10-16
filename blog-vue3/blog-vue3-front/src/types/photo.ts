export interface Album {
  id: number;
  album_name: string;
  album_cover: string;
  description: string;
}

export type AlbumList = Album[]

interface Photo {
  id: number;
  url: string;
}

export type PhotoList = Photo[]