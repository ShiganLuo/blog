// 路由别名
export enum RoutesAlias {
  Home = '/index/index', // 首页
  Login = '/login', // 登录
  Register = '/register', // 注册
  ForgetPassword = '/forget-password', // 忘记密码
  Exception403 = '/exception/403', // 403
  Exception404 = '/exception/404', // 404
  Exception500 = '/exception/500', // 500
  Dashboard = '/dashboard/console', // 工作台
  Analysis = '/dashboard/analysis', // 分析页
  Ecommerce = '/dashboard/ecommerce', // 电子商务
  UserCenter = '/user/user', // 用户中心
  GenEdit = '/tool/gen/edit-table', // 代码生成器
  DictData = '/system/dict/data', // 字典数据
  AuthRole = '/system/user/authRole', // 用户授权角色
  RoleManage = '/system/user/index',
  JobLog = '/monitor/job/log', // 调度日志
  OutsideIframe = '/outside/iframe', // 外部iframe
  ArticlePublish = '/blog/article/publish', // 文章发布
  ArticleManage = '/blog/article/index', // 文章统计
  CommentManage = '/message/comment/index', // 评论管理
  TalkManage = '/message/talk/index', // 随言碎语
  WebsiteInfo = '/website/info/index', // 网站信息
  WebsiteLink = '/website/link/index', // 友链
  Photo = '/photo/photo', // 照片管理
  PhotoDelete = '/photo/delete', // 照片回收站
  PhotoIndex = '/photo/index' // 照片统计
}
