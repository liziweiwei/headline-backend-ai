package com.example.controller;

import com.example.pojo.dto.HeadlineDTO;
import com.example.pojo.dto.HeadlineUpdateDTO;
import com.example.service.HeadlineService;
import com.example.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("headline")
@Slf4j
//@CrossOrigin
public class HeadLineController {

    @Autowired
    private HeadlineService headlineService;

    /**
     * 发布新闻
     *
     * @param headlineDTO
     * @param token
     * @return
     */
    @PostMapping("publish")
    public Result publish(@RequestBody HeadlineDTO headlineDTO, @RequestHeader String token) {

        log.info("发布新闻...");

        Result result = headlineService.publish(headlineDTO, token);
        return result;
    }

    /**
     * 根据hid查询新闻详情
     *
     * @param hid
     * @return
     */
    @PostMapping("findHeadlineByHid")
    public Result findHeadlineByHid(Integer hid) {

        log.info("根据hid查询新闻详情...");

        Result result = headlineService.findHeadlineByHid(hid);
        return result;
    }

    /**
     * 修改新闻
     *
     * @param headlineUpdateDTO
     * @return
     */
    @PostMapping("update")
    public Result update(@RequestBody HeadlineUpdateDTO headlineUpdateDTO) {

        log.info("修改新闻...");

        Result result = headlineService.updateHeadlineByHid(headlineUpdateDTO);
        return result;
    }

    /**
     * 删除新闻
     *
     * @param hid
     * @return
     */
    @PostMapping("removeByHid")
    public Result removeByHid(Integer hid) {

        log.info("删除新闻...");

        Result result = headlineService.removeByHid(hid);
        return result;
    }
}
