package com.sucker.educenter.mapper;

import com.sucker.educenter.entity.UcenterMember;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 会员表 Mapper 接口
 * </p>
 *
 * @author sucker
 * @since 2022-02-14
 */
@Mapper
public interface UcenterMemberMapper extends BaseMapper<UcenterMember> {

    //查询m某一天注册人数
    Integer countRegister(String day);
}
