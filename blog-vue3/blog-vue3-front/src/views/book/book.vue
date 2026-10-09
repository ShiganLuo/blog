<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/index";
import { BookService, type BookListItem } from "@/api/blog/bookApi";
import { ConfigService } from "@/api/configApi";
import SkeletonItem from "@/components/SkeletonItem/skeleton-item.vue";
import PageHeader from "@/components/PageHeader/index.vue";

const userStore = useUserStore();
const router = useRouter();
const bookList = ref<BookListItem[]>([]);
const loading = ref(false);
const bgUrl = ref("");

// 封面完整 URL（后端已补全域名，这里做兼容兜底）
const getCoverUrl = (cover?: string): string => {
  if (!cover) return "";
  if (cover.startsWith("http://") || cover.startsWith("https://")) return cover;
  const base = import.meta.env.VITE_MINIO_URL || "http://localhost:9007";
  const path = cover.startsWith("/") ? cover : `/${cover}`;
  return `${base}${path}`;
};

// 书高随书微差（三档），让书架不像等高栅格
const coverScale = (id: number): string => ["0.9", "0.95", "1"][id % 3] ?? "1";

// 无封面书：CSS 生成布纹封面（底色按 id 取色）
const coverTones: Array<readonly [string, string]> = [
  ["#7a6248", "#463727"],
  ["#4f6b62", "#2c3f39"],
  ["#5b5a7d", "#34334b"],
  ["#7d5450", "#49302d"],
];
const genBg = (id: number): string => {
  const tone = coverTones[Math.abs(id) % coverTones.length] as readonly [string, string];
  return `linear-gradient(155deg, ${tone[0]} 0%, ${tone[1]} 100%)`;
};

// 进入阅读页（不带 articleId，由阅读页解析首章/上次进度）
const goToRead = (item: BookListItem) => {
  router.push({ path: "/book/read", query: { bookId: String(item.id) } });
};

const getBooks = async () => {
  loading.value = true;
  const res = await BookService.getBookList();
  if (res.code === 200) {
    bookList.value = res.result || [];
  }
  loading.value = false;
};

const getFrontBackground = async () => {
  const res = await ConfigService.getFrontBackground(userStore.getUserInfo.id || 1);
  if (res.code === 200) {
    bgUrl.value = res.result.frontHeadBackground;
  }
};

onMounted(() => {
  getBooks();
  getFrontBackground();
});
</script>

