package com.sucker.eduorder.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.sucker.commonutils.JwtUtils;
import com.sucker.commonutils.R;
import com.sucker.eduorder.entity.Order;
import com.sucker.eduorder.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author sucker
 * @since 2022-02-19
 */
@RestController
@RequestMapping("/eduorder/order")
//@CrossOrigin
public class OrderController {

    @Autowired
    private OrderService orderService;

    //生成订单,远程调用
    @ApiOperation(value = "生成订单")
    @PostMapping("createOrder/{courseId}")
    public R saveOrder(@PathVariable String courseId, HttpServletRequest request){
        //创建订单，返回订单号
        String orderNo = orderService.createOrders(courseId,JwtUtils.getMemberIdByJwtToken(request));
        return R.ok().data("orderId",orderNo);
    }


    //根据订单id查询订单信息
    @ApiOperation(value = "根据订单id查询订单信息")
    @GetMapping("getOrderInfo/{orderId}")
    public R getOrderInfo(@PathVariable String orderId){
        Order order = orderService.getOne(new QueryWrapper<Order>().eq("order_no", orderId));
        return R.ok().data("item",order);
    }


    //根据课程id和用户id查询订单表中订单状态
    @ApiOperation(value = "根据课程id和用户id查询订单表中订单状态")
    @GetMapping("isBuyCourse/{courseId}/{memberId}")
    public Boolean isBuyCourse(@PathVariable String courseId,@PathVariable String memberId){
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id",courseId);
        wrapper.eq("member_id",memberId);
        wrapper.eq("status",1);//支付状态，1 表示已经支付
        int count = orderService.count(wrapper);
        if(count > 0) return true;//已经支付
        else return false;
    }


}

