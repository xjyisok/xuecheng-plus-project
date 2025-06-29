package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.AddCourseParamDto;
import com.xuecheng.content.model.dto.QueryCourseMarketDto;

public interface AddCourseService {
    public QueryCourseMarketDto queryCourseMarket(Long companyId, AddCourseParamDto addCourseParamDto);
}
