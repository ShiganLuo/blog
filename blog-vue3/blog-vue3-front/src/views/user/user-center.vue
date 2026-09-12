<script setup lang="ts">
import { ref, reactive, onMounted, computed } from "vue";
import type { FormInstance, FormRules, FormItemRule } from "element-plus";
import { UserService } from "@/api/userApi";
import { useUserStore } from "@/stores/index";
import { ConfigService } from "@/api/configApi";
import Upload from "@/components/Upload/upload.vue";
import PageHeader from "@/components/PageHeader/index.vue";
import { ElMessageBox, ElMessage } from "element-plus";
import router from "@/router";

// 用户信息表单类型
interface InfoForm {
  userId: string | number;
  nickname: string;
  avatar: string;
  avatarList: any[];
  gender: number;
  bio: string;
}

// 密码表单类型
interface PwdForm {
  oldPassword: string;
  newPassword: string;
  confirmPassword: string;
}

// 统计数据类型
interface UserStats {
  articleCount: number;
  commentCount: number;
  talkCount: number;
  likeCount: number;
}

// 活动记录类型
interface RecentComment {
  commentId: number;
  content: string;
  articleTitle: string;
  articleId: number;
  createdAt: string;
}
interface RecentTalk {
  talkId: number;
  content: string;
  createdAt: string;
  likes: number;
}

const userStore = useUserStore();
const bgUrl = ref<string>("");

// 表单校验
type ValidateCallback = (error?: Error) => void;

const avatarV: FormItemRule["validator"] = (_rule, _value, cb: ValidateCallback) => {
  if (!infoForm.avatarList.length) {
    return cb(new Error("请上传头像"));
  }
  cb();
};

// ref & reactive
const infoFormRef = ref<FormInstance>();
const pwdFormRef = ref<FormInstance>();
const activeTab = ref("info");
const loading = ref(false);
const statsLoading = ref(false);
const activityLoading = ref(false);

const infoForm = reactive<InfoForm>({
  userId: "",
  nickname: "",
  avatar: "",
  avatarList: [],
  gender: 9,
  bio: "",
});

const pwdForm = reactive<PwdForm>({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});

const userStats = reactive<UserStats>({
  articleCount: 0,
  commentCount: 0,
  talkCount: 0,
  likeCount: 0,
});

const recentComments = ref<RecentComment[]>([]);
const recentTalks = ref<RecentTalk[]>([]);

const infoRules: FormRules<InfoForm> = reactive({
  nickname: [{ required: true, message: "请输入昵称", trigger: "blur" }],
  avatar: [{ required: true, validator: avatarV, trigger: "blur" }],
});

