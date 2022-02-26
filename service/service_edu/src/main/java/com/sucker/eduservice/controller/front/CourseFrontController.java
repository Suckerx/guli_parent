package com.sucker.eduservice.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sucker.commonutils.JwtUtils;
import com.sucker.commonutils.R;
import com.sucker.commonutils.ordervo.CourseWebVoOrder;
import com.sucker.eduservice.client.OrdersClient;
import com.sucker.eduservice.entity.EduCourse;
import com.sucker.eduservice.entity.EduTeacher;
import com.sucker.eduservice.entity.chapter.ChapterVo;
import com.sucker.eduservice.entity.frontvo.CourseFrontVo;
import com.sucker.eduservice.entity.frontvo.CourseWebVo;
import com.sucker.eduservice.service.EduChapterService;
import com.sucker.eduservice.service.EduCourseService;
import com.sucker.eduservice.service.EduTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Api(description = "前台课程管理")
@RestController
@RequestMapping("/eduservice/coursefront")
//@CrossOrigin
public class CourseFrontController {

    @Autowired
    private EduCourseService courseService;

    @Autowired
    private EduChapterService chapterService;

    @Autowired
    private OrdersClient ordersClient;

    //前台条件查询课程带分页
    @ApiOperation(value = "前台条件查询课程带分页")
    @PostMapping("getFrontCourseList/{page}/{limit}")
    public R getFrontCourseList(@PathVariable long page , @PathVariable long limit,
                                @RequestBody(required = false) CourseFrontVo courseFrontVo){//required = false表示条件可以为空，查询全部
        Page<EduCourse> pageCourse = new Page<>(page,limit);
        Map<String ,Object> map = courseService.getCourseFrontList(pageCourse,courseFrontVo);
        return R.ok().data(map);//返回分页所有数据
    }


    //多表查询课程详情
    @ApiOperation(value = "多表查询课程详情")
    @GetMapping("getFrontCourseInfo/{courseId}")
    public R getFrontCourseInfo(@PathVariable String courseId, HttpServletRequest request){
        //根据课程id，编写sql语句查询课程信息
        CourseWebVo courseWebVo = courseService.getBaseCourseInfo(courseId);

        //根据课程id查询章节和小节
        List<ChapterVo> chapterVideoList = chapterService.getChapterVideoByCourseId(courseId);

        //根据课程id和用户id查询当前课程是否已经支付过了,远程调用接口
        Boolean buyCourse = ordersClient.isBuyCourse(courseId, JwtUtils.getMemberIdByJwtToken(request));

        return R.ok().data("courseWebVo",courseWebVo).data("chapterVideoList",chapterVideoList).data("isBuy",buyCourse);
    }


    //根据课程id查询课程信息
    @ApiOperation(value = "根据课程id查询课程信息")
    @PostMapping("getCourseInfoOrder/{id}")
    public CourseWebVoOrder getCourseInfoOrder(@PathVariable String id){
        CourseWebVo courseInfo = courseService.getBaseCourseInfo(id);
        CourseWebVoOrder courseWebVoOrder = new CourseWebVoOrder();
        BeanUtils.copyProperties(courseInfo,courseWebVoOrder);
        return courseWebVoOrder;
    }


}
