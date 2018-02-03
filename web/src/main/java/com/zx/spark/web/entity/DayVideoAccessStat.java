package com.zx.spark.web.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * author:ZhengXing
 * datetime:2018-02-03 20:38
 * 每天视频访问统计 实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Entity(name = "day_video_access_topn_stat")
public class DayVideoAccessStat {

    /**
     * id
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 日期
     */
    private String day;

    /**
     * 视频id
     */
    private Long cmsId;

    /**
     * 访问次数
     */
    private Long times;
}
