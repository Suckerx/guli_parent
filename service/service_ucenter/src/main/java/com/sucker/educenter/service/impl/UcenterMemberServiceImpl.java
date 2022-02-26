package com.sucker.educenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sucker.commonutils.JwtUtils;
import com.sucker.commonutils.MD5;
import com.sucker.educenter.entity.UcenterMember;
import com.sucker.educenter.entity.vo.RegisterVo;
import com.sucker.educenter.mapper.UcenterMemberMapper;
import com.sucker.educenter.service.UcenterMemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sucker.servicebase.exceptionhandler.GuliException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * <p>
 * 会员表 服务实现类
 * </p>
 *
 * @author sucker
 * @since 2022-02-14
 */
@Service
public class UcenterMemberServiceImpl extends ServiceImpl<UcenterMemberMapper, UcenterMember> implements UcenterMemberService {

    @Autowired
    private RedisTemplate<String ,String> redisTemplate;


    //登录
    @Override
    public String login(UcenterMember member) {
        String mobile = member.getMobile();
        String password = member.getPassword();

        //判断手机号和密码是否为空
        if(ObjectUtils.isEmpty(mobile) || ObjectUtils.isEmpty(password)) throw new GuliException(20001,"登录失败");

        //根据手机号查询会员
        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("mobile",mobile);
        UcenterMember mobileMember = baseMapper.selectOne(wrapper);
        if(mobileMember == null) throw new GuliException(20001,"登录失败");

        //判断密码
        //因为存储的密码是加密了的，所以要先把输入的密码加密再判断
        if(!MD5.encrypt(password).equals(mobileMember.getPassword())) throw new GuliException(20001,"登录失败");

        //判断用户是否被禁用
        if(mobileMember.getIsDisabled()) throw new GuliException(20001,"登录失败");

        //登录成功
        //生成JWT字符串
        String jwtToken = JwtUtils.getJwtToken(mobileMember.getId(), mobileMember.getNickname());
        return jwtToken;

    }

    //注册
    @Override
    public void register(RegisterVo registerVo) {
        //获取注册信息，进行校验
        String nickname = registerVo.getNickname();
        String mobile = registerVo.getMobile();
        String password = registerVo.getPassword();
        String code = registerVo.getCode();//验证码

        //校验参数
        if(ObjectUtils.isEmpty(mobile) || ObjectUtils.isEmpty(nickname) ||
                ObjectUtils.isEmpty(password) || ObjectUtils.isEmpty(code)) {
            throw new GuliException(20001,"error");
        }

        //校验验证码
        //从redis获取发送的验证码
        String mobileCode = redisTemplate.opsForValue().get(mobile);
        if(!code.equals(mobileCode)) { throw new GuliException(20001,"error"); }

        //查询数据库中是否存在相同的手机号码
        Integer count = baseMapper.selectCount(new QueryWrapper<UcenterMember>().eq("mobile", mobile));
        if(count > 0) { throw new GuliException(20001,"error"); }

        //添加注册信息到数据库
        UcenterMember member = new UcenterMember();
        BeanUtils.copyProperties(registerVo,member);
        member.setPassword(MD5.encrypt(password));//密码加密

        baseMapper.insert(member);
    }

    //根据openid查询用户
    @Override
    public UcenterMember getOpenIdMember(String openid) {
        QueryWrapper<UcenterMember> wrapper = new QueryWrapper<>();
        wrapper.eq("openid",openid);
        UcenterMember member = baseMapper.selectOne(wrapper);
        return member;
    }


    //查询m某一天注册人数
    @Override
    public Integer countRegister(String day) {
        return baseMapper.countRegister(day);
    }
}
