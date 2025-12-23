import request from "@/utils/http/index";
import { type AlbumList, type PhotoList } from "@/types/photo";
export class PhotoService {
  static getAllPhotosByAlbumId(data?: object) {
    return request.post<PhotoList>({
      url: "/admin/users/refreshToken",
      data: data
    })
  }

  static getAllAlbum() {
    return request.get<AlbumList>({
      url: "/front/images/getAllAlbum"  
    })
  }
}
