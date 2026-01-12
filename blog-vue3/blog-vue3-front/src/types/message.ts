export interface MessageItem {
  id: number;
  avatar: string;
  nickName: string;
  userId: number;
  content: string;
  color: string;
  font_size: number;
  font_weight: number;
  createdAt: string;
  isLiked: boolean;
  likes: number;
}

export type MessageItemListResponse = {
    sum: number,
    list: MessageItem[]
}
export interface MessageCompoentItem {
  id: number | string;
  isView: number;
  message: string;
  type: string | number;
  to_id: number;
  // Add other properties as needed
}
export type MessageCompoentItemListResponse = {
    total: number,
    list: MessageCompoentItem[]
}
