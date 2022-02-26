package com.sucker.eduorder.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.wxpay.sdk.WXPayUtil;
import com.sucker.eduorder.entity.Order;
import com.sucker.eduorder.entity.PayLog;
import com.sucker.eduorder.mapper.PayLogMapper;
import com.sucker.eduorder.service.OrderService;
import com.sucker.eduorder.service.PayLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sucker.eduorder.utils.HttpClient;
import com.sucker.servicebase.exceptionhandler.GuliException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 支付日志表 服务实现类
 * </p>
 *
 * @author sucker
 * @since 2022-02-19
 */
@Service
public class PayLogServiceImpl extends ServiceImpl<PayLogMapper, PayLog> implements PayLogService {

    @Autowired
    private OrderService orderService;

    //生成微信支付二维码
    @Override
    public Map createNative(String orderNo) {
        try {
            //根据订单号查询订单信息
            Order order = orderService.getOne(new QueryWrapper<Order>().eq("order_no", orderNo));

            //使用map设置二维码需要参数
            Map m = new HashMap<>();
            m.put("appid", "wx74862e0dfcf69954");
            m.put("mch_id", "1558950191");
            m.put("nonce_str", WXPayUtil.generateNonceStr());
            m.put("body", order.getCourseTitle());//课程标题
            m.put("out_trade_no", orderNo);//二维码唯一标识，用订单号
            m.put("total_fee", order.getTotalFee().multiply(new BigDecimal("100")).longValue()+"");
            m.put("spbill_create_ip", "127.0.0.1");//非本地用域名
            m.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify\n");//支付后回调地址
            m.put("trade_type", "NATIVE");

            //发送httpclient请求，传递参数Xml格式，微信支付提供的固定地址
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            //根据商户的partnerkey对map集合的参数做加密，再转换成Xml格式
            client.setXmlParam(WXPayUtil.generateSignedXml(m, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));
            client.setHttps(true);

            //得到post请求返回结果
            client.post();
            //返回的内容是xml格式，要转换为map集合再返回前端比较方便
            String xml = client.getContent();
            Map<String,String> resultMap = WXPayUtil.xmlToMap(xml);

            //最终返回数据的封装，因为还需要其他参数
            Map map = new HashMap<>();
            map.put("out_trade_no", orderNo);
            map.put("course_id", order.getCourseId());
            map.put("total_fee", order.getTotalFee());
            map.put("result_code", resultMap.get("result_code"));//返回二维码操作的状态码
            map.put("code_url", resultMap.get("code_url"));//二维码地址

            return map;

        }catch (Exception e) {
            e.printStackTrace();
            throw new GuliException(20001,"生成二维码失败");
        }

    }

    //根据订单号查询订单支付状态
    @Override
    public Map<String, String> queryPayStatus(String orderNo) {
        try {
            //1、封装参数
            Map m = new HashMap<>();
            m.put("appid", "wx74862e0dfcf69954");
            m.put("mch_id", "1558950191");
            m.put("out_trade_no", orderNo);
            m.put("nonce_str", WXPayUtil.generateNonceStr());
            //2、设置请求
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setXmlParam(WXPayUtil.generateSignedXml(m, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));
            client.setHttps(true);
            client.post();
            //3、返回第三方的数据
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);
            //6、转成Map
            //7、返回
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    //向支付表中添加记录，更新订单状态
    @Override
    public void updateOrderStatus(Map<String, String> map) {
        //从map获取订单号
        String orderNo = map.get("out_trade_no");
        //根据订单id查询订单信息
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no",orderNo);
        Order order = orderService.getOne(wrapper);

        //更新订单状态
        if(order.getStatus().intValue() == 1) return;
        order.setStatus(1);//1表示已经支付
        orderService.updateById(order);

        //向支付表添加支付日志
        PayLog payLog=new PayLog();
        payLog.setOrderNo(order.getOrderNo());//支付订单号
        payLog.setPayTime(new Date());//订单完成时间
        payLog.setPayType(1);//支付类型
        payLog.setTotalFee(order.getTotalFee());//总金额(分)
        payLog.setTradeState(map.get("trade_state"));//支付状态
        payLog.setTransactionId(map.get("transaction_id"));//流水号
        payLog.setAttr(JSONObject.toJSONString(map));

        baseMapper.insert(payLog);//插入到支付日志表
    }
}
