package com.yoobee.licenseplate.mapper;

import com.yoobee.licenseplate.entity.PlateTypeEntity;

import java.util.List;
import java.util.Map;

public interface PlateTypeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PlateTypeEntity record);

    int insertSelective(PlateTypeEntity record);

    PlateTypeEntity selectByPrimaryKey(Integer id);

    List<PlateTypeEntity> selectByCondition(Map map);

    int updateByPrimaryKeySelective(PlateTypeEntity record);

    int updateByPrimaryKey(PlateTypeEntity record);
}
