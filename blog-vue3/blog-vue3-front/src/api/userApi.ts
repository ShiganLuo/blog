import request from "@/utils/http/index";
import { type UserResult, type RefreshTokenResult } from "@/types/user"

export class UserService {
  static login(data?: object) {
    return request.post({
      url: "/admin/users/login",
      data: data 
    })
  }

  static refreshTokenApi(data?: object) {
    return request.post<RefreshTokenResult>({
      url: "/admin/users/refreshToken",
      data: data     
    })
  }

  static register(data?: object) {
    return request.post({
      url: "/admin/users/register",
      data: data
    })
  }

  static getUserInfoById(id?: string | number) {
    return request.get({
      url: `/front/users/getUserInfoById/${id}`
    })
  }

  static reqLogin(data?: object) {
    return request.post<UserResult>({
      url: "/front/users/login",
      data: data
    })
  }

  static reqRegister(data?: object) {
    return request.post({
      url: "/admin/users/register",
      data: data      
    })
  }

  static imgUpload(data?: object) {
    return request.post<string>({
      url: "/front/images/uploadImage",
      data: data,
      headers: {
        'Content-Type': undefined
      }
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
