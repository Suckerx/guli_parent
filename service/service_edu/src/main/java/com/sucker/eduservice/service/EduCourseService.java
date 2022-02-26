package com.sucker.eduservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sucker.eduservice.entity.EduCourse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sucker.eduservice.entity.frontvo.CourseFrontVo;
import com.sucker.eduservice.entity.frontvo.CourseWebVo;
import com.sucker.eduservice.entity.vo.CourseInfoVo;
import com.sucker.eduservice.entity.vo.CoursePublishVo;

import java.util.Map;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author sucker
 * @since 2022-02-08
 */
public interface EduCourseService extends IService<EduCourse> {

    //添加课程基本信息
    String addCourseInfo(CourseInfoVo courceInfoVo);

    //根据课程id查询课程基本信息
    CourseInfoVo getCourseInfo(String courseId);

    //修改课程信息
    void updateCourseInfo(CourseInfoVo courseInfoVo);

    //根据课程id查询课程确认信息
    CoursePublishVo publishCourseInfo(String id);

    //删除课程通过courseId
    void removeCourse(String courseId);

    //前台条件查询课程带分页
    Map<String, Object> getCourseFrontList(Page<EduCourse> pageCourse, CourseFrontVo courseFrontVo);

    //根据课程id，编写sql语句查询课程信息
    CourseWebVo getBaseCourseInfo(String courseId);
}
