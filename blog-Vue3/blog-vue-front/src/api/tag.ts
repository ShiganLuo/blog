import { fullRequest } from "./http";
export const getAllTag = ( data?: object ) => {
    return fullRequest({
        method: 'get',
        url: '/api/front/tags/getAllTags',
        data: data
    })
}
