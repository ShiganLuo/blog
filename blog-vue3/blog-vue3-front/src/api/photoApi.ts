import request from "@/utils/http/index";
import { type AlbumList, type PhotoList } from "@/types/photo";
export class PhotoService {
  static getAllPhotosByAlbumId(data?: object) {
    return request.post<PhotoList>({
      url: "/api/admin/users/refreshToken",
      data: data
    })
  }

  static getAllAlbum(data?: object) {
    return request.get<AlbumList>({
      url: "/api/front/images/getAllAlbum",
      data: data      
    })
  }
}
