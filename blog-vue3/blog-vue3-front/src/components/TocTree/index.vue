<script setup lang="ts">
import { ref, computed, watch, onMounted, onBeforeUnmount } from "vue";

export interface TocItem {
  text: string;
  level: number;
  line?: number;
}
interface TocNode {
  idx: number;
  item: TocItem;
  children: TocNode[];
}

const props = withDefaults(
  defineProps<{ items: TocItem[]; container?: string }>(),
  { container: ".md-editor-preview-wrapper" }
);

const HEADING_SEL = "h1, h2, h3, h4, h5, h6";

const collapsed = ref(new Set<number>());
const activeIdx = ref(-1);

/** 按 level 构建层级树（跳级标题贴到上一层） */
const tree = computed<TocNode[]>(() => {
  const roots: TocNode[] = [];
  const stack: TocNode[] = [];
  props.items.forEach((item, idx) => {
    const node: TocNode = { idx, item, children: [] };
    while (stack.length && stack[stack.length - 1].item.level >= item.level) stack.pop();
    if (stack.length) stack[stack.length - 1].children.push(node);
    else roots.push(node);
    stack.push(node);
  });
  return roots;
});

/** 有子节点的标题下标集合 */
const parentIdxs = computed(() => {
  const s = new Set<number>();
  const walk = (nodes: TocNode[]) =>
    nodes.forEach((n) => {
      if (n.children.length) {
        s.add(n.idx);
        walk(n.children);
      }
    });
  walk(tree.value);
  return s;
});

/** 扁平化渲染列表（折叠的子树不展开） */
const flat = computed(() => {
  const out: { idx: number; text: string; depth: number; hasChildren: boolean }[] = [];
  const walk = (nodes: TocNode[], depth: number) =>
    nodes.forEach((n) => {
      out.push({
        idx: n.idx,
        text: n.item.text,
        depth,
        hasChildren: n.children.length > 0
      });
      if (!(n.children.length && collapsed.value.has(n.idx))) walk(n.children, depth + 1);
    });
  walk(tree.value, 0);
  return out;
});

// 目录刷新时重置状态；标题太多（>40）默认按层级折叠，避免一屏刷不完
watch(
  () => props.items,
  (list) => {
    activeIdx.value = -1;
    collapsed.value = list && list.length > 40 ? new Set(parentIdxs.value) : new Set();
  },
  { immediate: true }
);

const toggle = (idx: number) => {
  const s = new Set(collapsed.value);
  s.has(idx) ? s.delete(idx) : s.add(idx);
  collapsed.value = s;
};
const collapseAll = () => (collapsed.value = new Set(parentIdxs.value));
const expandAll = () => (collapsed.value = new Set());

const headings = () =>
  document.querySelectorAll(`${props.container} ${HEADING_SEL}`);

/** 点击目录跳到对应标题（按 DOM 顺序对齐，不依赖标题 id；抽屉遮罩下 scrollIntoView 会失效，改用显式滚动） */
const go = (idx: number) => {
  const el = headings()[idx] as HTMLElement | undefined;
  if (!el) return;
  const top = el.getBoundingClientRect().top + window.scrollY - 80;
  window.scrollTo({ top, behavior: "smooth" });
  activeIdx.value = idx;
};

/** 滚动高亮当前标题 */
let raf = 0;
const onScroll = () => {
  if (raf) return;
  raf = requestAnimationFrame(() => {
    raf = 0;
    const hs = headings();
    let cur = -1;
    for (let i = 0; i < hs.length; i++) {
      if ((hs[i] as HTMLElement).getBoundingClientRect().top <= 120) cur = i;
      else break;
    }
    activeIdx.value = cur;
  });
};
onMounted(() => window.addEventListener("scroll", onScroll, { passive: true }));
onBeforeUnmount(() => {
  window.removeEventListener("scroll", onScroll);
  if (raf) cancelAnimationFrame(raf);
});
</script>

<template>
  <div class="toc-tree">
    <div class="toc-actions">
      <span class="toc-count">{{ items.length }} 个小节</span>
      <span class="toc-btns">
        <em @click="collapseAll">全部折叠</em>
        <em @click="expandAll">全部展开</em>
      </span>
    </div>

    <div
      v-for="row in flat"
      :key="row.idx"
      :class="['toc-row', { active: row.idx === activeIdx }]"
      :style="{ paddingLeft: 4 + row.depth * 14 + 'px' }"
    >
      <span
        v-if="row.hasChildren"
        class="toc-toggle"
        @click.stop="toggle(row.idx)"
      >{{ collapsed.has(row.idx) ? "▸" : "▾" }}</span>
      <span v-else class="toc-toggle placeholder"></span>
      <span class="toc-text" :title="row.text" @click="go(row.idx)">{{ row.text }}</span>
    </div>

    <div v-if="!flat.length" class="toc-empty">暂无小节标题</div>
  </div>
</template>

<style lang="scss" scoped>
.toc-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 4px 8px;
  margin-bottom: 6px;
  border-bottom: 1px dashed var(--el-border-color);
  font-size: 0.8rem;
  color: var(--el-text-color-secondary);

  .toc-btns em {
    font-style: normal;
    cursor: pointer;
    margin-left: 10px;
    color: var(--el-color-primary);
    opacity: 0.85;
    &:hover {
      opacity: 1;
    }
  }
}

.toc-row {
  display: flex;
  align-items: center;
  gap: 2px;
  padding-top: 5px;
  padding-right: 4px;
  padding-bottom: 5px;
  border-radius: 5px;
  line-height: 1.4;
  cursor: pointer;
  transition: background 0.15s;

  &:hover {
    background: var(--el-fill-color-light);
  }

  .toc-toggle {
    flex-shrink: 0;
    width: 14px;
    text-align: center;
    font-size: 0.7rem;
    color: var(--el-text-color-secondary);
    cursor: pointer;
    &.placeholder {
      cursor: default;
    }
  }

  .toc-text {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    font-size: 0.88rem;
    color: var(--el-text-color-regular);
  }

  &.active {
    background: var(--el-color-primary-light-9);
    .toc-text {
      color: var(--el-color-primary);
      font-weight: 600;
    }
    .toc-toggle {
      color: var(--el-color-primary);
    }
  }
}

.toc-empty {
  padding: 10px 4px;
  font-size: 0.85rem;
  color: var(--el-text-color-secondary);
}
</style>
