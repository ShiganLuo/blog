import request from "@/utils/http/index";
import { type UserResult, type RefreshTokenResult } from "@/types/user"

interface image {
  imageId: string,
  imageUrl: string
}
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

  static sendEmailCode(email:string) {
    return request.get<string>({
      url: '/admin/users/getEmailCode/email',
      params: {email}
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
      url: "/admin/users/emailRegister",
      data: data      
    })
  }

  static imgUpload(data?: object) {
    return request.post<image>({
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
