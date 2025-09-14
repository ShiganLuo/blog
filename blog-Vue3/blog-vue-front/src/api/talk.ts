import { request,fullRequest } from "./http/index";
export const getTalkList = (data?: object) => {
  return fullRequest({
    method: "post",
    url: "/api/front/comments/getMessageTalkPage",
    data: data
    });
};