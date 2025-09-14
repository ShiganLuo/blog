import { request  } from "./http";

export const addView = (data?: object) => {
  return request({
    method: "put",
    url: "/api/front/settings/addView",
    data: data
  });
}