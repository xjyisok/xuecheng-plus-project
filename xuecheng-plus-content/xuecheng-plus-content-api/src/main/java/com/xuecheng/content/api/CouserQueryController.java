package com.xuecheng.content.api;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.service.CoursebaseInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@Tag(name = "课程管理", description = "提供课程的增删改查接口")
public class CouserQueryController {
    @Autowired
    CoursebaseInfoService coursebaseInfoService;
    @PostMapping("/course/list")
    @Operation(summary = "查询课程", description = "根据课程ID查询课程详细信息")
    public PageResult<CourseBase> courseList(PageParams pageParams, @RequestBody(required = false)QueryCourseParamsDto queryCourseParamsDto) {
        PageResult<CourseBase>result=coursebaseInfoService.queryCourseBaseInfo(pageParams, queryCourseParamsDto);
        return result;
    }
}
