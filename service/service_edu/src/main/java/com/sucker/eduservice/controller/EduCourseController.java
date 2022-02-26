package com.sucker.eduservice.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sucker.commonutils.R;
import com.sucker.eduservice.entity.EduCourse;
import com.sucker.eduservice.entity.vo.CourseInfoVo;
import com.sucker.eduservice.entity.vo.CoursePublishVo;
import com.sucker.eduservice.entity.vo.CourseQuery;
import com.sucker.eduservice.service.EduCourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.List;

/**
 * <p>
 * 课程
 * </p>
 *
 * @author sucker
 * @since 2022-02-08
 */
@Api(description = "课程管理")
@RestController
@RequestMapping("/eduservice/course")
public class EduCourseController {

    @Autowired
    private EduCourseService courseService;

    @ApiOperation(value = "添加课程基本信息")
    @PostMapping("addCourseInfo")
    public R addCourseInfo(@RequestBody CourseInfoVo courceInfoVo){
        String id = courseService.addCourseInfo(courceInfoVo);
        return R.ok().data("courseId",id);
    }

    //根据课程id查询课程基本信息
    @ApiOperation(value = "根据课程id查询课程基本信息")
    @GetMapping("getCourseInfo/{courseId}")
    public R getCourseInfo(@PathVariable String courseId){
        CourseInfoVo courseInfoVo = courseService.getCourseInfo(courseId);
        return R.ok().data("courseInfoVo",courseInfoVo);
    }

    //修改课程信息
    @ApiOperation(value = "修改课程信息")
    @PostMapping("updateCourseInfo")
    public R updateCourseInfo(@RequestBody CourseInfoVo courseInfoVo){
        courseService.updateCourseInfo(courseInfoVo);
        return R.ok();
    }

    @ApiOperation(value = "根据课程id查询课程确认信息")
    @GetMapping("getPublishCourseInfo/{id}")
    public R getPublishCourseInfo(@PathVariable String id){
        CoursePublishVo coursePublishVo = courseService.publishCourseInfo(id);
        return R.ok().data("publishCourse",coursePublishVo);
    }

    //确认课程信息后，修改课程状态为Normal表示已发布
    @ApiOperation(value = "课程最终发布,通过id修改")
    @PostMapping("publishCourse/{id}")
    public R publishCourse(@PathVariable String id){
        EduCourse eduCourse = new EduCourse();
        eduCourse.setId(id);
        eduCourse.setStatus("Normal");
        courseService.updateById(eduCourse);
        return R.ok();
    }

    //课程列表 查询所有
    @ApiOperation(value = "课程列表查询所有")
    @GetMapping
    public R getCourseList(){
        List<EduCourse> list = courseService.list(null);
        return R.ok().data("list",list);
    }

    @ApiOperation(value = "分页查询课程")
    @GetMapping("pageCourse/{current}/{limit}")
    public R pageTeacher(@PathVariable Long current,
                         @PathVariable Long limit) {
        //创建page
        Page<EduCourse> pageTeacher = new Page<>(current, limit);
        IPage<EduCourse> page = courseService.page(pageTeacher, null);
        List<EduCourse> records = page.getRecords();
        long total = page.getTotal();
        return R.ok().data("total", total).data("records", records);
    }


    @ApiOperation(value = "条件查询带分页")
    @PostMapping("pageCourseCondition/{current}/{limit}")
    public R pageCourseCondition(@PathVariable Long current,
                                 @PathVariable Long limit,
                                 @RequestBody(required = false) CourseQuery courseQuery){
        Page<EduCourse> pageConditon = new Page<>(current,limit);

        //QueryWrapper构建
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        //多条件组合查询，动态sql
        String status = courseQuery.getStatus();
        String title = courseQuery.getTitle();
        if(!ObjectUtils.isEmpty(status)) wrapper.eq("status",status);
        if(!ObjectUtils.isEmpty(title)) wrapper.like("title",title);

        wrapper.orderByDesc("gmt_create");

        //调用方法实现分页查询
        Page<EduCourse> page = courseService.page(pageConditon, wrapper);

        long total = page.getTotal();
        List<EduCourse> records = page.getRecords();

        return R.ok().data("total",total).data("records",records);

    }


    @ApiOperation(value = "删除课程通过courseId")
    @DeleteMapping("{courseId}")
    public R deleteCourse(@PathVariable String courseId){
        courseService.removeCourse(courseId);
        return R.ok();
    }

}

