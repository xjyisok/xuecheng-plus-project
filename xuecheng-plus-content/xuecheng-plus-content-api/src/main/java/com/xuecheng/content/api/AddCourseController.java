package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.AddCourseParamDto;
import com.xuecheng.content.model.dto.QueryCourseMarketDto;
import com.xuecheng.content.service.AddCourseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "课程添加", description = "提供课程的添加功能")
public class AddCourseController {
    @Autowired
    private AddCourseService addCourseService;
    @PostMapping("/course")
    public QueryCourseMarketDto queryCourseMarket(@RequestBody AddCourseParamDto addCourseParamDto) {
        Long companyId=123L;
        return addCourseService.queryCourseMarket(companyId, addCourseParamDto);
    }
}
