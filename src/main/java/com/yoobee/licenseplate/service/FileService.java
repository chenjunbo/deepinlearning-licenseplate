package com.yoobee.licenseplate.service;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.util.List;


public interface FileService {

    List<JSONObject> getFileTreeByDir(String rootPath, String dir, String typeFilter);

    File readFile(String filePath);


}
