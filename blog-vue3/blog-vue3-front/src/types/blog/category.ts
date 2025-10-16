import type Category from "@/views/category/category.vue";

export interface Category {
  id: number;
  name: string;
  fontSize: number;
  fontColor: string;
}

// 接口返回数据的原始结构
export interface CategoryResponse {
  id: number;
  name: string;
}

export type CategoryListResponse = CategoryResponse[]