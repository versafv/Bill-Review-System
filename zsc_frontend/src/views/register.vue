<template>
  <div class="register">
    <el-form ref="registerRef" :model="registerForm" :rules="registerRules" class="register-form">
      <div class="login-logo">
        <img src="@/assets/logo/logo.svg" alt="logo" />
        <h3 class="title">票据报销系统</h3>
      </div>
      <el-form-item prop="email">
        <el-input
          v-model="registerForm.email"
          size="large"
          auto-complete="off"
          placeholder="邮箱"
        >
          <template #prefix><svg-icon icon-class="email" class="el-input__icon input-icon" /></template>
        </el-input>
      </el-form-item>
      <el-form-item prop="note">
        <el-input
          v-model="registerForm.note"
          type="textarea"
          :rows="3"
          size="large"
          placeholder="申请说明（选填）"
          :maxlength="500"
          show-word-limit
        />
      </el-form-item>
      <el-form-item prop="roleKey">
        <el-radio-group v-model="registerForm.roleKey" size="large">
          <el-radio-button value="user">
            <svg-icon icon-class="user" style="margin-right: 4px;" />普通用户
          </el-radio-button>
          <el-radio-button value="reviewer">
            <svg-icon icon-class="peoples" style="margin-right: 4px;" />票据审核员
          </el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="large"
          type="primary"
          style="width:100%;"
          @click.prevent="handleRegister"
        >
          <span v-if="!loading">提交申请</span>
          <span v-else>提交中...</span>
        </el-button>
        <div style="float: right;">
          <router-link class="link-type" :to="'/login'">使用已有账户登录</router-link>
        </div>
      </el-form-item>
    </el-form>
    <div class="el-register-footer">
      <span>{{ footerContent }}</span>
    </div>
  </div>
</template>

<script setup>
import { ElMessageBox } from "element-plus"
import { submitRegisterRequest } from "@/api/login"
const footerContent = 'Copyright © 2026 票据报销系统. All Rights Reserved.'
const router = useRouter()
const { proxy } = getCurrentInstance()

const registerForm = ref({
  email: "",
  note: "",
  roleKey: "user"
})

const registerRules = {
  email: [
    { required: true, trigger: "blur", message: "请输入邮箱" },
    { type: "email", message: "邮箱格式不正确", trigger: "blur" }
  ],
  roleKey: [{ required: true, trigger: "change", message: "请选择注册角色" }]
}

const loading = ref(false)

function handleRegister() {
  proxy.$refs.registerRef.validate(valid => {
    if (valid) {
      loading.value = true
      submitRegisterRequest(registerForm.value).then(res => {
        ElMessageBox.alert("您的注册申请已提交，请等待管理员审核。审核通过后会将账号信息发送至您的邮箱。", "系统提示", {
          type: "success",
          confirmButtonText: "知道了"
        }).then(() => {
          router.push("/login")
        }).catch(() => {})
      }).catch(() => {
        loading.value = false
      })
    }
  })
}
</script>

<style lang='scss' scoped>
.register {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url("../assets/images/login-background.jpg");
  background-size: cover;
}
.login-logo {
  text-align: center;
  margin-bottom: 30px;
}
.login-logo img {
  width: 64px;
  height: 64px;
  display: block;
  margin: 0 auto 12px;
}
.title {
  margin: 0;
  text-align: center;
  color: #444;
  font-size: 22px;
}
.register-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;
  .el-input {
    height: 40px;
    input {
      height: 40px;
    }
  }
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 0px;
  }
}
.el-register-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}
</style>
