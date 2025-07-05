package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.SaveTeachplanDto;

public interface SaveorUpdateTeachplan {
    public void saveorupdate(SaveTeachplanDto saveTeachplanDto);
    public void deleteTeachplan(long id);
    public void moveupordown(Long id,int status);
}
