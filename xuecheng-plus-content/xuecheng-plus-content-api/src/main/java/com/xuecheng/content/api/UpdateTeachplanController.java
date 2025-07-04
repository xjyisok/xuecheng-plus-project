package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.service.SaveorUpdateTeachplan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UpdateTeachplanController {
    @Autowired
    private SaveorUpdateTeachplan saveorUpdateTeachplan;
    @PostMapping("/teachplan")
    public void updateTeachplan(@RequestBody SaveTeachplanDto saveTeachplanDto) {
        saveorUpdateTeachplan.saveorupdate(saveTeachplanDto);
    }
}
