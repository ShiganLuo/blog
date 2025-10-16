// 类型定义
export interface TalkItem {
  id: number | string;
  from_id: number | string;
  nick_name: string;
  avatar: string;
  content: string;
  talkImgList: string[];
  createdAt: string;
  is_top: number;
  is_like: boolean;
  like_times: number;
}

export type TalkItemListResponse = {
    total: number
    list: TalkItem[]
}