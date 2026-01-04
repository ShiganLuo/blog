import WebsiteService from '@/api/website/websiteApi';
import { setFavicon } from '@/utils/utils';

export async function initApp() {
  const setting = await WebsiteService.getWebsiteInfo();
    if (setting.code == 200) {
        setFavicon(
        setting.result.favicon
        );
    }

}
