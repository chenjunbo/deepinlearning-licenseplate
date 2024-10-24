package com.yoobee.licenseplate.mapper;

import com.yoobee.licenseplate.entity.SystemMenuEntity;

import java.util.List;
import java.util.Map;

public interface SystemMenuMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SystemMenuEntity record);

    int insertSelective(SystemMenuEntity record);

    SystemMenuEntity selectByPrimaryKey(Integer id);

    List<SystemMenuEntity> selectByCondition(Map<String, Object> map);

    int updateByPrimaryKeySelective(SystemMenuEntity record);

    int updateByPrimaryKey(SystemMenuEntity record);
}
