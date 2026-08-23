import { ConfigService } from '@/api/configApi';
import { setFavicon } from '@/utils/tool';

// 相对路径转完整 URL
function resolveUrl(path: string): string {
  if (!path) return ''
  if (path.startsWith('http://') || path.startsWith('https://')) return path
  const base = import.meta.env.VITE_MINIO_URL || 'http://localhost:9007'
  const p = path.startsWith('/') ? path : `/${path}`
  return `${base}${p}`
}

export async function initApp() {
  // 无论是否登录都获取 favicon
  try {
    const setting = await ConfigService.getFrontInfo();
    if (setting.code == 200 && setting.result?.favicon) {
      setFavicon(resolveUrl(setting.result.favicon));
    }
  } catch (e) {
    // 静默失败
  }
}