const pwdRules: FormRules<PwdForm> = reactive({
  oldPassword: [
    { required: true, message: "请输入旧密码", trigger: "blur" },
    { min: 6, message: "密码至少6位", trigger: "blur" }
  ],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { min: 6, message: "密码至少6位", trigger: "blur" }
  ],
  confirmPassword: [
    { required: true, message: "请确认新密码", trigger: "blur" },
    {
      validator: (_rule: any, value: string, callback: (error?: Error) => void) => {
        if (value !== pwdForm.newPassword) {
          callback(new Error("两次密码不一致"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
});

const genderOptions = [
  { label: "男", value: 1 },
  { label: "女", value: 2 },
  { label: "保密", value: 9 },
];

const genderLabel = computed(() => {
  const found = genderOptions.find(g => g.value === infoForm.gender);
  return found ? found.label : "保密";
});

// 获取登录用户信息
const getCurrentUserInfo = async () => {
  infoForm.nickname = userStore.getUserInfo.nickname || '';
  infoForm.avatar = userStore.getUserInfo.avatar || '';
  infoForm.userId = userStore.getUserInfo.id || '';
  if (infoForm.avatar) {
    infoForm.avatarList = [{
      id: 1,
      url: infoForm.avatar,
      name: infoForm.avatar?.split("/").slice(-1)[0],
    }];
  }
  // 从后端获取完整信息（含gender、bio）
  if (infoForm.userId) {
    const res = await UserService.getUserInfoById(infoForm.userId);
    if (res.code === 200) {
      const data = res.result as any;
      infoForm.gender = data.sex ?? 9;
      infoForm.bio = data.bio || "";
    }
  }
};

// 获取统计数据
const getUserStats = async () => {
  if (!infoForm.userId) return;
  statsLoading.value = true;
  try {
    const res = await UserService.getUserStats(infoForm.userId);
    if (res.code === 200) {
      Object.assign(userStats, res.result as any);
    }
  } finally {
    statsLoading.value = false;
  }
};

// 获取活动记录
const getUserActivity = async () => {
  if (!infoForm.userId) return;
  activityLoading.value = true;
  try {
    const res = await UserService.getUserActivity(infoForm.userId);
    if (res.code === 200) {
      const data = res.result as any;
      recentComments.value = data.recentComments || [];
      recentTalks.value = data.recentTalks || [];
    }
  } finally {
    activityLoading.value = false;
  }
};

// 修改用户信息
const updateInfo = async () => {
  await infoFormRef.value?.validate(async (valid) => {
    if (valid) {
      ElMessageBox.confirm("确认修改用户信息？", "提示", {
        confirmButtonText: "确认",
        cancelButtonText: "取消",
      }).then(async () => {
        loading.value = true;
        // 上传图片
        const file = infoForm.avatarList[0];
        if (file?.raw instanceof File) {
          const formData = new FormData();
          formData.append('file', file.raw);
          const img = await UserService.imgUpload(formData);
          if (img.code === 200) {
            infoForm.avatar = img.result.imageUrl;
          }
        }

        const res = await UserService.updateUserInfo({
          userId: infoForm.userId,
          username: userStore.getUserInfo.username,
          nickname: infoForm.nickname,
          avatar: infoForm.avatar,
          gender: infoForm.gender,
          bio: infoForm.bio,
        });
        if (res && res.code === 200) {
          ElMessage.success("修改成功");
          await getCurrentUserInfo();
        } else {
          ElMessage.error(res.message);
        }
        loading.value = false;
      });
    }
  });
};

// 修改密码
const updatePassword = async () => {
  await pwdFormRef.value?.validate(async (valid) => {
    if (valid) {
      ElMessageBox.confirm("确认修改密码？", "提示", {
        confirmButtonText: "确认",
        cancelButtonText: "取消",
      }).then(async () => {
        loading.value = true;
        const res = await UserService.updatePassword({
          userId: infoForm.userId,
          oldPassword: pwdForm.oldPassword,
          newPassword: pwdForm.newPassword,
        });
        if (res && res.code === 200) {
          ElMessage.success("密码修改成功");
          pwdForm.oldPassword = "";
          pwdForm.newPassword = "";
          pwdForm.confirmPassword = "";
        } else {
          ElMessage.error(res.message);
        }
        loading.value = false;
      });
    }
  });
};

const getFrontBackground = async (): Promise<void> => {
  const res = await ConfigService.getFrontBackground(userStore.getUserInfo.id || 1);
  if (res.code === 200) {
    bgUrl.value = res.result.frontHeadBackground;
  }
};

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return "";
  return time.replace("T", " ").substring(0, 16);
};

// 截断内容
const truncateContent = (content: string, maxLen: number = 60) => {
  if (!content) return "";
  return content.length > maxLen ? content.substring(0, maxLen) + "..." : content;
};

onMounted(async () => {
  // 未登录则提示并跳转
  if (!userStore.getUserInfo.id) {
    ElMessage.warning("请先登录");
    userStore.setShowLogin(true);
    router.push("/");
    return;
  }
  await getCurrentUserInfo();
  getFrontBackground();
  getUserStats();
  getUserActivity();
});
</script>

<template>
  <PageHeader :bg-url="bgUrl"/>
  <div class="user-center center_box">
    <div class="user-center__layout">
      <!-- 左侧：用户信息卡 -->
      <div class="user-center__sidebar">
        <el-card class="profile-card" shadow="hover">
          <div class="profile-card__avatar">
            <el-avatar :size="80" :src="infoForm.avatar" />
          </div>
          <h3 class="profile-card__name">{{ infoForm.nickname || '未设置昵称' }}</h3>
          <p class="profile-card__bio">{{ infoForm.bio || '这个人很懒，什么都没写~' }}</p>
          
          <!-- 数据统计 -->
          <div class="profile-card__stats" v-loading="statsLoading">
            <div class="stat-item">
              <span class="stat-value">{{ userStats.articleCount }}</span>
              <span class="stat-label">文章</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ userStats.commentCount }}</span>
              <span class="stat-label">评论</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ userStats.talkCount }}</span>
              <span class="stat-label">说说</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ userStats.likeCount }}</span>
              <span class="stat-label">点赞</span>
            </div>
          </div>

          <el-divider />
          
          <!-- 基本信息 -->
          <div class="profile-card__info">
            <div class="info-item">
              <i class="iconfont icon-icon"></i>
              <span>{{ genderLabel }}</span>
            </div>
            <div class="info-item">
              <i class="iconfont icon-pinglun"></i>
              <span>{{ userStore.getUserInfo.username }}</span>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧：Tab内容 -->
      <div class="user-center__main">
        <el-card shadow="hover">
          <el-tabs v-model="activeTab">
            <!-- 个人资料 Tab -->
            <el-tab-pane label="个人资料" name="info">
              <el-form
                ref="infoFormRef"
                :model="infoForm"
                :rules="infoRules"
                label-width="80px"
                class="edit-form"
              >
                <el-form-item label="头像" prop="avatar">
                  <Upload
                    v-model:file-list="infoForm.avatarList"
                    :limit="1"
                    :width="100"
                    :height="100"
                    :multiple="false"
                    :preview="true"
                  />
                </el-form-item>
                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="infoForm.nickname" placeholder="请输入昵称" clearable style="max-width: 300px" />
                </el-form-item>
                <el-form-item label="性别" prop="gender">
                  <el-radio-group v-model="infoForm.gender">
                    <el-radio :value="1">男</el-radio>
                    <el-radio :value="2">女</el-radio>
                    <el-radio :value="9">保密</el-radio>
                  </el-radio-group>
                </el-form-item>
                <el-form-item label="简介" prop="bio">
                  <el-input
                    v-model="infoForm.bio"
                    type="textarea"
                    :rows="3"
                    placeholder="介绍一下自己吧~"
                    maxlength="200"
                    show-word-limit
                    style="max-width: 400px"
                  />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="loading" @click="updateInfo">保存修改</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- 修改密码 Tab -->
            <el-tab-pane label="修改密码" name="password">
              <el-form
                ref="pwdFormRef"
                :model="pwdForm"
                :rules="pwdRules"
                label-width="80px"
                class="edit-form"
              >
                <el-form-item label="旧密码" prop="oldPassword">
                  <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入旧密码" style="max-width: 300px" />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码（至少6位）" style="max-width: 300px" />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" style="max-width: 300px" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="loading" @click="updatePassword">修改密码</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- 动态 Tab -->
            <el-tab-pane label="我的动态" name="activity">
              <div v-loading="activityLoading" class="activity-section">
                <!-- 最近说说 -->
                <div class="activity-block" v-if="recentTalks.length">
                  <h4 class="activity-block__title">
                    <i class="iconfont icon-speechbubble"></i> 最近说说
                  </h4>
                  <div class="activity-list">
                    <div class="activity-item" v-for="talk in recentTalks" :key="talk.talkId">
                      <div class="activity-item__content">
                        <router-link :to="`/talk?id=${talk.talkId}`" class="activity-link">
                          {{ truncateContent(talk.content) }}
                        </router-link>
                      </div>
                      <div class="activity-item__meta">
                        <span>{{ formatTime(talk.createdAt) }}</span>
                        <span class="like-count">
                          <i class="iconfont icon-icon"></i> {{ talk.likes }}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 最近评论 -->
                <div class="activity-block" v-if="recentComments.length">
                  <h4 class="activity-block__title">
                    <i class="iconfont icon-pinglun"></i> 最近评论
                  </h4>
                  <div class="activity-list">
                    <div class="activity-item" v-for="comment in recentComments" :key="comment.commentId">
                      <div class="activity-item__content">
                        <span class="comment-text">{{ truncateContent(comment.content) }}</span>
                        <span v-if="comment.articleTitle" class="comment-target">
                          → 
                          <router-link :to="`/article/${comment.articleId}`" class="activity-link">
                            {{ truncateContent(comment.articleTitle, 30) }}
                          </router-link>
                        </span>
                      </div>
                      <div class="activity-item__meta">
                        <span>{{ formatTime(comment.createdAt) }}</span>
                      </div>
                    </div>
                  </div>
                </div>

                <!-- 空状态 -->
                <el-empty v-if="!activityLoading && !recentTalks.length && !recentComments.length" description="暂无动态记录" />
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.user-center {
  padding: 20px 0 40px;

  &__layout {
    display: flex;
    gap: 20px;
    max-width: 900px;
    margin: 0 auto;
  }

  &__sidebar {
    width: 260px;
    flex-shrink: 0;
  }

  &__main {
    flex: 1;
    min-width: 0;
  }
}

