package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.QueryCourseCategoryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.xuecheng.content.service.CoursecategoryqueryService;

import java.util.List;

@RestController
@Tag(name = "课程类别管理", description = "提供课程类别树")
public class CourseCategoryQueryController {
    @Autowired
    CoursecategoryqueryService coursecategoryqueryService;

    @GetMapping("/course-category/tree-nodes")
    @Operation(summary = "查询课程", description = "根据课程ID查询课程详细信息")
    public List<QueryCourseCategoryDto>categoryTree(){
        List<QueryCourseCategoryDto>categoryTree=coursecategoryqueryService.queryCourseCategory("1");
        return categoryTree;
    }
}
