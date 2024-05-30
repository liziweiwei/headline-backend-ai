package com.example.controller;

import com.example.pojo.dto.HeadlineAiDTO;
import com.example.service.HeadLineAIService;
import com.example.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
     * @param headlineAiDTO
     * @return
     */
    @PostMapping("/summary")
    public Result summarize(@RequestBody HeadlineAiDTO headlineAiDTO) {

        log.info("需要总结的文章id:{}", headlineAiDTO.getHid());

        Result result = headLineAIService.summarize(headlineAiDTO);
        return result;
    }

    /**
     * 文章润色
     *
     * @param headlineAiDTO
     * @return
     */
    @PostMapping("polish")
    public Result polish(@RequestBody HeadlineAiDTO headlineAiDTO) {

        log.info("需要润色文章id:{}", headlineAiDTO.getHid());

        Result result = headLineAIService.polish(headlineAiDTO);
        return result;
    }
}
