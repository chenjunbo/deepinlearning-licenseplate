package com.yoobee.licenseplate.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yoobee.licenseplate.constant.Constant;
import com.yoobee.licenseplate.exception.ResultReturnException;
import com.yoobee.licenseplate.service.FileService;
import com.yoobee.licenseplate.util.FileUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;


@Service
public class FileServiceImpl implements FileService {

    @Override
    public List<JSONObject> getFileTreeByDir(String rootPath, String dir, String typeFilter) {

        if(StringUtils.isEmpty(dir)){
            if(StringUtils.isEmpty(rootPath)){
                dir = Constant.DEFAULT_DIR;
            } else {
                dir = rootPath;
            }
        }
        if(StringUtils.isEmpty(typeFilter)){
            typeFilter = Constant.DEFAULT_TYPE;
        }

        File f = new File(dir);
        List<File> list = FileUtil.listFile(f, typeFilter, false);
        List<JSONObject> result = Lists.newArrayList();
        list.stream().forEach(n->{
            JSONObject jo = new JSONObject();
            jo.put("id", n.getAbsolutePath());
            jo.put("pid", n.getParentFile().getAbsolutePath());
            jo.put("filePath", n.getAbsolutePath());
            jo.put("fileName", n.getName());
            jo.put("isDir", n.isDirectory());
            result.add(jo);
        });
        return result;
    }


    @Override
    public File readFile(String filePath) {

        File f = new File(filePath);
        if(!f.exists() || f.isDirectory()) {
            throw new ResultReturnException("filePath参数异常，找不到指定的文件: " + filePath);
        }

        if(!f.exists() || f.isDirectory()) {
            throw new ResultReturnException("读取图片异常：" + f.getName());
        }
        return f;
    }




}
