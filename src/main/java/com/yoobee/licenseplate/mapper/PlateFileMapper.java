package com.yoobee.licenseplate.mapper;

import com.yoobee.licenseplate.entity.PlateFileEntity;

import java.util.List;
import java.util.Map;
public interface PlateFileMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PlateFileEntity record);

    int insertSelective(PlateFileEntity record);

    PlateFileEntity selectByPrimaryKey(Integer id);

    List<PlateFileEntity> selectByCondition(Map<String, Object> map);

    int updateByPrimaryKeySelective(PlateFileEntity record);

    int updateByPrimaryKey(PlateFileEntity record);

    List<PlateFileEntity> getUnRecogniseList();
}
