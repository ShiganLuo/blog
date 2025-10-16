// stores/user.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useDark, useToggle } from "@vueuse/core";
import { _getLocalItem, _setLocalItem,_removeLocalItem } from "@/utils/tool";

interface UserInfo {
  id?: number | string
  username?: string
  avatar?: string
  nickname?: string
  email?: string
  avatarList?: Array<{ id?: number; name: string; url: string }>;
  [key: string]: any // 其他可能的动态属性
}

const isDark = useDark({
  // 存储到localStorage/sessionStorage中的Key 根据自己的需求更改
  storageKey: "useDarkKEY",
  // 暗黑class名字
  valueDark: "dark",
  // 高亮class名字
  valueLight: "light",
});

const toggle = useToggle(isDark);

export const useStaticData = defineStore(
  "staticData",
  () => {
    // ====================
    // state
    // ====================
    const previewThemeList = ref([
      "default",
      "github",
      "vuepress",
      "mk-cute",
      "smart-blue",
      "cyanosis",
    ]);

    const codeThemeList = ref([
      "atom",
      "a11y",
      "github",
      "gradient",
      "kimbie",
      "paraiso",
      "qtcreator",
      "stackoverflow",
    ]);

    const previewTheme = ref("default");
    const codeTheme = ref("atom");
    const theme = ref(isDark.value); // 当前主题（黑/白）
    const pageHeaderList = ref<any[]>([]);
    const messageTypeIsCard = ref(false);

    // ====================
    // getters (computed)
    // ====================
    const mainTheme = computed(() => isDark.value);

    const getPageHeaderList = computed(() => {
      // 优先使用当前的 pageHeaderList
      if (Array.isArray(pageHeaderList.value) && pageHeaderList.value.length > 0) {
        return pageHeaderList.value;
      }

      // 尝试从 localStorage 获取
      const localData = _getLocalItem("pageHeaderList");

      // 确保返回的始终是数组
      return Array.isArray(localData) ? localData : [];
    });

    const getMessageTypeIsCard = computed(() => messageTypeIsCard.value);

    // ====================
    // actions
    // ====================
    const switchMainTheme = () => {
      toggle();
      theme.value = isDark.value;
      _setLocalItem("mainTheme", theme.value ? "dark" : "light");
    };

    const setPageHeaderList = (list: any[]) => {
      pageHeaderList.value = list;
      _setLocalItem("pageHeaderList", list);
    };

    const setMessageTypeIsCard = (type: boolean) => {
      messageTypeIsCard.value = type;
    };

    return {
      // state
      previewThemeList,
      codeThemeList,
      previewTheme,
      codeTheme,
      theme,
      pageHeaderList,
      messageTypeIsCard,

      // getters
      mainTheme,
      getPageHeaderList,
      getMessageTypeIsCard,

      // actions
      switchMainTheme,
      setPageHeaderList,
      setMessageTypeIsCard,
    };
  },
);

export const useUserStore = defineStore('user', () => {
  // State，在 store 启动时从本地存储加载初始值
  const blogAvatar = ref<string>('');
  const userInfo = ref<UserInfo>(_getLocalItem("userInfo") || {}); 
  const accessToken = ref<string>('');
  const refreshToken = ref<string>('');
  const infoFlag = ref<boolean>(false);
  const tokenFlag = ref<boolean>(false);
  const showLogin = ref<boolean>(false);

  // Getters (使用 computed)
  const isLoggedIn = computed(() => !!accessToken.value);
  const getUserName = computed(() => userInfo.value.nick_name || userInfo.value.username);
  const getBlogAvatar = computed(() => {
    return userInfo.value.avatar || blogAvatar.value || 'default-avatar.png';
  });
  const getUserInfo = computed(() => userInfo.value);
  const getShowLogin = computed(() => showLogin.value);

  // Actions
  function setBlogAvatar(avatar: string) {
    blogAvatar.value = avatar;
  }
  function setUserInfo(info: UserInfo) {
    userInfo.value = info;
    
  }

  function setToken(newAccessToken: string, newRefreshToken?: string) {
    accessToken.value = newAccessToken;
    _setLocalItem("accessToken",accessToken.value);
    if (newRefreshToken) {
      refreshToken.value = newRefreshToken;
      _setLocalItem("refreshToken",refreshToken.value);
    }
  }

  function logOut() {
    userInfo.value = {};
    showLogin.value = false;
    accessToken.value = '';
    refreshToken.value = '';
    _removeLocalItem("accessToken");
    _removeLocalItem("refreshToken");
    _removeLocalItem("userInfo");
  }
  
  function setShowLogin(show: boolean) {
    showLogin.value = show;
  }

  // 关键代码：使用 $subscribe 监听所有状态变化
  // 这个订阅器在 store 实例被创建后运行
  useUserStore().$subscribe((mutation, state) => {
    // 监听 userInfo 状态的变化
    _setLocalItem("userInfo", state.userInfo);
    
    // 你也可以在这里处理 token 的持久化
    // _setLocalItem("token", state.token);
  });

  // 暴露 state 和 actions
  return {
    // State
    blogAvatar,
    userInfo,
    accessToken,
    refreshToken,
    infoFlag,
    tokenFlag,
    showLogin,

    // Getters
    isLoggedIn,
    getUserName,
    getBlogAvatar,
    getUserInfo,
    getShowLogin,

    // Actions
    setUserInfo,
    setToken,
    logOut,
    setShowLogin,
    setBlogAvatar
  };
});
