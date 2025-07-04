package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.SaveorUpdateTeachplan;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaveorUpdateTeachplanImpl implements SaveorUpdateTeachplan {
    @Autowired
    private TeachplanMapper teachplanMapper;
    @Override
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
}
