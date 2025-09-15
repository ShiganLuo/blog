export type CommentType = "post" | "comment" | "talk";

export interface CommentItem {
  id: number | string;
  from_id: number | string;
  from_name: string;
  from_avatar: string;
  to_name: string;
  content: string;
  createdAt: string;
  thumbs_up: number;
  is_like: boolean;
  ipAddress: string;
  showApplyInput: boolean;
  childComments: CommentItem[];
  [key: string]: any;
}

export interface CommentParams {
  current: number;
  size: number;
  user_id?: number | string;
  type?: string;
  for_id?: number | string;
  rootId?: number | string;
  order?: string;
  loading: boolean
}
