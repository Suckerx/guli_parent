package com.sucker.educenter.controller;

import com.google.gson.Gson;
import com.sucker.commonutils.JwtUtils;
import com.sucker.commonutils.R;
import com.sucker.educenter.entity.UcenterMember;
import com.sucker.educenter.service.UcenterMemberService;
import com.sucker.educenter.utils.ConstantWxUtils;
import com.sucker.educenter.utils.HttpClientUtils;
import com.sucker.servicebase.exceptionhandler.GuliException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.util.HashMap;

@Api(description = "微信扫码登录")
@Controller
//@CrossOrigin
@RequestMapping("/api/ucenter/wx")
public class WxApiController {

    @Autowired
    private UcenterMemberService memberService;

    //获取扫描人信息，添加数据
    @ApiOperation(value = "获取扫描人信息，添加数据")
    @GetMapping("callback")
    public String callback(String code ,String state){
        try {
            // 获取code值，类似验证码

            //带着code请求 微信固定地址，得到两个固定值 access_token 和 openid
            String baseAccessTokenUrl =
                    "https://api.weixin.qq.com/sns/oauth2/access_token" +
                    "?appid=%s" +
                    "&secret=%s" +
                    "&code=%s" +
                    "&grant_type=authorization_code";

            //拼接三个参数 id 密钥 code值
            String accessTokenUrl = String.format(baseAccessTokenUrl, ConstantWxUtils.WX_OPEN_APP_ID,
                    ConstantWxUtils.WX_OPEN_APP_SECRET, code);

            //请求这个拼接好的地址，得到返回两个值 access_token 和 openid
            //使用httpclient发送请求，模拟浏览器得到返回结果（古老技术，但是能用）
            String accessTokenInfo = HttpClientUtils.get(accessTokenUrl);

            //从accessTokenInfo中取出 access_token 和 openid
            //把这个json字符串转换为map集合，根据key取出值
            Gson gson = new Gson();
            HashMap mapAccessToken = gson.fromJson(accessTokenInfo, HashMap.class);
            String accessToken = (String) mapAccessToken.get("access_token");
            String openid = (String) mapAccessToken.get("openid");


            //把扫码人信息添加到数据库
            //判断数据库是否有相同数据信息
            UcenterMember member = memberService.getOpenIdMember(openid);
            if(member == null){

                //访问微信的资源服务器，获取用户信息
                String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                        "?access_token=%s" +
                        "&openid=%s";
                String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);
                String userInfo = HttpClientUtils.get(userInfoUrl);
                //获取返回的userInfo字符串扫描人信息
                HashMap userInfoMap = gson.fromJson(userInfo, HashMap.class);
                String nickname = (String) userInfoMap.get("nickname");//昵称
                String headimgurl = (String) userInfoMap.get("headimgurl");//头像

                member = new UcenterMember();
                member.setOpenid(openid);
                member.setNickname(nickname);
                member.setAvatar(headimgurl);
                memberService.save(member);
            }

            //使用jwt根据member对象生成token字符串
            String jwtToken = JwtUtils.getJwtToken(member.getId(), member.getNickname());
            //最后，返回首页通过路径传递token字符串,解决跨域问题
            //最后返回首页
            return "redirect:http://localhost:3000?token="+jwtToken;

        } catch (Exception e) {
            e.printStackTrace();
            throw new GuliException(20001,"获取扫描人信息失败");
        }


    }


    //生成微信二维码
    @ApiOperation(value = "生成微信二维码")
    @GetMapping("login")
    public String getWxCode(){
        //固定地址，后面拼接参数,但是不建议这么做
        //String url = "https://open.weixin.qq.com/connect/qrconnect?appid="+ ConstantWxUtils.WX_OPEN_APP_ID

        // 微信开放平台授权baseUrl  %s相当于占位符
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";

        // 回调地址处理，要求用URLEncoder处理
        String redirectUrl = ConstantWxUtils.WX_OPEN_REDIRECT_URL; //获取业务服务器重定向地址
        try {
            redirectUrl = URLEncoder.encode(redirectUrl, "UTF-8"); //url编码
        } catch (Exception e) {
            throw new GuliException(20001, e.getMessage());
        }

        String url = String.format(baseUrl,ConstantWxUtils.WX_OPEN_APP_ID,
                redirectUrl,"sucker");

        //请求微信地址
        return "redirect:"+url;
    }

}
