package com.sucker.eduservice.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sucker.commonutils.R;
import com.sucker.eduservice.entity.EduTeacher;
import com.sucker.eduservice.entity.vo.TeacherQuery;
import com.sucker.eduservice.service.EduTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.Result;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author sucker
 * @since 2022-01-27
 */
@Api(description = "讲师管理")
@RestController
@RequestMapping("/eduservice/teacher")
//@CrossOrigin
public class EduTeacherController {

    @Autowired
    private EduTeacherService eduTeacherService;

    //查询讲师表所有数据
    //rest风格
    @ApiOperation(value = "查询所有讲师列表")
    @GetMapping("findAll")
    public R list(){
        List<EduTeacher> list = eduTeacherService.list(null);
        return R.ok().data("items",list);
    }


    @ApiOperation(value = "根据ID逻辑删除讲师")
    @DeleteMapping("{id}")//id通过路径传递
    public R removeById(@PathVariable String id){
        boolean flag = eduTeacherService.removeById(id);
        if(flag) return R.ok();
        else return R.error();
    }

    //分页查询讲师方法
    @ApiOperation(value = "分页讲师列表")
    @GetMapping("pageTeacher/{current}/{limit}")
    public R pageList(
            @ApiParam(name = "current", value = "当前页码", required = true)
            @PathVariable Long current,
            @ApiParam(name = "limit", value = "每页记录数", required = true)
            @PathVariable Long limit){
        //创建page对象
        Page<EduTeacher> pageTeacher = new Page<>(current, limit);
        //调用方法实现分页
        //调用方法的时候，底层封装，把分页所有数据封装到pageTeacher对象里
        eduTeacherService.page(pageTeacher,null);

        long total = pageTeacher.getTotal();
        List<EduTeacher> records = pageTeacher.getRecords();
        return R.ok().data("total", total).data("rows", records);
//        Map map = new HashMap();
//        map.put("total",total);
        //return R.ok().data(map);
    }

    @ApiOperation(value = "条件分页讲师列表")
    //条件查询带分页的方法
    //@RequestBody是在传递数据过程中以json对象形式，需要配合post提交
    @PostMapping("pageTeacherCondition/{current}/{limit}")
    public R pageTeacherCondition(@PathVariable long current,@PathVariable long limit,
                                  @RequestBody(required = false) TeacherQuery teacherQuery){
        Page<EduTeacher> pageTeacher = new Page<>(current, limit);

        //构建条件
        QueryWrapper<EduTeacher> wrapper = new QueryWrapper<>();
        //多条件组合查询，判断条件值是否为空，如果不为空拼接条件
        String name = teacherQuery.getName();
        Integer level = teacherQuery.getLevel();
        String begin = teacherQuery.getBegin();
        String end = teacherQuery.getEnd();
        if(!ObjectUtils.isEmpty(name)){
            //构建条件
            wrapper.like("name",name);
        }
        if(!ObjectUtils.isEmpty(level)) wrapper.eq("level",level);
        if(!ObjectUtils.isEmpty(begin)) wrapper.ge("gmt_create",begin);//注意这里是数据库字段名
        if(!ObjectUtils.isEmpty(end)) wrapper.le("gmt_modified",end);

        //调用方法实现条件查询分页
        eduTeacherService.page(pageTeacher,wrapper);
        List<EduTeacher> records = pageTeacher.getRecords();
        long total = pageTeacher.getTotal();
        return R.ok().data("total", total).data("rows", records);
    }

    //添加讲师方法
    @ApiOperation(value = "新增讲师")
    @PostMapping("addTeacher")
    public R addTeacher(@RequestBody EduTeacher eduTeacher){
        boolean save = eduTeacherService.save(eduTeacher);
        if(save) return R.ok();
        else return R.error();
    }

    //根据ID查询讲师
    @ApiOperation(value = "根据ID查询讲师")
    @GetMapping("getTeacher/{id}")
    public R getById(
            @ApiParam(name = "id", value = "讲师ID", required = true)
            @PathVariable String id){
        EduTeacher teacher = eduTeacherService.getById(id);
        System.out.println("---");
        return R.ok().data("item", teacher);
    }

    @ApiOperation(value = "根据ID修改讲师")
    @PostMapping("updateTeacher")
    public R updateById(
            @ApiParam(name = "teacher", value = "讲师对象", required = true)
            @RequestBody EduTeacher teacher){
        boolean flag = eduTeacherService.updateById(teacher);
        if(flag) return R.ok();
        else return R.error();
    }


}

