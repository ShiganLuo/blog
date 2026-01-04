import { ConfigService } from '@/api/configApi';
import { setFavicon } from '@/utils/tool';

export async function initApp() {
  const setting = await ConfigService.getSomeFrontInformation();
    if (setting.code == 200) {
        setFavicon(
        setting.result.favicon
        );
    }

}
