package com.zx.spark.web.service;

import com.zx.spark.web.dao.DayVideoAccessStatRepository;
import com.zx.spark.web.entity.DayVideoAccessStat;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * author:ZhengXing
 * datetime:2018-02-03 20:52
 * TopN统计相关服务类
 */
@Service
public class TopNService {
    private final DayVideoAccessStatRepository dayVideoAccessStatRepository;

    public TopNService(DayVideoAccessStatRepository dayVideoAccessStatRepository) {
        this.dayVideoAccessStatRepository = dayVideoAccessStatRepository;
    }

    /**
     * 根据day 查询当天访问次数TopN课程
     */
    public List<DayVideoAccessStat> findByDayTopN(String day) {
        return dayVideoAccessStatRepository.findTop5ByDayOrderByTimesDesc(day);
    }
}
