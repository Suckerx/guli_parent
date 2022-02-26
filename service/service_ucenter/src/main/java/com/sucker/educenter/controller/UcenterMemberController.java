package com.sucker.educenter.controller;


import com.sucker.commonutils.JwtUtils;
import com.sucker.commonutils.R;
import com.sucker.commonutils.ordervo.UcenterMemberOrder;
import com.sucker.educenter.entity.UcenterMember;
import com.sucker.educenter.entity.vo.RegisterVo;
import com.sucker.educenter.service.UcenterMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 会员表 前端控制器
 * </p>
 *
 * @author sucker
 * @since 2022-02-14
 */
@Api(description = "用户中心管理")
@RestController
@RequestMapping("/educenter/member")
//@CrossOrigin
public class UcenterMemberController {

    @Autowired
    private UcenterMemberService memberService;

    //登录
    @ApiOperation(value = "登录")
    @PostMapping("login")
    public R loginUser(@RequestBody UcenterMember member){
        //menmber对象封装手机号和密码
        //返回token值，使用jwt生成
        String token = memberService.login(member);
        return R.ok().data("token",token);
    }


    //注册
    @ApiOperation(value = "注册")
    @PostMapping("register")
    public R registerUser(@RequestBody RegisterVo registerVo){
        memberService.register(registerVo);
        return R.ok();
    }


    //根据token获取用户信息，前端将token放到request请求的header中
    @ApiOperation(value = "根据token获取用户信息")
    @GetMapping("getMemberInfo")
    public R getMemberInfo(HttpServletRequest request){
        //调用jwt工具类方法，根据request对象获取头信息，返回用户id
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        //根据用户id查询用户信息
        UcenterMember member = memberService.getById(memberId);
        member.setPassword("xxxxxxx");//敏感数据处理
        return R.ok().data("userInfo",member);
    }


    //根据用户id得到用户信息
    @ApiOperation(value = "根据用户id得到用户信息")
    @PostMapping("getUserInfoOrder/{id}")
    public UcenterMemberOrder getUserInfoOrder(@PathVariable String id){
        UcenterMember member = memberService.getById(id);
        //把member对象里面的值复制给UcenterMemberOrder对象
        UcenterMemberOrder ucenterMemberOrder = new UcenterMemberOrder();
        BeanUtils.copyProperties(member,ucenterMemberOrder);
        return ucenterMemberOrder;
    }


    //查询m某一天注册人数
    @ApiOperation(value = "查询m某一天注册人数")
    @GetMapping("countRegister/{day}")
    public R countRegister(@PathVariable String day){
        Integer count = memberService.countRegister(day);
        return R.ok().data("countRegister",count);
    }


}

