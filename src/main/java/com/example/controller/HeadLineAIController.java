package com.example.controller;

import com.example.service.HeadLineAIService;
import com.example.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ai")
@Slf4j
public class HeadLineAIController {

    @Autowired
    private HeadLineAIService headLineAIService;

    /**
     * 文章总结
     *
     * @param hid
     * @return
     */
    @PostMapping("/summary")
    public Result summarize(Integer hid) {

        log.info("需要总结的文章id:{}", hid);

        Result result = headLineAIService.summarize(hid);
        return result;
    }

    /**
     * 文章润色
     *
     * @param hid
     * @return
     */
    @PostMapping("polish")
    public Result polish(Integer hid) {

        log.info("需要润色文章id:{}", hid);

        Result result = headLineAIService.polish(hid);
        return result;
    }
}
