import { fullRequest  } from "./http";

export const getNotifylist = (data?: object) => {
    return fullRequest({
        method: 'post',
        url: '/api/front/comments/getNotifyPage',
        data:data
    })
}

export const updateNotify = (data?: number | string) => {

}

export const deleteNotify = (data?: number | string) => {
    return fullRequest({
        method: 'post',
        url: ' ',
        data:data
    })
}