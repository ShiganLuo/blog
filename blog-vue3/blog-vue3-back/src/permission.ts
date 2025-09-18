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
  const { roles } = userStore;
  const accessToken = _getLocalItem("accessToken");
  appStore.setLoading(true);
  // 先判断有没有登录
  if (accessToken) {
    // userStore.setToken(hasToken);
    if (to.path === '/login') {
      next('/');
    }
    // 判断用户有没有角色
    if (roles && roles.length) {
      next();
    } else {
      const { code, data }: any = await userStore.getUserInfo();
      if (code !== 200) {
        // next(false);
        next(true);
        return;
      }
      if (!data.roles || !data.roles.length) {
        // next(false);
        next(true)
        window.$message.error('你没有角色');
        return;
      }
      const routeRes = userStore.generateAsyncRoutes(data.roles);
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
