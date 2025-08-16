package com.xuecheng.content.service.impl;

import com.xuecheng.base.exception.XueChengError;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseParamDto;
import com.xuecheng.content.model.dto.QueryCourseMarketDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.AddCourseService;
import io.swagger.v3.oas.annotations.servers.Server;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AddCourseServiceImpl implements AddCourseService {
    @Autowired
    CourseMarketMapper courseMarketMapper;
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    private CourseCategoryMapper courseCategoryMapper;

    @Override
    public QueryCourseMarketDto queryCourseMarket(Long companyId, AddCourseParamDto addCourseParamDto) {
//        if (StringUtils.isBlank(addCourseParamDto.getName())) {
//            XueChengError.cast("课程名称为空");
//        }
//
//        if (StringUtils.isBlank(addCourseParamDto.getMt())) {
//            XueChengError.cast("课程分类为空");
//        }
//
//        if (StringUtils.isBlank(addCourseParamDto.getSt())) {
//            XueChengError.cast("课程分类为空");
//        }
//
//        if (StringUtils.isBlank(addCourseParamDto.getGrade())) {
//            XueChengError.cast("课程等级为空");
//        }
//
//        if (StringUtils.isBlank(addCourseParamDto.getTeachmode())) {
//            XueChengError.cast("教育模式为空");
//        }
//
//        if (StringUtils.isBlank(addCourseParamDto.getUsers())) {
//            XueChengError.cast("适应人群为空");
//        }
//
//        if (StringUtils.isBlank(addCourseParamDto.getCharge())) {
//            XueChengError.cast("收费规则为空");
//        }
        CourseBase courseBaseNew = new CourseBase();
        BeanUtils.copyProperties(addCourseParamDto, courseBaseNew);
        courseBaseNew.setCompanyId(companyId);
        courseBaseNew.setAuditStatus("202002");
        //设置发布状态
        courseBaseNew.setStatus("203001");
        //机构id
        courseBaseNew.setCompanyId(companyId);
        //添加时间
        courseBaseNew.setCreateDate(LocalDateTime.now());
        int result=courseBaseMapper.insert(courseBaseNew);
        if(result < 0){
            XueChengError.cast("课程保存错误");
        }
        Long courseId=courseBaseNew.getId();
        CourseMarket courseMarketNew = new CourseMarket();
        courseMarketNew.setId(courseId);
        BeanUtils.copyProperties(addCourseParamDto, courseMarketNew);
        int resultsavemarker=saveCourseMarket(courseMarketNew);
        if(resultsavemarker < 0){
            XueChengError.cast("课程商业状态保存失败");
        }
        return getCourseMarket(courseId);
    }
    private int saveCourseMarket(CourseMarket courseMarket) {
        if(StringUtils.isBlank(courseMarket.getCharge())){
            XueChengError.cast("收费规则不能为空");
        }
        if(courseMarket.getCharge().equals("201001")){
            if(courseMarket.getPrice()==null||courseMarket.getPrice()<0){
                XueChengError.cast("课程收费必须大于0且不为空");}
        }
        CourseMarket courseMarketNew = courseMarketMapper.selectById(courseMarket.getId());
        if(courseMarketNew==null){
            return courseMarketMapper.insert(courseMarket);
        }
        else{
            BeanUtils.copyProperties(courseMarket, courseMarketNew);
            courseMarketNew.setId(courseMarket.getId());
            return courseMarketMapper.updateById(courseMarketNew);
        }
    }
    public QueryCourseMarketDto getCourseMarket(long courseId) {
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
}