// 个人信息卡片
.profile-card {
  text-align: center;

  &__avatar {
    margin-bottom: 12px;

    .el-avatar {
      border: 3px solid var(--el-border-color-lighter);
    }
  }

  &__name {
    font-size: 18px;
    font-weight: 600;
    margin: 0 0 8px;
    color: var(--el-text-color-primary);
  }

  &__bio {
    font-size: 13px;
    color: var(--el-text-color-secondary);
    margin: 0 0 16px;
    line-height: 1.5;
    padding: 0 10px;
  }

  &__stats {
    display: flex;
    justify-content: space-around;
    padding: 12px 0;

    .stat-item {
      display: flex;
      flex-direction: column;
      align-items: center;

      .stat-value {
        font-size: 20px;
        font-weight: 700;
        color: var(--el-color-primary);
      }

      .stat-label {
        font-size: 12px;
        color: var(--el-text-color-secondary);
        margin-top: 4px;
      }
    }
  }

  &__info {
    text-align: left;
    padding: 0 10px;

    .info-item {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 6px 0;
      font-size: 13px;
      color: var(--el-text-color-regular);

      i {
        font-size: 14px;
        color: var(--el-text-color-secondary);
      }
    }
  }
}

// 编辑表单
.edit-form {
  max-width: 500px;
  padding: 10px 0;
}

