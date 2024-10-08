package com.yoobee.licenseplate.service;

import com.yoobee.licenseplate.entity.PlateTypeEntity;

import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;


/**
 * 服务实现层接口
 * @author jackiechan
 * @date 2024-09-15
 */
public interface PlateTypeService {

    public PlateTypeEntity getByPrimaryKey(Integer id);

    public PageInfo<PlateTypeEntity> queryByPage(Integer pageNo, Integer pageSize, Map<String, Object> map);

    public List<PlateTypeEntity> queryByCondition(Map<String, Object> map);

    public Map<String, Object> save(PlateTypeEntity plateTypeEntity);

	public Integer deleteById(Integer id);

    public Integer updateById(PlateTypeEntity plateTypeEntity);
}
