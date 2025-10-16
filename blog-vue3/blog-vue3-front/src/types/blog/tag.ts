// 接口返回类型
export interface TagResponse {
  id: string; 
  tag_name: string;
}

export type TagListResponse = TagResponse[]

// 标签类型
export interface TagItem {
  id: number | string;
  tag_name: string;
  fontSize: number;
  fontColor: string;
}

export interface Tag {
  id: string;
  tag_name: string;
}

export interface ColoredTag extends Tag {
  color: string;
}