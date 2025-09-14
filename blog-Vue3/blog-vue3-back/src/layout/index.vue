<script setup lang="ts">
import AdminHeader from '@/layout/header/index.vue';
import AdminFooter from '@/layout/footer/index.vue';
import AdminMenu from '@/layout/menu/index.vue';
import AdminTagList from '@/layout/tag-list/index.vue';
import { useStaticData } from '@/stores/index'

const staticStore = useStaticData();
</script>

<template>
    <el-container>
        <el-aside :width='staticStore.menuWidth'>
            <AdminMenu></AdminMenu>
        </el-aside>

        <el-container>
            <el-header>
                <AdminHeader></AdminHeader>
            </el-header>

            <el-main>
                <AdminTagList></AdminTagList>
                <router-view v-slot="{ Component }">
                    <Transition name="fade">
                        <keep-alive :max="10">
                            <component :is="Component"></component>
                        </keep-alive>
                    </Transition>
                </router-view>
            </el-main>

            <el-footer>
                <AdminFooter></AdminFooter>
            </el-footer>
        </el-container>
    </el-container>
</template>


<style scoped lang="scss">
.el-aside {
    transition: all 0.3s;
}

.el-header {
    padding: 0;
}

.el-footer {
    padding: 0!important;
}

.fade-enter-from {
    opacity: 0;
}

.fade-enter-to {
    opacity: 1;
}

.fade-leave-from {
  opacity: 1;
}

.fade-leave-to {
  opacity: 0;
}

.fade-leave-active {
    transition: all 0.3s;
}

.fade-enter-active {
    transition: all 0.3s;
    transition-delay: 0.3s;
}
</style>