import { defineStore } from "pinia";
import { store } from "@/store";
import { ref, computed } from "vue";
import { router, resetRouter } from "@/router";
import { storageSession } from "@pureadmin/utils";
import { getLogin, getUserInfoById } from "@/api/user";
import { UserResult } from "@/api/user";
import { useMultiTagsStoreHook } from "@/store/modules/multiTags";
import { type DataInfo, setToken, removeToken, sessionKey } from "@/utils/auth";
import { userType, userInfoType } from "./types";
import { routerArrays } from "@/layout/types";

export const useUserStore = defineStore(
  "pure-user",
  () => {
    // State
    const username = ref<string>(
      storageSession().getItem<DataInfo<number>>(sessionKey)?.username ?? ""
    );
    const role = ref<number | undefined>(
      storageSession().getItem<DataInfo<number>>(sessionKey)?.role
    );
    const avatar = ref<string>("");
    const nick_name = ref<string>("");
    const id = ref<number>(0);
    // 修复: 增加 roles 状态，并初始化为数组
    const roles = ref<string[]>([]);

    // Getters
    const getAvatar = computed(() => {
      return (
        avatar.value ||
        storageSession().getItem<userInfoType>("blogCurrentUser")?.avatar
      );
    });

    const getNickName = computed(() => {
      return (
        nick_name.value ||
        storageSession().getItem<userInfoType>("blogCurrentUser")?.nick_name
      );
    });

    const getUserId = computed(() => {
      return (
        id.value ||
        storageSession().getItem<userInfoType>("blogCurrentUser")?.id
      );
    });

    // Actions
    const SET_USERNAME = (value: string) => {
      username.value = value;
    };
    const SET_ROLE = (value: number) => {
      role.value = value;
    };
    const SET_AVATAR = (value: string) => {
      avatar.value = value;
    };
    const SET_NICKNAME = (value: string) => {
      nick_name.value = value;
    };
    const SET_ID = (value: number) => {
      id.value = value;
    };

    const loginByUsername = async (data: any): Promise<UserResult> => {
      try {
        const res = await getLogin(data);
        if (res.code === 200) {
          setToken(res.result);
          SET_ID(Number(res.result.id));
          await saveUserInfo();
        }
        return res;
      } catch (error) {
        throw error;
      }
    };

    const saveUserInfo = async () => {
      const res = await getUserInfoById(getUserId.value as number);
      if (res.code === 200) {
        SET_AVATAR(res.result.avatar);
        SET_NICKNAME(res.result.nick_name);
        SET_ID(Number(res.result.id));
        storageSession().setItem<userInfoType>("blogCurrentUser", res.result);
      }
    };

    const logOut = () => {
      username.value = "";
      // 修复: 将 this.roles = [] 替换为 roles.value = []
      roles.value = [];
      removeToken();
      useMultiTagsStoreHook().handleTags("equal", [...routerArrays]);
      resetRouter();
      router.push("/login");
    };

    return {
      username,
      role,
      avatar,
      nick_name,
      id,
      roles,
      getAvatar,
      getNickName,
      getUserId,
      SET_USERNAME,
      SET_ROLE,
      SET_AVATAR,
      SET_NICKNAME,
      SET_ID,
      loginByUsername,
      saveUserInfo,
      logOut,
    };
  }
);

export function useUserStoreHook() {
  return useUserStore(store);
}