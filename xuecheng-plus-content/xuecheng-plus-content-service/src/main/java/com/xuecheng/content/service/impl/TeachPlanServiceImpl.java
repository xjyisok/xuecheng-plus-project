package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
