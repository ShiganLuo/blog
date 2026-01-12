// 类型定义
export interface TalkItem {
  id: number | string;
  userId: number | string;
  nickName: string;
  avatar: string;
  content: string;
  talkImgList: string[];
  createdAt: string;
  isToped: number;
  isLiked: boolean;
  likes: number;
}

export type TalkItemListResponse = {
    total: number
    list: TalkItem[]
}