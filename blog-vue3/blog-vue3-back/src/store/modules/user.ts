import { defineStore } from 'pinia'
import { LanguageEnum } from '@/enums/appEnum'
import { router, setPageTitle } from '@/router'
import { UserInfo } from '@/types/store'
import { useSettingStore } from './setting'
import { useWorktabStore } from './worktab'
import { getSysStorage } from '@/utils/storage'
import { MenuListType } from '@/types/menu'
import { AvatarImga } from '@/utils/utils'
import { _getLocalItem,_setLocalItem,_removeLocalItem } from '@/utils/storage'
import { Base64 } from 'js-base64'

interface UserState {
  language: LanguageEnum // 语言
  isLogin: boolean // 是否登录
  isLock: boolean // 是否锁屏
  lockPassword: string // 锁屏密码
  info: Partial<UserInfo> // 用户信息
  searchHistory: MenuListType[] // 搜索历史
  accessToken: string // 添加访问令牌
  refreshToken: string // 添加刷新令牌
}

// 从前台localStorage读取token的辅助函数
function getFrontendToken(): { accessToken: string; refreshToken: string } {
  try {
    // 前台使用Base64编码存储，没有前缀
    // 存储格式：key = Base64.encode(key), value = Base64.encode(JSON.stringify(value))
    const accessTokenKey = Base64.encode('accessToken')
    const refreshTokenKey = Base64.encode('refreshToken')

    const accessTokenRaw = localStorage.getItem(accessTokenKey)
    const refreshTokenRaw = localStorage.getItem(refreshTokenKey)

    // 解码：先Base64.decode，再JSON.parse
    const accessToken = accessTokenRaw ? JSON.parse(Base64.decode(accessTokenRaw)) : ''
    const refreshToken = refreshTokenRaw ? JSON.parse(Base64.decode(refreshTokenRaw)) : ''

    return {
      accessToken: accessToken || '',
      refreshToken: refreshToken || ''
    }
  } catch (err) {
    console.error('Failed to load token from frontend:', err)
    return { accessToken: '', refreshToken: '' }
  }
}

export const useUserStore = defineStore({
  id: 'userStore',
  state: (): UserState => {
    // 尝试从前台获取token
    const frontendToken = getFrontendToken()

    return {
      language: LanguageEnum.ZH,
      isLogin: !!frontendToken.accessToken,
      isLock: false,
      lockPassword: '',
      info: {},
      searchHistory: [],
      accessToken: frontendToken.accessToken,
      refreshToken: frontendToken.refreshToken
    }
  },
  getters: {
    getUserInfo(): Partial<UserInfo> {
      return this.info
    },
    getSettingState() {
      return useSettingStore().$state
    },
    getWorktabState() {
      return useWorktabStore().$state
    }
  },
  actions: {
    initState() {
      let sys = getSysStorage()

      if (sys) {
        sys = JSON.parse(sys)
        const { info, isLogin, language, searchHistory, isLock, lockPassword, refreshToken } =
          sys.user

        this.info = info || {}
        this.isLogin = isLogin || false
        this.isLock = isLock || false
        this.language = language || LanguageEnum.ZH
        this.searchHistory = searchHistory || []
        this.lockPassword = lockPassword || ''
        this.refreshToken = refreshToken || ''
        this.accessToken = sessionStorage.getItem('accessToken') || ''
      }

      // 如果后台没有token，尝试从前台localStorage读取
      if (!this.accessToken) {
        const frontendToken = getFrontendToken()
        if (frontendToken.accessToken) {
          this.accessToken = frontendToken.accessToken
          this.refreshToken = frontendToken.refreshToken
          this.isLogin = true
          sessionStorage.setItem('accessToken', this.accessToken)
        }
      }
    },
    // 用户数据持久化存储
    saveUserData() {
      saveStoreStorage({
        user: {
          info: this.info,
          isLogin: this.isLogin,
          language: this.language,
          isLock: this.isLock,
          lockPassword: this.lockPassword,
          searchHistory: this.searchHistory,
          refreshToken: this.refreshToken,
          worktab: this.getWorktabState,
          setting: this.getSettingState
        }
      })
    },
    setUserInfo(info: UserInfo) {
      info.avatar = AvatarImga(info.avatar) as string
      this.info = info
      this.saveUserData()
    },
    setLoginStatus(isLogin: boolean) {
      this.isLogin = isLogin
    },
    setLanguage(lang: LanguageEnum) {
      setPageTitle(router.currentRoute.value)
      this.language = lang
    },
    setSearchHistory(list: Array<MenuListType>) {
      this.searchHistory = list
    },
    setLockStatus(isLock: boolean) {
      this.isLock = isLock
    },
    setLockPassword(password: string) {
      this.lockPassword = password
    },
    setToken(accessToken: string, refreshToken?: string) {
      this.accessToken = accessToken
      if (refreshToken) {
        this.refreshToken = refreshToken
      }
      sessionStorage.setItem('accessToken', accessToken)

      // 同时存储到前台的localStorage格式，以便前台共享登录状态
      try {
        const accessTokenKey = Base64.encode('accessToken')
        const refreshTokenKey = Base64.encode('refreshToken')
        localStorage.setItem(accessTokenKey, Base64.encode(JSON.stringify(accessToken)))
        if (refreshToken) {
          localStorage.setItem(refreshTokenKey, Base64.encode(JSON.stringify(refreshToken)))
        }
      } catch (err) {
        console.error('Failed to save token to frontend storage:', err)
      }

      this.saveUserData()
    },
    setAvatar(url: string) {
      this.info.avatar = AvatarImga(url)
    },
    logOut() {
      setTimeout(() => {
        this.info = {}
        this.isLogin = false
        this.isLock = false
        this.lockPassword = ''
        this.accessToken = ''
        this.refreshToken = ''
        sessionStorage.removeItem('accessToken')

        // 同时清除前台的localStorage
        try {
          const accessTokenKey = Base64.encode('accessToken')
          const refreshTokenKey = Base64.encode('refreshToken')
          localStorage.removeItem(accessTokenKey)
          localStorage.removeItem(refreshTokenKey)
        } catch (err) {
          console.error('Failed to remove token from frontend storage:', err)
        }

        useWorktabStore().opened = []
        this.saveUserData()
        sessionStorage.removeItem('iframeRoutes')
        router.push('/login')
      }, 300)
    }
  }
})

function initVersion(version: string) {
  const vs = localStorage.getItem('version')
  if (!vs) {
    localStorage.setItem('version', version)
  }
}

// 数据持久化存储
function saveStoreStorage<T>(newData: T) {
  const version = import.meta.env.VITE_VERSION
  initVersion(version)
  const vs = localStorage.getItem('version') || version
  // const storedData = JSON.parse(localStorage.getItem(`sys-v${vs}`) || '{}')
  const storedData = JSON.parse(_getLocalItem(`sys-v${vs}`) || '{}')

  // 合并新数据与现有数据
  const mergedData = { ...storedData, ...newData }
  _setLocalItem(`sys-v${vs}`, JSON.stringify(mergedData))
  // localStorage.setItem(`sys-v${vs}`, JSON.stringify(mergedData))
}