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
 * datetime:2018-02-03 20:41
 * 根据城市统计TopN
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Entity
public class DayVideoCityAccessStat {

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
     * 城市
     */
    private String city;

    /**
     * 视频id
     */
    private Long cmsId;

    /**
     * 访问次数
     */
    private Long times;

    /**
     * 每个城市的访问次数排行
     */
    private Integer timesRank;
}
