<script setup lang="ts">
import { ref, reactive, onMounted, h } from "vue";
import type { FormInstance, FormRules, FormItemRule } from "element-plus";
import { UserService } from "@/api/userApi";
import { useUserStore } from "@/stores/index";
import Upload from "@/components/Upload/upload.vue";
import PageHeader from "@/components/PageHeader/index.vue";
import { ElNotification, ElMessageBox, type UploadFiles } from "element-plus";
import router from "@/router";


// 用户信息表单类型
interface InfoForm {
  userId: string | number;
  nickname: string;
  avatar: string;
  avatarList: any[];
}


const userStore = useUserStore();


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
const infoPreview = ref(true);
const loading = ref(false);

const infoForm = reactive<InfoForm>({
  userId: "",
  nickname: "",
  avatar: "",
  avatarList: [],
});
const primaryinfoForm = reactive<InfoForm>({ ...infoForm });

const activeName = ref("info");

const infoRules: FormRules<InfoForm> = reactive({
  nickname: [{ required: true, message: "请输入昵称", trigger: "blur" }],
  avatar: [{ required: true, validator: avatarV, trigger: "blur" }],
});

// 获取登录用户信息
const getCurrentUserInfo = async () => {
  infoForm.nickname = userStore.getUserInfo.nickname || '';
  infoForm.avatar = userStore.getUserInfo.avatar || '';
  infoForm.avatarList = [{
      id: 1,
      url: userStore.getUserInfo.avatar,
      name: userStore.getUserInfo.avatar?.split("/").slice(-1)[0], 
    },];
  infoForm.userId = userStore.getUserInfo.id || '';
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

        const res = await UserService.updateUserInfo(infoForm);
        if (res && res.code === 200) {
          ElNotification({
            offset: 60,
            title: "提示",
            message: h("div", { style: "color: #7ec050; font-weight: 600;" }, "修改成功"),
          });
          Object.assign(infoForm, primaryinfoForm);
          await getCurrentUserInfo();
          infoPreview.value = true;
        } else {
          ElNotification({
            offset: 60,
            title: "错误提示",
            message: h("div", { style: "color: #e47470" }, res.message),
          });
        }
        loading.value = false;
      });
    }
  });
};


onMounted(getCurrentUserInfo);
</script>
<template>
  <PageHeader />
  <div class="center_box">
    <div class="info">
      <el-tabs v-model="activeName">
        <el-tab-pane label="个人信息" name="info">
          <el-form
            class="info-form"
            ref="infoFormRef"
            :model="infoForm"
            :rules="infoRules"
            label-width="120px"
            label-suffix=":"
          >
            <el-form-item class="user-avatar" label="头像" prop="avatar">
              <div class="!ml-[50px]">
                <Upload
                  v-model:file-list="infoForm.avatarList"
                  :limit="1"
                  :width="100"
                  :height="100"
                  :multiple="false"
                  :preview="infoPreview"
                />
              </div>
            </el-form-item>
            <el-form-item label="昵称" prop="nickname">
              <span v-if="infoPreview"> {{ infoForm.nickname }}</span>
              <el-input
                v-else
                v-model="infoForm.nickname"
                :style="{ width: '220px' }"
                placeholder="请输入昵称"
                clearable
              />
            </el-form-item>
          </el-form>
          <div class="pos">
            <el-button v-if="infoPreview" class="apply-button" @click="infoPreview = false"
              >编辑</el-button
            >
            <div v-else>
              <el-button class="apply-button cancel" type="info" @click="infoPreview = true"
                >取消
              </el-button>
              <el-button
                class="apply-button"
                type="danger"
                :disabled="loading"
                :loading="loading"
                @click="updateInfo"
                >保存</el-button
              >
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.center_box {
  display: flex;
  flex-direction: column;
  align-items: center;
  .info {
    max-width: 400px;
    padding: 0 20px;
    .pos {
      width: 400px;
      padding: 0.8rem 0 12px 10rem;
    }

    &-button {
      height: 24px;
      padding: 0 30px;
      background-color: var(--border-color);
      border: none;
      transition: all 0.5s;
      &:hover {
        background-color: var(--primary);
      }
    }
  }
}

.cancel {
  color: rgb(255, 118, 118);
}

:deep(.el-form-item) {
  padding: 15px 0;
  width: 400px;
}

.user-avatar {
  height: 140px;
  width: 100%;
  :deep(.el-form-item__error) {
    margin-left: 5rem;
  }
  :deep(.el-upload--picture-card) {
    width: 100px !important;
    height: 100px !important;
    border-radius: 50px !important;
  }

  :deep(.el-upload-list__item) {
    width: 100px !important;
    height: 100px !important;
    border-radius: 50px !important;
    margin: 0;
  }

  :deep(.el-upload-list--picture-card) {
    width: 100px !important;
    height: 100px !important;
    border-radius: 50px !important;
  }
}

:deep(.el-tabs__nav-scroll) {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>