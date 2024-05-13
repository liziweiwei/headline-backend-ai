package com.example.controller;

import com.example.service.HeadLineAIService;
import com.example.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ai")
@Slf4j
public class HeadLineAIController {

    @Autowired
    private HeadLineAIService headLineAIService;

    @GetMapping("/summary")
    public Result summarize(Integer hid) {

        log.info("需要总结的文章id:{}", hid);

        Result result = headLineAIService.summarize(hid);
        return result;
    }
}
