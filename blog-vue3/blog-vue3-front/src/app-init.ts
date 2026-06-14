import { ConfigService } from '@/api/configApi';
import { setFavicon } from '@/utils/tool';
import { useUserStore } from '@/stores/index';

export async function initApp() {
  const userStore = useUserStore();
  const userId = userStore.getUserInfo.id;
  if (!userId) {
    // 用户未登录，跳过favicon设置
    return;
  }
  const setting = await ConfigService.getSomeFrontInformation(userId);
    if (setting.code == 200) {
        setFavicon(
        setting.result.favicon
        );
    }

}
