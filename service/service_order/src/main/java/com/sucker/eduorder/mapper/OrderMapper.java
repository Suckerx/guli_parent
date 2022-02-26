package com.sucker.eduorder.mapper;

import com.sucker.eduorder.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 订单 Mapper 接口
 * </p>
 *
 * @author sucker
 * @since 2022-02-19
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}
