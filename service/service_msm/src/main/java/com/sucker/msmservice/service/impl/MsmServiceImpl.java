package com.sucker.msmservice.service.impl;

import com.sucker.msmservice.service.MsmService;
import com.sucker.msmservice.utils.MsmConstantUtils;
import com.sucker.msmservice.utils.RandomUtil;
import com.tencentcloudapi.common.Credential;
import lombok.extern.slf4j.Slf4j;

//导入可选配置类
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;

// 导入对应SMS模块的client
import com.tencentcloudapi.sms.v20210111.SmsClient;

// 导入要请求接口对应的request response类
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class MsmServiceImpl implements MsmService {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //发送短信
    @Override
    public boolean send(String phone) {
        if(ObjectUtils.isEmpty(phone)) return false;
        try {
            //这里是实例化一个Credential，也就是认证对象，参数是密钥对；你要使用肯定要进行认证
            Credential credential = new Credential(MsmConstantUtils.SECRET_ID, MsmConstantUtils.SECRET_KEY);

            //HttpProfile这是http的配置文件操作，比如设置请求类型(post,get)或者设置超时时间了、还有指定域名了
            //最简单的就是实例化该对象即可，它的构造方法已经帮我们设置了一些默认的值
            HttpProfile httpProfile = new HttpProfile();
            //这个setEndpoint可以省略的
            httpProfile.setEndpoint(MsmConstantUtils.END_POINT);

            //实例化一个客户端配置对象,这个配置可以进行签名（使用私钥进行加密的过程），对方可以利用公钥进行解密
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            //实例化要请求产品(以sms为例)的client对象
            SmsClient smsClient = new SmsClient(credential, "ap-guangzhou", clientProfile);

            //实例化request封装请求信息
            SendSmsRequest request = new SendSmsRequest();
            String[] phoneNumber = {"+86"+phone};
            request.setPhoneNumberSet(phoneNumber);     //设置手机号
            request.setSmsSdkAppId(MsmConstantUtils.APP_ID);
            request.setSignName(MsmConstantUtils.SIGN_NAME);
            request.setTemplateId(MsmConstantUtils.TEMPLATE_ID);
            //生成随机验证码，我的模板内容的参数只有一个
            //从redis中获取验证码，如果取到就直接返回
            String code = redisTemplate.opsForValue().get(phone);
            boolean flag = false;
            String verificationCode = null;
            if(!ObjectUtils.isEmpty(code)) verificationCode = code;
            else {
                verificationCode = RandomUtil.getSixBitRandom();
                flag = true;
            }
            String[] templateParamSet = {verificationCode};
            request.setTemplateParamSet(templateParamSet);

            //发送短信
            SendSmsResponse response = smsClient.SendSms(request);
            log.info(SendSmsResponse.toJsonString(response));
            if(flag){
                //表示redis没有存储该验证码，将其放入redis中,设置有效时间
                redisTemplate.opsForValue().set(phone,verificationCode,5, TimeUnit.MINUTES);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
