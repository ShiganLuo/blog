import { useAppStore } from '@/store/app';
import { useUserStore } from '@/store/user';
import {_getLocalItem} from '@/utils/localStorage/cache';

import router from './router';
// 用户守卫拦截页面跳转
// 白名单，不需要登录即可跳转，如登录页
const whiteList = ['/login', '/oauth/qq_login', '/oauth/github_login'];

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();
  const appStore = useAppStore();
  const userInfo = userStore.getUserInfo();
  const accessToken = _getLocalItem("accessToken");
  appStore.setLoading(true);
  // 先判断有没有登录
  if (accessToken) {
    // userStore.setToken(hasToken);
    if (to.path === '/login') {
      next('/');
    }
    console.log("-------1---------")
    console.log('roles',userInfo.roles)
    console.log("userInfo",userStore.getUserInfo())
    // 判断用户有没有角色
    if (userInfo.roles && userInfo.roles.length) {
      next();
    } else {
      if (!userInfo.roles || !userInfo.roles.length) {
        // next(false);
        next(true)
        window.$message.error('你没有角色');
        return;
      }
      console.log('userInfo.roles',userInfo.roles)
      const routeRes = userStore.generateAsyncRoutes(userInfo.roles);
      routeRes.forEach((v) => {
        router.addRoute(v);
      });
      appStore.setRoutes(routeRes);
      next({ ...to, replace: true });
      return;
    }
  } else {
    // 没登录的话，判断跳转的页面在不在白名单内
    if (whiteList.indexOf(to.path) !== -1) {
      return next();
    }
    next(`/login?redirect=${to.path}`);
  }
});

router.afterEach(() => {
  const appStore = useAppStore();
  appStore.setLoading(false);
});
