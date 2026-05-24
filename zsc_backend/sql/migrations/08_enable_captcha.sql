-- ============================================
-- 迁移: 08_enable_captcha.sql
-- 说明: 正式启用图片验证码，登录/注册时需输入验证码
-- ============================================

UPDATE sys_config SET config_value = 'true', update_time = NOW()
WHERE config_key = 'sys.account.captchaEnabled';
