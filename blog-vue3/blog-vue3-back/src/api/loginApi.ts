import request from '@/utils/http'
import { BaseResult,UserLoginResponse } from '@/types/axios'
import { CaptchaResult, loginFunctionEnabledResult, LoginResult } from '@/types/login'
import { LoginUserResult } from '@/types/system/user'
import { MenuListType } from '@/types/menu'

// 登陆
export class LoginService {
  // 登录方法
  static login(data: any) {
    return request.post<UserLoginResponse>({
      url: '/api/admin/users/login',
      data
    })
  }
  // 获取登录功能开关
  static getLoginFunctionEnabled() {
    return request.get<loginFunctionEnabledResult>({
      url: '/api/admin/mock/loginFunctionEnabled'
    })
  }
  // 注册方法
  static register(data: any) {
    return request.post<BaseResult>({
      url: '/register',
      data
    })
  }
  // 获取用户信息
  static getInfo() {
    return request.get<LoginResult<LoginUserResult>>({
      url: '/getInfo'
    })
  }
  // 退出方法
  static logout() {
    return request.post<BaseResult>({
      url: '/logout'
    })
  }
  // 获取验证码
  static getCodeImg() {
    return request.get<CaptchaResult>({
      url: '/api/admin/util/get-captcha',
      headers: {
        isToken: false
      },
      timeout: 20000
    })
  }
  // 发送邮箱验证码
  static sendEmailCode(data: { mailAddress: string }) {
    return request.get<BaseResult>({
      url: '/mailCode',
      params: data
    })
  }
  // 重置密码
  static forgetPwd(data: { email: string; verifyCode: string; password: string }) {
    return request.post<BaseResult>({
      url: '/forgetPwd',
      data
    })
  }

  // 获取路由信息
  static getRouters() {
    return request.get<MenuListType>({
      url: '/api/admin/mock/get-async-routes'
    })
  }
}
