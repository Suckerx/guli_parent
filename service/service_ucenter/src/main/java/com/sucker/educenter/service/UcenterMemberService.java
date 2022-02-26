package com.sucker.educenter.service;

import com.sucker.educenter.entity.UcenterMember;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sucker.educenter.entity.vo.RegisterVo;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author sucker
 * @since 2022-02-14
 */
public interface UcenterMemberService extends IService<UcenterMember> {

    //登录
    String login(UcenterMember member);

    //注册
    void register(RegisterVo registerVo);

    //根据openid查询用户
    UcenterMember getOpenIdMember(String openid);

    //查询m某一天注册人数
    Integer countRegister(String day);
}
