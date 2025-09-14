import { request,fullRequest } from "./http/index";

export type UserResult = {

    accessToken: string;
    refreshToken: string;
    /** `accessToken`的过期时间（格式'xxxx/xx/xx xx:xx:xx'） */
    expires: Date;
    id: number;
    avatar: string;
    username: string;
    nickname: string;
    roles: string;
    /** 按钮级别权限 */
    permissions: Array<string>;
};

export type RefreshTokenResult = {
    /** `token` */
    accessToken: string;
    /** 用于调用刷新`accessToken`的接口时所需的`token` */
    refreshToken: string;
    /** `accessToken`的过期时间（格式'xxxx/xx/xx xx:xx:xx'） */
    expires: Date;
};

/** 登录 */
export const login = (data?: object) => {
  return request<UserResult>({
    method:"post",
    url: "/api/admin/users/login",
    data: data }
    );
};

/** 刷新`token` */
export const refreshTokenApi = (data?: object) => {
  return request<RefreshTokenResult>({
    method: "post",
    url: "/api/admin/users/refreshToken",
    data: data
    });
};
/** 注册 */
export const register = (data?: object) => {
  return request({
    method: "post",
    url: "/api/admin/users/register",
    data: data
  })
}
export const getUserInfoById = (id?: string | number) => {
  return fullRequest({
    method: "get",
    url: `api/front/users/getUserInfoById/${id}`,
  })
}

export const reqLogin = (data?: object) => {
  return fullRequest<UserResult>({
    method: "post",
    url: "/api/front/users/login",
    data: data
  });
}

export const reqRegister = (data?: object) => {
  return fullRequest({
    method: "post",
    url: "/api/admin/users/register",
    data: data
  });
}

export const imgUpload = (data?: object) => {
  return fullRequest({
    method: "post",
    url: "/api/front/images/uploadImage",
    data: data
  });
}

export const updateUserInfo = (data?: object) => {
  return fullRequest({
    method: "post",
    url: "",
    data: data
  });
}

export const updateUserPassword = (data?: object) => {
  return fullRequest({
    method: "post",
    url: "",
    data: data
  });
}