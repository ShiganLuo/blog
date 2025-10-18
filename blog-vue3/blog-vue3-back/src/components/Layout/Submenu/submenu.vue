<template>
  <template v-for="item in filteredMenuItems" :key="item.id">
    <!-- 有子菜单 -->
    <el-sub-menu
      v-if="hasChildren(item)"
      :index="item.path || item.meta?.title || ''"
      :level="level"
    >
      <template #title>
        <MenuItemIcon :icon="item.meta?.icon" :color="theme?.iconColor" />
        <span class="menu-name">{{ formatMenuTitle(item.meta?.title || '') }}</span>
        <div v-if="item.meta?.showBadge" class="badge" style="right: 35px" />
      </template>

      <!-- 递归子菜单 -->
      <Submenu
        :list="item.children || []"
        :is-mobile="isMobile"
        :level="level + 1"
        :theme="theme"
        @close="closeMenu"
      />
    </el-sub-menu>

    <!-- 普通菜单项 -->
    <el-menu-item
      v-else
      :index="item.path || item.meta?.title || ''"
      :level-item="level + 1"
      @click="goPage(item)"
    >
      <MenuItemIcon :icon="item.meta?.icon" :color="theme?.iconColor" />
      <template #title>
        <span class="menu-name">{{ formatMenuTitle(item.meta?.title || '') }}</span>
        <div v-if="item.meta?.showBadge" class="badge" />
        <div v-if="item.meta?.showTextBadge" class="text-badge">
          {{ item.meta?.showTextBadge }}
        </div>
      </template>
    </el-menu-item>
  </template>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { MenuListType } from '@/types/menu'
import { formatMenuTitle } from '@/utils/menu'
import { handleMenuJump } from '@/utils/jump'

/**
 * props 定义
 */
interface Props {
  title?: string
  list?: MenuListType[]
  theme?: {
    iconColor?: string
  }
  isMobile?: boolean
  level?: number
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  list: () => [],
  theme: () => ({}),
  isMobile: false,
  level: 0
})

/**
 * emits 定义
 */
const emit = defineEmits<{
  (e: 'close'): void
}>()

/**
 * 过滤菜单
 */
const filterRoutes = (items: MenuListType[]): MenuListType[] => {
  return items
    .filter((item) => !item.meta?.isHide) // 防止 meta 为空导致渲染失败
    .map((item) => ({
      ...item,
      children: item.children ? filterRoutes(item.children) : [] //防止children为undefined导致渲染失败
    }))
}

const filteredMenuItems = computed(() => filterRoutes(props.list))

/**
 * 判断是否有子菜单
 */
const hasChildren = (item: MenuListType): boolean => {
  return Array.isArray(item.children) && item.children.length > 0
}

/**
 * 跳转
 */
const goPage = (item: MenuListType) => {
  closeMenu()
  handleMenuJump(item)
}

/**
 * 关闭菜单
 */
const closeMenu = () => emit('close')
</script>

<script lang="ts">
import { defineComponent, h } from 'vue'

/**
 * 单独抽取图标组件
 */
export const MenuItemIcon = defineComponent({
  name: 'MenuItemIcon',
  props: {
    icon: String,
    color: String
  },
  setup(props) {
    return () =>
      h('i', {
        class: 'menu-icon iconfont-sys',
        style: props.color ? { color: props.color } : undefined,
        innerHTML: props.icon
      })
  }
})
</script>