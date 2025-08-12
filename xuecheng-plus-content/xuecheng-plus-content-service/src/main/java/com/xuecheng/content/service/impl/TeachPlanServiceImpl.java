package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengError;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TeachPlanServiceImpl implements TeachPlanService {
    @Autowired
    TeachplanMapper teachplanMapper;
    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;
    @Override
    public List<TeachplanDto>getTeachplan(Long courseId){
        List<TeachplanDto>boardTeachplan=teachplanMapper.QueryTeachPlanById(courseId);
        Map<Long,TeachplanDto>map=new HashMap<Long,TeachplanDto>();
        List<TeachplanDto>TreeTeachPlan=new ArrayList<TeachplanDto>();
        for(TeachplanDto dto:boardTeachplan){
            TeachplanMedia teachplanMedia=teachplanMediaMapper.selectByTeachplanId(dto.getId());
            if(teachplanMedia!=null){
            dto.setTeachplanMedia(teachplanMedia);}
            map.put(dto.getId(),dto);
            if(dto.getParentid()==0){
                TreeTeachPlan.add(dto);
            }
        }
        for(TeachplanDto dto:boardTeachplan){
            if(dto.getParentid()!=0){
                TeachplanDto parentDto=map.get(dto.getParentid());
                if(parentDto!=null){
                    if(parentDto.getTeachPlanTreeNodes()==null){
                        parentDto.setTeachPlanTreeNodes(new ArrayList<TeachplanDto>());
                    }
                    parentDto.getTeachPlanTreeNodes().add(dto);
                }
            }
        }
        return TreeTeachPlan;
    }
    @Transactional
    @Override
    public void associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        Long teachplanId = bindTeachplanMediaDto.getTeachplanId();
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        if(teachplan==null){
            XueChengError.cast("教学计划不存在");
        }
        Integer grade = teachplan.getGrade();
        if(grade!=2){
            XueChengError.cast("只允许第二级教学计划绑定媒资文件");
        }
        Long courseId = teachplan.getCourseId();

        //先删除原来该教学计划绑定的媒资
        teachplanMediaMapper.delete(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId,teachplanId));

        //再添加教学计划与媒资的绑定关系
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setCourseId(courseId);
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);
    }
}
