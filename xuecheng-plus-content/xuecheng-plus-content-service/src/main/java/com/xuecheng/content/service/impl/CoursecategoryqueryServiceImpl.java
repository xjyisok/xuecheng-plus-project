package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.QueryCourseCategoryDto;
import com.xuecheng.content.service.CoursecategoryqueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CoursecategoryqueryServiceImpl implements CoursecategoryqueryService {
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    @Override
    public List<QueryCourseCategoryDto> queryCourseCategory(String id) {
        List<QueryCourseCategoryDto> queryCourseCategoryDtos = courseCategoryMapper.QueryCourseCategory(id);
        Map<String, QueryCourseCategoryDto> map = new HashMap<>();
        for (QueryCourseCategoryDto queryCourseCategoryDto : queryCourseCategoryDtos) {
            if(!queryCourseCategoryDto.getId().equals(id)){
                map.put(queryCourseCategoryDto.getId(), queryCourseCategoryDto);
            }
        }
        List<QueryCourseCategoryDto>CourseCategoryTree=new ArrayList<>();
        List<QueryCourseCategoryDto> filtered = queryCourseCategoryDtos.stream()
                .filter(item -> !id.equals(item.getId()))
                .collect(Collectors.toList());

        for (QueryCourseCategoryDto queryCourseCategoryDto : filtered) {
            if (queryCourseCategoryDto.getParentid().equals(id)) {
                CourseCategoryTree.add(queryCourseCategoryDto);
            }
            QueryCourseCategoryDto item = map.get(queryCourseCategoryDto.getParentid());
            if (item != null) {
                if (item.getChildrenTreeNodes() == null) {
                    item.setChildrenTreeNodes(new ArrayList<QueryCourseCategoryDto>());
                }
                item.getChildrenTreeNodes().add(queryCourseCategoryDto);
            }
        }
        return CourseCategoryTree;
    }
}