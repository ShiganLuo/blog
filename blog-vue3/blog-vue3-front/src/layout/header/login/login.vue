<script setup lang="ts">
import { ref, reactive, watch, h, nextTick } from "vue";
import type { FormInstance, FormRules, FormItemRule } from "element-plus";
import { ElMessage } from "element-plus";
import { ElNotification } from "element-plus";
import { UserService } from "@/api/userApi";
import { useUserStore } from "@/stores/index";
import { storeToRefs } from "pinia";
import { getWelcomeSay, _getLocalItem, _setLocalItem, _removeLocalItem } from "@/utils/tool";
import blogAvatar from "@/assets/img/blogAvatar.png";
import { _encrypt, _decrypt } from "@/utils/encipher";

interface LoginForm {
  username: string;
  password: string;
}

interface RegisterForm {
  username: string;
  email: string;
  verifyCode: string;
  password1: string;
  password2: string;
}

const userStore = useUserStore();
const { getShowLogin } = storeToRefs(userStore);

const loginFormRef = ref<FormInstance>();
const loginForm = reactive<LoginForm>({ username: "", password: "" });
const primaryLoginForm: LoginForm = { username: "", password: "" };

const isRemember = ref<boolean>(false);
const registerFormRef = ref<FormInstance>();
const registerForm = reactive<RegisterForm>({
  username: "",
  email: "",
  verifyCode: "",
  password1: "",
  password2: "",
  
});
const primaryRegisterForm: RegisterForm = { username: "",  email: "", verifyCode: "", password1: "", password2: ""};

const isLogin = ref<boolean>(true);
const showDialog = ref<boolean>(false);

const usernameV: FormItemRule["validator"] = (rule, value, cb) => {
  if (!value) cb(new Error("请输入用户账号"));
  else if (value.length > 16 || value.length < 3) cb(new Error("用户账号长度应该在3-16之间"));
  else cb();
};

const REGEXP_PWD = /^(?![0-9]+$)(?![a-z]+$)(?![A-Z]+$)(?!([^(0-9a-zA-Z)]|[()])+$)(?!^.*[\u4E00-\u9FA5].*$)([^(0-9a-zA-Z)]|[()]|[a-z]|[A-Z]|[0-9]){6,18}$/;

const password1V: FormItemRule["validator"] = (rule, value, cb) => {
  if (!value) cb(new Error("请输入密码"));
  else if (!REGEXP_PWD.test(value)) cb(new Error("密码格式应为6-18位数字、字母、符号的任意两种组合"));
  else cb();
};

const password2V: FormItemRule["validator"] = (rule, value, cb) => {
  if (!value) cb(new Error("请输入二次确认密码"));
  else if (value !== registerForm.password1) cb(new Error("两次密码不一致"));
  else cb();
};

const loginRules: FormRules = {
  username: [{ required: true, message: "请输入用户账号", trigger: "blur" }],
  password: [{ required: true, message: "请输入用户密码", trigger: "blur" }],
};

const registerRules: FormRules = {
  username: [{ required: true, validator: usernameV, trigger: "blur" }],
  password1: [{ required: true, validator: password1V, trigger: "blur" }],
  password2: [{ required: true, validator: password2V, trigger: "blur" }],
};

const welcome = (id: number, nick_name: string) => {
  let msg = getWelcomeSay(nick_name);
  // if (id === 3) msg = "某某光临，真是三生有幸";
  ElNotification({
    offset: 60,
    title: "欢迎～",
    message: h("div", { style: "font-weight: 600;" }, msg),
  });
};

const userRegister = async () => {
  await registerFormRef.value?.validate(async (valid) => {
    if (valid) {
      const register = {
        username: registerForm.username,
        password: registerForm.password1,
        email: registerForm.email,
        verifyCode: registerForm.verifyCode
      };
      const res = await UserService.reqRegister(register);
      if (res?.code === 200) {
        await userLogin("register");
      } else {
        ElNotification({ offset: 60, title: "错误提示", message: h("div", { style: "color: #f56c6c; font-weight: 600;" }, res.message) });
      }
    }
  });
};

// 用户登录
const userLogin = async (type?: "register") => {
  // 如果是用户注册以后进行登录得，参数需要整合一下
  if (type === "register") {
    const loginForm = {
      username: registerForm.username,
      password: registerForm.password1,
    };
    await onLogin(loginForm, "register");
  } else {
    await loginFormRef.value?.validate(async (valid) => {
      if (valid) await onLogin(loginForm);
    });
  }
};

