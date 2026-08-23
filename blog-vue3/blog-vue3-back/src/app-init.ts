import WebsiteService from '@/api/website/websiteApi';
import { setFavicon } from '@/utils/utils';
import { useUserStore } from '@/store/modules/user';

export async function initApp() {
  const userStore = useUserStore();
  if (!userStore.accessToken) {
    return;
  }
  try {
    const setting = await WebsiteService.getWebsiteInfo();
    if (setting.code == 200) {
      setFavicon(setting.result.favicon);
    }
  } catch (e) {
    console.warn('initApp: 获取博客配置失败', e);
  }
}
