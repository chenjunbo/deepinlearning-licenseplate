package com.yoobee.licenseplate.controller;

import com.yoobee.licenseplate.annotation.RetExclude;
import com.yoobee.licenseplate.exception.ResultReturnException;
import com.yoobee.licenseplate.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


/**
 * @author jackiechan
 */
@Tag(name = "file_manager", description = "文件管理")
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;


    /**
     * 加载文件树结构
     * 输入：文件夹路径，缺省值：D:\\PlateDetect\\ 文件类型，缺省值：png,jpg,jpeg
     * 输出：当前目录下第一层级文件的list
     *
     * @param dir
     * @return
     */
    @Operation(summary = "获取文件结构")
    @Parameter(name = "dir", description = "文件夹路径", required = true, schema =@Schema(implementation= String.class))
    @RequestMapping(value = "/getFileTreeByDir", method = RequestMethod.GET)
    public Object getFileTreeByDir(String rootPath, String dir, String typeFilter) {
        try {
            if (null != dir) {
                dir = URLDecoder.decode(dir, "utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            throw new ResultReturnException("dir参数异常");
        }
        return fileService.getFileTreeByDir(rootPath, dir, typeFilter);
    }


    /**
     * 预览图片文件
     *
     * @param filePath
     * @param response
     * @return
     * @throws IOException
     */
    @RetExclude
    @Operation(summary = "预览文件", description = "根据路径，直接读取盘符文件; 返回输出流")
    @GetMapping(value = "/readFile", produces = {"image/jpeg"})
    public ResponseEntity<InputStreamResource> readFile(String filePath, HttpServletResponse response) throws IOException {
        try {
            filePath = URLDecoder.decode(filePath, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new ResultReturnException("filePath参数异常");
        }
        //文件输出流，输出到客户端
        File file = fileService.readFile(filePath);
        InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<InputStreamResource>(isr, headers, HttpStatus.OK);
    }

}
