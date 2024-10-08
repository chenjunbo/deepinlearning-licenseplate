package com.yoobee.licenseplate.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 用于生成ID的工具类
 *
 * @author jackiechan
 * @date 2024-09-16
 */
public class GenerateIdUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmmssSSS");

    /**
     * 获取时间戳，生成文件名称
     */
    public static synchronized Long getId() {
        try {
            Thread.sleep(1);
        } catch (Exception e) {
            System.out.println(e);
        }
        return System.currentTimeMillis();
    }

    public static synchronized String getStrId() {
        try {
            Thread.sleep(1);
        } catch (Exception e) {
        }
        return FORMATTER.format(LocalDateTime.now());
    }


}
