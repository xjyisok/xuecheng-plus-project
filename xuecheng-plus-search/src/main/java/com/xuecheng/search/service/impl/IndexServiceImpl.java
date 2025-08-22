package com.xuecheng.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.UpdateResponse;
import com.alibaba.fastjson.JSON;
import com.xuecheng.base.exception.XueChengError;
import com.xuecheng.search.service.IndexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @description 课程索引管理接口实现 (ES 8.x 客户端)
 * @author Mr.M
 * @date 2022/9/25 7:23
 * @version 2.0
 */
@Slf4j
@Service
public class IndexServiceImpl implements IndexService {

 @Autowired
 private ElasticsearchClient client;

 @Override
 public Boolean addCourseIndex(String indexName, String id, Object object) {
  try {
   Map<String, Object> map = JSON.parseObject(JSON.toJSONString(object), Map.class);
   //String jsonString = JSON.toJSONString(object);

   IndexResponse response = client.index(i -> i
           .index(indexName)
           .id(id)
           .document(map)   // 直接存储 JSON 字符串
   );

   log.info("添加索引响应: {}", response.result().jsonValue());
   return response.result().name().equalsIgnoreCase("Created")
           || response.result().name().equalsIgnoreCase("Updated");

  } catch (Exception e) {
   log.error("添加索引出错: {}", e.getMessage(), e);
   XueChengError.cast("添加索引出错");
   return false;
  }
 }

 @Override
 public Boolean updateCourseIndex(String indexName, String id, Object object) {
  try {

   UpdateResponse<Map> response = client.update(u -> u
           .index(indexName)
           .id(id)
           .doc(JSON.parseObject(JSON.toJSONString(object), Map.class)),
           Map.class
   );

   log.info("更新索引响应: {}", response.result().jsonValue());
   return response.result().name().equalsIgnoreCase("Updated");

  } catch (Exception e) {
   log.error("更新索引出错: {}", e.getMessage(), e);
   XueChengError.cast("更新索引出错");
   return false;
  }
 }

 @Override
 public Boolean deleteCourseIndex(String indexName, String id) {
  try {
   DeleteResponse response = client.delete(d -> d
           .index(indexName)
           .id(id)
   );

   log.info("删除索引响应: {}", response.result().jsonValue());
   return response.result().name().equalsIgnoreCase("Deleted");

  } catch (Exception e) {
   log.error("删除索引出错: {}", e.getMessage(), e);
   XueChengError.cast("删除索引出错");
   return false;
  }
 }
}
