package com.xuecheng.content.api;

import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.ModifyCourseDto;
import com.xuecheng.content.model.dto.QueryCourseMarketDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.ModifyCourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class updateCourseController {
    @Autowired
    ModifyCourseService modifyCourseService;
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    CourseMarketMapper courseMarketMapper;
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @PutMapping("/course")
    public QueryCourseMarketDto queryCourseMarket(@RequestBody @Validated ModifyCourseDto modifyCourseDto) {
        Long companyId=1232141425L;
        return modifyCourseService.updatecourse(companyId, modifyCourseDto);
    }
    @GetMapping("/course/{courseId}")
    public QueryCourseMarketDto querycourseById(@PathVariable Long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null){
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        QueryCourseMarketDto queryCourseMarketDto = new QueryCourseMarketDto();
        BeanUtils.copyProperties(courseBase, queryCourseMarketDto);
        if(courseMarket!=null){
            BeanUtils.copyProperties(courseMarket, queryCourseMarketDto);
        }
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        queryCourseMarketDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        queryCourseMarketDto.setMtName(courseCategoryByMt.getName());

        return queryCourseMarketDto;
    }
    @DeleteMapping("/course/courseId")
    public void deleteCourseById(@PathVariable Long courseId) {
        modifyCourseService.deletecourse(courseId);
    }
}
