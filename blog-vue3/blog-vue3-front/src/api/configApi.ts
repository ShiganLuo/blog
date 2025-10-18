import request from "@/utils/http/index";
import { type ConfigDetail } from "@/types/config";
export class ConfigService {
    static homeGetConfig(){
        return request.get<ConfigDetail>({
            url: '/api/front/settings/getBlogConfig'
        })
    }
}

