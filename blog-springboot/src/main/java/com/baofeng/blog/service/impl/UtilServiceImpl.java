package com.baofeng.blog.service.impl;

import com.baofeng.blog.service.UtilService;
import com.baofeng.blog.vo.common.Util.CaptchaResponse;
import com.baofeng.blog.enums.ResultCodeEnum;
import com.baofeng.blog.vo.ApiResponse;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import java.awt.image.BufferedImage;
import java.util.UUID;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.util.Base64;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
@Service
public class UtilServiceImpl implements UtilService {
    // 与登录接口集成待完成
    private final DefaultKaptcha captchaProducer;
    private static final Logger logger = LoggerFactory.getLogger(UtilServiceImpl.class);

    public UtilServiceImpl(DefaultKaptcha captchaProducer) {
        this.captchaProducer = captchaProducer;
    }
    @Override
    public ApiResponse<CaptchaResponse> getCaptcha() {
        String text = captchaProducer.createText();
        BufferedImage image = captchaProducer.createImage(text);
        String uuid = UUID.randomUUID().toString();
        CaptchaResponse captchaResponse = new CaptchaResponse();
        captchaResponse.setCaptchaEnabled(true);
        captchaResponse.setUuid(uuid);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", bos);
        } catch (IOException e) {
            logger.error("生成验证码失败", e);
            return ApiResponse.error(ResultCodeEnum.INTERNAL_SERVER_ERROR, "生成验证码失败" );
        }

        // 转 Base64
        String imgBase64 = Base64.getEncoder().encodeToString(bos.toByteArray());
        captchaResponse.setImg("data:image/jpg;base64," + imgBase64);
        return ApiResponse.success(captchaResponse);
    }
}
