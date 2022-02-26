package com.sucker.eduservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sucker.eduservice.entity.EduCourse;
import com.sucker.eduservice.entity.EduCourseDescription;
import com.sucker.eduservice.entity.EduTeacher;
import com.sucker.eduservice.entity.frontvo.CourseFrontVo;
import com.sucker.eduservice.entity.frontvo.CourseWebVo;
import com.sucker.eduservice.entity.vo.CourseInfoVo;
import com.sucker.eduservice.entity.vo.CoursePublishVo;
import com.sucker.eduservice.mapper.EduCourseMapper;
import com.sucker.eduservice.service.EduChapterService;
import com.sucker.eduservice.service.EduCourseDescriptionService;
import com.sucker.eduservice.service.EduCourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sucker.eduservice.service.EduVideoService;
import com.sucker.servicebase.exceptionhandler.GuliException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author sucker
 * @since 2022-02-08
 */
@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {

    //课程描述注入
    @Autowired
    private EduCourseDescriptionService eduCourseDescriptionService;

    //注入章节
    @Autowired
    private EduChapterService chapterService;

    //注入小节
    @Autowired
    private EduVideoService eduVideoService;


    //添加课程基本信息
    @Override
    public String addCourseInfo(CourseInfoVo courseInfoVo) {
        // 向课程表添加课程基本信息
        //CourseInfoVo对象转换eduCourse对象
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo,eduCourse);
        int insert = baseMapper.insert(eduCourse);

        if(insert == 0) throw new GuliException(20001,"添加课程信息失败");

        //获取添加之后课程id
        String cid = eduCourse.getId();

        // 向课程简介表添加课程简介
        EduCourseDescription courseDescription = new EduCourseDescription();
        courseDescription.setDescription(courseInfoVo.getDescription());
        courseDescription.setId(cid);
        eduCourseDescriptionService.save(courseDescription);

        return cid;
    }

    //根据课程id查询课程基本信息
    @Override
    public CourseInfoVo getCourseInfo(String courseId) {

        //查询课程表
        EduCourse eduCourse = baseMapper.selectById(courseId);
        CourseInfoVo courseInfoVo = new CourseInfoVo();
        BeanUtils.copyProperties(eduCourse,courseInfoVo);

        //查询描述表
        EduCourseDescription courseDescription = eduCourseDescriptionService.getById(courseId);
        courseInfoVo.setDescription(courseDescription.getDescription());

        return courseInfoVo;
    }

    //修改课程信息
    @Override
    public void updateCourseInfo(CourseInfoVo courseInfoVo) {
        //修改课程表
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo,eduCourse);
        int i = baseMapper.updateById(eduCourse);
        if(i == 0) throw new GuliException(20001,"修改课程信息失败");

        //修改描述表
        EduCourseDescription eduCourseDescription = new EduCourseDescription();
        eduCourseDescription.setDescription(courseInfoVo.getDescription());
        eduCourseDescriptionService.updateById(eduCourseDescription);

    }

    //根据课程id查询课程确认信息
    @Override
    public CoursePublishVo publishCourseInfo(String id) {
        //调用mapper
        CoursePublishVo publishCourseInfo = baseMapper.getPublishCourseInfo(id);
        return publishCourseInfo;
    }

    //删除课程通过courseId
    @Override
    public void removeCourse(String courseId) {
        //根据课程id删除小节
        eduVideoService.removeVideoByCourseId(courseId);
        //根据课程id删除章节
        chapterService.removeChapterByCourseId(courseId);
        //根据课程id删除描述
        eduCourseDescriptionService.removeById(courseId);//因为这两个表是一对一关系，id相同
        //根据课程id删除课程本身
        int result = baseMapper.deleteById(courseId);
        if(result == 0) throw new GuliException(20001,"删除失败");

    }

    //前台条件查询课程带分页
    @Override
    public Map<String, Object> getCourseFrontList(Page<EduCourse> pageParam, CourseFrontVo courseFrontVo) {
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        //判断条件是否为空，不为空就拼接  前端会点击的时候传值
        if(!ObjectUtils.isEmpty(courseFrontVo.getSubjectParentId())) wrapper.eq("subject_parent_id",courseFrontVo.getSubjectParentId());//一级分类
        if(!ObjectUtils.isEmpty(courseFrontVo.getSubjectId())) wrapper.eq("subject_id",courseFrontVo.getSubjectId());//二级分类
        if(!ObjectUtils.isEmpty(courseFrontVo.getBuyCountSort())) wrapper.orderByDesc("buy_count");//关注度
        if(!ObjectUtils.isEmpty(courseFrontVo.getGmtCreateSort())) wrapper.orderByDesc("gmt_create");//最新
        if(!ObjectUtils.isEmpty(courseFrontVo.getPriceSort())) wrapper.orderByDesc("price");//价格
        baseMapper.selectPage(pageParam,wrapper);

        List<EduCourse> records = pageParam.getRecords();
        long current = pageParam.getCurrent();
        long pages = pageParam.getPages();
        long size = pageParam.getSize();
        long total = pageParam.getTotal();
        boolean hasNext = pageParam.hasNext();//是否有下一页
        boolean hasPrevious = pageParam.hasPrevious();//是否有上一页

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("items", records);
        map.put("current", current);
        map.put("pages", pages);
        map.put("size", size);
        map.put("total", total);
        map.put("hasNext", hasNext);
        map.put("hasPrevious", hasPrevious);

        return map;

    }

    //根据课程id，编写sql语句查询课程信息
    @Override
    public CourseWebVo getBaseCourseInfo(String courseId) {
        return baseMapper.getBaseCourseInfo(courseId);
    }
}