<template>
  <PageHeader :bg-url="bgUrl" />
  <div class="bookList">
    <!-- 内容区：显式左右留白，不贴屏幕边缘 -->
    <el-row class="center_box">
      <el-col :span="24">
        <el-card class="bookList-card">
          <el-empty v-if="!loading && bookList.length === 0" description="暂无上架的书" />

          <!-- 加载态：竖版书形骨架，同样站在隔板上 -->
          <div class="bookshelf" v-else-if="loading">
            <div class="book" v-for="i in 4" :key="i">
              <el-skeleton animated style="width: 100%; height: 100%">
                <template #template>
                  <SkeletonItem variant="image" width="100%" height="100%" />
                </template>
              </el-skeleton>
            </div>
          </div>

          <!-- 书架：书立在隔板上，hover 抽书 -->
          <div class="bookshelf" v-else>
            <div
              class="book"
              v-for="item in bookList"
              :key="item.id"
              role="button"
              tabindex="0"
              :aria-label="`阅读《${item.title}》`"
              @click="goToRead(item)"
              @keyup.enter="goToRead(item)"
            >
              <div class="book-cover" :style="{ '--h': coverScale(item.id) }">
                <el-image
                  v-if="item.coverImage"
                  class="book-cover__img"
                  :src="getCoverUrl(item.coverImage)"
                  fit="cover"
                  lazy
                >
                  <template #error>
                    <div class="book-cover__gen" :style="{ background: genBg(item.id) }">
                      <span class="gen-title">{{ item.title }}</span>
                    </div>
                  </template>
                </el-image>
                <div v-else class="book-cover__gen" :style="{ background: genBg(item.id) }">
                  <span class="gen-title">{{ item.title }}</span>
                </div>

                <!-- 书签飘带：章节数 -->
                <span class="book-cover__ribbon">{{ item.chapterCount }} 章</span>

                <!-- 封面底部：书名 + 简介（有则显示） -->
                <div class="book-cover__mask">
                  <span class="name">{{ item.title }}</span>
                  <span class="desc" v-if="item.subtitle || item.description">
                    {{ item.subtitle || item.description }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style lang="scss" scoped>
.bookList {
  box-sizing: border-box;
  width: 100%;
  max-width: 1180px;
  margin: 0 auto;
  padding: 0 24px 32px;

  &-card {
    padding: 10px;
    min-height: 12em;
    border-radius: 10px;
  }
}

// ===================== 书架 =====================
// 隔板不是每个书格画一段，而是整层书架一条通板：
// 行高 = --cover-max-h + --plank-h，行间距 = --row-gap，
// 用 repeating-linear-gradient 按完全相同的周期铺满整行，
// 所以没有书的空位也会延伸出隔板（半满的书架）。
.bookshelf {
  --cover-max-h: 15rem; // 书格最大高度（随断点变）
  --plank-h: 10px; // 隔板厚度
  --row-gap: 20px; // 行距
  --row-h: calc(var(--cover-max-h) + var(--plank-h) + var(--row-gap));

  display: flex;
  flex-wrap: wrap;
  align-items: flex-end; // 同行的书统一坐在隔板上
  row-gap: var(--row-gap);
  margin-top: 8px; // 用 margin 而非 padding，保证背景渐变原点与第一行对齐

  background-image: repeating-linear-gradient(
    to bottom,
    transparent 0,
    transparent var(--cover-max-h),
    #b99469 var(--cover-max-h), // 板顶高光
    #8b6b4a calc(var(--cover-max-h) + 3px),
    #76583c calc(var(--cover-max-h) + var(--plank-h)), // 板体
    rgba(0, 0, 0, 0.13) calc(var(--cover-max-h) + var(--plank-h) + 6px), // 板下落影
    transparent var(--row-h)
  );
}

.book {
  position: relative;
  box-sizing: border-box;
  flex: 0 0 50%;
  height: calc(var(--cover-max-h) + var(--plank-h));
  padding: 0 9px var(--plank-h); // 底部留出隔板区，书底正好落在板顶
  display: flex;
  align-items: flex-end;
  justify-content: center;
  cursor: pointer;
  outline: none;
}

// ===================== 单本书 =====================
.book-cover {
  position: relative;
  width: 100%;
  height: calc(var(--cover-max-h) * var(--h, 1));
  border-radius: 5px;
  overflow: hidden;
  background: #dfe3e8;
  box-shadow: 0 5px 10px -6px rgba(0, 0, 0, 0.5);
  transition:
    transform 0.25s ease,
    box-shadow 0.25s ease;

  &__img {
    display: block;
    width: 100%;
    height: 100%;
  }

  // 书脊：左侧压暗条，让每本书都“像书”
  &::before {
    content: "";
    position: absolute;
    top: 0;
    left: 0;
    bottom: 0;
    width: 7px;
    z-index: 1;
    background: linear-gradient(
      to right,
      rgba(0, 0, 0, 0.45),
      rgba(0, 0, 0, 0.15) 60%,
      rgba(0, 0, 0, 0)
    );
    pointer-events: none;
  }

  // 书口：右侧纸页条纹
  &::after {
    content: "";
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    width: 4px;
    z-index: 1;
    background: repeating-linear-gradient(
      to bottom,
      rgba(255, 255, 255, 0.55) 0 2px,
      rgba(214, 204, 190, 0.5) 2px 4px
    );
    opacity: 0.75;
    pointer-events: none;
  }

  // 无封面：CSS 生成封面
  &__gen {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 100%;

    // 布纹装帧线框
    &::after {
      content: "";
      position: absolute;
      inset: 8px;
      border: 1px solid rgba(255, 255, 255, 0.28);
      border-radius: 2px;
      pointer-events: none;
    }

    .gen-title {
      writing-mode: vertical-rl;
      max-height: 82%;
      overflow: hidden;
      color: rgba(255, 255, 255, 0.95);
      font-size: 1.1rem;
      letter-spacing: 0.25em;
      line-height: 1;
      text-shadow: 0 1px 3px rgba(0, 0, 0, 0.4);
    }
  }

  // 章节数：书签飘带
  &__ribbon {
    position: absolute;
    top: 0;
    right: 14px;
    z-index: 4;
    padding: 7px 4px 12px;
    background: linear-gradient(180deg, #e08b5a, #c65a36);
    clip-path: polygon(0 0, 100% 0, 100% 100%, 50% 74%, 0 100%);
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.35);
    color: #fff;
    font-size: 0.72rem;
    letter-spacing: 2px;
    line-height: 1;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.35);
    writing-mode: vertical-rl;
  }

  // 封面底部信息：书名 + 简介
  &__mask {
    position: absolute;
    right: 0;
    bottom: 0;
    left: 0;
    z-index: 2;
    padding: 2.4rem 9px 9px;
    background: linear-gradient(
      to top,
      rgba(12, 10, 8, 0.88),
      rgba(12, 10, 8, 0.55) 55%,
      transparent
    );

    .name {
      display: block;
      overflow: hidden;
      color: #fff;
      font-size: 1rem;
      font-weight: 600;
      text-overflow: ellipsis;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
      white-space: nowrap;
    }

    .desc {
      display: -webkit-box;
      overflow: hidden;
      margin-top: 2px;
      color: rgba(255, 255, 255, 0.78);
      font-size: 0.76rem;
      line-height: 1.45;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 2;
    }
  }
}

// hover 抽书
.book:hover .book-cover,
.book:focus-visible .book-cover {
  transform: translateY(-14px);
  box-shadow: 0 22px 26px -14px rgba(0, 0, 0, 0.55);
}

// ===================== 断点 =====================
// 依据：内容区被全局 .center_box 锁定在 ~833px（≥900 视口时不变），
// 所以列数按“书架实际宽度”而不是视口宽度配比，保持封面比 ~0.6-0.7。
@media screen and (min-width: 560px) {
  .book {
    flex-basis: 33.3333%;
  }
}

@media screen and (min-width: 768px) {
  .bookshelf {
    --cover-max-h: 19rem;
  }
  .book {
    flex-basis: 25%;
  }
}

@media screen and (min-width: 992px) {
  .bookshelf {
    --cover-max-h: 17rem;
  }
  .book {
    flex-basis: 20%;
  }
}
</style>
