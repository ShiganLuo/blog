import request from "@/utils/http/index";
import { type UserResult, type RefreshTokenResult } from "@/types/user"

export class UserService {
  static login(data?: object) {
    return request.post({
      url: "/api/admin/users/login",
      data: data 
    })
  }

  static refreshTokenApi(data?: object) {
    return request.post<RefreshTokenResult>({
      url: "/api/admin/users/refreshToken",
      data: data     
    })
  }

  static register(data?: object) {
    return request.post({
      url: "/api/admin/users/register",
      data: data
    })
  }

  static getUserInfoById(id?: string | number) {
    return request.get({
      url: `/api/front/users/getUserInfoById/${id}`
    })
  }

  static reqLogin(data?: object) {
    return request.post<UserResult>({
      url: "/api/front/users/login",
      data: data
    })
  }

  static reqRegister(data?: object) {
    return request.post({
      url: "/api/admin/users/register",
      data: data      
    })
  }

  static imgUpload(data?: object) {
    return request.post<string>({
      url: "/api/front/images/uploadImage",
      data: data      
    })
  }

  static updateUserInfo(data?: object) {
    return request.post({
      url: "",
      data: data
    })
  }

  static updateUserPassword(data?: object) {
    return request.post({
      url: "",
      data: data      
    })
  }
}
