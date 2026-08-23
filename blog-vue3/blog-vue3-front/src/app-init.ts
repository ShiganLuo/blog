import { ConfigService } from '@/api/configApi';
import { setFavicon } from '@/utils/tool';
import { useUserStore } from '@/stores/index';

export async function initApp() {
  // 无论是否登录都获取 favicon
  try {
    const setting = await ConfigService.getFrontInfo();
    if (setting.code == 200 && setting.result?.favicon) {
      setFavicon(setting.result.favicon);
    }
  } catch (e) {
    // 静默失败
  }
}
