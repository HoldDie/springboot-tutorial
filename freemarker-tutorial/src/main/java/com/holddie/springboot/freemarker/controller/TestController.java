package com.holddie.springboot.freemarker.controller;

import com.holddie.springboot.freemarker.component.ApplicationContextRegister;
import com.holddie.springboot.freemarker.framework.SpringContextHolder;
import com.holddie.springboot.freemarker.framework.SpringUtil;
import com.holddie.springboot.freemarker.framework.cache.ICache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 测试freemarker
 * @author yangze1
 * @version 1.0.0
 * @email holddie@163.com
 * @date 2018/5/11 21:34
 */
@Controller
@RequestMapping("/")
public class TestController {


    @Value("${site.baseUrl:''}")
    private String site_bash_url;

    @GetMapping
    public String index(Model model){
        model.addAttribute("name","123456789");
        return "index";
    }

    @GetMapping("/test")
    public String test(Model model) {
        model.addAttribute("name", "holddie");
        ICache cache = (ICache) SpringUtil.getBean("cache");
        System.out.println(cache.toString());
        ICache cache1= SpringContextHolder.getBean("cache");
        ICache cache2= (ICache) ApplicationContextRegister.getApplicationContext().getBean("cache");
        System.out.println(cache1);
        System.out.println(cache2);
        return "test";
    }
}
