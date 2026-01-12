export type CommentType = "post" | "comment" | "talk";

export interface CommentItem {
  id: number | string;
  userId: number | string;
  userName: string;
  userAvatar: string;
  replyUserName: string;
  content: string;
  createdAt: string;
  likes: number;
  isLiked: boolean;
  ipAddress: string;
  showApplyInput: boolean;
  childComments: CommentItem[];
  [key: string]: any;
}

export interface CommentPage {
    list: CommentItem[],
    total: number
}

export interface CommentParams {
  current: number;
  size: number;
  userId?: number | string;
  type?: string;
  forId?: number | string;
  rootId?: number | string;
  order?: string;
  loading: boolean
}
