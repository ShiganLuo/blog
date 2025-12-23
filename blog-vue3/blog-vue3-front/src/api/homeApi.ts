import request from "@/utils/http/index";

export class HomeService {
    static homeGetStatistic(data?: object){
        return request.post({
            url: '',
            data
        })
    }
    static getOneStentence() {
        return request.get<string>({
            url: '/front/utils/oneSentence'
        })
    }
}