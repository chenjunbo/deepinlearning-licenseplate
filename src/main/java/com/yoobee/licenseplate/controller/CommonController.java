package com.yoobee.licenseplate.controller;

import com.yoobee.licenseplate.annotation.RetExclude;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * @author jackiechan
 */
@Hidden
@Controller
public class CommonController {

    @RetExclude
    @RequestMapping(value = "", method = {RequestMethod.GET})
    public String doc() {
        // return "redirect:swagger-ui.html";
        return "home/index";
    }

    @RetExclude
    @RequestMapping(value = "login", method = {RequestMethod.GET})
    public String loginPage() {
        return "home/login";
    }

    @RetExclude
    @RequestMapping(value = "index", method = {RequestMethod.GET})
    public String indexPage() {
        return "home/index";
    }

    @RetExclude
    @RequestMapping(value = "unauthorized", method = {RequestMethod.GET})
    public String unauthorizedPage() {
        return "unauthorized";
    }

}
