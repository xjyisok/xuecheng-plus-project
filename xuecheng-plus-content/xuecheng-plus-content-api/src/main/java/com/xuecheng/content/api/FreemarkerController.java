package com.xuecheng.content.api;

import org.bouncycastle.math.raw.Mod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * @author Mr.M
 * @version 1.0
 * @description freemarker测试
 * @date 2022/9/15 19:20
 */
@Controller

public class FreemarkerController {

    @GetMapping("/testfreemarker")
    @ResponseBody
    public ModelAndView test(){
        System.out.println("testfreemarker 被访问");
        ModelAndView modelAndView = new ModelAndView();
        //设置模型数据
        modelAndView.addObject("model", Map.of("name", "小明"));
        //设置模板名称
        modelAndView.setViewName("test");
        //return "Controller hit!";
        return modelAndView;
    }


}