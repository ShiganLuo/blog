import request from "@/utils/http/index";

export class SiteService {
  static addView(data?: object) {
    return request.put({
      url: "/api/front/settings/addView",
      data: data
    })
  }
}
