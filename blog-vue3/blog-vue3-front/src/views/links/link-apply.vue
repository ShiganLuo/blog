<script setup lang="ts">
import { ref, reactive, h, watch, nextTick } from "vue";
import type { FormInstance, FormRules } from "element-plus";
import { LinkService } from "@/api/linksApi";
import { ElNotification } from "element-plus";

import { UserService } from "@/api/userApi";

import Upload from "@/components/Upload/upload.vue";
import { _getLocalItem, _removeLocalItem } from "@/utils/tool";
import { useUserStore } from "@/stores/index";
import { storeToRefs } from "pinia";

// 定义 props 的类型
interface Props {
  show: boolean;
  type: "add" | "edit";
}

const props = withDefaults(defineProps<Props>(), {
  show: false,
  type: "add",
});

const emit = defineEmits(["update:show"]);

const { getUserInfo } = storeToRefs(useUserStore());

// 表单验证规则的自定义校验器
const urlV = (rule: any, value: string, cb: (error?: Error) => void) => {
  if (!value) {
    return cb(new Error("请输入网站地址"));
  } else if (value.indexOf("http") === -1) {
    return cb(new Error("请输入带http://或https://的网站地址"));
  } else {
    cb();
  }
};

const loading = ref<boolean>(false);
const dialogVisible = ref<boolean>(false);

// 表单引用，使用类型断言
const formRef = ref<FormInstance>();


// 定义表单数据类型
interface FriendLinkData {
  id?: number | string; // id可能存在
  site_name: string;
  site_desc: string;
  site_url: string;
  site_avatar: string;
  bgList: any[]; // Upload组件的文件列表，类型不确定时用any[]
  status: number;
  user_id?: number | string;
}

const form = reactive<FriendLinkData>({
  site_name: "",
  site_desc: "",
  site_url: "",
  site_avatar: "",
  bgList: [],
  status: 1,
  user_id: undefined,
});

const primaryForm = reactive<FriendLinkData>({ ...form });

// 定义表单校验规则的类型
const rules = reactive<FormRules<FriendLinkData>>({
  site_name: [{ required: true, message: "请输入网站名称", trigger: "blur" }],
  site_desc: [{ required: true, message: "请输入网站描述", trigger: "blur" }],
  site_url: [{ required: true, validator: urlV, trigger: "blur" }],
});

const handleClose = () => {
  emit("update:show", false);
};

// 申请/修改友链
const applyLinks = async () => {
  // 使用可选链操作符和类型守卫来确保formRef.value存在
  if (!formRef.value) return;

  try {
    const valid = await formRef.value.validate();
    if (valid) {
      loading.value = true;
      // 检查bgList是否存在文件且不是已有的
      if (form.bgList.length && !form.bgList[0].id) {
        // 类型断言，确保bgList[0]是一个File或Blob对象
        const formData = new FormData();
        formData.append('file', form.bgList[0].raw);
        const img = await UserService.imgUpload(formData);
        if (img.code === 200 && img.result) {
          const url = img.result;
          form.site_avatar = url;
        }
      }
      form.status = 1;
      let res;
      if (form.id) {
        res = await LinkService.updateFriendLinks(form);
      } else {
        form.user_id = getUserInfo.value.id;
        res = await LinkService.addFriendLinks(form);
      }

      if (res && res.code === 200) {
        ElNotification({
          offset: 60,
          title: "提示",
          message: h(
            "div",
            { style: "color: #7ec050; font-weight: 600;" },
            `${form.id ? "修改" : "申请"}成功，等待博主审核通过`
          ),
        });
        Object.assign(form, primaryForm);
        handleClose();
      } else {
        ElNotification({
          offset: 60,
          title: "错误提示",
          message: h("div", { style: "color: #f56c6c; font-weight: 600;" }, res?.message || "未知错误"),
        });
      }
    }
  } catch (err) {
    console.error(err);
    ElNotification({
      offset: 60,
      title: "错误提示",
      message: h("div", { style: "color: #f56c6c; font-weight: 600;" }, "未知错误"),
    });
  } finally {
    loading.value = false;
  }
};

watch(
  () => props.show,
  (newV) => {
    dialogVisible.value = newV;
    if (newV) {
      // 打开弹窗时，先重置表单再加载数据，确保更新操作的数据不会残留
      Object.assign(form, primaryForm);
      // 等待DOM更新，然后重置表单校验
      nextTick(() => {
        formRef.value?.clearValidate();
      });

      const item = _getLocalItem("blog-link-update");
      if (item) {
        // site_avatar可能为null
        if (item.site_avatar) {
          form.bgList = [
            {
              id: 1,
              url: item.site_avatar,
              name: item.site_avatar.split("/").slice(-1)[0], 
            },
          ];
        }
        // 将本地存储的数据赋给表单
        Object.assign(form, item);
      }
    } else {
      _removeLocalItem("blog-link-update");
    }
  }
);
</script>

<template>
  <el-dialog v-model="dialogVisible" width="120" :before-close="handleClose">
    <template #header>
      <h1>{{ props.type === "add" ? "友链申请" : "友链修改" }}</h1>
    </template>
    <div class="apply-box flex flex-col justify-between items-center">
      <el-form class="apply-form" ref="formRef" :model="form" :rules="rules" label-suffix=":">
        <el-form-item label="网站头像" prop="site_avatar">
          <div class="w-[100%] flex justify-center">
            <Upload
              v-model:file-list="form.bgList"
              :width="150"
              :height="150"
              :limit="1"
              :multiple="false"
              :preview="false"
            />
          </div>
        </el-form-item>

        <el-form-item label="网站名称" prop="site_name">
          <el-input v-model="form.site_name" placeholder="请输入网站名称" clearable />
        </el-form-item>
        <el-form-item label="网站描述" prop="site_desc">
          <el-input
            type="textarea"
            v-model="form.site_desc"
            maxlength="125"
            resize="none"
            :autosize="{ minRows: 2, maxRows: 3 }"
            show-word-limit
            placeholder="请输入网站描述"
            clearable
          />
        </el-form-item>
        <el-form-item label="网站地址" prop="url">
          <el-input v-model="form.site_url" placeholder="请输入网站地址" clearable />
        </el-form-item>
      </el-form>
      <div class="pos">
        <el-button :disabled="loading" :loading="loading" class="apply-button" @click="applyLinks">
          {{ loading ? "努力上传中..." : props.type === "add" ? "申请友链" : "修改友链" }}
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<style lang="scss" scoped>
.pos {
  width: 100%;
  margin: 5px 0 10px 0;
}
.apply-form {
  width: 100%;
}

:deep(.el-form-item) {
  padding: 15px 0;
}

.apply-button {
  width: 100%;
}

:deep(.el-input__wrapper) {
  height: 40px;
  line-height: 40px;
}

:deep(.el-upload--picture-card) {
  width: 150px !important;
  height: 150px !important;
  border-radius: 8px !important;
}

:deep(.el-upload-list__item) {
  width: 150px !important;
  height: 150px !important;
  border-radius: 8px !important;
  margin: 0;
}

:deep(.el-upload-list--picture-card) {
  width: 150px !important;
  height: 150px !important;
  border-radius: 8px !important;
}

// mobile
@media screen and (max-width: 768px) {
  .apply-box {
    height: 88vh;
  }
}
</style>