// 动态区域
.activity-section {
  min-height: 200px;
}

.activity-block {
  margin-bottom: 24px;

  &__title {
    font-size: 15px;
    font-weight: 600;
    color: var(--el-text-color-primary);
    margin: 0 0 12px;
    display: flex;
    align-items: center;
    gap: 6px;

    i {
      color: var(--el-color-primary);
    }
  }
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.activity-item {
  padding: 10px 14px;
  background: var(--el-fill-color-lighter);
  border-radius: 8px;
  transition: background 0.2s;

  &:hover {
    background: var(--el-fill-color-light);
  }

  &__content {
    font-size: 14px;
    color: var(--el-text-color-primary);
    line-height: 1.5;
    margin-bottom: 6px;

    .comment-target {
      color: var(--el-text-color-secondary);
      font-size: 13px;
    }
  }

  &__meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 12px;
    color: var(--el-text-color-placeholder);

    .like-count {
      display: flex;
      align-items: center;
      gap: 3px;

      i {
        font-size: 12px;
      }
    }
  }
}

.activity-link {
  color: var(--el-color-primary);
  text-decoration: none;

  &:hover {
    text-decoration: underline;
  }
}

// 响应式
@media screen and (max-width: 768px) {
  .user-center {
    &__layout {
      flex-direction: column;
    }

    &__sidebar {
      width: 100%;
    }
  }
}
</style>
