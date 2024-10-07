package com.example.service;

import cn.hutool.core.util.RandomUtil;
import com.example.constant.Constant;
import com.example.exception.BusinessException;
import com.example.utils.bo.EmailCodeBo;
import com.example.utils.redis.RedisProcessor;
import com.example.utils.redis.RedisTransKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.example.constant.Constant.SYMBOLS;

/**
 * [邮箱业务类]
 *
 * @author : [24360]
 * @version : [v1.0]
 * @createTime : [2024/10/3 11:05]
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSenderImpl javaMailSender;

    private final EmailCodeBo emailCodeBo;

    private final RedisProcessor redisProcessor;

    public void sendCode(String email) {
        String code = RandomUtil.randomString(SYMBOLS, 6);
        emailCodeBo.setCode(code);
        emailCodeBo.setEmail(email);
        redisProcessor.set(RedisTransKey.emailKey(email), emailCodeBo, 1, TimeUnit.DAYS);
        sendTextMailMessage(email, "博客验证码", code);
    }


    /**
     * 发送文本邮件
     * @param to 目标邮箱
     * @param subject 邮件主题
     * @param text 邮件内容
     */
    private void sendTextMailMessage(String to, String subject, String text) {
        try {
            //true 代表支持复杂的类型
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(javaMailSender.createMimeMessage(), true);
            //邮件发信人
            mimeMessageHelper.setFrom(Constant.SEND_MAILER);
            //邮件收信人  1或多个
            mimeMessageHelper.setTo(to.split(","));
            //邮件主题
            mimeMessageHelper.setSubject(subject);
            //邮件内容
            mimeMessageHelper.setText(text);
            //邮件发送时间

            mimeMessageHelper.setSentDate(new Date());

            //发送邮件
            javaMailSender.send(mimeMessageHelper.getMimeMessage());
            log.info("邮件发送成功" + Constant.SEND_MAILER + "->" + to);

        } catch (Exception e) {
            log.error("邮件发送失败" + Constant.SEND_MAILER + "->" + to, e);
            throw new BusinessException("邮件发送失败，请检查邮箱是否输入正确");
        }
    }


}
