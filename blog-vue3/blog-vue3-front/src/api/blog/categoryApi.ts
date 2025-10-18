import request from "@/utils/http/index";
import { type CategoryListResponse } from "@/types/blog/category"
export class CategoryService {
  static getAllCategory() {
    return request.get<CategoryListResponse>({
      url: '/api/front/categories/getAllcategories'
    })
  }
}
