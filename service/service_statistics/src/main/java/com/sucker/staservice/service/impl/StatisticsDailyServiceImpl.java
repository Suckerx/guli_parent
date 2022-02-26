package com.sucker.staservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sucker.commonutils.R;
import com.sucker.commonutils.ordervo.UcenterMemberOrder;
import com.sucker.staservice.client.UcenterClient;
import com.sucker.staservice.entity.StatisticsDaily;
import com.sucker.staservice.mapper.StatisticsDailyMapper;
import com.sucker.staservice.service.StatisticsDailyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 网站统计日数据 服务实现类
 * </p>
 *
 * @author sucker
 * @since 2022-02-20
 */
@Service
public class StatisticsDailyServiceImpl extends ServiceImpl<StatisticsDailyMapper, StatisticsDaily> implements StatisticsDailyService {

    @Autowired
    private UcenterClient ucenterClient;

    //统计某一天注册人数,生成统计数据
    @Override
    public void registerCount(String day) {

        //添加记录前删除表中相同日期的数据
        baseMapper.delete(new QueryWrapper<StatisticsDaily>().eq("date_calculated",day));

        //远程调用得到某一天注册人数
        R registerR = ucenterClient.countRegister(day);
        Integer countRegister = (Integer) registerR.getData().get("countRegister");

        //把获取数据添加到数据库，统计分析表
        StatisticsDaily sta = new StatisticsDaily();
        sta.setRegisterNum(countRegister);//注册人数
        sta.setDateCalculated(day);//统计日期
        //其他值仅仅模拟，不做真实情况，真实也是去查询当天记录来存
        sta.setVideoViewNum(RandomUtils.nextInt(100,200));
        sta.setLoginNum(RandomUtils.nextInt(100,200));
        sta.setCourseNum(RandomUtils.nextInt(100,200));
        baseMapper.insert(sta);
    }


    //图表显示，返回两部分数据，日期Json数组，数量Json数组
    @Override
    public Map<String, Object> getShowData(String type, String begin, String end) {
        QueryWrapper<StatisticsDaily> wrapper = new QueryWrapper<>();
        wrapper.between("date_calculated",begin,end);
        wrapper.select("date_calculated",type);//根据参数指定要查询的列，这里表示查询date_calculated 和前端传来的type
        List<StatisticsDaily> staList = baseMapper.selectList(wrapper);

        //因为返回两部分数据，日期和 日期对应的数量
        //前端要求Json数组,在后端中，对象会变成Json对象形式，list集合会变成Json数组
        //创建两个list 一个日期，一个数量
        List<String> date_calculatedList = new ArrayList<>();
        List<Integer> numDataList = new ArrayList<>();

        //遍历查询出来的所有数据的list集合，分别封装
        for (int i = 0; i < staList.size(); i++) {
            StatisticsDaily daily = staList.get(i);
            //封装日期
            date_calculatedList.add(daily.getDateCalculated());
            //封装数量
            switch (type){
                case "login_num":
                    numDataList.add(daily.getLoginNum());
                    break;
                case "register_num":
                    numDataList.add(daily.getRegisterNum());
                    break;
                case "video_view_num":
                    numDataList.add(daily.getVideoViewNum());
                    break;
                case "course_num":
                    numDataList.add(daily.getCourseNum());
                    break;
                default:
                    break;
            }

        }
        Map<String, Object> map = new HashMap<>();
        map.put("data_calculatedList",date_calculatedList);
        map.put("numDataList",numDataList);
        return map;
    }



}
