import { BaseArrayResult, DeptResult, BaseResult, CodeMsgResult, BaseObjectResult } from '../axios'

export interface LoginUserResult {
  id:number,
  username:string,
  email:string,
  password:string,
  avatarUrl:string,
  bio:string,
  nickName:string,
  gender:string,
  phoneNumber:string,
  roles:[],
  status:string,
  createdAt:string,
  updatedAt:string,
  lastLogin:string,
  loginAttempts:number,
  isEmailVerified:boolean,
  isActive:boolean

}



// 修改头像返回数据结构
export interface EditProfileAvatarResult {
  imgUrl: string
}

export interface UserResult {
  userId: string
  userName: string
  nickName: string
  userType: string
  email: string
  phoneNumber: string
  sex: string
  avatarUrl: string
  password: string
  status: string
  loginIp: string
  loginDate: string
  createBy: string
  createdAt: string
  updateBy: string
  updatedAt: string
  bio: string
}

export type UserListPageResult = {
  total: number
  list: UserResult[]
}
export type UserListResult = BaseArrayResult<UserResult>

export interface DeptOptionType {
  id: number
  label: string
  children?: DeptOptionType[]
}

export type DeptOptionListResult = DeptOptionType[]

export interface RoleType {
  roleId: number
  roleName: string
  roleSort: number
  dataScope: string
  menuCheckStrictly: boolean
  deptCheckStrictly: boolean
  status: string
  delFlag: string
  flag: boolean
  menuIds: number[]
  permissions: string[]
  createdAt: string
  updatedAt: string
  remark: string
}

export interface PostType {
  postId: number
  postCode: string
  postName: string
  postSort: number
  status: string
  flag: boolean
  createBy: string
  createTime: string
  updateBy: string
  remark: string
  updateTime: string
}

export interface UserInfoResult{
  data: LoginUserResult
  roles: RoleType[]
  posts: PostType[]
  postIds: number[]
  roleIds: number[]
}

export interface UserInfoSimple {
  nickName:string
  userName:string
  userId:number
  roles:[]
}
export interface AuthRolesResult{
  roles: RoleType[]
  user: UserInfoSimple
}
