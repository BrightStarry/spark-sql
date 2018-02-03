package com.zx.spark.web.dao;

import com.zx.spark.web.entity.DayVideoAccessStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * author:ZhengXing
 * datetime:2018-02-03 20:47
 */
public interface DayVideoAccessStatRepository extends JpaRepository<DayVideoAccessStat,Long> {

    /**
     * 根据day查询访问量前5的视频
     */
    List<DayVideoAccessStat> findTop5ByDayOrderByTimesDesc(String day);
}
