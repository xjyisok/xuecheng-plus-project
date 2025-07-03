package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.ModifyCourseDto;
import com.xuecheng.content.model.dto.QueryCourseMarketDto;

public interface ModifyCourseService {
    public QueryCourseMarketDto updatecourse(long companyId,ModifyCourseDto modifyCourseDto);
}
