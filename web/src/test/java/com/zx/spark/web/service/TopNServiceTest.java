package com.zx.spark.web.service;

import com.zx.spark.web.WebApplicationTests;
import com.zx.spark.web.entity.DayVideoAccessStat;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2018-02-03 21:00
 */
public class TopNServiceTest extends WebApplicationTests{
    @Resource
    private  TopNService topNService;



    @Test
    public void findByDayTopN() throws Exception {
        List<DayVideoAccessStat> list = topNService.findByDayTopN("20161110");
        list.forEach(System.out::println);
    }

}