const cooldown = ref(0)

  const startCooldown = () => {
    cooldown.value = 60
    const timer = setInterval(() => {
      cooldown.value--
      if (cooldown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  }

const getVerifyCode = async () => {
  try {
    const res = await UserService.sendEmailCode(registerForm.email)
    if (res.code === 200) {
      ElMessage.success("验证码发送成功")
      startCooldown()
    }
  } catch (error) {
    console.error(error)
  }
}

const onLogin = async (form: LoginForm, type: "login" | "register" = "login") => {
  const res = await UserService.reqLogin(form);
  if (res?.code === 200) {
    await userStore.setToken(res.result.accessToken);
    // 用户注册后登录
    if (type === "register" || isRemember.value) {
      _setLocalItem("loginForm", _encrypt(form));
    } else {
      _removeLocalItem("loginForm");
    }
    await userStore.setUserInfo(res.result);
    Object.assign(loginForm, primaryLoginForm);
    Object.assign(registerForm, primaryRegisterForm);
    handleClose();
    await welcome(res.result.id, res.result.nickname);
    ElNotification({ offset: 60, title: "提示", message: h("div", { style: "color: #7ec050; font-weight: 600;" }, type === "login" ? "登录成功" : "自动登录成功") });

  } else {
    ElNotification({ offset: 60, title: "错误提示", message: h("div", { style: "color: #f56c6c; font-weight: 600;" }, res.message) });
  }
};

const toLogin = () => (isLogin.value = true);
const toRegister = () => (isLogin.value = false);
const submit = async () => (isLogin.value ? await userLogin() : await userRegister());
const handleClose = () => userStore.setShowLogin(false);

watch(
  () => isLogin.value,
  (newV) => {
    if (newV) loginFormRef.value?.resetFields();
    else registerFormRef.value?.resetFields();
  },
  { immediate: true }
);

watch(
  () => getShowLogin.value,
  (newV) => {
    showDialog.value = newV;
    if (newV) {
      isLogin.value = true;
      nextTick(() => {
        loginFormRef.value?.resetFields();
        registerFormRef.value?.resetFields();
        const form = _decrypt(_getLocalItem("loginForm"));
        if (form) {
          isRemember.value = true;
          Object.assign(loginForm, JSON.parse(form));
        }
      });
    }
  },
  { immediate: true }
);
</script>

<template>
  <el-dialog v-model="showDialog" width="120" :before-close="handleClose">
    <template #header>
      <h1>{{ isLogin ? "登录" : "注册" }}</h1>
    </template>
    <div class="login-box">
      <div>
        <div v-if="isLogin" class="no-account">
          没有账号？<span class="line" @click="toRegister">去注册</span>
        </div>
        <div v-else class="no-account">
          已有账号？<span class="line" @click="toLogin">去登录</span>
        </div>
      </div>
      <el-form
        v-if="isLogin"
        class="login-register-form"
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
      >
        <div class="!w-[100%] !h-[6rem]">
          <el-image style="width: 80px; height: 80px" :src="blogAvatar" fit="cover" />
        </div>
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            :style="{ width: '100%' }"
            placeholder="请输入用户名"
            clearable
            @keyup.enter="submit"
          />
        </el-form-item>
        <el-form-item prop="password" @keyup.enter="submit">
          <el-input
            v-model="loginForm.password"
            show-password
            :style="{ width: '100%' }"
            placeholder="请输入密码"
            clearable
          />
        </el-form-item>
        <el-form-item class="remember-me">
          <div class="flex justify-between items-center w-[100%]">
            <el-checkbox v-model="isRemember">记住我</el-checkbox>
            <span></span>
          </div>
        </el-form-item>
      </el-form>
      <el-form
        v-else
        class="login-register-form"
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            :style="{ width: '100%' }"
            placeholder="请输入您的用户名"
            clearable
          />
        </el-form-item>
        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            :style="{ width: '199%' }"
            placeholder="请输入您的邮箱"
            clearable
          />
        </el-form-item>
        <el-form-item prop="verifyCode">
          <div style="display: flex; width:100%">
            <el-input
              v-model.trim="registerForm.verifyCode"
              size="large",
              placeholder="请输入验证码"
              style="width: 65%"
            />
            <el-button
            type="primary"
            size="large"
            style="width: 33%; margin-left: 2%; height: 44px"
            @click="getVerifyCode"
            :disabled="!registerForm.email || cooldown > 0"
            >
            {{ cooldown > 0 ? `${cooldown}s` : "获取验证码" }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item prop="password1">
          <el-input
            show-password
            v-model="registerForm.password1"
            :style="{ width: '100%' }"
            placeholder="请输入您的密码"
            clearable
          />
        </el-form-item>
        <el-form-item prop="password2">
          <el-input
            show-password
            v-model="registerForm.password2"
            :style="{ width: '100%' }"
            placeholder="请再次输入您的密码"
            clearable
            @keyup.enter="submit"
          />
        </el-form-item>
      </el-form>
      <div v-if="isLogin" class="flex justify-between items-center w-[100%] mb-3">
        <span class="apply-button" @click="submit">登录</span>
      </div>
      <div v-else class="flex justify-between items-center w-[100%] mb-3">
        <span class="apply-button" @click="submit">注册</span>
      </div>
    </div>
  </el-dialog>
</template>

<style lang="scss" scoped>
.login-register {
  &-form {
    width: 100%;
  }
}

:deep(.el-overlay){
  z-index: 10002 !important;
}

.apply-button {
  width: 100%;
  text-align: center;
}
.line {
  cursor: pointer;
  text-decoration: underline;
}
.no-account {
  font-size: 1rem;
}

:deep(.el-form-item) {
  padding: 15px 0;
}

:deep(.el-input__wrapper) {
  height: 40px;
  line-height: 40px;
}

.remember-me {
  padding: 0;
}

// pc
@media screen and (min-width: 768px) {
  .login-box {
    height: 50vh !important;
  }
}

// mobile
@media screen and (max-width: 768px) {
  .login-register-box {
    width: 80%;
  }

  .login-box {
    height: 88vh !important;
  }
}
</style>
