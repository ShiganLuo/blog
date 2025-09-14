import type { RouteRecordRaw } from 'vue-router'
import Layout from '../../layout/index.vue'
const routes: RouteRecordRaw[] = [
  {
    path: "/",
    name: "Layout",
    meta: {
      name: "Layout",
    },
    component: Layout,
    redirect: "/home",
    children: [
      {
        path: '/home',
        name: 'Home', // 路由唯一标识
        component: () => import('@/views/home/HomeView.vue'), // 懒加载组件
          meta: {
            title: '首页', // 页面标题
            requiresAuth: true, // 需要登录
            icon: 'i-carbon-home' // 菜单图标（使用Unocss图标）
          }
      },
      {
        path: "/article",
        name: "Article",
        meta: {
          name: "文章",
        },
        component: () => import("@/views/article/article.vue"),
      },
      {
        path: "/articleList",
        name: "ArticleList",
        meta: {
          name: "文章列表",
        },
        component: () => import("@/views/article/article-list.vue"),
      },
            {
        path: "/archives",
        name: "Archives",
        meta: {
          name: "时间轴",
        },
        component: () => import("@/views/archives/archives.vue"),
      },
       {
        path: "/resources",
        name: "Resources",
        meta: {
          name: "资源导航",
        },
        children: [
          {
            path: "/resources/front",
            name: "Front",
            meta: {
              name: "前端",
            },
            component: () => import("@/views/resources/category-list.vue"),
          },
          {
            path: "/resources/back",
            name: "Back",
            meta: {
              name: "后端",
            },
            component: () => import("@/views/resources/category-list.vue"),
          },
        ],
      },
      {
        path: "/siteList",
        name: "SiteList",
        meta: {
          name: "网站列表",
        },
        component: () => import("@/views/resources/site-list.vue"),
      },
      {
        path: "/category",
        name: "Category",
        meta: {
          name: "分类",
        },
        component: () => import("@/views/category/category.vue"),
      },
      {
        path: "/tag",
        name: "Tag",
        meta: {
          name: "标签",
        },
        component: () => import("@/views/tag/tag.vue"),
      },
       {
        path: "/photoAlbum",
        name: "PhotoAlbum",
        meta: {
          name: "相册",
        },
        component: () => import("@/views/photo/photo-album.vue"),
      },
      {
        path: "/photos",
        name: "Photos",
        meta: {
          name: "图库",
        },
        component: () => import("@/views/photo/photos.vue"),
      },
      {
        path: "/talk",
        name: "Talk",
        meta: {
          name: "说说",
        },
        component: () => import("@/views/talk/talk.vue"),
      },
      {
        path: "/userCenter",
        name: "UserCenter",
        meta: {
          name: "个人中心",
        },
        component: () => import("@/views/user/user-center.vue"),
      },
      {
        path: "/link",
        name: "link",
        meta: {
          name: "友链",
        },
        children: [
          {
            path: "list",
            name: "LinkList",
            meta: {
              name: "友情链接",
            },
            component: () => import("@/views/links/link-list.vue"),
          },
        ],
      },
      {
        path: "/message",
        name: "Message",
        meta: {
          name: "留言",
        },
        children: [
          {
            path: "list",
            name: "MessageList",
            meta: {
              keepAlive: true,
              name: "留言",
            },
            component: () => import("@/views/message/index.vue"),
          },
          {
            path: "publish",
            name: "PublishMessage",
            meta: {
              name: "编辑/新增留言",
            },
            component: () => import("@/views/message/publish.vue"),
          },
          {
            path: "detail",
            name: "MessageDetail",
            meta: {
              name: "留言详情",
            },
            component: () => import("@/views/message/detail.vue"),
          },
        ],
      },
      {
        path: "/:pathMatch(.*)*",
        name: "404",
        meta: {
          name: "404 not found",
        },
        component: () => import("@/views/404/index.vue"),
    },
    ]
  }

]

export default routes
