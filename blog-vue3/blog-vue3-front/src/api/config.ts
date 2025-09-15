import { request,fullRequest } from "./http";
export const homeGetConfig = (data?: object) => {
    return fullRequest({
        method: 'get',
        url: '/api/front/settings/getBlogConfig',
        data:data
    })

}
