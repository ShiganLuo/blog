// 接口返回类型
export interface TagResponse {
  id: string; 
  tagName: string;
}

export type TagListResponse = TagResponse[]

// 标签类型
export interface TagItem {
  id: number | string;
  tagName: string;
  fontSize: number;
  fontColor: string;
}

export interface Tag {
  id: string;
  tagName: string;
}

export interface ColoredTag extends Tag {
  color: string;
}