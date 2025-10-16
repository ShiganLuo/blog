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