import request from "@/utils/http/index";
import { type ConfigDetail, type FrontBackground } from "@/types/config";
interface FooterInfomation {
    websiteChineseName:string
    icpFilingNumber:string,
    psbFilingNumber:string,
    favicon:string
}
export class ConfigService {
    /**
     * 获取指定用户的博客配置（前台展示）
     * @param userId 用户id
     */
    static homeGetConfig(userId: number | string){
        return request.get<ConfigDetail>({
            url: `/front/settings/getBlogConfig/${userId}`,
            silent: true
        }).catch(() => {
            return { code: 404, result: null } as any;
        })
    }

    /**
     * 获取指定用户的部分前台信息
     * @param userId 用户id
     */
    static getSomeFrontInformation(userId: number | string) {
        return request.get<FooterInfomation>({
            url: `/front/settings/getSomeFrontInformation/${userId}`,
            silent: true
        }).catch(() => {
            return { code: 404, result: null } as any;
        })
    }

    /**
     * 获取指定用户的前台背景图片
     * @param userId 用户id
     */
    static getFrontBackground(userId: number | string) {
        return request.get<FrontBackground>({
            url: `/front/settings/getFrontBackground/${userId}`,
            silent: true
        }).catch(() => {
            return { code: 404, result: null } as any;
        })
    }
}
