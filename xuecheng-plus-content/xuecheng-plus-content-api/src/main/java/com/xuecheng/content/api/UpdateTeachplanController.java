package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.service.SaveorUpdateTeachplan;
import com.xuecheng.content.service.TeachPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UpdateTeachplanController {
    @Autowired
    private SaveorUpdateTeachplan saveorUpdateTeachplan;
    @Autowired
    private TeachPlanService teachplanService;
    @PostMapping("/teachplan")
    public void updateTeachplan(@RequestBody SaveTeachplanDto saveTeachplanDto) {
        saveorUpdateTeachplan.saveorupdate(saveTeachplanDto);
    }
    @DeleteMapping("/teachplan/{id}")
    public void deleteTeachplan(@PathVariable Integer id) {
        saveorUpdateTeachplan.deleteTeachplan(id);
    }
    @PostMapping("teachplan/moveup/{id}")
    public void moveup(@PathVariable long id) {
        saveorUpdateTeachplan.moveupordown(id,0);
    }
    @PostMapping("teachplan/movedown/{id}")
    public void movedown(@PathVariable long id) {
        saveorUpdateTeachplan.moveupordown(id,1);
    }
    @PostMapping("teachplan/association/media")
    public void associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto){
        teachplanService.associationMedia(bindTeachplanMediaDto);
    }
}
