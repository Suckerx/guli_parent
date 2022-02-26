package com.sucker.eduorder.service;

import com.sucker.eduorder.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author sucker
 * @since 2022-02-19
 */
public interface OrderService extends IService<Order> {

    //生成订单,远程调用
    String createOrders(String courseId, String memberIdByJwtToken);
}
