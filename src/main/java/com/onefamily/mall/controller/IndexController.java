package com.onefamily.mall.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("main")
@Controller
public class IndexController {

    private Logger log = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping("index.do")
    @ResponseBody
    Map<String, Object> index() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("bRet", true);
        map.put("message", "success");
        return map;
    }

}
