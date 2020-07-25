package com.springcloud.zuul.goodsservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GoodsController {

    //模拟几个商品
    private static List<String> goodsList = new ArrayList<>();

    static {
        goodsList.add("图书");
        goodsList.add("相册");
        goodsList.add("风扇");
        goodsList.add("手机");
        goodsList.add("电脑");
    }

    @GetMapping("/getGoodsList")
    public List<String> getGoodsList() {
        return goodsList;
    }

}
