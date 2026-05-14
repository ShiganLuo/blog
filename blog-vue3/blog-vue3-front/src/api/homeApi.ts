import request from "@/utils/http/index";

export class HomeService {
    // TODO: 后端暂未实现首页统计接口，待实现后取消注释
    // static homeGetStatistic(data?: object){
    //     return request.post({
    //         url: '/front/home/getStatistic',
    //         data
    //     })
    // }

    static getOneStentence() {
        return request.get<string>({
            url: '/front/utils/oneSentence'
        })
    }
}