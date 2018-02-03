package com.zx.spark.web.dto;

import com.zx.spark.web.entity.DayVideoAccessStat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.LinkedList;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2018-02-03 20:45
 * 组合了EchartsDTO 和 实体类
 */
@Data
@Accessors(chain = true)
public class DayVideoAccessStatEChartsDTO {
    private List<EChartsDTO> eCharts = new LinkedList<>();
    private List<DayVideoAccessStat> data = new LinkedList<>();
}
