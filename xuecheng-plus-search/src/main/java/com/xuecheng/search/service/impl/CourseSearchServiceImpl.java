package com.xuecheng.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.search.dto.SearchCourseParamDto;
import com.xuecheng.search.dto.SearchPageResultDto;
import com.xuecheng.search.po.CourseIndex;
import com.xuecheng.search.service.CourseSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CourseSearchServiceImpl implements CourseSearchService {

    @Value("${elasticsearch.course.index}")
    private String courseIndexStore;

    @Value("${elasticsearch.course.source_fields}")
    private String sourceFields;

    @Autowired
    private ElasticsearchClient client;

    @Override
    public SearchPageResultDto<CourseIndex> queryCoursePubIndex(PageParams pageParams,
                                                                SearchCourseParamDto courseSearchParam) {
        if (courseSearchParam == null) {
            courseSearchParam = new SearchCourseParamDto();
        }

        try {
            // 构建 bool 查询
            List<Query> mustQueries = new ArrayList<>();
            List<Query> filterQueries = new ArrayList<>();

            if (StringUtils.isNotEmpty(courseSearchParam.getKeywords())) {
                String keyword = courseSearchParam.getKeywords();
                mustQueries.add(Query.of(q -> q
                        .multiMatch(m -> m
                                .fields("name^10", "description")
                                .query(keyword)
                                .minimumShouldMatch("70%")
                        )));
            }

            if (StringUtils.isNotEmpty(courseSearchParam.getMt())) {
                SearchCourseParamDto finalCourseSearchParam = courseSearchParam;
                filterQueries.add(Query.of(q -> q.term(t -> t.field("mtName").value(finalCourseSearchParam.getMt()))));
            }
            if (StringUtils.isNotEmpty(courseSearchParam.getSt())) {
                SearchCourseParamDto finalCourseSearchParam1 = courseSearchParam;
                filterQueries.add(Query.of(q -> q.term(t -> t.field("stName").value(finalCourseSearchParam1.getSt()))));
            }
            if (StringUtils.isNotEmpty(courseSearchParam.getGrade())) {
                SearchCourseParamDto finalCourseSearchParam2 = courseSearchParam;
                filterQueries.add(Query.of(q -> q.term(t -> t.field("grade").value(finalCourseSearchParam2.getGrade()))));
            }

            BoolQuery boolQuery = BoolQuery.of(b -> b.must(mustQueries).filter(filterQueries));

            long pageNo = pageParams.getPageNo();
            long pageSize = pageParams.getPageSize();
            int from = (int) ((pageNo - 1) * pageSize);

            Highlight highlight = Highlight.of(h -> h
                    .preTags("<font class='eslight'>")
                    .postTags("</font>")
                    .fields("name", HighlightField.of(f -> f))
            );

            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index(courseIndexStore)
                    .query(q -> q.bool(boolQuery))
                    .from(from)
                    .size((int) pageSize)
                    .highlight(highlight)
                    .source(src -> src.filter(f -> f.includes(Arrays.asList(sourceFields.split(",")))))
                    .aggregations("mtAgg", a -> a.terms(t -> t.field("mtName").size(100)))
                    .aggregations("stAgg", a -> a.terms(t -> t.field("stName").size(100)))
            );

            SearchResponse<CourseIndex> response = client.search(searchRequest, CourseIndex.class);

            TotalHits totalHits = response.hits().total();
            long total = totalHits != null ? totalHits.value() : 0;

            List<CourseIndex> list = new ArrayList<>();
            for (Hit<CourseIndex> hit : response.hits().hits()) {
                CourseIndex courseIndex = hit.source();
                if (courseIndex == null) continue;

                Map<String, List<String>> highlightMap = hit.highlight();
                if (highlightMap != null && highlightMap.containsKey("name")) {
                    courseIndex.setName(String.join("", highlightMap.get("name")));
                }
                list.add(courseIndex);
            }

            SearchPageResultDto<CourseIndex> pageResult =
                    new SearchPageResultDto<>(list, total, pageNo, pageSize);

            // 聚合结果
            pageResult.setMtList(getTermsAgg(response, "mtAgg"));
            pageResult.setStList(getTermsAgg(response, "stAgg"));

            return pageResult;

        } catch (Exception e) {
            log.error("课程搜索异常：{}", e.getMessage(), e);
            return new SearchPageResultDto<>(new ArrayList<>(), 0, 0, 0);
        }
    }

    /**
     * 获取 terms 聚合结果
     */
    private List<String> getTermsAgg(SearchResponse<CourseIndex> response, String aggName) {
        if (response.aggregations() == null || response.aggregations().get(aggName) == null) {
            return Collections.emptyList();
        }

        Aggregate aggregate = response.aggregations().get(aggName);
        if (!aggregate.isSterms()) return Collections.emptyList();

        return aggregate.sterms().buckets().array().stream()
                .map(StringTermsBucket::key)
                .map(FieldValue::stringValue)  // 修复这里
                .collect(Collectors.toList());
    }

}
