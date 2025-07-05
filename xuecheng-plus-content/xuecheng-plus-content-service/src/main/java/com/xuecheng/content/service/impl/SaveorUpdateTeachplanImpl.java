package com.xuecheng.content.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengError;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.SaveorUpdateTeachplan;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaveorUpdateTeachplanImpl implements SaveorUpdateTeachplan {
    @Autowired
    private TeachplanMapper teachplanMapper;
    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;
    @Override
    @Transactional
    public void saveorupdate(SaveTeachplanDto teachplan) {
        Long id=teachplan.getId();
        Long parentid=teachplan.getParentid();
        long courseid=teachplan.getCourseId();
        if (id == null) {
            LambdaQueryWrapper<Teachplan> queryWrapper=new LambdaQueryWrapper<Teachplan>();
            queryWrapper.eq(Teachplan::getCourseId, courseid);
            queryWrapper.eq(Teachplan::getParentid, parentid);
            Long count=teachplanMapper.selectCount(queryWrapper);
            Teachplan teachplannew=new Teachplan();
            BeanUtils.copyProperties(teachplan, teachplannew);
            teachplannew.setOrderby(count.intValue()+1);
            teachplanMapper.insert(teachplannew);
        }
        else{
            Teachplan teachplannew=teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplan, teachplannew);
            teachplanMapper.updateById(teachplannew);
        }
    }
    @Override
    @Transactional
    public void deleteTeachplan(long id) {
        Teachplan teachplan=teachplanMapper.selectById(id);
        int grade=teachplan.getGrade();
        if(grade==1){
            LambdaQueryWrapper<Teachplan> queryWrapper=new LambdaQueryWrapper<Teachplan>();
            queryWrapper.eq(Teachplan::getParentid, id);
            long count=teachplanMapper.selectCount(queryWrapper);
            if(count>0){
                XueChengError.cast("errCode\":\"120409\",\"errMessage\":\"课程计划信息还有子级信息，无法操作");
            }
            else{
                teachplanMapper.deleteById(id);
            }
        }
        else if (grade==2) {
            teachplanMapper.deleteById(id);
            LambdaQueryWrapper<TeachplanMedia>mediamapper=new LambdaQueryWrapper<>();
            mediamapper.eq(TeachplanMedia::getTeachplanId, id);
            teachplanMediaMapper.delete(mediamapper);
        }
    }
    @Override
    @Transactional
    //status=0 往上，status=1往下
    public void moveupordown(Long id,int status) {
        Teachplan teacahplan=teachplanMapper.selectById(id);
        long parentid=teacahplan.getParentid();
        int orderby=teacahplan.getOrderby();
        if(status==0){
            if(orderby==1){
                XueChengError.cast("当前课程已经居于首位不可前移");
                return;
            }
            LambdaQueryWrapper<Teachplan> queryWrapper=new LambdaQueryWrapper<Teachplan>();
            queryWrapper.eq(Teachplan::getParentid, parentid);
            queryWrapper.eq(Teachplan::getOrderby, orderby-1);
            Teachplan teachplanpre=teachplanMapper.selectOne(queryWrapper);
            teachplanpre.setOrderby(orderby);
            teacahplan.setOrderby(orderby-1);
            teachplanMapper.updateById(teacahplan);
            teachplanMapper.updateById(teachplanpre);
        }
        if(status==1){
            LambdaQueryWrapper<Teachplan> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getParentid, parentid);
            long count=teachplanMapper.selectCount(queryWrapper);
            if(orderby==count){
                XueChengError.cast("当前课程已经处于末尾不可后移");
            }
            LambdaQueryWrapper<Teachplan> queryWrapperdown=new LambdaQueryWrapper<Teachplan>();
            queryWrapperdown.eq(Teachplan::getParentid, parentid);
            queryWrapperdown.eq(Teachplan::getOrderby, orderby+1);
            Teachplan teachplannext=teachplanMapper.selectOne(queryWrapperdown);
            teachplannext.setOrderby(orderby);
            teacahplan.setOrderby(orderby+1);
            teachplanMapper.updateById(teachplannext);
            teachplanMapper.updateById(teacahplan);
        }
    }
}
