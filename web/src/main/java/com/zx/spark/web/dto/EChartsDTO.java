package com.zx.spark.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author:ZhengXing
 * datetime:2018-02-03 20:39
 * 用于传递给前端构造 ECharts 所需的参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EChartsDTO {
    /**
     * 饼图中的每个块的 名字
     */
    private String name;


    /**
     * 饼图中的每个块的 值
     */
    private Long value;
}
