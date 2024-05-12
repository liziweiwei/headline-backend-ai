package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.TypeMapper;
import com.example.pojo.entity.Type;
import com.example.service.TypeService;
import com.example.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type> implements TypeService {

    @Autowired
    private TypeMapper typeMapper;

    /**
     * 查询首页分类(新闻,体育,娱乐,科技,其他)
     */
    @Override
    public Result findAllTypes() {
        LambdaQueryWrapper<Type> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(Type::getTid, Type::getTname);

        // 没有实体类可以接值,Type有四列,而我只有两列,使用List<Map<String, Object>> selectMaps()来查询指定列
        List<Map<String, Object>> mapList = typeMapper.selectMaps(lambdaQueryWrapper);
        // [{tname=新闻, tid=1}, {tname=体育, tid=2}, {tname=娱乐, tid=3}, {tname=科技, tid=4}, {tname=其他, tid=5}]
        return Result.success(mapList);
    }
}




