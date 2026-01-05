import request from "@/utils/http/index";
import { type ConfigDetail } from "@/types/config";
interface FooterInfomation {
    websiteChineseName:string
    icpFilingNumber:string,
    favicon:string
}
export class ConfigService {
    static homeGetConfig(){
        return request.get<ConfigDetail>({
            url: '/front/settings/getBlogConfig'
        })
    }

    static getSomeFrontInformation() {
        return request.get<FooterInfomation>({
            url: '/front/settings/getSomeFrontInformation'
        })
    }
}

