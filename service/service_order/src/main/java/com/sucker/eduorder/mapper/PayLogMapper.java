package com.sucker.eduorder.mapper;

import com.sucker.eduorder.entity.PayLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 支付日志表 Mapper 接口
 * </p>
 *
 * @author sucker
 * @since 2022-02-19
 */
@Mapper
public interface PayLogMapper extends BaseMapper<PayLog> {

}
