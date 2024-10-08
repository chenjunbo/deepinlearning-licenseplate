package com.yoobee.licenseplate.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * t_plate_type
 *
 * @author jackiechan
 * 2024-09-16
 */
@Data
@NoArgsConstructor
public class PlateTypeEntity implements Serializable {
    /**
     * id
     */
    @Schema(example = "Integer-id")
    private Integer id;

    /**
     * typeName
     */
    @Schema(example = "String-typeName")
    private String typeName;

    /**
     * plateColor
     */
    @Schema(example = "String-plateColor")
    private String plateColor;

    /**
     * charColor
     */
    @Schema(example = "String-charColor")
    private String charColor;

    /**
     * charCount
     */
    @Schema(example = "Integer-charCount")
    private Integer charCount;

    /**
     * chIndex
     */
    @Schema(example = "String-chIndex")
    private String chIndex;

    /**
     * lineCount
     */
    @Schema(example = "Integer-lineCount")
    private Integer lineCount;

    /**
     * heightPx
     */
    @Schema(example = "Integer-heightPx")
    private Integer heightPx;

    /**
     * widthPx
     */
    @Schema(example = "Integer-widthPx")
    private Integer widthPx;

    /**
     * heightGb
     */
    @Schema(example = "Integer-heightGb")
    private Integer heightGb;

    /**
     * widthGb
     */
    @Schema(example = "Integer-widthGb")
    private Integer widthGb;

    /**
     * plateMinH
     */
    @Schema(example = "Integer-plateMinH")
    private Integer plateMinH;

    /**
     * plateMaxH
     */
    @Schema(example = "Integer-plateMaxH")
    private Integer plateMaxH;

    /**
     * charMinH
     */
    @Schema(example = "Integer-charMinH")
    private Integer charMinH;

    /**
     * charMaxH
     */
    @Schema(example = "Integer-charMaxH")
    private Integer charMaxH;

    /**
     * remark
     */
    @Schema(example = "String-remark")
    private String remark;

    /**
     * createTime
     */
    @Schema(example = "String-createTime")
    private String createTime;

    /**
     * creatorId
     */
    @Schema(example = "Integer-creatorId")
    private Integer creatorId;

    /**
     * version
     */
    @Schema(example = "Integer-version")
    private Integer version;

    /**
     * delFlag
     */
    @Schema(example = "Integer-delFlag")
    private Integer delFlag;

    private static final long serialVersionUID = 1L;
}
