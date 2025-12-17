import { RoutesAlias } from './routesAlias'
import { MenuListType } from '@/types/menu'
import { uuid } from '@/utils/utils'

/**
 * 菜单列表、异步路由
 *
 * 支持两种模式:
 * 1. 前端静态配置 - 直接使用本文件中定义的路由配置
 * 2. 后端动态配置 - 后端返回菜单数据，前端解析生成路由
 *
 * 菜单标题（title）:
 * 可以是 i18n 的 key，也可以是字符串，比如：'用户列表'
 */
export const asyncRoutes: MenuListType[] = [
  {
    id: uuid(),
    name: 'Dashboard',
    path: '/dashboard',
    component: RoutesAlias.Home,
    meta: {
      title: 'menus.dashboard.title',
      icon: '&#xe721;',
      keepAlive: false,
      isHide: false
    },
    children: [
      {
        id: uuid(),
        path: 'console',
        name: 'Console',
        component: RoutesAlias.Dashboard,
        meta: {
          title: 'menus.dashboard.console',
          keepAlive: true
        }
      },
      {
        id: uuid(),
        path: 'analysis',
        name: 'Analysis',
        component: RoutesAlias.Analysis,
        meta: {
          title: 'menus.dashboard.analysis',
          keepAlive: true
        }
      },
      {
        id: uuid(),
        path: 'ecommerce',
        name: 'Ecommerce',
        component: RoutesAlias.Ecommerce,
        meta: {
          title: 'menus.dashboard.ecommerce',
          keepAlive: true,
          showTextBadge: 'new'
        }
      }
    ]
  },
  {
    id: uuid(),
    path: '/tool/gen-edit',
    name: 'GenEdit',
    component: RoutesAlias.Home,
    meta: {
      title: '修改生成配置',
      icon: '&#xe755',
      keepAlive: false,
      isHide: false
    },
    children: [
      {
        id: uuid(),
        path: 'index/:tableId(\\d+)?',
        component: RoutesAlias.GenEdit,
        name: 'GenEditIndex',
        meta: { title: '修改生成配置', keepAlive: false }
      }
    ]
  },
  {
    id: uuid(),
    path: '/system/dict-data',
    name: 'DictData',
    component: RoutesAlias.Home,
    meta: {
      title: '字典数据',
      icon: '&#xe6d9',
      keepAlive: false,
      isHide: false
    },
    children: [
      {
        id: uuid(),
        path: 'index/:dictId(\\d+)?',
        component: RoutesAlias.DictData,
        name: 'DictDataIndex',
        meta: { title: '字典数据', keepAlive: false }
      }
    ]
  },
  {
    id: uuid(),
    path: '/system/user-auth',
    name: 'UserAuth',
    component: RoutesAlias.Home,
    meta: {
      title: '用户管理',
      icon: '&#xe608',
      keepAlive: false,
      isHide: false
    },
    children: [
      {
        id: uuid(),
        path: 'role/index',
        component: RoutesAlias.RoleManage,
        name: "list",
        meta: {title: '用户列表', keepAlive: false }
      },
      {
        id: uuid(),
        path: 'role/authRole/:userId(\\d+)?',
        component: RoutesAlias.AuthRole,
        name: 'AuthRole',
        meta: { title: '角色编辑', keepAlive: false }
      }
    ]
  },
  {
    id: uuid(),
    path: '/blog/article',
    name: 'article',
    component: RoutesAlias.Home,
    meta: {
      title: '文章管理',
      icon: '&#xe7ae',
      keepAlive: false,
      isHide: false
    },
    children: [
      {
        id: uuid(),
        path: 'list',
        component: RoutesAlias.ArticleManage,
        name: 'ArticleManage',
        meta: {title: '文章列表', keepAlive: false}
      },
      {
        id: uuid(),
        path: 'index/:articleId(\\d+)?',
        component: RoutesAlias.ArticlePublish,
        name: 'ArticlePublishIndex',
        meta: { title: '文章编辑', keepAlive: false }
      }

    ]
  },
  {
    id: uuid(),
    path: '/photo',
    name: 'PhotoList',
    component: RoutesAlias.Home,
    meta: {
      title: '照片管理',
      icon: '&#xe6ee',
      keepAlive: false,
      isHide: false
    },
    children: [
      {
        id: uuid(),
        path: 'photo/:albumId(\\d+)?',
        component: RoutesAlias.Photo,
        name: 'PhotoManagement',
        meta: { title: '照片管理', keepAlive: false }
      },
      {
        id: uuid(),
        path: 'index',
        component: RoutesAlias.PhotoIndex,
        name: 'PhotoIndex',
        meta: { title: '照片统计', keepAlive: false}
      }
    ]
  },
  {
    id: uuid(),
    path: '/photo-delete',
    name: 'PhotoDelete',
    component: RoutesAlias.PhotoDelete,
    meta: {
      title: '照片回收站',
      keepAlive: false,
      isHide: false,
      isInMainContainer: true
    }
  }
]
