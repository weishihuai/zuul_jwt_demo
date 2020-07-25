package com.springcloud.zuul.apigateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {


    @GetMapping(value = "/userLogin")
    public String loginByPassword() {
        //此处省略具体的登录逻辑
        return "登录成功！";
    }
}
