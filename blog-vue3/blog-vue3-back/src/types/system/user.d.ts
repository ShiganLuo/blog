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
  deptId: string
  userName: string
  nickName: string
  userType: string
  email: string
  phonenumber: string
  sex: string
  avatar: string
  password: string
  status: string
  delFlag: string
  loginIp: string
  loginDate: string
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
  remark: string
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
  roleKey: string
  roleSort: number
  dataScope: string
  menuCheckStrictly: boolean
  deptCheckStrictly: boolean
  status: string
  delFlag: string
  flag: boolean
  menuIds: number[]
  deptIds: number[]
  permissions: string[]
  createBy: string
  createTime: string
  updateBy: string
  admin: boolean
  remark: string
  updateTime: string
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
}
export interface AuthRolesResult{
  roles: RoleType[]
  user: UserInfoSimple
}
