<template>
  <n-space class="layout-wrap">
    <n-layout
      position="absolute"
      has-sider
    >
      <n-layout-sider
        bordered
        collapse-mode="width"
        :collapsed-width="64"
        :width="240"
        :collapsed="appStore.collapsed"
        show-trigger
        @collapse="appStore.setCollapsed(true)"
        @expand="appStore.setCollapsed(false)"
      >
        <n-menu
          :value="currentPath"
          :collapsed="appStore.collapsed"
          :collapsed-width="64"
          :collapsed-icon-size="22"
          :options="menuOptions"
          :render-label="renderMenuLabel"
          :render-icon="renderMenuIcon"
          :expand-icon="expandIcon"
          @update:value="handleUpdateValue"
        />
      </n-layout-sider>
      <n-layout>
        <div
          class="head-wrap"
          :style="{
            width: !appStore.collapsed
              ? 'calc(100vw - 240px)'
              : 'calc(100vw - 64px)',
          }"
        >
          <HeaderCpt></HeaderCpt>
          <openTabCpt></openTabCpt>
        </div>
        <div class="main-wrap">
          <router-view></router-view>
        </div>
      </n-layout>
    </n-layout>
    <PoweredByCpt></PoweredByCpt>
  </n-space>
</template>

<script lang="ts" setup>
import { CaretDownOutline } from '@vicons/ionicons5';
import { NIcon } from 'naive-ui';
import { h, ref, watch } from 'vue';
import { RouteRecordRaw, useRoute, useRouter } from 'vue-router';

import PoweredByCpt from '@/components/PoweredBy/index.vue';
import { defaultRoutes } from '@/router/index';
import { useAppStore } from '@/store/app';
import { useUserStore } from '@/store/user';

import HeaderCpt from './header/header.vue';
import openTabCpt from './openTab/openTab.vue';

// import { deepCloneExclude } from '@/utils';

const router = useRouter();
const route = useRoute();
const appStore = useAppStore();
const userStore = useUserStore();
// const copyOriginDefaultRoutes = deepCloneExclude(
//   defaultRoutes,
//   'component'
// );
const handleRoutes = (routes: RouteRecordRaw[]) => {
  return routes
    .map((v) => {
      // Remove any non-menu properties like 'component'
      const { component, ...rest } = v as any;
      if (rest.children && rest.children.length === 1) {
        rest.meta = {
          title: rest.children[0].meta?.title,
          icon: rest.children[0].meta?.icon,
          hidden: rest.children[0].meta?.hidden,
          sort: rest.meta?.sort,
        };
        rest.label = rest.meta.title;
        rest.key = rest.children[0].path;
        rest.show = !rest.meta.hidden;
        delete rest.children;
      } else if (rest.children && rest.children.length > 1) {
        rest.label = rest.meta && rest.meta.title;
        rest.key = rest.path;
        rest.children = handleRoutes(rest.children);
      } else if (!rest.children) {
        if (rest.meta) {
          rest.label = rest.meta && rest.meta.title;
          rest.key = rest.path;
          rest.show = !rest.meta.hidden;
        }
      }
      // If this is a divider, add type: 'divider'
      if (rest.meta && rest.meta.type === 'divider') {
        rest.type = 'divider';
      }
      return rest;
    })
    .filter((item) => item.show !== false);
};

function sortRoute(a, b) {
  return (b.meta?.sort || 0) - (a.meta?.sort || 0);
}

const menuOptions = ref(
  handleRoutes([...defaultRoutes, ...appStore.routes]).sort(sortRoute)
);
const handleUpdateValue = (key: string, item) => {
  currentPath.value = key;
  if (!appStore.tabList[key]) {
    appStore.setTabList({
      ...appStore.tabList,
      [key]: item.meta.title,
    });
  }
  router.push(key);
};
const currentPath = ref(route.path);
watch(
  () => route.path,
  () => {
    appStore.setPath(route.path);
    currentPath.value = route.path;
  }
);
appStore.setRoutes(menuOptions);
appStore.setPath(route.path);
appStore.setTabList({ [route.path]: route.meta.title });

function renderMenuLabel(option) {
  return option.label;
}
function renderMenuIcon(option) {
  const vn = option.meta && option.meta.icon;
  return vn ? h(vn) : false;
}
function expandIcon() {
  return h(NIcon, null, { default: () => h(CaretDownOutline) });
}
</script>

<style lang="scss" scoped>
.layout-wrap {
  height: 100vh;

  .head-wrap {
    position: fixed;
    z-index: 10;
    background-color: white;
  }
  .main-wrap {
    margin-top: 90px;
    padding: 10px 10px 50px 10px;
  }
}
</style>
