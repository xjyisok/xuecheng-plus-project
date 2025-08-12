package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.TeachplanMedia;

import java.util.List;

public interface TeachPlanService {
    public List<TeachplanDto> getTeachplan(Long courseId);
    public void associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);
}
