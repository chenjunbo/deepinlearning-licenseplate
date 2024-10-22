package com.yoobee.licenseplate.controller;

import com.yoobee.licenseplate.exception.ResultReturnException;
import com.yoobee.licenseplate.service.PlateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * @author jackiechan
 */
@Tag(name = "PlateController", description = "车牌识别")
@RestController
@RequestMapping("/plate")
public class PlateController {

    @Autowired
    private PlateService plateService;


    /**
     * 扫描d:/PlateDetect目录图片的基础信息
     * 将扫描到的信息，更新到数据库
     * 排除temp目录
     */
    @Operation(summary = "更新IMG文件基础信息", description = "")
    @RequestMapping(value = "/refreshFileInfo", method = RequestMethod.GET)
    public void refreshFileInfo() {
        plateService.refreshFileInfo();
    }


    /**
     * 根据数据库的图片基础信息，进行车牌识别
     * 更新图片识别信息到数据库
     * 生成识别结果; 多线程执行
     */
    @Operation(summary = "图片车牌识别", description = "路径不能包含中文，opencv路径转码过程乱码会报异常")
    @RequestMapping(value = "/recogniseAll", method = RequestMethod.GET)
    public Object recogniseAll() {
        return plateService.recogniseAll();
    }


    /**
     * 车牌识别接口
     * 输入：图片path
     * 处理：识别过程切图，识别结果切图；切图保存到temp/timestamp文件夹，图片文件名按timestamp排序
     * 操作过程结果保存数据库，操作前检查数据库及temp文件夹下是否有对应的切图文件
     * 输出：返回过程切图、识别结果切图文件路径集合
     */
    @Operation(summary = "图片车牌识别", description = "路径不能包含中文，opencv路径转码过程乱码会报异常")
    @Parameters({
            @Parameter(name = "filePath", description = "文件路径", required = true, schema = @Schema(implementation = String.class)),
            @Parameter(name = "reRecognise", description = "重新识别", schema = @Schema(implementation = Boolean.class), example = "false")
    })
    @RequestMapping(value = "/recogniselocal", method = RequestMethod.GET)
    public Object recognise(String filePath, Boolean reRecognise) {
        try {
            if (null != filePath) {
                filePath = URLDecoder.decode(filePath, "utf-8");
            }
            if (null == reRecognise) {
                reRecognise = false;
            }
        } catch (UnsupportedEncodingException e) {
            throw new ResultReturnException("filePath参数异常");
        }
        return plateService.recognise(filePath, reRecognise);
    }


    @Operation(summary = "获取图片信息", description = "通过opencv计算，获取图片基础信息等")
    @RequestMapping(value = "/getImgInfo", method = RequestMethod.POST)
    public Object getImgInfo(String imgPath) {
        if (null != imgPath) {
            try {
                imgPath = URLDecoder.decode(imgPath, "utf-8");
            } catch (UnsupportedEncodingException e) {
                throw new ResultReturnException("imgPath参数异常");
            }
        }
        return plateService.getImgInfo(imgPath);
    }


    @Operation(summary = "获取hsv值", description = "根据前端传递的坐标，通过opencv计算，获取图片坐标位置的hsv值")
    @RequestMapping(value = "/getHSVValue", method = RequestMethod.POST)
    public Object getHSVValue(String imgPath, Integer row, Integer col) {
        if (null != imgPath) {
            try {
                imgPath = URLDecoder.decode(imgPath, "utf-8");
            } catch (UnsupportedEncodingException e) {
                throw new ResultReturnException("imgPath参数异常");
            }
        }
        return plateService.getHSVValue(imgPath, row, col);
    }


    @RequestMapping(value = "/recognise", method = RequestMethod.POST)
    public Object recognise(MultipartFile image, @RequestParam(defaultValue = "true") Boolean reRecognise) throws IOException {
        String relativePath = "uploads/";
        // 创建路径（如果不存在）
        Path path = Paths.get(relativePath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
        // 获取完整的文件路径
        Path filePath = path.resolve(image.getOriginalFilename());
        image.transferTo(filePath);
        return plateService.recognise(filePath.toAbsolutePath().toString(), reRecognise);
    }
}
