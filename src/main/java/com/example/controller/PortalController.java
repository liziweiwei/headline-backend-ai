package com.example.controller;

import com.example.pojo.dto.PortalPageQueryDTO;
import com.example.service.HeadlineService;
import com.example.service.TypeService;
import com.example.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("portal")
@Slf4j
public class PortalController {

    @Autowired
    private TypeService typeService;

    @Autowired
    private HeadlineService headlineService;

    /**
     * 查询首页分类
     */
    @GetMapping("findAllTypes")
    public Result findAllTypes() {

        log.info("查询首页分类(新闻,体育,娱乐,科技,其他)...");

        Result result = typeService.findAllTypes();
        return result;
    }

    /**
     * 分页查询首页头条信息
     */
    @PostMapping("findNewsPage")
    public Result findNewsPage(@RequestBody PortalPageQueryDTO portalDTO) {

        log.info("前端传来的分页信息:{}", portalDTO);

        Result result = headlineService.findNewsPage(portalDTO);
        return result;
    }

    /**
     * 查询头条详情
     */
    @PostMapping("showHeadlineDetail")
    public Result showHeadlineDetail(Integer hid, Integer uid) {

        log.info("查询头条详情,前端传来的hid:{}", hid);

        Result result = headlineService.showHeadlineDetail(hid, uid);
        return result;
    }

}
