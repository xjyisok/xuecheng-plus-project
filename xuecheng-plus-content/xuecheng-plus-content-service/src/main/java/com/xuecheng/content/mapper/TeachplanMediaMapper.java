package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.po.TeachplanMedia;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author itcast
 */
@Mapper
public interface TeachplanMediaMapper extends BaseMapper<TeachplanMedia> {
    @Select("select * from teachplan_media where teachplan_id = #{id}")
    TeachplanMedia selectByTeachplanId(@Param("id") Long id);
}
