export interface MessageItem {
  id: number;
  avatar: string;
  nick_name: string;
  user_id: number;
  content: string;
  color: string;
  font_size: number;
  font_weight: number;
  createdAt: string;
  is_like: boolean;
  thumbs_up: number;
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
