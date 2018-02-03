package com.zx.spark.web.controller;

import com.zx.spark.web.dto.DayVideoAccessStatEChartsDTO;
import com.zx.spark.web.dto.EChartsDTO;
import com.zx.spark.web.entity.DayVideoAccessStat;
import com.zx.spark.web.service.TopNService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * author:ZhengXing
 * datetime:2018-02-03 20:59
 */
@RestController
@RequestMapping("/")
public class TopNController {

    private final TopNService topNService;

    public TopNController(TopNService topNService) {
        this.topNService = topNService;
    }

    /**
     * 根据day查询Top5视频
     */
    @RequestMapping("/top5")
    public DayVideoAccessStatEChartsDTO findByDayTop5(String day) {
        DayVideoAccessStatEChartsDTO result = new DayVideoAccessStatEChartsDTO();
        List<EChartsDTO> eCharts = result.getECharts();

        List<DayVideoAccessStat> list = topNService.findByDayTopN(day);
        list.forEach(item -> {
            eCharts.add(new EChartsDTO(String.valueOf(item.getCmsId()),item.getTimes()));
        });
        return result.setData(list);
    }
}
