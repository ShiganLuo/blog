export interface FriendLink {
  id: number;
  site_avatar: string;
  site_name: string;
  site_desc: string;
  site_url: string;
  user_id: number;
}
export type FriendLinkListResponse = {
    total: number,
    list: FriendLink[]
}