import { request,fullRequest } from "./http/index";

export const getAllPhotosByAlbumId = (data?: object) => {
  return fullRequest({
    method: "post",
    url: "/api/admin/users/refreshToken",
    data: data
    });
};

export const getAllAlbum = (data?: object) => {
  return fullRequest({
    method: "get",
    url: "/api/front/images/getAllAlbum",
    data: data
    });
};