package com.sucker.eduservice.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sucker.commonutils.R;
import com.sucker.eduservice.entity.EduCourse;
import com.sucker.eduservice.entity.EduTeacher;
import com.sucker.eduservice.service.EduCourseService;
import com.sucker.eduservice.service.EduTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(description = "前台讲师管理")
@RestController
@RequestMapping("/eduservice/teacherfront")
//@CrossOrigin
public class TeacherFrontController {

    @Autowired
    private EduTeacherService teacherService;

    @Autowired
    private EduCourseService courseService;

    //前台分页查询讲师
    @ApiOperation(value = "前台分页查询讲师")
    @PostMapping("getTeacherFrontList/{page}/{limit}")
    public R getTeacherFrontList(@PathVariable long page,@PathVariable long limit){
        Page<EduTeacher> pageTeacher = new Page<>(page,limit);
        Map<String,Object> map = teacherService.getTeacherFrontList(pageTeacher);
        //返回分页所有数据，因为前端不是用element-ui而是其他写法
        return R.ok().data(map);
    }


    //前台根据讲师id查询讲师详情
    @ApiOperation(value = "前台根据讲师id查询讲师详情")
    @GetMapping("getTeacherFrontInfo/{teacherId}")
    public R getTeacherFrontInfo(@PathVariable String teacherId){
        //根据讲师id查询讲师基本信息
        EduTeacher eduTeacher = teacherService.getById(teacherId);
        //根据讲师id查询讲师所讲课程
        List<EduCourse> courseList = courseService.list(new QueryWrapper<EduCourse>().eq("teacher_id", teacherId));

        return R.ok().data("teacher",eduTeacher).data("courseList",courseList);
    }


}
