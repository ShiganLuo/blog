import { request,fullRequest } from "./http";
export const homeGetStatistic = ( data?: object ) => {
    return request({
        method: 'post',
        url: ' ',
        data:data
    })
}
export const getOneStentence = ( ) => {
    return fullRequest({
        method: 'get',
        url: '/api/front/utils/oneSentence'
    })
}