package com.xuecheng.content.model.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.xuecheng.base.exception.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddCourseParamDto {
    @NotEmpty(groups = {ValidationGroups.Insert.class},message = "添加课程名称不能为空")
    @NotEmpty(groups = {ValidationGroups.Update.class},message = "修改课程名称不能为空")
    @NotEmpty(message = "课程名称不能为空")
    private String name;

    /**
     * 适用人群
     */
    @NotEmpty(groups = {ValidationGroups.Insert.class},message = "添加适用人群不能为空")
    @NotEmpty(groups = {ValidationGroups.Update.class},message = "修改适用人群不能为空")
    @NotEmpty(message = "适用人群不能为空")
    private String users;

    /**
     * 课程标签
     */
    private String tags;

    /**
     * 大分类
     */
    @NotEmpty(groups = {ValidationGroups.Insert.class},message = "添加课程分类不能为空")
    @NotEmpty(groups = {ValidationGroups.Update.class},message = "修改课程分类不能为空")
    @NotEmpty(message = "课程分类不能为空")
    private String mt;

    /**
     * 小分类
     */
    @NotEmpty(message = "课程分类不能为空")
    @NotEmpty(groups = {ValidationGroups.Insert.class},message = "添加课程小分类不能为空")
    @NotEmpty(groups = {ValidationGroups.Update.class},message = "修改课程小分类不能为空")
    private String st;

    /**
     * 课程等级
     */
    @NotEmpty(groups = {ValidationGroups.Insert.class},message = "添加课程等级不能为空")
    @NotEmpty(groups = {ValidationGroups.Update.class},message = "修改课程等级不能为空")
    @NotEmpty(message = "课程等级不能为空")
    private String grade;

    /**
     * 教育模式(common普通，record 录播，live直播等）
     */
    @NotEmpty(groups = {ValidationGroups.Insert.class},message = "添加教学模式不能为空")
    @NotEmpty(groups = {ValidationGroups.Update.class},message = "修改教学模式不能为空")
    @NotEmpty(message = "教学模式不能为空")
    private String teachmode;

    /**
     * 课程介绍
     */
    private String description;

    /**
     * 课程图片
     */
    @NotEmpty(groups = {ValidationGroups.Insert.class},message = "添加收费规则不能为空")
    @NotEmpty(groups = {ValidationGroups.Update.class},message = "修改收费规则不能为空")
    @NotEmpty(message = "收费规则不能为空")
    private String charge;
    private Integer price;
    private Float originalPrice;
    private String qq;
    private String wechat;
    private String phone;
    private Integer validDays;
//    @NotEmpty(groups = {ValidationGroups.Insert.class},message = "添加课程封面不能为空")
//    @NotEmpty(groups = {ValidationGroups.Update.class},message = "修改课程封面不能为空")
//    @NotEmpty(message = "课程封面不能为空")
    private String pic;


}
