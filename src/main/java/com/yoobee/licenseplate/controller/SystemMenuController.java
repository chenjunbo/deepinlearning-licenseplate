package com.yoobee.licenseplate.controller;

import com.yoobee.licenseplate.entity.SystemMenuEntity;
import com.yoobee.licenseplate.service.SystemMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * @author jackiechan
 */
@Tag(name = "SystemMenuController", description = "菜单管理")
@RestController
@RequestMapping("/systemMenu")
public class SystemMenuController {

    @Autowired
    private SystemMenuService systemMenuService;

    /**
     * 分页查询
     *
     * @param pageNo
     * @param pageSize
     * @param map
     */
    @Operation(summary = "分页获取记录", description = "分页获取记录")
    @Parameters({
            @Parameter(name = "pageNo", description = "当前页码", required = true, schema =@Schema(implementation= Integer.class), example = "1"),
            @Parameter(name = "pageSize", description = "每页数量", required = true, schema =@Schema(implementation= Integer.class), example = "10"),
            @Parameter(name = "map", description = "举例：{} or {\"name\":\"张三\"}")
    })
    @RequestMapping(value = "/queryByPage", method = RequestMethod.POST)
    public Object queryByPage(@RequestParam Integer pageNo, @RequestParam Integer pageSize, @RequestBody Map<String, Object> map) {
        return systemMenuService.queryByPage(pageNo, pageSize, map);
    }


    @Operation(summary = "按条件查询", description = "不分页", responses = @ApiResponse(content = @Content(schema = @Schema(implementation = SystemMenuEntity.class))))
    @Parameters({
            @Parameter(name = "map", description = "举例：{} or {\"name\":\"张三\"}")
    })
    @RequestMapping(value = "/queryByCondition", method = RequestMethod.POST)
    public Object queryByCondition(@RequestBody Map<String, Object> map) {
        return systemMenuService.queryByCondition(map);
    }


    /**
     * Post请求，新增数据，成功返回ID
     *
     * @param entity
     */
    @Operation(summary = "新增数据，成功返回ID", description = "新增数据，成功返回ID")
    @Parameter(name = "entity", description = "举例：{} or {\"name\":\"张三\"}", required = true)
    @RequestMapping(value = "", method = RequestMethod.POST)
    public Object save(@RequestBody SystemMenuEntity entity) {
        return systemMenuService.save(entity);
    }


    /**
     * 获取登录用户的权限下菜单
     *
     * @return
     */
    @Operation(summary = "获取登录用户菜单", description = "")
    @GetMapping("/getUserMenu")
    public Object getUserMenu() {
        return systemMenuService.getUserMenu();
    }


}

