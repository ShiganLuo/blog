export interface FriendLink {
  id: number;
  siteLogo: string;
  siteName: string;
  siteDesc: string;
  siteUrl: string;
  userId: number;
}
export type FriendLinkListResponse = {
    total: number,
    list: FriendLink[]
}